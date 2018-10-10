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

void JVMWriter::printBasicBlock(const BasicBlock *block) {
  printLabel(getLabelName(block));
  for (BasicBlock::const_iterator i = block->begin(), e = block->end(); i != e; i++) {
    instNum++;
    if (debug >= 3) {
      // Prints current instruction as comment.
      // note that this block of code significantly increases code generation time.
      std::string str;
      raw_string_ostream ss(str); ss << *i;
      std::string::size_type pos = 0;
      while ((pos = str.find("\n", pos)) != std::string::npos) {
        str.replace(pos++, 1, "\n;");
      }
      out << ';' << str << '\n';
    }
    if (debug >= 1) {
      printSimpleInstruction(".line", utostr(instNum));
    }

    if (i->getOpcode() == Instruction::PHI) {
      // Don't handle phi instruction in current block
      continue;
    }
    const Instruction& inst = *i;
    printInstruction(&inst);
    if (i->getType() != Type::getVoidTy(block->getContext()) && i->getOpcode() != Instruction::Invoke) {
      // Instruction doesn't return anything, or is an invoke instruction
      // which handles storing the return value itself
      printValueStore(&inst);
    }
  }
}

void JVMWriter::printInstruction(const Instruction *inst) {
  const Value *left, *right;
  if (inst->getNumOperands() >= 1) {
    left = inst->getOperand(0);
  }
  if (inst->getNumOperands() >= 2) {
    right = inst->getOperand(1);
  }

  // LLVM 7 instructions are listed below:
  // - https://releases.llvm.org/7.0.0/docs/LangRef.html#instruction-reference
  switch (inst->getOpcode()) {
    case Instruction::Ret:
      printSimpleInstruction("invokestatic", "io/github/maropu/lljvm/runtime/VMemory/destroyStackFrame()V");
      if (inst->getNumOperands() >= 1) {
        printValueLoad(left);
        printSimpleInstruction(getTypePrefix(left->getType(), true) + "return");
      } else {
        printSimpleInstruction("return");
      }
      break;
    case Instruction::Unreachable:
      printSimpleInstruction(
        "getstatic",
        "io/github/maropu/lljvm/runtime/Instruction$Unreachable/instance "
          "Lio/github/maropu/lljvm/runtime/Instruction$Unreachable;");
      printSimpleInstruction("athrow");
      break;
    case Instruction::Add:
    case Instruction::FAdd:
    case Instruction::Sub:
    case Instruction::FSub:
    case Instruction::Mul:
    case Instruction::FMul:
    case Instruction::UDiv:
    case Instruction::SDiv:
    case Instruction::FDiv:
    case Instruction::URem:
    case Instruction::SRem:
    case Instruction::FRem:
    case Instruction::And:
    case Instruction::Or:
    case Instruction::Xor:
    case Instruction::Shl:
    case Instruction::LShr:
    case Instruction::AShr:
      printArithmeticInstruction(inst->getOpcode(), left, right);
      break;
    case Instruction::SExt:
    case Instruction::Trunc:
    case Instruction::ZExt:
    case Instruction::FPTrunc:
    case Instruction::FPExt:
    case Instruction::UIToFP:
    case Instruction::SIToFP:
    case Instruction::FPToUI:
    case Instruction::FPToSI:
    case Instruction::PtrToInt:
    case Instruction::IntToPtr:
    case Instruction::BitCast:
      printCastInstruction(
        inst->getOpcode(), left, cast<CastInst>(inst)->getDestTy(), cast<CastInst>(inst)->getSrcTy());
      break;
    case Instruction::ICmp:
    case Instruction::FCmp:
      printCmpInstruction(cast<CmpInst>(inst)->getPredicate(), left, right);
      break;
    case Instruction::Br:
      printBranchInstruction(cast<BranchInst>(inst));
      break;
    case Instruction::Select:
      printSelectInstruction(inst->getOperand(0), inst->getOperand(1), inst->getOperand(2));
      break;
    case Instruction::Load:
      printIndirectLoad(inst->getOperand(0));
      break;
    case Instruction::Store:
      printIndirectStore(inst->getOperand(1), inst->getOperand(0));
      break;
    case Instruction::GetElementPtr:
      printGepInstruction(inst->getOperand(0), gep_type_begin(inst), gep_type_end(inst));
      break;
    case Instruction::Call:
      printCallInstruction(cast<CallInst>(inst));
      break;
    case Instruction::Invoke:
      printInvokeInstruction(cast<InvokeInst>(inst));
      break;
    case Instruction::Switch:
      printSwitchInstruction(cast<SwitchInst>(inst));
      break;
    case Instruction::Alloca:
      printAllocaInstruction(cast<AllocaInst>(inst));
      break;
    case Instruction::VAArg:
      printVAArgInstruction(cast<VAArgInst>(inst));
      break;
    case Instruction::ExtractValue:
      printExtractValue(cast<ExtractValueInst>(inst));
      break;
    case Instruction::ExtractElement:
      printExtractElement(cast<ExtractElementInst>(inst));
      break;
    case Instruction::InsertElement:
      printInsertElement(cast<InsertElementInst>(inst));
      break;
    case Instruction::InsertValue:
      printInsertValue(cast<InsertValueInst>(inst));
      break;
    case Instruction::ShuffleVector:
      printShuffleVector(cast<ShuffleVectorInst>(inst));
      break;
    case Instruction::AtomicRMW:
      printAtomicRMW(cast<AtomicRMWInst>(inst));
      break;
    case Instruction::Fence:
      // Does nothing for this instruction group
      break;

    // TODO: LLVM 7.x unsupported instructions are listed below and
    // need to be supported in future.
    case Instruction::AtomicCmpXchg:
    case Instruction::AddrSpaceCast:
    case Instruction::IndirectBr: {
      std::stringstream err_msg;
      err_msg << "Unsupported LLVM instruction: " << inst->getOpcodeName() <<
        " (Opcode=" << inst->getOpcode() << ")";
      throw err_msg.str();
    }
    // LLVM exception handling insructions are listed below:
    // - https://releases.llvm.org/7.0.0/docs/ExceptionHandling.html#overview
    case Instruction::Resume:
    case Instruction::CatchSwitch:
    case Instruction::CatchRet:
    case Instruction::CleanupRet:
    case Instruction::LandingPad:
    case Instruction::CatchPad:
    case Instruction::CleanupPad: {
      std::stringstream err_msg;
      err_msg << "Unsupported LLVM exception handling instruction: " << inst->getOpcodeName() <<
        " (Opcode=" << inst->getOpcode() << ")";
      throw err_msg.str();
    }

    default: {
      std::stringstream err_msg;
      err_msg << "Unknown LLVM instruction: " << inst->getOpcodeName() <<
        " (Opcode=" << inst->getOpcode() << ")";
      throw err_msg.str();
    }
  }
}
