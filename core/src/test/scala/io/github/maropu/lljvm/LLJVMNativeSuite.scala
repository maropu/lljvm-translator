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

  test("broken bitcode tests") {
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.loadClassFromBitcodeInResource("corrupt.bc")
    }.getMessage
    assert(errMsg.contains("Expected bitcode header is -34,-64,23,11 or 66,67,-64,-34, " +
      "but 22,99,76,-117 found"))
  }

  ignore("veryfyBitcode") {
    // TODO: Add tests for `LLJVMNative.veryfyBitcode`
  }

  test("versionNumber") {
    assert(lljvmApi.versionNumber() === "0.1.0-EXPERIMENTAL")
  }

  test("magicNumber") {
    assert(lljvmApi.magicNumber() === "20180731HMKjwzxmew")
  }

  test("asLLVMAssemblyCode") {
    val bitcode = TestUtils.resourceToBytes("cfunc-add-int32.bc")

    val optO0 = (0, 0) // -O0
    TestUtils.compareCode(lljvmApi.asLLVMAssemblyCode(bitcode, optO0._1, optO0._2),
      s"""target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
         |target triple = "x86_64-apple-macosx10.10.0"
         |
         |; Function Attrs: nounwind ssp uwtable
         |define i32 @add_test(i32 %x, i32 %y) #0 {
         |  %1 = alloca i32, align 4
         |  %2 = alloca i32, align 4
         |  store i32 %x, i32* %1, align 4
         |  store i32 %y, i32* %2, align 4
         |  %3 = load i32, i32* %1, align 4
         |  %4 = load i32, i32* %2, align 4
         |  %5 = add nsw i32 %3, %4
         |  ret i32 %5
         |}
         |
         |attributes #0 = { nounwind ssp uwtable "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="core2" "target-features"="+ssse3,+cx16,+sse,+sse2,+sse3" "unsafe-fp-math"="false" "use-soft-float"="false" }
         |
         |!llvm.module.flags = !{!0}
         |!llvm.ident = !{!1}
         |
         |!0 = !{i32 7, !"PIC Level", i32 2}
         |!1 = !{!"Apple LLVM version 7.0.2 (clang-700.1.81)"}
         |
       """.stripMargin)

    val optOz = (2, 2) // -Oz
    TestUtils.compareCode(lljvmApi.asLLVMAssemblyCode(bitcode, optOz._1, optOz._2),
      s"""target datalayout = "e-m:o-i64:64-f80:128-n8:16:32:64-S128"
         |target triple = "x86_64-apple-macosx10.10.0"
         |
         |; Function Attrs: norecurse nounwind readnone ssp uwtable
         |define i32 @add_test(i32 %x, i32 %y) local_unnamed_addr #0 {
         |  %1 = add nsw i32 %y, %x
         |  ret i32 %1
         |}
         |
         |attributes #0 = { norecurse nounwind readnone ssp uwtable "less-precise-fpmad"="false" "no-frame-pointer-elim"="true" "no-frame-pointer-elim-non-leaf" "no-infs-fp-math"="false" "no-nans-fp-math"="false" "stack-protector-buffer-size"="8" "target-cpu"="core2" "target-features"="+ssse3,+cx16,+sse,+sse2,+sse3" "unsafe-fp-math"="false" "use-soft-float"="false" }
         |
         |!llvm.module.flags = !{!0}
         |!llvm.ident = !{!1}
         |
         |!0 = !{i32 7, !"PIC Level", i32 2}
         |!1 = !{!"Apple LLVM version 7.0.2 (clang-700.1.81)"}
       """.stripMargin)
  }

  test("asJVMAssemblyCode") {
    val bitcode = TestUtils.resourceToBytes("cfunc-add-int32.bc")
    val optO0 = (0, 0) // -O0
    TestUtils.compareCode(lljvmApi.asJVMAssemblyCode(bitcode, optO0._1, optO0._2, 0),
      s""".bytecode 50.0 ; Java v1.6 compatible bytecode
         |.class public final GeneratedClass20180731HMKjwzxmew
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
         |	.limit stack 8
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
         |	lconst_0
         |	lstore 2
         |	lconst_0
         |	lstore 4
         |	iconst_0
         |	istore 6
         |	iconst_0
         |	istore 7
         |	iconst_0
         |	istore 8
         |begin_method:
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/createStackFrame()V
         |label1:
         |	bipush 4
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J
         |	lstore_2 ; _2
         |	bipush 4
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J
         |	lstore 4 ; _4
         |	lload_2 ; _2
         |	iload_0 ; _x
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/store(JI)V
         |	lload 4 ; _4
         |	iload_1 ; _y
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/store(JI)V
         |	lload_2 ; _2
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/load_i32(J)I
         |	istore 6 ; _6
         |	lload 4 ; _4
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/load_i32(J)I
         |	istore 7 ; _7
         |	iload 6 ; _6
         |	iload 7 ; _7
         |	iadd
         |	istore 8 ; _8
         |	invokestatic io/github/maropu/lljvm/runtime/VMemory/destroyStackFrame()V
         |	iload 8 ; _8
         |	ireturn
         |	.limit stack 16
         |	.limit locals 9
         |end_method:
         |.end method
       """.stripMargin)

    val optOz = (2, 2) // -Oz
    TestUtils.compareCode(lljvmApi.asJVMAssemblyCode(bitcode, optOz._1, optOz._2, 0),
      s""".bytecode 50.0 ; Java v1.6 compatible bytecode
         |.class public final GeneratedClass20180731HMKjwzxmew
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
         |	.limit stack 8
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

  test("No field value exists in LLJVM runtime") {
    val errMsg = intercept[LLJVMRuntimeException] {
      // @no_existent_field = external global i32
      //
      // define i32* @no_field_exists_in_runtime() {
      //   ret i32* @no_existent_field
      // }
      TestUtils.loadClassFromBitcodeInResource("no_field_exists_in_runtime.bc")
    }.getMessage
    assert(errMsg.contains("Can't find a field value in LLJVM runtime: no_existent_field"))
  }

  test("No function exists in LLJVM runtime") {
    val errMsg = intercept[LLJVMRuntimeException] {
      // define i32 @no_function_exists_in_runtime() {
      //   call void @no_existent_function()
      //   ret i32 0
      // }
      //
      // declare void @no_existent_function() local_unnamed_addr
      TestUtils.loadClassFromBitcodeInResource("no_function_exists_in_runtime.bc")
    }.getMessage
    assert(errMsg.contains("Can't find a function in LLJVM runtime: _no_existent_function()V"))
  }
}
