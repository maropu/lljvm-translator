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

#include "gtest/gtest.h"

#include <llvm/Config/llvm-config.h>

#include <cstdio>
#include <string>

struct Version {
  int major = 0, minor = 0, revision = 0;

  Version(std::string version) {
    std::sscanf(version.c_str(), "%d.%d.%d", &major, &minor, &revision);
  }

  bool operator <=(const Version& other) {
    if (major <= other.major) return true;
    if (minor <= other.minor) return true;
    if (revision <= other.revision) return true;
    return false;
  }

  bool operator <(const Version& other) {
    if (major < other.major) return true;
    if (minor < other.minor) return true;
    if (revision < other.revision) return true;
    return false;
  }
};

GTEST_TEST(LLJVMUnitTest, SimpleTest) {
  EXPECT_TRUE(Version("7.0.0") <= Version(LLVM_VERSION_STRING) &&
    Version(LLVM_VERSION_STRING) < Version("7.1.0"));
}

