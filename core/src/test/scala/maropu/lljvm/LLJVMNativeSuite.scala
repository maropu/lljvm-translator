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

import org.scalatest.FunSuite

class LLJVMNativeSuite extends FunSuite {

  test("broken bitcode tests") {
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.loadClassFromResource("corrupt.bc")
    }.getMessage
    assert(errMsg.contains("Corrupt LLVM bitcode found"))
  }

  ignore("veryfyBitcode") {
    // TODO: Add tests for `LLJVMNative.veryfyBitcode`
  }

  test("asBitcode") {
    val bitcode = TestUtils.resourceToBytes("llvm-pyfunc-bitcode/pyfunc1-cfunc-int32.bc")
    val lljvmApi = LLJVMLoader.loadLLJVMApi()
    // scalastyle:off line.size.limit
    TestUtils.compareCode(lljvmApi.asBitcode(bitcode),
      s"""
         |source_filename = "<string>"
         |target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
         |target triple = "x86_64-apple-darwin15.3.0"
         |
         |; Function Attrs: norecurse nounwind
         |define i32 @"_ZN7pyfunc111pyfunc1$$241Eii"(i32* noalias nocapture %retptr, { i8*, i32 }** noalias nocapture readnone %excinfo, i8* noalias nocapture readnone %env, i32 %arg.x, i32 %arg.y) local_unnamed_addr #0 {
         |entry:
         |  %.17 = add i32 %arg.y, %arg.x
         |  store i32 %.17, i32* %retptr, align 4
         |  ret i32 0
         |}
         |
         |; Function Attrs: norecurse nounwind readnone
         |define i32 @"cfunc._ZN7pyfunc111pyfunc1$$241Eii"(i32 %.1, i32 %.2) local_unnamed_addr #1 {
         |entry:
         |  %.17.i = add i32 %.2, %.1
         |  ret i32 %.17.i
         |}
         |
         |attributes #0 = { norecurse nounwind }
         |attributes #1 = { norecurse nounwind readnone }
       """.stripMargin)
    // scalastyle:on line.size.limit
  }
}
