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

void JVMWriter::printLoadInstruction(const Value *v) {
  // Hacky warning: We need to regard composite types (structs, arrays, and vectors) as pointers
  if (const PointerType *pTy = dyn_cast<PointerType>(v->getType())) {
    if (isa<CompositeType>(pTy->getElementType())) {
      printValueLoad(v);
      return;
    }
  }
  printIndirectLoad(v);
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
  if (const VectorType *leftVecTy = dyn_cast<VectorType>(left->getType())) {
    assert(leftVecTy == right->getType());

    const VectorType *rightVecTy = cast<VectorType>(right->getType());

    // TODO: A return type is always i1?
    Type *rTy = Type::getInt1Ty(module->getContext());
    int size = getTypeAllocSize(rTy);
    printSimpleInstruction("sipush", utostr(leftVecTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (int i = 0; i < leftVecTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (const Constant *c = dyn_cast<Constant>(left)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(left)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            // In case of undef, we set 0
            printSimpleInstruction("iconst_0");
            printCastInstruction(getTypePrefix(leftVecTy->getElementType(), true), "i");
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(left)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(left)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(leftVecTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown left constant value type: Type=" << getTypeIDName(left->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(left);
        int lsize = getTypeAllocSize(leftVecTy->getElementType());
        printSimpleInstruction("ldc2_w", utostr(i * lsize));
        printSimpleInstruction("ladd");
        printIndirectLoad(leftVecTy->getElementType());
      }
      if (const Constant *c = dyn_cast<Constant>(right)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(right)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            // In case of undef, we set 0
            printSimpleInstruction("iconst_0");
            printCastInstruction(getTypePrefix(rightVecTy->getElementType(), true), "i");
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(right)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(right)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(rightVecTy->getElementType(), true), "i");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown right constant value type: Type=" << getTypeIDName(right->getType());
          lljvm_unreachable(err_msg.str());
        }
      } else {
        // We assume a pointer case here
        printValueLoad(right);
        int rsize = getTypeAllocSize(rightVecTy->getElementType());
        printSimpleInstruction("ldc2_w", utostr(i * rsize));
        printSimpleInstruction("ladd");
        printIndirectLoad(rightVecTy->getElementType());
      }

      const std::string inst = getPredicate(predicate);
      printVirtualInstruction(
        inst + "(" + getTypeDescriptor(leftVecTy->getElementType(), true) +
          getTypeDescriptor(rightVecTy->getElementType(), true) + ")Z");
      printIndirectStore(rTy);
    }
  } else if ( left->getType()->getTypeID() == Type::PointerTyID ||
      left->getType()->getTypeID() == Type::IntegerTyID ||
      left->getType()->getTypeID() == Type::FloatTyID ||
      left->getType()->getTypeID() == Type::DoubleTyID) {
    const std::string inst = getPredicate(predicate);
    printVirtualInstruction(inst + "(" + getTypeDescriptor(left->getType(), true) +
      getTypeDescriptor(right->getType(), true) + ")Z", left, right);
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown operand type in cmp: Type=" << getTypeIDName(left->getType());
    lljvm_unreachable(err_msg.str());
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
  if (const VectorType *vecTy = dyn_cast<VectorType>(left->getType())) {
    std::string typePrefix = getTypePrefix(vecTy->getElementType(), true);
    std::string typeDescriptor = getTypeDescriptor(vecTy->getElementType());
    int size = getTypeAllocSize(vecTy->getElementType());

    printSimpleInstruction("sipush", utostr(vecTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (unsigned i = 0; i < vecTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (const Constant *c = dyn_cast<Constant>(left)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(left)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            // In case of undef, we set 0
            printSimpleInstruction("iconst_0");
            printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(left)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(left)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
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
        printIndirectLoad(vecTy->getElementType());
      }
      if (const Constant *c = dyn_cast<Constant>(right)) {
        if (const ConstantVector *vec = dyn_cast<ConstantVector>(right)) {
          if (const UndefValue *undef = dyn_cast<UndefValue>(vec->getAggregateElement(i))) {
            // In case of undef, we set 0
            printSimpleInstruction("iconst_0");
            printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
          } else {
            printValueLoad(vec->getAggregateElement(i));
          }
        } else if (const ConstantDataVector *vec = dyn_cast<ConstantDataVector>(right)) {
          printValueLoad(vec->getElementAsConstant(i));
        } else if (isa<ConstantAggregateZero>(right)) {
          printSimpleInstruction("iconst_0");
          printCastInstruction(getTypePrefix(vecTy->getElementType(), true), "i");
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
        printIndirectLoad(vecTy->getElementType());
      }

      printArithmeticInstruction(op, typeDescriptor, typePrefix, getTypeBitWidth(vecTy->getElementType()));
      printIndirectStore(vecTy->getElementType());
    }
  } else if (left->getType()->getTypeID() == Type::IntegerTyID ||
      left->getType()->getTypeID() == Type::FloatTyID ||
      left->getType()->getTypeID() == Type::DoubleTyID) {
    printValueLoad(left);
    printValueLoad(right);
    std::string typePrefix = getTypePrefix(left->getType(), true);
    std::string typeDescriptor = getTypeDescriptor(left->getType());
    printArithmeticInstruction(op, typeDescriptor, typePrefix, getTypeBitWidth(right->getType()));
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown operand type: Type=" << getTypeIDName(left->getType());
    lljvm_unreachable(err_msg.str());
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
      // if (getTypeBitWidth(srcTy) < 32) {
      //   printCastInstruction(getTypePrefix(srcTy), "i");
      // }
      printCastInstruction(getTypePrefix(destTy, true), getTypePrefix(srcTy, true));
      break;
    case Instruction::Trunc:
      if (getTypeBitWidth(srcTy) == 64 && getTypeBitWidth(destTy) < 32) {
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
    int size = getTypeAllocSize(destElemTy);
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
        int ssize = getTypeAllocSize(destElemTy);
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
 */
void JVMWriter::printGepInstruction(const GetElementPtrInst *inst) {
  const Value *base = inst->getOperand(0);
  const Type *ptrElementType = cast<PointerType>(base->getType())->getElementType();

  if (inst->getNumIndices() > 3) {
    std::stringstream err_msg;
    err_msg << "Unsupported complicated pointer calculation (#index > 3) in getelementptr: Num=" << inst->getNumIndices();
    throw err_msg.str();
  }

  // Checks if the given types supported, e.g., the pointer of composite types not supported
  if (!checkIfTypeSupported(ptrElementType)) {
    std::stringstream err_msg;
    err_msg << "Unsupported types in getelementptr: Type=" << getTypeIDName(ptrElementType);
    throw err_msg.str();
  }

  // Load a base address for pointer calculation
  printValueLoad(base);

  const Value *arIndexValue = inst->getOperand(1);
  if (isa<UndefValue>(arIndexValue)) {
    printPtrLoad(0);
  } else if (const ConstantInt *c = dyn_cast<ConstantInt>(arIndexValue)) {
    // if (c->isNullValue()) {
    //   std::stringstream err_msg;
    //   err_msg << "Unsupported null index value in getelementptr";
    //   throw err_msg.str();
    // }
    printValueLoad(c);
    printCastInstruction("l", getTypePrefix(c->getType(), true));
  } else {
    printCastInstruction(Instruction::IntToPtr, arIndexValue, NULL, arIndexValue->getType());
  }

  // Top-level aggregate typed values are inlined in memory
  if (const StructType *structTy = dyn_cast<StructType>(ptrElementType)) {
    printPtrLoad(getTypeAllocSize(structTy));
    printSimpleInstruction("lmul");
    printSimpleInstruction("ladd");

    if (inst->getNumIndices() > 1) {
      const Type *ty = structTy;
      for (unsigned int i = 2; i <= inst->getNumIndices(); i++) {
        if (isa<StructType>(ty)) {
          if (const ConstantInt *constInt = dyn_cast<ConstantInt>(inst->getOperand(i))) {
            unsigned int fieldIndex = constInt->getZExtValue();
            unsigned int offset = 0;
            for (unsigned int f = 0; f < fieldIndex; f++) {
              offset = advanceNextOffset(offset, cast<StructType>(ty)->getContainedType(f));
            }
            printPtrLoad(offset);
            printSimpleInstruction("ladd");
            ty = structTy->getContainedType(fieldIndex);
          } else {
            std::stringstream err_msg;
            err_msg << "Can't use a variable value index for struct types in getelementptr";
            lljvm_unreachable(err_msg.str());
          }
        } else if (const SequentialType *seqTy = dyn_cast<SequentialType>(ty)) {
          const Type *elementTy = seqTy->getElementType();
          // if (const ConstantInt *constInt = dyn_cast<ConstantInt>(inst->getOperand(i))) {
          //   unsigned int fieldIndex = constInt->getZExtValue();
          //   printPtrLoad(fieldIndex * getTypeByteWidth(elementTy));
          // } else {
            const Value *fieldIndex = inst->getOperand(i);
            printValueLoad(fieldIndex);
            printCastInstruction("l", getTypePrefix(fieldIndex->getType(), true));
            printSimpleInstruction("ldc2_w", utostr(getTypeByteWidth(elementTy)));
            printSimpleInstruction("lmul");
          // }
          printSimpleInstruction("ladd");
        } else {
          std::stringstream err_msg;
          err_msg << "Unknown aggregate/vector type in getelementptr: Type=" << getTypeIDName(ty);
          lljvm_unreachable(err_msg.str());
        }
      }
    }
  } else if (const SequentialType *seqTy = dyn_cast<SequentialType>(ptrElementType)) {
    unsigned int arSize = getTypeAllocSize(seqTy);
    printPtrLoad(arSize);
    printSimpleInstruction("lmul");
    printSimpleInstruction("ladd");

    if (inst->getNumIndices() > 1) {
      // unsigned int fieldIndex = cast<ConstantInt>(inst->getOperand(2))->getZExtValue();
      // printPtrLoad(fieldIndex * getTypeByteWidth(seqTy->getElementType()));
      const Value *fieldIndex = inst->getOperand(2);
      printValueLoad(fieldIndex);
      printCastInstruction("l", getTypePrefix(fieldIndex->getType(), true));
      printSimpleInstruction("ldc2_w", utostr(getTypeByteWidth(seqTy->getElementType())));
      printSimpleInstruction("lmul");
      printSimpleInstruction("ladd");
    }
  } else {
    assert(inst->getNumIndices() == 1);
    assert(isPrimitiveType(ptrElementType));
    printPtrLoad(getTypeByteWidth(ptrElementType));
    printSimpleInstruction("lmul");
    printSimpleInstruction("ladd");
  }
}

void JVMWriter::printAllocaInstruction(const AllocaInst *inst) {
  // TODO: Needs to check if given types supported or not
  const Type *allocatedTy = inst->getAllocatedType();

  // Checks if the given types supported, e.g., the pointer of composite types not supported
  if (!checkIfTypeSupported(allocatedTy)) {
    std::stringstream err_msg;
    err_msg << "Unsupported types in alloca: Type=" << getTypeIDName(allocatedTy);
    throw err_msg.str();
  }

  const Value *v = inst->getOperand(0);
  uint64_t size = getTypeAllocSize(allocatedTy);
  if (const ConstantInt *c = dyn_cast<ConstantInt>(v)) {
    // constant optimization
    // printPtrLoad(c->getZExtValue() * size);
    printSimpleInstruction("bipush", utostr(c->getZExtValue() * size));
  } else {
    printPtrLoad(size);
    printValueLoad(v);
    printSimpleInstruction("imul");
  }
  printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
}

void JVMWriter::printVAArgInstruction(const VAArgInst *inst) {
  printIndirectLoad(inst->getOperand(0));
  printSimpleInstruction("dup");
  printConstLoad(APInt(32, getTypeAllocSize(inst->getType()), false));
  printSimpleInstruction("iadd");
  printValueLoad(inst->getOperand(0));
  printSimpleInstruction("swap");
  printIndirectStore(PointerType::getUnqual(IntegerType::get(inst->getContext(), 8)));
  printIndirectLoad(inst->getType());
}

void JVMWriter::printExtractValue(const ExtractValueInst *inst) {
  const Value *aggValue = inst->getAggregateOperand();
  const Type *aggType = aggValue->getType();

  if (inst->getNumIndices() > 1) {
    std::stringstream err_msg;
    err_msg << "Unsupported multiple indices for nested aggregate types in extractvalue: Num=" << inst->getNumIndices();
    throw err_msg.str();
  }

  // Load a pointer for the aggreateted value
  printValueLoad(aggValue);

  // The value to insert must have the same type as the value identified by the indices
  unsigned fieldIndex = inst->getIndices()[0];
  int aggSize = 0;
  if (const StructType *structTy = dyn_cast<StructType>(aggType)) {
    for (unsigned int f = 0; f < fieldIndex; f++) {
      aggSize = advanceNextOffset(aggSize, structTy->getContainedType(f));
    }
    printPtrLoad(aggSize);
    printSimpleInstruction("ladd");
    const Type *fieldTy = structTy->getContainedType(fieldIndex);
    if (!isa<CompositeType>(fieldTy)) {
      // We load a value itself for regular/pointer types.
      printIndirectLoad(fieldTy);
    }
  } else if (const ArrayType *arTy = dyn_cast<ArrayType>(aggType)) {
    aggSize = getTypeAllocSize(arTy->getElementType());
    printPtrLoad(fieldIndex * aggSize);
    printSimpleInstruction("ladd");
    printIndirectLoad(arTy->getElementType());
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown aggregate type in insertvalue: Type=" << getTypeIDName(aggType);
    lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printInsertElement(const InsertElementInst *inst) {
  const Value *v = inst->getOperand(0);

  if (const VectorType *vecTy = dyn_cast<VectorType>(v->getType())) {
    uint64_t elementSize = getTypeAllocSize(vecTy->getElementType());
    printSimpleInstruction("bipush", utostr(elementSize * vecTy->getNumElements()));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
    if (!isa<UndefValue>(v)) {
      // Copy ArrayTy values element-by-element into the allocated
      for (unsigned i = 0; i < vecTy->getNumElements(); i++) {
        // Locate output position
        printSimpleInstruction("dup2");
        printPtrLoad(elementSize * i);
        printSimpleInstruction("ladd");

        // Load a value from the current position
        printValueLoad(v);
        printPtrLoad(elementSize * i);
        printSimpleInstruction("ladd");
        printIndirectLoad(vecTy->getElementType());

        // Copy the value
        printIndirectStore(vecTy->getElementType());
      }
    }

    printSimpleInstruction("dup2");
    printSimpleInstruction("ldc2_w", utostr(elementSize));
    printValueLoad(inst->getOperand(2));
    printCastInstruction("l", getTypePrefix(inst->getOperand(2)->getType(), true));
    printSimpleInstruction("lmul");
    printSimpleInstruction("ladd");
    printValueLoad(inst->getOperand(1));
    printIndirectStore(inst->getOperand(1)->getType());
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown operand type in insertelement: Type=" << getTypeIDName(v->getType());
    lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printExtractElement(const ExtractElementInst *inst) {
  const Value *v = inst->getOperand(0);

  if (const VectorType *vecTy = dyn_cast<VectorType>(v->getType())) {
    uint64_t elementSize = getTypeAllocSize(vecTy->getElementType());
    if (const UndefValue *undef = dyn_cast<UndefValue>(v)) {
      printSimpleInstruction("bipush", utostr(elementSize * undef->getNumElements()));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
    } else {
      printValueLoad(v);
    }
    // printSimpleInstruction("dup2");
    printSimpleInstruction("ldc2_w", utostr(elementSize));
    printValueLoad(inst->getOperand(1));
    printCastInstruction("l", getTypePrefix(inst->getOperand(1)->getType(), true));
    printSimpleInstruction("lmul");
    printSimpleInstruction("ladd");
    printIndirectLoad(vecTy->getElementType());
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown operand type in extractelement: Type=" << getTypeIDName(v->getType());
    lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printInsertValue(const InsertValueInst *inst) {
  const Value *aggValue = inst->getOperand(0);
  const Type *aggType = aggValue->getType();

  if (inst->getNumIndices() > 1) {
    /**
     * // struct AggType {
     * //   long a;
     * //   int b[3];
     * //   double c;
     * // };
     * // struct AggType *foo(struct AggType *x, int v) {
     * //   x->b[2] = v;
     * //   return x;
     * // }
     * define { i64, [3 x i32], double } @foo({ i64, [3 x i32], double } %x, i32 %v) {
     *   %ret = insertvalue { i64, [3 x i32], double } %x, i32 %v, 1, 2
     *   ret { i64, [3 x i32], double } %ret
     * }
     */
    std::stringstream err_msg;
    err_msg << "Unsupported multiple indices for nested aggregate types in insertvalue: Num=" << inst->getNumIndices();
    throw err_msg.str();
  }

  // Because of SSA, we copy input value `aggValue` first
  if (const StructType *structTy = dyn_cast<StructType>(aggType)) {
    unsigned int copySize = getTypeAllocSize(structTy);
    printSimpleInstruction("bipush", utostr(copySize));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    if (!isa<UndefValue>(aggValue)) {
      printSimpleInstruction("dup2");
      printValueLoad(aggValue);
      printSimpleInstruction("ldc2_w", utostr(copySize));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/memcpy(JJJ)V");
    }

    // The value to insert must have the same type as the value identified by the indices
    unsigned fieldIndex = inst->getIndices()[0];
    unsigned int aggSize = 0;
    assert(structTy->getNumElements() > fieldIndex);
    for (unsigned int f = 0; f < fieldIndex; f++) {
      aggSize = advanceNextOffset(aggSize, structTy->getContainedType(f));
    }

    // Insert a value itself into the output position
    printSimpleInstruction("dup2");
    printSimpleInstruction("ldc2_w", utostr(aggSize));
    printSimpleInstruction("ladd");
    const Type *fieldTy = structTy->getContainedType(fieldIndex);
    if (isa<CompositeType>(fieldTy)) {
      printValueLoad(inst->getOperand(1));
      printSimpleInstruction("ldc2_w", utostr(getTypeAllocSize(fieldTy)));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/memcpy(JJJ)V");
    } else {
      printValueLoad(inst->getOperand(1));
      printIndirectStore(fieldTy);
    }
  } else if (const ArrayType *arTy = dyn_cast<ArrayType>(aggType)) {
    assert(arTy->getElementType() == inst->getOperand(1)->getType());

    int elementSize = getTypeAllocSize(arTy->getElementType());
    int copySize = elementSize * arTy->getNumElements();
    printSimpleInstruction("bipush", utostr(copySize));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    if (!isa<UndefValue>(aggValue)) {
      printSimpleInstruction("dup2");
      printValueLoad(aggValue);
      printSimpleInstruction("ldc2_w", utostr(copySize));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/memcpy(JJJ)V");
    }

    // Override the values with given indices
    assert(arTy->getElementType() == structTy->getContainedType(i));

    // Locate output position
    unsigned fieldIndex = inst->getIndices()[0];
    printSimpleInstruction("dup2");
    printPtrLoad(elementSize * fieldIndex);
    printSimpleInstruction("ladd");

    // Insert a value into the position
    printValueLoad(inst->getOperand(1));
    printIndirectStore(inst->getOperand(1)->getType());
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown aggregate type in insertvalue: Type=" << getTypeIDName(aggType);
    lljvm_unreachable(err_msg.str());
  }
}

void JVMWriter::printShuffleVector(const ShuffleVectorInst *inst) {
  if (const ConstantAggregateZero *zeroinit = dyn_cast<ConstantAggregateZero>(inst->getOperand(2))) {
    const Value *v = inst->getOperand(0);
    if (const VectorType *vecTy = dyn_cast<VectorType>(v->getType())) {
      uint64_t vecElemSize = getTypeAllocSize(vecTy->getElementType());
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
      std::stringstream err_msg;
      err_msg << "Unknown operand type in shufflevector: Type=" << getTypeIDName(v->getType());
      lljvm_unreachable(err_msg.str());
    }
  } else {
    const Value *v1 = inst->getOperand(0);
    const Value *v2 = inst->getOperand(1);

    if (const VectorType *v1Ty = dyn_cast<VectorType>(v1->getType())) {
      assert(v1Ty == v2->getType());

      // TODO: If an one-side argument is undef, we don't copy input data
      unsigned numInputElems = 0;
      numInputElems += v1Ty->getNumElements();

      const VectorType *v2Ty = cast<VectorType>(v2->getType());
      const bool v2IsUndef = isa<UndefValue>(v2);
      if (!v2IsUndef) {
        numInputElems += v2Ty->getNumElements();
      }

      uint64_t vecElemSize = getTypeAllocSize(v1Ty->getElementType());
      printSimpleInstruction("bipush", utostr(vecElemSize * numInputElems));
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

      // TODO: Needs bulk copy (memcpy) for efficiency
      unsigned storePos = 0;
      for (int i = 0; i < v1Ty->getNumElements(); i++) {
        // Locate a store position
        printSimpleInstruction("dup2");
        printSimpleInstruction("ldc2_w", utostr(storePos));
        printSimpleInstruction("ladd");
        // Load a value from a given input address
        printValueLoad(v1);
        printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
        printSimpleInstruction("ladd");
        printIndirectLoad(v1Ty->getElementType());
        // Then, store it
        printIndirectStore(v1Ty->getElementType());
        storePos += vecElemSize;
      }
      for (int i = 0; !v2IsUndef && i < v2Ty->getNumElements(); i++) {
        // Locate a store position
        printSimpleInstruction("dup2");
        printSimpleInstruction("ldc2_w", utostr(storePos));
        printSimpleInstruction("ladd");
        // Load a value from a given input address
        printValueLoad(v2);
        printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
        printSimpleInstruction("ladd");
        printIndirectLoad(v2Ty->getElementType());
        // Then, store it
        printIndirectStore(v2Ty->getElementType());
        storePos += vecElemSize;
      }

      // TODO: Reuse the local variable of `v1` here (revisit this)
      printValueStore(v1);

      if (const ConstantDataVector *mask = dyn_cast<ConstantDataVector>(inst->getOperand(2))) {
        printSimpleInstruction("bipush", utostr(vecElemSize * mask->getNumElements()));
        printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
        for (int i = 0; i < mask->getNumElements(); i++) {
          // Locate a store position
          printSimpleInstruction("dup2");
          printSimpleInstruction("ldc2_w", utostr(i * vecElemSize));
          printSimpleInstruction("ladd");
          // Load a value from a given index
          printValueLoad(v1);
          printSimpleInstruction("ldc2_w", utostr(vecElemSize));
          printValueLoad(mask->getElementAsConstant(i));
          printCastInstruction("l", getTypePrefix(mask->getElementType(), true));
          printSimpleInstruction("lmul");
          printSimpleInstruction("ladd");
          printIndirectLoad(v1Ty->getElementType());
          // Then, store it
          printIndirectStore(v1Ty->getElementType());
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
            printCastInstruction(getTypePrefix(v1Ty->getElementType(), true), "i");
          } else {
            // Load from a given index
            const Constant *elem = mask->getAggregateElement(i);
            printValueLoad(v1);
            printSimpleInstruction("ldc2_w", utostr(vecElemSize));
            printValueLoad(mask->getAggregateElement(i));
            printCastInstruction("l", getTypePrefix(elem->getType(), true));
            printSimpleInstruction("lmul");
            printSimpleInstruction("ladd");
            printIndirectLoad(v1Ty->getElementType());
          }
          // Then, store it
          printIndirectStore(v1Ty->getElementType());
        }
      } else {
        std::stringstream err_msg;
        err_msg << "Unknown operand type in shufflevector: Type=" << getTypeIDName(v1->getType());
        lljvm_unreachable(err_msg.str());
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
  const Value *val = inst->getValOperand();
  printIndirectLoad(ptr);
  printValueLoad(ptr);
  printIndirectLoad(ptr);
  if (isa<UndefValue>(val)) {
    printSimpleInstruction("iconst_0");
    printCastInstruction(getTypePrefix(val->getType(), true), "i");
  } else {
    printValueLoad(val);
  }

  std::string typePrefix = getTypePrefix(inst->getValOperand()->getType(), true);
  switch (inst->getOperation()) {
    case AtomicRMWInst::Add:
      printSimpleInstruction(typePrefix + "add");
      break;
    case AtomicRMWInst::Sub:
      printSimpleInstruction(typePrefix + "sub");
      break;
    default:
      std::string atomicOp;
      switch (inst->getOperation()) {
        case AtomicRMWInst::Xchg:
          atomicOp = "xchg";
          break;
        case AtomicRMWInst::And:
          atomicOp = "and";
          break;
        case AtomicRMWInst::Nand:
          atomicOp = "nand";
          break;
        case AtomicRMWInst::Or:
          atomicOp = "or";
          break;
        case AtomicRMWInst::Xor:
          atomicOp = "xor";
          break;
        case AtomicRMWInst::Max:
          atomicOp = "max";
          break;
        case AtomicRMWInst::Min:
          atomicOp = "min";
          break;
        case AtomicRMWInst::UMax:
          atomicOp = "umax";
          break;
        case AtomicRMWInst::UMin:
          atomicOp = "umin";
          break;
        default:
          std::stringstream err_msg;
          err_msg << "Unknown atomic operation: BinOp=" << inst->getOperation();
          lljvm_unreachable(err_msg.str());
      }
      std::stringstream err_msg;
      err_msg << "Unsupported atomic operation: Name=" << atomicOp;
      throw err_msg.str();
  }
  printIndirectStore(val->getType());
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

  const Value *v1 = inst->getOperand(0);

  // First, we need to check if the input is a vector type or not
  if (const VectorType *vecTy = dyn_cast<VectorType>(v1->getType())) {
    bool f32 = false;
    bool f32_2nd = false;
    if (inst->getNumOperands() == 2) {
      f32 = (getTypeBitWidth(vecTy->getElementType()) == 32);
    } else {
      // For pow()
      f32 = (getTypeBitWidth(vecTy->getElementType()) == 32);

      // TODO: The return type depends on the 1st type
      // f32_2nd = (getTypeBitWidth(vecTy->getElementType()) == 32);
      f32_2nd = f32;
    }

    int size = getTypeAllocSize(vecTy->getElementType());

    printSimpleInstruction("sipush", utostr(vecTy->getNumElements() * size));
    printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");

    // TODO: Needs to support vector computation?
    for (int i = 0; i < vecTy->getNumElements(); i++) {
      printSimpleInstruction("dup2");
      printSimpleInstruction("ldc2_w", utostr(i * size));
      printSimpleInstruction("ladd");

      if (inst->getNumOperands() == 2) {
         printValueLoad(inst->getOperand(0));
         printSimpleInstruction("ldc2_w", utostr(i * size));
         printSimpleInstruction("ladd");
         printIndirectLoad(vecTy->getElementType());
         if (f32) printSimpleInstruction("f2d");
      } else {
        // For pow()
        printValueLoad(inst->getOperand(0));
        printSimpleInstruction("ldc2_w", utostr(i * size));
        printSimpleInstruction("ladd");
        printIndirectLoad(vecTy->getElementType());
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
      printIndirectStore(vecTy->getElementType());
    }
  } else if (v1->getType()->getTypeID() == Type::FloatTyID ||
      v1->getType()->getTypeID() == Type::DoubleTyID) {
    bool f32 = false;
    if (inst->getNumOperands() == 2) {
      f32 = (getTypeBitWidth(inst->getOperand(0)->getType()) == 32);
      printValueLoad(inst->getOperand(0));
      if (f32) printSimpleInstruction("f2d");
    } else {
      // For pow()
      f32 = (getTypeBitWidth(inst->getOperand(0)->getType()) == 32);
      printValueLoad(inst->getOperand(0));
      if (f32) printSimpleInstruction("f2d");

      // TODO: The return type depends on the 1st type
      bool f32_2nd = (getTypeBitWidth(inst->getOperand(1)->getType()) == 32);
      printValueLoad(inst->getOperand(1));
      if (f32_2nd) printSimpleInstruction("f2d");
    }

    printMathIntrinsic(inst->getIntrinsicID());
    if (f32) printSimpleInstruction("d2f");
  } else {
    std::stringstream err_msg;
    err_msg << "Unknown operand type: Type=" << getTypeIDName(inst->getOperand(0)->getType());
    lljvm_unreachable(err_msg.str());
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
