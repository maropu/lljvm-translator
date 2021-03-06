/*
 * Copyright (c) 2009 David Roberts <d@vidr.cc>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#include "backend.h"

#include <sstream>

void JVMWriter::printValueLoad(const Value *v) {
  if (const Function *f = dyn_cast<Function>(v)) {
    std::string sig = getValueName(f) + getCallSignature(f->getFunctionType());
    if (externRefs.count(v)) {
      printSimpleInstruction("ldc", "\"\"");
      printSimpleInstruction("ldc", '"' + sig + '"');
    } else {
      printSimpleInstruction("ldc", '"' + classname + '"');
      printSimpleInstruction("ldc", '"' + sig + '"');
    }
  } else if (isa<GlobalVariable>(v)) {
    if (externRefs.count(v)) {
      std::string funcName;
      raw_string_ostream strbuf(funcName);
      const Type *pt = v->getType();
      strbuf << "io/github/maropu/lljvm/runtime/FieldValue/get_" << getTypePostfix(pt, true) <<
        "(Ljava/lang/String;)" << getTypeDescriptor(pt);
      strbuf.flush();
      printSimpleInstruction("ldc", '"' + getValueName(v) + '"');
      printSimpleInstruction("invokestatic", funcName);
    } else {
      printSimpleInstruction("getstatic", classname + "/" + getValueName(v) + " J");
    }
  } else if (isa<ConstantPointerNull>(v)) {
    printPtrLoad(0);
  } else if (const ConstantExpr *ce = dyn_cast<ConstantExpr>(v)) {
    printConstantExpr(ce);
  } else if (const Constant *c = dyn_cast<Constant>(v)) {
    printConstLoad(c);
  } else {
    if (getLocalVarNumber(v) <= 3) {
      printSimpleInstruction(getTypePrefix(v->getType(), true) +
        "load_" + utostr(getLocalVarNumber(v)) + " ; " + getValueName(v));
    } else {
      printSimpleInstruction(getTypePrefix(v->getType(), true) +
        "load", utostr(getLocalVarNumber(v)) + " ; " + getValueName(v));
    }
  }
}

/**
 * Stores the value currently on top of the stack to the given local variable.
 */
void JVMWriter::printValueStore(const Value *v) {
  if (isa<Function>(v) || isa<GlobalVariable>(v) || isa<Constant>(v)) {
    std::stringstream err_msg;
    err_msg << "Invalid value: Value=" << getValueName(v);
    lljvm_unreachable(err_msg.str());
  }

  unsigned int bitWidth = getTypeBitWidth(v->getType());
  // Truncates int
  if (bitWidth == 16) {
    printSimpleInstruction("i2s");
  } else if (bitWidth == 8) {
    printSimpleInstruction("i2b");
  } else if (bitWidth == 1) {
    printSimpleInstruction("iconst_1");
    printSimpleInstruction("iand");
  }

  if (getLocalVarNumber(v) <= 3) {
    printSimpleInstruction(
      getTypePrefix(v->getType(), true) + "store_" + utostr(getLocalVarNumber(v)) +
        " ; " + getValueName(v));
  } else {
    printSimpleInstruction(
      getTypePrefix(v->getType(), true) + "store", utostr(getLocalVarNumber(v)) +
        " ; " + getValueName(v));
  }
}

/**
 * Loads a value from the given address.
 */
void JVMWriter::printIndirectLoad(const Value *v) {
  printValueLoad(v);
  const Type *ty = v->getType();
  if (const PointerType *p = dyn_cast<PointerType>(ty)) {
    ty = p->getElementType();
  }
  printIndirectLoad(ty);
}

/**
 * Loads a value of the given type from the address curently on top of the stack.
 */
void JVMWriter::printIndirectLoad(const Type *ty) {
  printSimpleInstruction(
    "invokestatic",
    "io/github/maropu/lljvm/runtime/VMemory/load_" + getTypePostfix(ty) + "(J)" + getTypeDescriptor(ty));
}

/**
 * Stores a value at the given address.
 */
void JVMWriter::printIndirectStore(const Value *ptr, const Value *val) {
  const PointerType *pTy = cast<PointerType>(ptr->getType());
  if (const VectorType *vecTy = dyn_cast<VectorType>(pTy->getElementType())) {
    for (int i = 0; i < vecTy->getNumElements(); i++) {
      // Moves the pointer forward
      printValueLoad(ptr);
      int elemSize = getTypeAllocSize(vecTy->getElementType());
      printSimpleInstruction("ldc2_w", utostr(i * elemSize));
      printSimpleInstruction("ladd");

      // Loads a value in a vector
      if (const Constant *c = dyn_cast<Constant>(val)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(val)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            // In case of undef, we set 0
            printSimpleInstruction("iconst_0");
            printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(val)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(val)) {
          // We need to handle the all zero case, e.g.,
          // store <2 x double> <double 0.000000e+00, double 0.000000e+00>, <2 x double>* %1, align 8
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
        } else if (isa<UndefValue>(val)) {
          // If undef, we set 0 for safe-guards, e.g.,
          // store <4 x double> undef, <4 x double>* %1, align 8
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown constant value type: Type=" << getTypeIDName(val->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(val);
        printSimpleInstruction("ldc2_w", utostr(i * elemSize));
        printSimpleInstruction("ladd");
        printIndirectLoad(vecTy->getElementType());
      }

      printIndirectStore(vecTy->getElementType());
    }
  } else {
    printValueLoad(ptr);
    printValueLoad(val);
    printIndirectStore(val->getType());
  }
}

/**
 * Indirectly stores a value of the given type.
 */
void JVMWriter::printIndirectStore(const Type *ty) {
  printSimpleInstruction(
    "invokestatic",
    "io/github/maropu/lljvm/runtime/VMemory/store(J" + getTypeDescriptor(ty) + ")V");
}
