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
#include "lljvm-internals.h"

#include <iostream>
#include <fstream>
#include <iostream>
#include <string>
#include <iterator>

#include <llvm/IR/Verifier.h>
#include <llvm/IR/LLVMContext.h>
#include <llvm/IR/DataLayout.h>
// #include <llvm/Bitcode/ReaderWriter.h>
#include <llvm/Bitcode/BitcodeReader.h>
#include <llvm/CodeGen/Passes.h>
// #include <llvm/IR/PassManager.h>
#include <llvm/IR/LegacyPassManager.h>
#include <llvm/Support/CommandLine.h>
#include <llvm/Support/FormattedStream.h>
#include <llvm/Support/MemoryBuffer.h>
#include <llvm/Support/raw_ostream.h>
#include <llvm/Transforms/Scalar.h>

using namespace llvm;

static cl::opt<std::string> input(
    cl::Positional, cl::desc("<input bitcode>"), cl::init("-"));
static cl::opt<std::string> classname(
    "classname", cl::desc("Binary name of the generated class"));

enum DebugLevel {g0 = 0, g1 = 1, g2 = 2, g3 = 3};
cl::opt<DebugLevel> debugLevel(cl::desc("Debugging level:"), cl::init(g1),
    cl::values(
    clEnumValN(g2, "g", "Same as -g2"),
    clEnumVal(g0, "No debugging information"),
    clEnumVal(g1, "Source file and line number information (default)"),
    clEnumVal(g2, "-g1 + Local variable information"),
    clEnumVal(g3, "-g2 + Commented LLVM assembly")));

int main(int argc, char** argv) {
    std::string filename = argv[1];
    std::ifstream ifs(filename);
    if (ifs.fail()) {
      std::cerr << "read failure" << std::endl;
      return -1;
    }
    std::string str((std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>());
    std::string bytecode = parseBitcode(str.c_str(), str.size());
    std::cout << bytecode << "\n";
    return 0;
}

