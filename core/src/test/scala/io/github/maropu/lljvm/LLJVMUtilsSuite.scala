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

import java.lang.{Double => jDouble, Integer => jInt}

import scala.collection.JavaConverters._

import org.scalatest.FunSuite

import io.github.maropu.lljvm.util.JvmAssembler

class TestClass {

  def method1(a: Int, b: Double): Unit = {}
  def method2(a: String): Unit = {}
  def method3(a: String): Unit = {}
  def method4(): Unit = {}
  def method5(a: Int, b: Int): Int = a + b
}

class LLJVMUtilsSuite extends FunSuite {

  test("asJVMAssemblyCode") {
    val bitcode = TestUtils.resourceToBytes("cfunc/add_test.bc")
    TestUtils.compareCode(LLJVMUtils.asJVMAssemblyCode(bitcode),
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |; Fields
         |
         |; External methods
         |
         |; Constructor
         |.method public <init>()V
         |        aload_0
         |        invokespecial java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public <clinit>()V
         |        .limit stack 4
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/resetHeap()V
         |
         |        ; allocate global variables
         |
         |        ; initialise global variables
         |        return
         |.end method
         |
         |.method public static _add_test(II)I
         |        lconst_0
         |        lstore 2
         |        lconst_0
         |        lstore 4
         |        iconst_0
         |        istore 6
         |        iconst_0
         |        istore 7
         |        iconst_0
         |        istore 8
         |begin_method:
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/createStackFrame()V
         |label1:
         |        bipush 4
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J
         |        lstore_2 ; _2
         |        bipush 4
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/allocateStack(I)J
         |        lstore 4 ; _4
         |        lload_2 ; _2
         |        iload_0 ; _x
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/store(JI)V
         |        lload 4 ; _4
         |        iload_1 ; _y
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/store(JI)V
         |        lload_2 ; _2
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/load_i32(J)I
         |        istore 6 ; _6
         |        lload 4 ; _4
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/load_i32(J)I
         |        istore 7 ; _7
         |        iload 6 ; _6
         |        iload 7 ; _7
         |        iadd
         |        istore 8 ; _8
         |        invokestatic io/github/maropu/lljvm/runtime/VMemory/destroyStackFrame()V
         |        iload 8 ; _8
         |        ireturn
         |        .limit stack 16
         |        .limit locals 9
         |end_method:
         |.end method
       """.stripMargin)
  }

  test("invoke") {
    val errMsg = intercept[LLJVMRuntimeException] {
      LLJVMUtils.invoke(classOf[TestClass], "func", "abc")
    }.getMessage
    assert(errMsg.contains("Unsupported argument type: String"))
  }

  test("getAllMethods") {
    val methods = LLJVMUtils.getAllMethods(classOf[TestClass]).asScala.map(_.getName)
    assert(methods.toSet === Set("method1", "method2", "method3", "method4", "method5"))
  }

  test("getMethod") {
    val errMsg = intercept[LLJVMRuntimeException] {
      LLJVMUtils.getMethod(classOf[TestClass], "unknownMethod", jInt.TYPE, classOf[String])
    }.getMessage
    assert(errMsg === "Method not found: unknownMethod(int, class java.lang.String)")

    val m1 = LLJVMUtils.getMethod(classOf[TestClass], "method1", jInt.TYPE, jDouble.TYPE)
    assert(m1.getName === "method1")
    assert(m1.getParameterTypes.toSeq === Seq(Integer.TYPE, jDouble.TYPE))

    val m2 = LLJVMUtils.getMethod(classOf[TestClass], "method2", classOf[String])
    assert(m2.getName === "method2")
    assert(m2.getParameterTypes.toSeq === Seq(classOf[String]))

    val m3 = LLJVMUtils.getMethod(classOf[TestClass], "method3", classOf[String])
    assert(m3.getName === "method3")
    assert(m3.getParameterTypes.toSeq === Seq(classOf[String]))

    val m4 = LLJVMUtils.getMethod(classOf[TestClass], "method4")
    assert(m4.getName === "method4")
    assert(m4.getParameterTypes.toSeq === Seq.empty)

    val m5 = LLJVMUtils.getMethod(classOf[TestClass], "method5", jInt.TYPE, jInt.TYPE)
    assert(m5.getName === "method5")
    assert(m5.getParameterTypes.toSeq === Seq(Integer.TYPE, Integer.TYPE))
  }
}
