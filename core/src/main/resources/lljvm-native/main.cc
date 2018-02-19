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
    std::string bytecode = parseBitcode(str.c_str(), str.size(), debugLevel);
    std::cout << bytecode << "\n";
  } catch (const std::string& e) {
    std::cerr << e << std::endl;
    return -1;
  }
  return 0;
}
