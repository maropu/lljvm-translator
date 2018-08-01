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

package maropu.lljvm

import java.lang.{Double => jDouble}

import org.scalatest.FunSuite

import maropu.lljvm.util.clang.ClangRunner

class ProcessRunnerSuite extends FunSuite {

  ignore("ClangRunner") {
    val bitcode = ClangRunner.exec(
      s"""#include <math.h>
         |double cfunc(double a, double b) {
         |  return pow(2.0 * a, 2.0) + 4.0 * b;
         |}
       """.stripMargin)

    val clazz = TestUtils.loadClassFromBitcode(bitcode)
    val result = LLJVMUtils.invoke(clazz, new jDouble(4.0), new jDouble(2.0))
    assert(result === 72.0)

    val errMsg = intercept[LLJVMRuntimeException] {
      ClangRunner.exec(
        s"""int cfunc(int a, int b) {
           |  return a + b + c;
           |}
         """.stripMargin)
    }.getMessage
    assert(errMsg.contains("use of undeclared identifier 'c'"))
  }
}
