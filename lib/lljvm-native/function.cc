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

#include <sstream>

static uint64_t getUID() {
  static uint64_t x = 0;
  return ++x;
}

/**
 * Returns the call signature of the given function type. An empty string is
 * returned if the function type appears to be non-prototyped.
 */
std::string JVMWriter::getCallSignature(const FunctionType *ty) {
  if (ty->isVarArg() && ty->getNumParams() == 0) {
    // non-prototyped function
    return "";
  }
  std::string sig;
  sig += '(';
  for (unsigned int i = 0, e = ty->getNumParams(); i < e; i++) {
    sig += getTypeDescriptor(ty->getParamType(i));
  }
  if (ty->isVarArg()) {
    sig += "I";
  }
  sig += ')';
  sig += getTypeDescriptor(ty->getReturnType());
  return sig;
}

/**
 * Packs the specified operands of the given instruction into memory. The
 * address of the packed values is left on the top of the stack.
 *
 * @param inst the given instruction
 * @param minOperand the lower bound on the operands to pack (inclusive)
 * @param maxOperand the upper bound on the operands to pack (exclusive)
 */
void JVMWriter::printOperandPack(
    const Instruction *inst, unsigned int minOperand, unsigned int maxOperand) {
  unsigned int size = 0;
  for (unsigned int i = minOperand; i < maxOperand; i++) {
    size += targetData->getTypeAllocSize(inst->getOperand(i)->getType());
  }
  if (size <= 0 || size > 32767) {
    throw "Stack size must be higher than 0 and smaller than 32768";
  }

  printSimpleInstruction("sipush", utostr(size));
  printSimpleInstruction("invokestatic", "org/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
  printSimpleInstruction("dup2");

  for (unsigned int i = minOperand; i < maxOperand; i++) {
    const Value *v = inst->getOperand(i);
    printValueLoad(v);
    printSimpleInstruction(
      "invokestatic", "org/maropu/lljvm/runtime/VMemory/pack(J" + getTypeDescriptor(v->getType()) + ")J");
  }
  printSimpleInstruction("pop2");
}

void JVMWriter::printFunctionCall(const Value *functionVal, const Instruction *inst) {
  unsigned int origin = isa<InvokeInst>(inst) ? 3 : 1;
  if (const Function *f = dyn_cast<Function>(functionVal)) { // direct call
    const FunctionType *ty = f->getFunctionType();
    if (externRefs.count(f)) {
      // printSimpleInstruction("invokestatic", getValueName(f) + getCallSignature(ty));
      printValueLoad(inst->getOperand(inst->getNumOperands() - 1));
      // TODO: Is `origin` is correct?
      if (inst->getNumOperands() > 1) {
        printOperandPack(inst, origin - 1, inst->getNumOperands() - 1);
      }

      // TODO: Reconsider this
      std::string funcName;
      raw_string_ostream strbuf(funcName);
      // const Type *pt = cast<PointerType>(functionVal->getType())->getElementType();
      // const FunctionType *fTy = cast<CallInst>(inst)->getFunctionType();
      // const Type *pt = functionVal->getType();
      const Type *pt = ty->getReturnType();
      if (inst->getNumOperands() > 1) {
        strbuf << "org/maropu/lljvm/runtime/Function/invoke_" << getTypePostfix(pt, true) <<
          "(Ljava/lang/String;Ljava/lang/String;J)" << getTypeDescriptor(pt);
      } else {
        // Case for no argument
        strbuf << "org/maropu/lljvm/runtime/Function/invoke_" << getTypePostfix(pt, true) <<
          "(Ljava/lang/String;Ljava/lang/String;)" << getTypeDescriptor(pt);
      }
      strbuf.flush();
      printSimpleInstruction("invokestatic", funcName);
    } else {
      for (unsigned int i = origin, e = inst->getNumOperands(); i < e; i++) {
        printValueLoad(inst->getOperand(i));
      }
      for (unsigned int i = 0, e = ty->getNumParams(); i < e; i++) {
        printValueLoad(inst->getOperand(i + origin));
      }
      if (ty->isVarArg() && inst) {
        printOperandPack(inst, ty->getNumParams() + origin, inst->getNumOperands());
      }
      printSimpleInstruction("invokestatic", classname + "/" + getValueName(f) + getCallSignature(ty));
    }

    if (getValueName(f) == "setjmp") {
      unsigned int varNum = usedRegisters++;
      printSimpleInstruction("istore", utostr(varNum));
      printSimpleInstruction("iconst_0");
      printLabel("setjmp$" + utostr(varNum));
    }
  } else { // indirect call
    // printValueLoad(functionVal);
    printValueLoad(inst->getOperand(inst->getNumOperands() - 1));
    // TODO: Is `origin` is correct?
    if (inst->getNumOperands() > 1) {
      printOperandPack(inst, origin - 1, inst->getNumOperands() - 1);
    }

    // TODO: Reconsiders this
    std::string funcName;
    raw_string_ostream strbuf(funcName);
    // const Type *pt = cast<PointerType>(functionVal->getType())->getElementType();
    const FunctionType *fTy = cast<CallInst>(inst)->getFunctionType();
    // const Type *pt = functionVal->getType();
    const Type *pt = fTy->getReturnType();
    if (inst->getNumOperands() > 1) {
      strbuf << "org/maropu/lljvm/runtime/Function/invoke_" << getTypePostfix(pt, true) <<
        "(Ljava/lang/String;Ljava/lang/String;J)" << getTypeDescriptor(pt);
    } else {
      // Case for no argument
      strbuf << "org/maropu/lljvm/runtime/Function/invoke_" << getTypePostfix(pt, true) <<
        "(Ljava/lang/String;Ljava/lang/String;)" << getTypeDescriptor(pt);
    }
    strbuf.flush();
    printSimpleInstruction("invokestatic", funcName);
  }
}

void JVMWriter::printIntrinsicCall(const IntrinsicInst *inst) {
  switch (inst->getIntrinsicID()) {
    case Intrinsic::vastart:
    case Intrinsic::vacopy:
    case Intrinsic::vaend:
      printVAIntrinsic(inst);
      break;
    case Intrinsic::memcpy:
    case Intrinsic::memmove:
    case Intrinsic::memset:
      printMemIntrinsic(cast<MemIntrinsic>(inst));
      break;
    case Intrinsic::flt_rounds:
      printSimpleInstruction("iconst_m1");
      break;
    case Intrinsic::dbg_declare:
      // Ignores debugging intrinsics
      break;
    case Intrinsic::pow:
    case Intrinsic::exp:
    case Intrinsic::log10:
    case Intrinsic::log:
    case Intrinsic::sqrt:
      printMathIntrinsic(inst);
      break;
    case Intrinsic::bswap:
      printBitIntrinsic(inst);
      break;
    default:
      errs() << "Intrinsic = " << *inst << '\n';
      llvm_unreachable("Invalid intrinsic function");
  }
}

void JVMWriter::printCallInstruction(const Instruction *inst) {
  if (isa<IntrinsicInst>(inst)) {
    printIntrinsicCall(cast<IntrinsicInst>(inst));
  } else {
    printFunctionCall(inst->getOperand(0), inst);
  }
}

void JVMWriter::printInvokeInstruction(const InvokeInst *inst) {
  std::string labelname = getUID() + "$invoke";
  printLabel(labelname + "_begin");
  printFunctionCall(inst->getOperand(0), inst);
  if (!inst->getType()->isVoidTy()) {
    // Saves return value
    printValueStore(inst);
  }
  printLabel(labelname + "_end");
  printBranchInstruction(inst->getParent(), inst->getNormalDest());
  printLabel(labelname + "_catch");
  printSimpleInstruction("pop");
  printBranchInstruction(inst->getParent(), inst->getUnwindDest());
  printSimpleInstruction(
    ".catch org/maropu/lljvm/runtime/System$Unwind",
    "from " + labelname + "_begin " + "to " + labelname + "_end " + "using " + labelname + "_catch");
}

/**
 * Allocates a local variable for the given function. Variable initialisation
 * and any applicable debugging information is printed.
 */
void JVMWriter::printLocalVariable(const Function &f, const Instruction *inst) {
    const Type *ty;
    if (isa<AllocaInst>(inst) && !isa<GlobalVariable>(inst)) {
      // Local variable allocation
      ty = PointerType::getUnqual(cast<AllocaInst>(inst)->getAllocatedType());
    } else { // operation result
      ty = inst->getType();
    }
    // getLocalVarNumber must be called at least once in this method
    unsigned int varNum = getLocalVarNumber(inst);
    if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(varNum) + " is " + getValueName(inst) + ' ' +
          getTypeDescriptor(ty) + " from begin_method to end_method");
    }
    // Initialises variable to avoid class verification errors
    printSimpleInstruction(getTypePrefix(ty, true) + "const_0");
    printSimpleInstruction(getTypePrefix(ty, true) + "store", utostr(varNum));
}

void JVMWriter::printFunctionBody(const Function &f) {
  for (Function::const_iterator i = f.begin(), e = f.end(); i != e; i++) {
    const BasicBlock& block = *i;
    auto loopInfoPass = getAnalysisIfAvailable<LoopInfoWrapperPass>();
    if (loopInfoPass != NULL) {
      Loop *l = loopInfoPass->getLoopInfo().getLoopFor(&block);
      if (l->getHeader() == &block && l->getParentLoop() == 0) {
        printLoop(l);
      }
    } else {
      printBasicBlock(&block);
    }
  }
}

/**
 * Return the local variable number of the given value. Register/s are
 * allocated for the variable if necessary.
 */
unsigned int JVMWriter::getLocalVarNumber(const Value *v) {
  if (!localVars.count(v)) {
    localVars[v] = usedRegisters++;
    if (getBitWidth(v->getType()) == 64) {
      // 64 bit types occupy 2 registers
      usedRegisters++;
    }
  }
  return localVars[v];
}

/**
 * Prints the block to catch Jump objects (thrown by longjmp).
 *
 * @param numJumps the number of setjmp calls made by the current function
 */
void JVMWriter::printCatchJump(unsigned int numJumps) {
  unsigned int jumpVarNum = usedRegisters++;
  printSimpleInstruction(
    ".catch org/maropu/lljvm/runtime/Jump "
      "from begin_method to catch_jump using catch_jump");
  printLabel("catch_jump");
  printSimpleInstruction("astore", utostr(jumpVarNum));
  printSimpleInstruction("aload", utostr(jumpVarNum));
  printSimpleInstruction("getfield", "org/maropu/lljvm/runtime/Jump/value J");
  for (unsigned int i = usedRegisters - 1 - numJumps, e = usedRegisters - 1; i < e; i++) {
    if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(i) + " is setjmp_id_" + utostr(i) +
          " J from begin_method to end_method");
    }
    printSimpleInstruction("aload", utostr(jumpVarNum));
    printSimpleInstruction("getfield", "org/maropu/lljvm/runtime/Jump/id J");
    printSimpleInstruction("iload", utostr(i));
    printSimpleInstruction("if_icmpeq", "setjmp$" + utostr(i));
  }
  printSimpleInstruction("pop");
  printSimpleInstruction("aload", utostr(jumpVarNum));
  printSimpleInstruction("athrow");
  if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(jumpVarNum) + " is jump "
          "Lorg/maropu/lljvm/runtime/Jump; from begin_method to end_method");
  }
}

void JVMWriter::printFunction(const Function &f) {
  localVars.clear();
  usedRegisters = 0;

  out << '\n';
  out << ".method " << (f.hasLocalLinkage() ? "private " : "public ") << "static " << getValueName(&f) << '(';
  for (Function::const_arg_iterator i = f.arg_begin(), e = f.arg_end(); i != e; i++) {
    out << getTypeDescriptor(i->getType());
  }
  if (f.isVarArg()) {
    out << "I";
  }
  out << ')' << getTypeDescriptor(f.getReturnType()) << '\n';

  for (Function::const_arg_iterator i = f.arg_begin(), e = f.arg_end(); i != e; i++) {
    // getLocalVarNumber must be called at least once in each iteration
    unsigned int varNum = getLocalVarNumber(i);
    if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(varNum) + " is " + getValueName(i) + ' ' +
          getTypeDescriptor(i->getType()) + " from begin_method to end_method");
    }
  }
  if (f.isVarArg()) {
    vaArgNum = usedRegisters++;
    if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(vaArgNum) + " is varargptr J from begin_method to end_method");
    }
  }

  // TODO: Better stack depth analysis
  unsigned int stackDepth = 8;
  unsigned int numJumps = 0;

  for (const_inst_iterator i = inst_begin(&f), e = inst_end(&f); i != e; i++) {
    if (stackDepth < i->getNumOperands()) {
      stackDepth = i->getNumOperands();
    }
    if (i->getType() != Type::getVoidTy(f.getContext())) {
      printLocalVariable(f, &*i);
    }
    if (const CallInst *inst = dyn_cast<CallInst>(&*i)) {
      if (!isa<IntrinsicInst>(inst) && getValueName(inst->getOperand(0)) == "setjmp") {
        numJumps++;
      }
    }
  }

  for (unsigned int i = 0; i < numJumps; i++) {
    // Initialises jump IDs to prevent class verification errors
    printSimpleInstruction("iconst_0");
    printSimpleInstruction("istore", utostr(usedRegisters + i));
  }

  printLabel("begin_method");
  printSimpleInstruction("invokestatic", "org/maropu/lljvm/runtime/VMemory/createStackFrame()V");

  printFunctionBody(f);
  if (numJumps) {
    printCatchJump(numJumps);
  }
  printSimpleInstruction(".limit stack", utostr(stackDepth * 2));
  printSimpleInstruction(".limit locals", utostr(usedRegisters));
  printLabel("end_method");
  out << ".end method\n";
}
