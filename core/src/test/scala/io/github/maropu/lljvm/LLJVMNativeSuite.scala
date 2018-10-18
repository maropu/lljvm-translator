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

  val lljvmApi = LLJVMLoader.loadLLJVMApi()

  ignore("broken bitcode tests") {
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.loadClassFromBitcodeInResource("corrupt.bc")
    }.getMessage
    assert(errMsg.contains("Corrupt LLVM bitcode found"))
  }

  ignore("veryfyBitcode") {
    // TODO: Add tests for `LLJVMNative.veryfyBitcode`
  }

  test("magicNumber") {
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

  test("optimization level: -Oz") {
    val bitcode = TestUtils.resourceToBytes("cfunc-add-int32.bc")
    logDebug(
      s"""
         |========== LLVM Assembly Code =========
         |${TestUtils.asLLVMAssemblyCode(bitcode)}
         |========== JVM Assembly Code (-O0) =========
         |${lljvmApi.asJVMAssemblyCode(bitcode, 0, 0, 3)}
         |========== JVM Assembly Code (-Oz) =========
         |${lljvmApi.asJVMAssemblyCode(bitcode, 2, 2, 3)}
       """.stripMargin)

    val (optLevel, sizeLevel, debugLevel) = (2, 2, 0)
    TestUtils.compareCode(lljvmApi.asJVMAssemblyCode(bitcode, optLevel, sizeLevel, debugLevel),
      s""".class public final GeneratedClass20180731HMKjwzxmew
         |.super java/lang/Object
         |
         |; Fields
         |
         |; External methods
         |
         |; Constructor
         |.method public <init>()V
         |	aload_0
         |	invokespecial java/lang/Object/<init>()V
         |	return
         |.end method
         |
         |.method public <clinit>()V
         |	.limit stack 4
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/resetHeap()V
         |
         |	; allocate global variables
         |
         |	; initialise global variables
         |	return
         |.end method
         |
         |
         |.method public static _add_test(II)I
         |	iconst_0
         |	istore 2
         |begin_method:
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/createStackFrame()V
         |label1:
         |	iload_1 ; _y
         |	iload_0 ; _x
         |	iadd
         |	istore_2 ; _2
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/destroyStackFrame()V
         |	iload_2 ; _2
         |	ireturn
         |	.limit stack 16
         |	.limit locals 3
         |end_method:
         |.end method
       """.stripMargin)
  }
}
