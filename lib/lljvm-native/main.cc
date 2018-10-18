/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "lljvm-internals.h"

#include <fstream>
#include <iostream>

#include <llvm/Support/CommandLine.h>

using namespace llvm;

static cl::opt<std::string> input(
  cl::Positional, cl::desc("<input bitcode>"), cl::init("-"));
static cl::opt<std::string> classname(
  "classname", cl::desc("Binary name of the generated class"));

enum OptLevel { O0 = 0, O1 = 1, O2 = 2, O3 = 3, Os = 4, Oz = 5 };

const cl::opt<OptLevel> optimizationLevel(
  cl::desc("Optimization level:"),
  cl::init(O0),
  cl::values(
    clEnumValN(O0, "O0", "Optimization level 0. Similar to clang -O0"),
    clEnumValN(O1, "O1", "Optimization level 1. Similar to clang -O1"),
    clEnumValN(O2, "O2", "Optimization level 2. Similar to clang -O2"),
    clEnumValN(O3, "O3", "Optimization level 3. Similar to clang -O3"),
    clEnumValN(Os, "Os", "Like -O2 with extra optimizations for size. Similar to clang -Os"),
    clEnumValN(Oz, "Oz", "Like -Os but reduces code size further. Similar to clang -Oz")
  ));

enum DebugLevel { g0 = 0, g1 = 1, g2 = 2, g3 = 3 };

const cl::opt<DebugLevel> debugLevel(
  cl::desc("Debugging level:"),
  cl::init(g1),
  cl::values(
    clEnumValN(g2, "g", "Same as -g2"),
    clEnumVal(g0, "No debugging information"),
    clEnumVal(g1, "Source file and line number information (default)"),
    clEnumVal(g2, "-g1 + Local variable information"),
    clEnumVal(g3, "-g2 + Commented LLVM assembly")
  ));

int main(int argc, char** argv) {
  std::string filename = argv[1];
  std::ifstream ifs(filename);
  if (ifs.fail()) {
    std::cerr << "read failure" << std::endl;
    return -1;
  }
  std::string str((std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>());
  try {
    unsigned optLevel = 0;
    unsigned sizeLevel = 0;
    switch (optimizationLevel) {
      case O0: optLevel = 0; sizeLevel = 0; break;
      case O1: optLevel = 1; sizeLevel = 0; break;
      case O2: optLevel = 2; sizeLevel = 0; break;
      case Os: optLevel = 2; sizeLevel = 1; break;
      case Oz: optLevel = 2; sizeLevel = 2; break;
      case O3: optLevel = 3; sizeLevel = 0; break;
    }
    std::string jvmAsm = parseBitcode(str.c_str(), str.size(), optLevel, sizeLevel, debugLevel);
    std::cout << jvmAsm << "\n";
  } catch (const std::string& e) {
    std::cerr << e << std::endl;
    return -1;
  }
  return 0;
}
