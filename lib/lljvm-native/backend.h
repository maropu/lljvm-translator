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

#ifndef BACKEND_H
#define BACKEND_H

#include <llvm/ADT/DenseMap.h>
#include <llvm/ADT/DenseSet.h>
#include <llvm/ADT/StringExtras.h>
// #include <llvm/Analysis/ConstantsScanner.h>
#include <llvm/Analysis/LoopInfo.h>
#include <llvm/IR/Instructions.h>
#include <llvm/IR/IntrinsicInst.h>
#include <llvm/IR/Intrinsics.h>
#include <llvm/IR/Module.h>
#include <llvm/IR/GetElementPtrTypeIterator.h>
#include <llvm/IR/DerivedTypes.h>
#include <llvm/IR/InstIterator.h>
#include <llvm/IR/Type.h>
#include <llvm/IR/DerivedTypes.h>
#include <llvm/Support/ErrorHandling.h>
#include <llvm/Support/ErrorOr.h>
#include <llvm/Support/raw_ostream.h>

using namespace llvm;

/**
 * A FunctionPass for generating Jasmin-style assembly for the JVM.
 */
class JVMWriter : public FunctionPass {
  // The output stream
  raw_ostream &out;
  // The name of the source file
  std::string sourcename;
  // The binary name of the generated class
  std::string classname;
  // The debugging level
  unsigned int debug;
  // The current module
  Module *module;
  // The target data for the platform
  const DataLayout *targetData;
  // Pass ID
  static char id;

  // Set of external references
  DenseSet<const Value*> externRefs;
  // Mapping of blocks to unique IDs
  DenseMap<const BasicBlock*, unsigned int> blockIDs;
  // Mapping of values to local variable numbers
  DenseMap<const Value*, unsigned int> localVars;
  // Number of registers allocated for the function
  unsigned int usedRegisters;
  // Local variable number of the pointer to the packed list of varargs
  unsigned int vaArgNum;
  // Current instruction number
  unsigned int instNum;

public:
  JVMWriter(const DataLayout *td, raw_ostream &o, const std::string &cls, unsigned int dbg);

  StringRef getPassName() const override {
    return "JVMWriter";
  }

  // backend.cc
  void getAnalysisUsage(AnalysisUsage &au) const override;
  bool runOnFunction(Function &f) override;
  bool doInitialization(Module &m) override;
  bool doFinalization(Module &m) override;

private:
  // block.cc
  void printBasicBlock(const BasicBlock *block);
  void printInstruction(const Instruction *inst);

  // branch.cc
  void printPHICopy(const BasicBlock *src, const BasicBlock *dest);
  void printBranchInstruction(const BasicBlock *curBlock, const BasicBlock *destBlock);
  void printBranchInstruction(const BasicBlock *curBlock, const BasicBlock *trueBlock, const BasicBlock *falseBlock);
  void printBranchInstruction(const BranchInst *inst);
  void printSelectInstruction(const Value *cond, const Value *trueVal, const Value *falseVal);
  void printSwitchInstruction(const SwitchInst *inst);
  void printLoop(const Loop *l);

  // const.cc
  void printPtrLoad(uint64_t n);
  void printConstLoad(const APInt &i);
  void printConstLoad(float f);
  void printConstLoad(double d);
  void printConstLoad(const Constant *c);
  void printConstLoad(const std::string &str, bool cstring);
  void printStaticConstant(const Constant *c);
  void printConstantExpr(const ConstantExpr *ce);

  // function.cc
  std::string getCallSignature(const FunctionType *ty);
  void printOperandPack(const Instruction *inst, unsigned int minOperand, unsigned int maxOperand);
  void printIndirectFunctionCall(const Instruction *inst, const FunctionType *fTy);
  void printFunctionCall(const Value *functionVal, const Instruction *inst);
  void printIntrinsicCall(const IntrinsicInst *inst);
  void printCallInstruction(const Instruction *inst);
  void printInvokeInstruction(const InvokeInst *inst);
  void printLocalVariable(const Function &f, const Instruction *inst);
  void printFunctionBody(const Function &f);
  unsigned int getLocalVarNumber(const Value *v);
  void printCatchJump(unsigned int numJumps);
  void printFunction(const Function &f);

  // instruction.cc
  void printCmpInstruction(unsigned int predicate, const Value *left, const Value *right);
  void printArithmeticInstruction(unsigned int op, const Value *left, const Value *right);
  void printArithmeticInstruction(
    unsigned int op, const std::string& typeDescriptor, const std::string& typePrefix, int typeBitWidth);
  void printBitCastInstruction(const Type *ty, const Type *srcTy);
  void printCastInstruction(const std::string &typePrefix, const std::string &srcTypePrefix);
  void printCastInstruction(unsigned int op, const Type *srcTy, const Type *destTy);
  void printCastInstruction(unsigned int op, const Value *v, const Type *ty, const Type *srcTy);
  void printGepInstruction(const Value *v, gep_type_iterator i, gep_type_iterator e);
  void printGepInstruction1(const GetElementPtrInst *inst);
  void printAllocaInstruction(const AllocaInst *inst);
  void printVAArgInstruction(const VAArgInst *inst);
  void printExtractValue(const ExtractValueInst *inst);
  void printExtractElement(const ExtractElementInst *inst);
  void printInsertElement(const InsertElementInst *inst);
  void printInsertValue(const InsertValueInst *inst);
  void printShuffleVector(const ShuffleVectorInst *inst);
  void printVAIntrinsic(const IntrinsicInst *inst);
  void printMemIntrinsic(const MemIntrinsic *inst);
  void printMathIntrinsic(const IntrinsicInst *inst);
  void printMathIntrinsic(unsigned int op);
  void printBitIntrinsic(const IntrinsicInst *inst);
  void printAtomicRMW(const AtomicRMWInst *inst);

  // loadstore.cc
  void printValueLoad(const Value *v);
  void printValueStore(const Value *v);
  void printIndirectLoad(const Value *v);
  void printIndirectLoad(const Type *ty);
  void printIndirectStore(const Value *ptr, const Value *val);
  void printIndirectStore(const Type *ty);

  // name.cc
  std::string sanitizeName(std::string name);
  std::string getValueName(const Value *v);
  std::string getLabelName(const BasicBlock *block);

  // printinst.cc
  void printBinaryInstruction(const char *name, const Value *left, const Value *right);
  void printBinaryInstruction(const std::string &name, const Value *left, const Value *right);
  void printSimpleInstruction(const char *inst);
  void printSimpleInstruction(const char *inst, const char *operand);
  void printSimpleInstruction(const std::string &inst);
  void printSimpleInstruction(const std::string &inst, const std::string &operand);
  void printVirtualInstruction(const char *sig);
  void printVirtualInstruction(const char *sig, const Value *operand);
  void printVirtualInstruction(const char *sig, const Value *left, const Value *right);
  void printVirtualInstruction(const std::string &sig);
  void printVirtualInstruction(const std::string &sig, const Value *operand);
  void printVirtualInstruction(const std::string &sig, const Value *left, const Value *right);
  void printLabel(const char *label);
  void printLabel(const std::string &label);

  // sections.cc
  void printHeader();
  void printFields();
  void printExternalMethods();
  void printConstructor();
  void printClInit();
  void printMainMethod();

  // types.cc
  bool isPrimitiveType(const Type *ty);
  bool isNumericType(const Type *ty);
  unsigned int advanceNextOffset(unsigned int offset, const Type *ty);
  unsigned int getTypeAllocSize(const Type *ty);
  unsigned int getTypeByteWidth(const Type *ty, bool expand = false);
  unsigned int getTypeBitWidth(const Type *ty, bool expand = false);
  char getTypeID(const Type *ty, bool expand = false);
  std::string getTypeIDName(const Type *ty);
  std::string getTypeName(const Type *ty, bool expand = false);
  std::string getTypeDescriptor(const Type *ty, bool expand = false);
  std::string getTypePostfix(const Type *ty, bool expand = false);
  std::string getTypePrefix(const Type *ty, bool expand = false);
};

// `llvm_unreachable` leads to abortion, and then JVMs crash.
// So, we throw an C++ exception instead of `llvm_unreachable` to translate it
// into a JVM exception.
#define lljvm_unreachable(msg) \
   lljvm_unreachable_internal(msg, __FILE__, __LINE__)

extern void lljvm_unreachable_internal(
  const std::string& err_msg, const char *file, unsigned line);

#endif
