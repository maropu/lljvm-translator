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
    size += getTypeAllocSize(inst->getOperand(i)->getType());
  }
  if (size <= 0 || size > 32767) {
    std::stringstream err_msg;
    err_msg << "Stack size must be higher than 0 and smaller than 32768";
    throw err_msg.str();
  }

  printSimpleInstruction("sipush", utostr(size));
  printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J");
  printSimpleInstruction("dup2");

  for (unsigned int i = minOperand; i < maxOperand; i++) {
    const Value *v = inst->getOperand(i);
    printValueLoad(v);
    printSimpleInstruction(
      "invokestatic", "io/github/maropu/lljvm/runtime/VMemory/pack(J" + getTypeDescriptor(v->getType()) + ")J");
  }
  printSimpleInstruction("pop2");
}

// Handles an indirect function call, e.g.,
//
// ; External methods
// ; .extern method _PyString_FromString(J)J ; i8* (i8*)
//   ...
// ; %.50 = call i8* @PyString_FromString(i8* getelementptr inbounds ([40 x i8], [40 x i8]* @".const.<Numba C callback 'numpy_random2_test'>", i64 0, i64 0))
//      ldc ""
//      ldc "_PyString_FromString(J)J"
//      sipush 8
//      invokestatic io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J
//      dup2
//      getstatic GeneratedClass20180731HMKjwzxmew/__const__Numba_C_callback__numpy_random2_test__ J
//      invokestatic io/github/maropu/lljvm/runtime/VMemory/pack(JJ)J
//      pop2
//      invokestatic io/github/maropu/lljvm/runtime/Function/invoke_i64(Ljava/lang/String;Ljava/lang/String;J)J
//      lstore 60 ; __50
//
void JVMWriter::printIndirectFunctionCall(const Instruction *inst, const FunctionType *fTy) {
  printValueLoad(inst->getOperand(fTy->getNumParams()));
  if (fTy->getNumParams() > 0) {
    printOperandPack(inst, 0, fTy->getNumParams());
  }

  std::string funcName;
  raw_string_ostream strbuf(funcName);
  const Type *pt = fTy->getReturnType();
  if (inst->getNumOperands() > 1) {
    strbuf << "io/github/maropu/lljvm/runtime/Function/invoke_" << getTypePostfix(pt, true) <<
      "(Ljava/lang/String;Ljava/lang/String;J)" << getTypeDescriptor(pt);
  } else {
    // Case for no argument
    strbuf << "io/github/maropu/lljvm/runtime/Function/invoke_" << getTypePostfix(pt, true) <<
      "(Ljava/lang/String;Ljava/lang/String;)" << getTypeDescriptor(pt);
  }
  strbuf.flush();
  printSimpleInstruction("invokestatic", funcName);
}

void JVMWriter::printFunctionCall(const Value *functionVal, const Instruction *inst) {
  if (const Function *f = dyn_cast<Function>(functionVal)) {
    if (externRefs.count(f)) {
      const FunctionType *fTy = f->getFunctionType();
      printIndirectFunctionCall(inst, fTy);
    } else {
      std::stringstream err_msg;
      err_msg << "Unknown function call: Name=" << f->getName().str();
      lljvm_unreachable(err_msg.str());
    }

    if (getValueName(f) == "setjmp") {
      unsigned int varNum = usedRegisters++;
      printSimpleInstruction("istore", utostr(varNum));
      printSimpleInstruction("iconst_0");
      printLabel("setjmp$" + utostr(varNum));
    }
  } else {
    const FunctionType *fTy = cast<CallInst>(inst)->getFunctionType();
    printIndirectFunctionCall(inst, fTy);
  }
}

void JVMWriter::printIntrinsicCall(const IntrinsicInst *inst) {
  // See IR/IntrinsicEnums.inc
  //
  // TODO: Support more Intrinsic functions:
  //  - https://releases.llvm.org/7.0.0/docs/LangRef.html#intrinsic-functions
  switch (inst->getIntrinsicID()) {
    // Variable Argument Handling Intrinsics
    case Intrinsic::vastart:
    case Intrinsic::vaend:
    case Intrinsic::vacopy:
      printVAIntrinsic(inst);
      break;

    // Standard C Library Intrinsics
    case Intrinsic::memcpy:
    case Intrinsic::memmove:
    case Intrinsic::memset:
      printMemIntrinsic(cast<MemIntrinsic>(inst));
      break;
    case Intrinsic::sqrt:
    case Intrinsic::powi:
    case Intrinsic::sin:
    case Intrinsic::cos:
    case Intrinsic::pow:
    case Intrinsic::exp:
    case Intrinsic::exp2:
    case Intrinsic::log:
    case Intrinsic::log10:
    case Intrinsic::log2:
    case Intrinsic::fma:
    case Intrinsic::fabs:
    case Intrinsic::minnum:
    case Intrinsic::maxnum:
    case Intrinsic::copysign:
    case Intrinsic::floor:
    case Intrinsic::ceil:
    case Intrinsic::trunc:
    case Intrinsic::rint:
    case Intrinsic::nearbyint:
    case Intrinsic::round:
      printMathIntrinsic(inst);
      break;

    // Bit Manipulation Intrinsics
    case Intrinsic::bitreverse:
    case Intrinsic::bswap:
    case Intrinsic::ctpop:
    case Intrinsic::ctlz:
    case Intrinsic::cttz:
    case Intrinsic::fshl:
    case Intrinsic::fshr:
      printBitIntrinsic(inst);
      break;

    // Debugger Intrinsics
    case Intrinsic::dbg_declare:
      // Ignores debugging intrinsics
      break;

    default:
      std::stringstream err_msg;
      err_msg << "Unsupported intrinsic function: Name=" << Intrinsic::getName(inst->getIntrinsicID()).str();
      throw err_msg.str();
  }
}

void JVMWriter::printCallInstruction(const Instruction *inst) {
  if (isa<IntrinsicInst>(inst)) {
    // All the intrinsic function calls should be called directly, e.g.,
    //
    // ; %.34.le.i.3 = tail call double @llvm.pow.f64(double %.16.i.3, double 4.000000e+00) #3
    //      dload 552 ; __16_i_3
    //      ldc2_w 4.0000000000000000
    //      invokestatic java/lang/Math/pow(DD)D
    //      dstore 554 ; __34_le_i_3
    //
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
    ".catch io/github/maropu/lljvm/runtime/System$Unwind",
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
    if (getTypeBitWidth(v->getType()) == 64) {
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
    ".catch io/github/maropu/lljvm/runtime/Jump "
      "from begin_method to catch_jump using catch_jump");
  printLabel("catch_jump");
  printSimpleInstruction("astore", utostr(jumpVarNum));
  printSimpleInstruction("aload", utostr(jumpVarNum));
  printSimpleInstruction("getfield", "io/github/maropu/lljvm/runtime/Jump/value J");
  for (unsigned int i = usedRegisters - 1 - numJumps, e = usedRegisters - 1; i < e; i++) {
    if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(i) + " is setjmp_id_" + utostr(i) +
          " J from begin_method to end_method");
    }
    printSimpleInstruction("aload", utostr(jumpVarNum));
    printSimpleInstruction("getfield", "io/github/maropu/lljvm/runtime/Jump/id J");
    printSimpleInstruction("iload", utostr(i));
    printSimpleInstruction("if_icmpeq", "setjmp$" + utostr(i));
  }
  printSimpleInstruction("pop");
  printSimpleInstruction("aload", utostr(jumpVarNum));
  printSimpleInstruction("athrow");
  if (debug >= 2) {
      printSimpleInstruction(
        ".var " + utostr(jumpVarNum) + " is jump "
          "Lio/github/maropu/lljvm/runtime/Jump; from begin_method to end_method");
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
  printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/createStackFrame()V");

  printFunctionBody(f);
  if (numJumps) {
    printCatchJump(numJumps);
  }
  printSimpleInstruction(".limit stack", utostr(stackDepth * 2));
  printSimpleInstruction(".limit locals", utostr(usedRegisters));
  printLabel("end_method");
  out << ".end method\n";
}
