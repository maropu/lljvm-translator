/*
 * Copyright (c) 2009-2010 David Roberts <d@vidr.cc>
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

#include <assert.h>

#include <sstream>

static unsigned int alignOffset(unsigned int offset, unsigned int align) {
  return offset + ((align - (offset % align)) % align);
}

static std::string getPredicate(unsigned int predicate) {
  std::string inst;
  switch (predicate) {
    case ICmpInst::ICMP_EQ: inst = "icmp_eq"; break;
    case ICmpInst::ICMP_NE: inst = "icmp_ne"; break;
    case ICmpInst::ICMP_ULE: inst = "icmp_ule"; break;
    case ICmpInst::ICMP_SLE: inst = "icmp_sle"; break;
    case ICmpInst::ICMP_UGE: inst = "icmp_uge"; break;
    case ICmpInst::ICMP_SGE: inst = "icmp_sge"; break;
    case ICmpInst::ICMP_ULT: inst = "icmp_ult"; break;
    case ICmpInst::ICMP_SLT: inst = "icmp_slt"; break;
    case ICmpInst::ICMP_UGT: inst = "icmp_ugt"; break;
    case ICmpInst::ICMP_SGT: inst = "icmp_sgt"; break;
    case FCmpInst::FCMP_UGT: inst = "fcmp_ugt"; break;
    case FCmpInst::FCMP_OGT: inst = "fcmp_ogt"; break;
    case FCmpInst::FCMP_UGE: inst = "fcmp_uge"; break;
    case FCmpInst::FCMP_OGE: inst = "fcmp_oge"; break;
    case FCmpInst::FCMP_ULT: inst = "fcmp_ult"; break;
    case FCmpInst::FCMP_OLT: inst = "fcmp_olt"; break;
    case FCmpInst::FCMP_ULE: inst = "fcmp_ule"; break;
    case FCmpInst::FCMP_OLE: inst = "fcmp_ole"; break;
    case FCmpInst::FCMP_UEQ: inst = "fcmp_ueq"; break;
    case FCmpInst::FCMP_OEQ: inst = "fcmp_oeq"; break;
    case FCmpInst::FCMP_UNE: inst = "fcmp_une"; break;
    case FCmpInst::FCMP_ONE: inst = "fcmp_one"; break;
    case FCmpInst::FCMP_ORD: inst = "fcmp_ord"; break;
    case FCmpInst::FCMP_UNO: inst = "fcmp_uno"; break;
    default:
      std::stringstream err_msg;
      err_msg << "Unknown cmp predicate: Predicate=" << predicate;
      lljvm_unreachable(err_msg.str());
  }
  return inst;
}

void JVMWriter::printCmpInstruction(unsigned int predicate, const Value *left, const Value *right) {
  // First, we need to check if the input is a vector type or not.
  // TODO: We need to support vector types in other types?
  if (const SequentialType *leftSeqTy = dyn_cast<SequentialType>(left->getType())) {
    const SequentialType *rightSeqTy = cast<SequentialType>(right->getType());

    // TODO: A return type is always i1?
    Type *rTy = Type::getInt1Ty(module->getContext());
    int size = targetData->getTypeAllocSize(rTy);
    printSimpleInstruction("sipush", utostr(leftSeqTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (int i = 0; i < leftSeqTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (const Constant *c = dyn_cast<Constant>(left)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(left)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            printValueLoad(vec->getAggregateElement((unsigned) 0));
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(left)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(left)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(leftSeqTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown left constant value type: Type=" << getTypeIDName(left->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(left);
        int lsize = targetData->getTypeAllocSize(leftSeqTy->getElementType());
        printSimpleInstruction("ldc2_w", utostr(i * lsize));
        printSimpleInstruction("ladd");
        printIndirectLoad(leftSeqTy->getElementType());
      }
      if (const Constant *c = dyn_cast<Constant>(right)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(right)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            printValueLoad(vec->getAggregateElement((unsigned) 0));
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(right)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(right)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(rightSeqTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown right constant value type: Type=" << getTypeIDName(right->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(right);
        int rsize = targetData->getTypeAllocSize(rightSeqTy->getElementType());
        printSimpleInstruction("ldc2_w", utostr(i * rsize));
        printSimpleInstruction("ladd");
        printIndirectLoad(rightSeqTy->getElementType());
      }

      const std::string inst = getPredicate(predicate);
      printVirtualInstruction(
        inst + "(" + getTypeDescriptor(leftSeqTy->getElementType(), true) +
          getTypeDescriptor(rightSeqTy->getElementType(), true) + ")Z");
      printIndirectStore(rTy);
    }
  } else {
      const std::string inst = getPredicate(predicate);
      printVirtualInstruction(inst + "(" + getTypeDescriptor(left->getType(), true) +
        getTypeDescriptor(right->getType(), true) + ")Z", left, right);
  }
}

void JVMWriter::printArithmeticInstruction(
    unsigned int op,
    const std::string& typeDescriptor,
    const std::string& typePrefix,
    int typeBitWidth) {
  switch (op) {
    case Instruction::Add:
    case Instruction::FAdd:
      printSimpleInstruction(typePrefix + "add");
      break;
    case Instruction::Sub:
    case Instruction::FSub:
      printSimpleInstruction(typePrefix + "sub");
      break;
    case Instruction::Mul:
    case Instruction::FMul:
      printSimpleInstruction(typePrefix + "mul");
      break;
    case Instruction::SDiv:
    case Instruction::FDiv:
      printSimpleInstruction(typePrefix + "div");
      break;
    case Instruction::SRem:
    case Instruction::FRem:
      printSimpleInstruction(typePrefix + "rem");
      break;
    case Instruction::And:
      printSimpleInstruction(typePrefix + "and");
      break;
    case Instruction::Or:
      printSimpleInstruction(typePrefix + "or");
      break;
    case Instruction::Xor:
      printSimpleInstruction(typePrefix + "xor");
      break;
    case Instruction::Shl:
      if (typeBitWidth == 64) {
        printSimpleInstruction("l2i");
      }
      printSimpleInstruction(typePrefix + "shl");
      break;
    case Instruction::LShr:
      if (typeBitWidth == 64) {
        printSimpleInstruction("l2i");
      }
      printSimpleInstruction(typePrefix + "ushr");
      break;
    case Instruction::AShr:
      if (typeBitWidth == 64) {
        printSimpleInstruction("l2i");
      }
      printSimpleInstruction(typePrefix + "shr");
      break;
    case Instruction::UDiv:
      printVirtualInstruction(
        "udiv(" + typeDescriptor + typeDescriptor + ")" + typeDescriptor);
      break;
    case Instruction::URem:
      printVirtualInstruction(
        "urem(" + typeDescriptor + typeDescriptor + ")" + typeDescriptor);
      break;
  }
}

void JVMWriter::printArithmeticInstruction(unsigned int op, const Value *left, const Value *right) {
  // First, we need to check if the input is a vector type or not.
  // TODO: We need to support vector types in other types?
  if (const SequentialType *seqTy = dyn_cast<SequentialType>(left->getType())) {
    std::string typePrefix = getTypePrefix(seqTy->getElementType(), true);
    std::string typeDescriptor = getTypeDescriptor(seqTy->getElementType());
    int size = targetData->getTypeAllocSize(seqTy->getElementType());

    printSimpleInstruction("sipush", utostr(seqTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (unsigned i = 0; i < seqTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (const Constant *c = dyn_cast<Constant>(left)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(left)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            printValueLoad(vec->getAggregateElement((unsigned) 0));
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(left)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(left)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(seqTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown left constant value type: Type=" << getTypeIDName(left->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(left);
        printSimpleInstruction("ldc2_w", utostr(i * size));
        printSimpleInstruction("ladd");
        printIndirectLoad(seqTy->getElementType());
      }
      if (const Constant *c = dyn_cast<Constant>(right)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(right)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            printValueLoad(vec->getAggregateElement((unsigned) 0));
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(right)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(right)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(seqTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown right constant value type: Type=" << getTypeIDName(right->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(right);
        printSimpleInstruction("ldc2_w", utostr(i * size));
        printSimpleInstruction("ladd");
        printIndirectLoad(seqTy->getElementType());
      }

      printArithmeticInstruction(op, typeDescriptor, typePrefix, getBitWidth(right->getType()));
      printIndirectStore(seqTy->getElementType());
    }
  } else {
    printValueLoad(left);
    printValueLoad(right);
    std::string typePrefix = getTypePrefix(left->getType(), true);
    std::string typeDescriptor = getTypeDescriptor(left->getType());
    printArithmeticInstruction(op, typeDescriptor, typePrefix, getBitWidth(right->getType()));
  }
}

void JVMWriter::printBitCastInstruction(const Type *ty, const Type *srcTy) {
  char typeID = getTypeID(ty);
  char srcTypeID = getTypeID(srcTy);
  if (srcTypeID == 'J' && typeID == 'D') {
    printSimpleInstruction("invokestatic", "java/lang/Double/longBitsToDouble(J)D");
  } else if (srcTypeID == 'I' && typeID == 'F') {
    printSimpleInstruction("invokestatic", "java/lang/Float/intBitsToFloat(I)F");
  }
  if (srcTypeID == 'D' && typeID == 'J') {
    printSimpleInstruction("invokestatic", "java/lang/Double/doubleToRawLongBits(D)J");
  } else if (srcTypeID == 'F' && typeID == 'I') {
    printSimpleInstruction("invokestatic", "java/lang/Float/floatToRawIntBits(F)I");
  }
}

void JVMWriter::printCastInstruction(const std::string &typePrefix, const std::string &srcTypePrefix) {
  if (srcTypePrefix != typePrefix) {
    printSimpleInstruction(srcTypePrefix + "2" + typePrefix);
  }
}

void JVMWriter::printCastInstruction(unsigned int op, const Type *srcTy, const Type *destTy) {
  switch (op) {
    case Instruction::SIToFP:
    case Instruction::FPToSI:
    case Instruction::FPTrunc:
    case Instruction::FPExt:
    case Instruction::SExt:
      // if (getBitWidth(srcTy) < 32) {
      //   printCastInstruction(getTypePrefix(srcTy), "i");
      // }
      printCastInstruction(getTypePrefix(destTy, true), getTypePrefix(srcTy, true));
      break;
    case Instruction::Trunc:
      if (getBitWidth(srcTy) == 64 && getBitWidth(destTy) < 32) {
        printSimpleInstruction("l2i");
        printCastInstruction(getTypePrefix(destTy), "i");
      } else {
        printCastInstruction(getTypePrefix(destTy), getTypePrefix(srcTy, true));
      }
      break;
    case Instruction::IntToPtr:
      // TODO: "l" is correct?
      printCastInstruction("l", getTypePrefix(srcTy, true));
      break;
    case Instruction::PtrToInt:
      // TODO: "l" is correct?
      printCastInstruction(getTypePrefix(destTy), "l");
      break;
    case Instruction::ZExt:
      printVirtualInstruction(
        "zext_" + getTypePostfix(destTy, true) +
          "(" + getTypeDescriptor(srcTy) + ")" + getTypeDescriptor(destTy, true));
      break;
    case Instruction::UIToFP:
        printVirtualInstruction(
          "uitofp_" + getTypePostfix(destTy) +
            "(" + getTypeDescriptor(srcTy) + ")" + getTypeDescriptor(destTy));
        break;
    case Instruction::FPToUI:
      printVirtualInstruction(
        "fptoui_" + getTypePostfix(destTy) +
          "(" + getTypeDescriptor(srcTy) + ")" + getTypeDescriptor(destTy));
      break;
    case Instruction::BitCast:
      printBitCastInstruction(destTy, srcTy); break;
    default:
      std::stringstream err_msg;
      err_msg << "Unknown cast instruction: Opcode=" << op;
      lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printCastInstruction(unsigned int op, const Value *v, const Type *ty, const Type *srcTy) {
  // First, we need to check if the input is a vector type or not.
  // TODO: We need to support vector types in other types?
  // TODO: We need to check the `ty != NULL` case
  if (ty != NULL && dyn_cast<SequentialType>(v->getType())) {
    const SequentialType *seqTy = dyn_cast<SequentialType>(v->getType());
    Type *srcElemTy = seqTy->getElementType();
    Type *destElemTy = cast<SequentialType>(ty)->getElementType();
    int size = targetData->getTypeAllocSize(destElemTy);
    printSimpleInstruction("sipush", utostr(seqTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (int i = 0; i < seqTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(v)) {
        printValueLoad(vec->getElementAsConstant(i));
      } else {
        printValueLoad(v);
        int ssize = targetData->getTypeAllocSize(destElemTy);
        printSimpleInstruction("ldc2_w", utostr(i * ssize));
        printSimpleInstruction("ladd");
        printIndirectLoad(seqTy->getElementType());
      }

      printCastInstruction(op, srcElemTy, destElemTy);
      printIndirectStore(destElemTy);
    }
  } else {
    printValueLoad(v);
    printCastInstruction(op, srcTy, ty);
  }
}

/**
 * Prints a getelementptr instruction.
 *
 * @param v the aggregate data structure to index
 * @param i an iterator to the first type indexed by the instruction
 * @param e an iterator specifying the upper bound on the types indexed by the instruction
 */
void JVMWriter::printGepInstruction(const Value *v, gep_type_iterator i, gep_type_iterator e) {
  // Loads address
  printCastInstruction(Instruction::IntToPtr, v, NULL, v->getType());

  // Calculates offset
  for (; i != e; i++) {
    unsigned int size = 0;
    const Value *indexValue = i.getOperand();

    if (const StructType *structTy = i.getStructTypeOrNull()) {
      for (unsigned int f = 0, fieldIndex = cast<ConstantInt>(indexValue)->getZExtValue(); f < fieldIndex; f++) {
        size = alignOffset(
          size + targetData->getTypeAllocSize(structTy->getContainedType(f)),
          targetData->getABITypeAlignment(structTy->getContainedType(f + 1)));
      }
      printPtrLoad(size);
      printSimpleInstruction("ladd");
    } else {
      if (const SequentialType *seqTy = dyn_cast<SequentialType>(i.getIndexedType())) {
        size = targetData->getTypeAllocSize(seqTy->getElementType());
      } else {
        size = targetData->getTypeAllocSize(i.getIndexedType());
      }

      if (const ConstantInt *c = dyn_cast<ConstantInt>(indexValue)) {
        // Constant optimisation
        if (c->isNullValue()) {
          // Does nothing
        } else if (c->getValue().isNegative()) {
          printPtrLoad(c->getValue().abs().getZExtValue() * size);
          printSimpleInstruction("lsub");
        } else {
          printPtrLoad(c->getZExtValue() * size);
          printSimpleInstruction("ladd");
        }
      } else {
        printPtrLoad(size);
        printCastInstruction(Instruction::IntToPtr, indexValue, NULL, indexValue->getType());
        printSimpleInstruction("lmul");
        printSimpleInstruction("ladd");
      }
    }
  }
}

void JVMWriter::printAllocaInstruction(const AllocaInst *inst) {
  uint64_t size = targetData->getTypeAllocSize(inst->getAllocatedType());
  if (const ConstantInt *c = dyn_cast<ConstantInt>(inst->getOperand(0))) {
    // constant optimization
    // printPtrLoad(c->getZExtValue() * size);
    printSimpleInstruction("bipush", utostr(c->getZExtValue() * size));
  } else {
    printPtrLoad(size);
    printValueLoad(inst->getOperand(0));
    printSimpleInstruction("imul");
  }
  printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
}

void JVMWriter::printVAArgInstruction(const VAArgInst *inst) {
  printIndirectLoad(inst->getOperand(0));
  printSimpleInstruction("dup");
  printConstLoad(APInt(32, targetData->getTypeAllocSize(inst->getType()), false));
  printSimpleInstruction("iadd");
  printValueLoad(inst->getOperand(0));
  printSimpleInstruction("swap");
  printIndirectStore(PointerType::getUnqual(IntegerType::get(inst->getContext(), 8)));
  printIndirectLoad(inst->getType());
}

void JVMWriter::printExtractValue(const ExtractValueInst *inst) {
  const Value *v = inst->getAggregateOperand();
  const Type *aggType = v->getType();

  // Loads address
  printCastInstruction(Instruction::IntToPtr, v, NULL, aggType);

  // Calculates offset
  for (unsigned i = 0; i < inst->getNumIndices(); i++) {
    unsigned fieldIndex = inst->getIndices()[i];
    int size = 0;
    if (const StructType *structTy = dyn_cast<StructType>(aggType)) {
      for (unsigned int f = 0; f < fieldIndex; f++) {
        size = alignOffset(
          size + targetData->getTypeAllocSize(structTy->getContainedType(f)),
          targetData->getABITypeAlignment(structTy->getContainedType(f)));
      }
      printPtrLoad(size);
      printSimpleInstruction("ladd");
      // We load a value itself for regular/pointer types.
      // Otherwise, we need to load an address for sequential types (array/vector):
      //
      // e.g., %2 = extractvalue { i64, i8*, [4 x i64] } %.1, 3
      //
      // In this example, it is a value for `i64`/`i8*` and
      // an adress for `[4 x i64]`.
      if (const SequentialType *seqTy = dyn_cast<SequentialType>(structTy->getContainedType(fieldIndex))) {
        // Loads an adress itself
      } else {
        printIndirectLoad(structTy->getContainedType(fieldIndex));
      }
    } else if (const SequentialType *seqTy = dyn_cast<SequentialType>(aggType)) {
      size = targetData->getTypeAllocSize(seqTy->getElementType());
      printPtrLoad(fieldIndex * size);
      printSimpleInstruction("ladd");
      printIndirectLoad(aggType);
    } else {
      std::stringstream err_msg;
      err_msg << "Unknown type: Type=" << getTypeIDName(aggType);
      lljvm_unreachable(err_msg.str());
    }
  }
}

void JVMWriter::printInsertElement(const InsertElementInst *inst) {
  const Value *vec = inst->getOperand(0);
  const SequentialType *vecTy = cast<SequentialType>(vec->getType());
  uint64_t vecSize = targetData->getTypeAllocSize(vecTy->getElementType());
  if (const UndefValue *undef = dyn_cast<UndefValue>(vec)) {
    printSimpleInstruction("bipush", utostr(vecSize * undef->getNumElements()));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
  } else {
    printValueLoad(vec);
  }
  printSimpleInstruction("dup2");
  printSimpleInstruction("ldc2_w", utostr(vecSize));
  printValueLoad(inst->getOperand(2));
  printCastInstruction("l", getTypePrefix(inst->getOperand(2)->getType(), true));
  printSimpleInstruction("lmul");
  printSimpleInstruction("ladd");
  printValueLoad(inst->getOperand(1));
  printIndirectStore(inst->getOperand(1)->getType());
}

void JVMWriter::printExtractElement(const ExtractElementInst *inst) {
  const Value *vec = inst->getOperand(0);
  const SequentialType *vecTy = cast<SequentialType>(vec->getType());
  uint64_t vecSize = targetData->getTypeAllocSize(vecTy->getElementType());
  if (const UndefValue *undef = dyn_cast<UndefValue>(vec)) {
    printSimpleInstruction("bipush", utostr(vecSize * undef->getNumElements()));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
  } else {
    printValueLoad(vec);
  }
  // printSimpleInstruction("dup2");
  printSimpleInstruction("ldc2_w", utostr(vecSize));
  printValueLoad(inst->getOperand(1));
  printCastInstruction("l", getTypePrefix(inst->getOperand(1)->getType(), true));
  printSimpleInstruction("lmul");
  printSimpleInstruction("ladd");
  printIndirectLoad(vecTy->getElementType());
}

void JVMWriter::printInsertValue(const InsertValueInst *inst) {
  const Value *aggValue = inst->getOperand(0);
  const Type *aggType = aggValue->getType();
  if (const StructType *structTy = dyn_cast<StructType>(aggType)) {
    int aggSize = 0;
    for (unsigned int f = 0; f < structTy->getNumElements(); f++) {
      aggSize = alignOffset(
        aggSize + targetData->getTypeAllocSize(structTy->getContainedType(f)),
        targetData->getABITypeAlignment(structTy->getContainedType(f)));
    }
    if (const UndefValue *undef = dyn_cast<UndefValue>(aggValue)) {
      printSimpleInstruction("bipush", utostr(aggSize));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
    } else {
      printValueLoad(aggValue);
    }
    printSimpleInstruction("dup2");

    // Calculates offset
    for (unsigned i = 0; i < inst->getNumIndices(); i++) {
      unsigned fieldIndex = inst->getIndices()[i];
      int size = 0;
      for (unsigned int f = 0; f < fieldIndex; f++) {
        size = alignOffset(
          size + targetData->getTypeAllocSize(structTy->getContainedType(f)),
          targetData->getABITypeAlignment(structTy->getContainedType(f)));
      }
      printPtrLoad(size);
      printSimpleInstruction("ladd");
      printValueLoad(inst->getOperand(1));
      printIndirectStore(inst->getOperand(1)->getType());
    }
  } else if (const SequentialType *seqTy = dyn_cast<SequentialType>(aggType)) {
    int aggSize = targetData->getTypeAllocSize(seqTy->getElementType()) * seqTy->getNumElements();
    if (const UndefValue *undef = dyn_cast<UndefValue>(aggValue)) {
      printSimpleInstruction("bipush", utostr(aggSize));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
    } else {
      printValueLoad(aggValue);
    }
    printSimpleInstruction("dup2");

    // Calculates offset
    for (unsigned i = 0; i < inst->getNumIndices(); i++) {
      unsigned fieldIndex = inst->getIndices()[i];
      int size = targetData->getTypeAllocSize(seqTy->getElementType());
      printPtrLoad(fieldIndex * size);
      printSimpleInstruction("ladd");
      printValueLoad(inst->getOperand(1));
      printIndirectStore(inst->getOperand(1)->getType());
    }
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown type: Type=" << getTypeIDName(aggType);
    lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printShuffleVector(const ShuffleVectorInst *inst) {
  if (const ConstantAggregateZero *zeroinit = dyn_cast<ConstantAggregateZero>(inst->getOperand(2))) {
    const Value *vec1 = inst->getOperand(0);
    const SequentialType *vecTy = cast<SequentialType>(vec1->getType());
    uint64_t vecElemSize = targetData->getTypeAllocSize(vecTy->getElementType());
    int vecElemNum = zeroinit->getNumElements();
    printSimpleInstruction("bipush", utostr(vecElemSize * vecElemNum));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
    for (int i = 0; i < vecElemNum; i++) {
      // Locate a store position
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
      printSimpleInstruction("ladd");
      // Then, initialize it with 0
      printSimpleInstruction("iconst_0");
      printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
      printIndirectStore(vecTy->getElementType());
    }
  } else {
    // TODO: If an one-side argument is undef, we don't copy input data
    unsigned numInputElems = 0;
    const Value *vec1 = inst->getOperand(0);
    const SequentialType *vec1Ty = cast<SequentialType>(vec1->getType());
    numInputElems += vec1Ty->getNumElements();

    const Value *vec2 = inst->getOperand(1);
    const SequentialType *vec2Ty = dyn_cast<SequentialType>(vec2->getType());
    const bool vec2IsUndef = isa<UndefValue>(vec2);
    if (!vec2IsUndef) {
      numInputElems += vec2Ty->getNumElements();
    }

    uint64_t vecElemSize = targetData->getTypeAllocSize(vec1Ty->getElementType());
    printSimpleInstruction("bipush", utostr(vecElemSize * numInputElems));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    unsigned storePos = 0;
    for (int i = 0; i < vec1Ty->getNumElements(); i++) {
      // Locate a store position
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(storePos));
      printSimpleInstruction("ladd");
      // Load a value from a given input address
      printValueLoad(vec1);
      printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
      printSimpleInstruction("ladd");
      printIndirectLoad(vec1Ty->getElementType());
      // Then, store it
      printIndirectStore(vec1Ty->getElementType());
      storePos += vecElemSize;
    }
    for (int i = 0; !vec2IsUndef && i < vec2Ty->getNumElements(); i++) {
      // Locate a store position
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(storePos));
      printSimpleInstruction("ladd");
      // Load a value from a given input address
      printValueLoad(vec2);
      printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
      printSimpleInstruction("ladd");
      printIndirectLoad(vec2Ty->getElementType());
      // Then, store it
      printIndirectStore(vec2Ty->getElementType());
      storePos += vecElemSize;
    }

    // TODO: Reuse the local variable of `vec1` here (revisit this)
    printValueStore(vec1);

    if (const ConstantDataVector *mask = dyn_cast<ConstantDataVector>(inst->getOperand(2))) {
      printSimpleInstruction("bipush", utostr(vecElemSize * mask->getNumElements()));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
      for (int i = 0; i < mask->getNumElements(); i++) {
        // Locate a store position
        printSimpleInstruction("dup2");
        printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
        printSimpleInstruction("ladd");
        // Load a value from a given index
        printValueLoad(vec1);
        printSimpleInstruction("ldc2_w", utostr(vecElemSize));
        printValueLoad(mask->getElementAsConstant(i));
        printCastInstruction("l", getTypePrefix(mask->getElementType(), true));
        printSimpleInstruction("lmul");
        printSimpleInstruction("ladd");
        printIndirectLoad(vec1Ty->getElementType());
        // Then, store it
        printIndirectStore(vec1Ty->getElementType());
      }
    } else if (const ConstantVector *mask = dyn_cast<ConstantVector>(inst->getOperand(2))) {
      // Computes the number of elements for `ConstantVector`
      unsigned numElements = 0;
      for (unsigned i = 0; NULL != mask->getAggregateElement(i); i++) {
        numElements++;
      }
      printSimpleInstruction("bipush", utostr(vecElemSize * numElements));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
      for (int i = 0; i < numElements; i++) {
        // Locate a store position
        printSimpleInstruction("dup2");
        printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
        printSimpleInstruction("ladd");

        // Load a value....
        if (const UndefValue *undef = dyn_cast<UndefValue>(mask->getAggregateElement(i))) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(vec1Ty->getElementType(), true), "i");
        } else {
          // Load from a given index
          const Constant *elem = mask->getAggregateElement(i);
          printValueLoad(vec1);
          printSimpleInstruction("ldc2_w", utostr(vecElemSize));
          printValueLoad(mask->getAggregateElement(i));
          printCastInstruction("l", getTypePrefix(elem->getType(), true));
          printSimpleInstruction("lmul");
          printSimpleInstruction("ladd");
          printIndirectLoad(vec1Ty->getElementType());
        }
        // Then, store it
        printIndirectStore(vec1Ty->getElementType());
      }
    } else {
      std::stringstream err_msg;
      err_msg << "Unknown mask type: Type=" << getTypeIDName(inst->getOperand(2)->getType());
      lljvm_unreachable(err_msg.str());
    }
  }
}

void JVMWriter::printAtomicRMW(const AtomicRMWInst *inst) {
  const Value *ptr = inst->getPointerOperand();
  printValueLoad(ptr);
  printSimpleInstruction("dup2");
  printSimpleInstruction("dup2");
  printIndirectLoad(cast<PointerType>(ptr->getType())->getElementType());
  printValueLoad(inst->getValOperand());
  std::string typePrefix = getTypePrefix(inst->getValOperand()->getType(), true);
  switch (inst->getOperation()) {
    case AtomicRMWInst::Add:
      printSimpleInstruction(typePrefix + "add");
      break;
    case AtomicRMWInst::Sub:
      printSimpleInstruction(typePrefix + "sub");
      break;
    default:
      std::stringstream err_msg;
      err_msg << "Unknown Atomic operation: " << inst->getOperation();
      lljvm_unreachable(err_msg.str());
  }
  printIndirectStore(inst->getValOperand()->getType());
  printIndirectLoad(cast<PointerType>(ptr->getType())->getElementType());
}

/**
 * Prints a vararg intrinsic function.
 */
void JVMWriter::printVAIntrinsic(const IntrinsicInst *inst) {
  const Type *valistTy = PointerType::getUnqual(IntegerType::get(inst->getContext(), 8));
  switch (inst->getIntrinsicID()) {
    case Intrinsic::vastart:
      printValueLoad(inst->getOperand(1));
      printSimpleInstruction("iload", utostr(vaArgNum) + " ; varargptr");
      printIndirectStore(valistTy);
      break;
    case Intrinsic::vacopy:
      printValueLoad(inst->getOperand(1));
      printValueLoad(inst->getOperand(2));
      printIndirectLoad(valistTy);
      printIndirectStore(valistTy);
      break;
    case Intrinsic::vaend:
      break;
    default:
      std::stringstream err_msg;
      err_msg << "Unknown vararg intrinsic function: Name=" << Intrinsic::getName(inst->getIntrinsicID()).str();
      lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printMemIntrinsic(const MemIntrinsic *inst) {
  printValueLoad(inst->getDest());
  if (const MemTransferInst *minst = dyn_cast<MemTransferInst>(inst)) {
    printValueLoad(minst->getSource());
  } else if (const MemSetInst *minst = dyn_cast<MemSetInst>(inst)) {
    printValueLoad(minst->getValue());
  }
  printValueLoad(inst->getLength());
  // In LLVM v7.x, getVolatileCst() instead of getAlignmentCst() in LLVM 5.x?
  // printConstLoad(inst->getAlignmentCst());
  printConstLoad(inst->getVolatileCst());

  std::string lenDescriptor = getTypeDescriptor(inst->getLength()->getType(), true);
  switch (inst->getIntrinsicID()) {
    case Intrinsic::memcpy:
      printSimpleInstruction(
        "invokestatic", "io/github/maropu/lljvm/runtime/VMemory/memcpy(JJ" + lenDescriptor + "I)V");
      break;
    case Intrinsic::memmove:
      printSimpleInstruction(
        "invokestatic", "io/github/maropu/lljvm/runtime/VMemory/memmove(JJ" + lenDescriptor + "I)V");
      break;
    case Intrinsic::memset:
      printSimpleInstruction(
        "invokestatic", "io/github/maropu/lljvm/runtime/VMemory/memset(JB" + lenDescriptor + "I)V");
      break;
    default:
      std::stringstream err_msg;
      err_msg << "Unknown mem intrinsic function: Name=" << Intrinsic::getName(inst->getIntrinsicID()).str();
      lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printMathIntrinsic(unsigned int op) {
  switch (op) {
    case Intrinsic::sqrt:
      printSimpleInstruction("invokestatic", "java/lang/Math/sqrt(D)D");
      break;
    case Intrinsic::sin:
      printSimpleInstruction("invokestatic", "java/lang/Math/sin(D)D");
      break;
    case Intrinsic::cos:
      printSimpleInstruction("invokestatic", "java/lang/Math/cos(D)D");
      break;
    case Intrinsic::pow:
      printSimpleInstruction("invokestatic", "java/lang/Math/pow(DD)D");
      break;
    case Intrinsic::exp:
      printSimpleInstruction("invokestatic", "java/lang/Math/exp(D)D");
      break;
    case Intrinsic::log:
      printSimpleInstruction("invokestatic", "java/lang/Math/log(D)D");
      break;
    case Intrinsic::log10:
      printSimpleInstruction("invokestatic", "java/lang/Math/log10(D)D");
      break;
    case Intrinsic::log2: // Math.log(x) / Math.log(2)
      printSimpleInstruction("invokestatic", "java/lang/Math/log(D)D");
      printConstLoad(2.0);
      printSimpleInstruction("invokestatic", "java/lang/Math/log(D)D");
      printSimpleInstruction("ddiv");
      break;
    case Intrinsic::fabs:
      printSimpleInstruction("invokestatic", "java/lang/Math/abs(D)D");
      break;
    case Intrinsic::floor:
      printSimpleInstruction("invokestatic", "java/lang/Math/floor(D)D");
      break;
    case Intrinsic::ceil:
      printSimpleInstruction("invokestatic", "java/lang/Math/ceil(D)D");
      break;
    case Intrinsic::rint:
      printSimpleInstruction("invokestatic", "java/lang/Math/rint(D)D");
      break;
    case Intrinsic::round:
      printSimpleInstruction("invokestatic", "java/lang/Math/round(D)J");
      printCastInstruction("d", "l");
      break;

    // TODO: Unsupported math intrinsic functions below
    case Intrinsic::exp2:
    case Intrinsic::powi:
    case Intrinsic::trunc:
    case Intrinsic::nearbyint:
    default:
      std::stringstream err_msg;
      err_msg << "Unsupported math intrinsic function: Name=" << Intrinsic::getName((Intrinsic::ID) op).str();
      throw err_msg.str();
  }
}

// TODO: Needs to support Intrinsic::fma, Intrinsic::minnum, Intrinsic::maxnum,
// and Intrinsic::copysign here.
void JVMWriter::printMathIntrinsic(const IntrinsicInst *inst) {
  assert(inst->getNumOperands() >= 2 && inst->getNumOperands() <= 3);
  // First, we need to check if the input is a vector type or not
  if (const SequentialType *seqTy = dyn_cast<SequentialType>(inst->getOperand(0)->getType())) {
    bool f32 = false;
    bool f32_2nd = false;
    if (inst->getNumOperands() == 2) {
      f32 = (getBitWidth(seqTy->getElementType()) == 32);
    } else {
      // For pow()
      f32 = (getBitWidth(seqTy->getElementType()) == 32);

      // TODO: The return type depends on the 1st type
      // f32_2nd = (getBitWidth(inst->getOperand(1)->getType()) == 32);
      f32_2nd = f32;
    }

    int size = targetData->getTypeAllocSize(seqTy->getElementType());

    printSimpleInstruction("sipush", utostr(seqTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (int i = 0; i < seqTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (inst->getNumOperands() == 2) {
         printValueLoad(inst->getOperand(0));
         printSimpleInstruction("ldc2_w", utostr(i * size));
         printSimpleInstruction("ladd");
         printIndirectLoad(seqTy->getElementType());
         if (f32) printSimpleInstruction("f2d");
      } else {
        // For pow()
        printValueLoad(inst->getOperand(0));
        printSimpleInstruction("ldc2_w", utostr(i * size));
        printSimpleInstruction("ladd");
        printIndirectLoad(seqTy->getElementType());
        if (f32) printSimpleInstruction("f2d");

        if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(inst->getOperand(1))) {
          printValueLoad(vec->getElementAsConstant(i));
        } else {
          printValueLoad(inst->getOperand(1));
          printSimpleInstruction("ldc2_w", utostr(i * size));
          printSimpleInstruction("ladd");
          printIndirectLoad(vec->getElementType());
        }
        if (f32_2nd) printSimpleInstruction("f2d");
      }

      printMathIntrinsic(inst->getIntrinsicID());
      if (f32) printSimpleInstruction("d2f");
      printIndirectStore(seqTy->getElementType());
    }
  } else {
    bool f32 = false;
    if (inst->getNumOperands() == 2) {
      f32 = (getBitWidth(inst->getOperand(0)->getType()) == 32);
      printValueLoad(inst->getOperand(0));
      if (f32) printSimpleInstruction("f2d");
    } else {
      // For pow()
      f32 = (getBitWidth(inst->getOperand(0)->getType()) == 32);
      printValueLoad(inst->getOperand(0));
      if (f32) printSimpleInstruction("f2d");

      // TODO: The return type depends on the 1st type
      bool f32_2nd = (getBitWidth(inst->getOperand(1)->getType()) == 32);
      printValueLoad(inst->getOperand(1));
      if (f32_2nd) printSimpleInstruction("f2d");
    }

    printMathIntrinsic(inst->getIntrinsicID());
    if (f32) printSimpleInstruction("d2f");
  }
}

void JVMWriter::printBitIntrinsic(const IntrinsicInst *inst) {
  // TODO: ctpop, ctlz, cttz
  const Value *value = inst->getOperand(1);
  const std::string typeDescriptor = getTypeDescriptor(value->getType());
  switch (inst->getIntrinsicID()) {
    case Intrinsic::bswap:
      printVirtualInstruction("bswap(" + typeDescriptor + ")" + typeDescriptor, value);
      break;

    // TODO: Unsupported bit intrinsic functions below
    case Intrinsic::bitreverse:
    case Intrinsic::ctpop:
    case Intrinsic::ctlz:
    case Intrinsic::cttz:
    case Intrinsic::fshl:
    case Intrinsic::fshr:
    default:
      std::stringstream err_msg;
      err_msg << "Unsupported bit intrinsic function: Name=" << Intrinsic::getName(inst->getIntrinsicID()).str();
      throw err_msg.str();
  }
}
