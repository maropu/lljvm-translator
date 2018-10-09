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

package io.github.maropu.lljvm

class LLJVMNativeSuite extends LLJVMFuncSuite {

  test("broken bitcode tests") {
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.loadClassFromBitcodeInResource("corrupt.bc")
    }.getMessage
    assert(errMsg.contains("Corrupt LLVM bitcode found"))
  }

  ignore("veryfyBitcode") {
    // TODO: Add tests for `LLJVMNative.veryfyBitcode`
  }

  test("magicNumber") {
    val lljvmApi = LLJVMLoader.loadLLJVMApi()
    assert(lljvmApi.magicNumber() === "20180731HMKjwzxmew")
  }

  test("asBitcode") {
    val bitcode = TestUtils.resourceToBytes("pyfunc-add-float32.bc")
    val lljvmApi = LLJVMLoader.loadLLJVMApi()
    TestUtils.compareCode(lljvmApi.asLLVMAssemblyCode(bitcode),
      s"""source_filename = "<string>"
         |target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
         |target triple = "x86_64-apple-darwin15.3.0"
         |
         |; Function Attrs: norecurse nounwind
         |define i32 @"_ZN8add_test12add_test $$243Eff"(float* noalias nocapture %retptr, { i8*, i32 }** noalias nocapture readnone %excinfo, i8* noalias nocapture readnone %env, float %arg.x, float %arg.y) local_unnamed_addr #0 {
         |entry:
         |  %.15 = fadd float %arg.x, %arg.y
         |  store float %.15, float* %retptr, align 4
         |  ret i32 0
         |}
         |
         |; Function Attrs: norecurse nounwind readnone
         |define float @"cfunc._ZN8add_test12add_test $$243Eff"(float %.1, float %.2) local_unnamed_addr #1 {
         |entry:
         |  %.15.i = fadd float %.1, %.2
         |  ret float %.15.i
         |}
         |
         |attributes #0 = { norecurse nounwind }
         |attributes #1 = { norecurse nounwind readnone }
       """.stripMargin)
  }
}
