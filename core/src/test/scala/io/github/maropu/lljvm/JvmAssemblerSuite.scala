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

import org.scalatest.FunSuite

import io.github.maropu.lljvm.util.JvmAssembler

class JvmAssemblerSuite extends FunSuite {

  test("plus") {
    val code =
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static plus(II)I
         |.limit stack 2
         |.limit locals 2
         |        iload_0
         |        iload_1
         |        iadd
         |        ireturn
         |
         |.end method
       """.stripMargin

    val bytecode = JvmAssembler.compile(code)
    val clazz = TestUtils.loadClassFromBytecode(bytecode)
    val method = LLJVMUtils.getMethod(clazz, "plus", Seq(jInt.TYPE, jInt.TYPE): _*)
    val obj = clazz.newInstance()
    val args = Seq(new jInt(1), new jInt(2))
    assert(method.invoke(obj, args: _*) === 3)
  }

  test("pow") {
    val code =
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static pow(DD)D
         |        dconst_0
         |        dstore 4
         |label2:
         |        dload_0 ; _a
         |        dload_2 ; _b
         |        invokestatic java/lang/Math/pow(DD)D
         |        dstore 4 ; _4
         |        dload 4 ; _4
         |        dreturn
         |        .limit stack 16
         |        .limit locals 6
         |end_method:
         |.end method
       """.stripMargin

    val bytecode = JvmAssembler.compile(code)
    val clazz = TestUtils.loadClassFromBytecode(bytecode)
    val method = LLJVMUtils.getMethod(clazz, "pow", Seq(jDouble.TYPE, jDouble.TYPE): _*)
    val obj = clazz.newInstance()
    val args = Seq(new jDouble(2.0), new jDouble(2.0))
    assert(method.invoke(obj, args: _*) === 4.0)
  }

  test("log10") {
    val code =
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static log10(DD)D
         |        dconst_0
         |        dstore 4
         |        dconst_0
         |        dstore 6
         |        dconst_0
         |        dstore 8
         |label2:
         |        dload_2 ; __2
         |        ldc2_w 2.000000
         |        dmul
         |        dstore 4 ; __19_i
         |        dload_0 ; __1
         |        invokestatic java/lang/Math/log10(D)D
         |        dstore 6 ; __37_i
         |        dload 4 ; __19_i
         |        dload 6 ; __37_i
         |        dadd
         |        dstore 8 ; __47_i
         |        dload 8 ; __47_i
         |        dreturn
         |        .limit stack 16
         |        .limit locals 10
         |end_method:
         |.end method
       """.stripMargin

    val bytecode = JvmAssembler.compile(code)
    val clazz = TestUtils.loadClassFromBytecode(bytecode)
    val method = LLJVMUtils.getMethod(clazz, "log10", Seq(jDouble.TYPE, jDouble.TYPE): _*)
    val obj = clazz.newInstance()
    val args = Seq(new jDouble(100.0), new jDouble(2.0))
    assert(method.invoke(obj, args: _*) === 6.0)
  }

  test("throw exceptions if illegal bytecode found") {
    val illegalCode =
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static plus(II)I
         |.limit stack 2
         |.limit locals 2
         |        lload_0 ; Push wrong type data onto the operand stack
         |        iload_1
         |        iadd
         |        ireturn
         |
         |.end method
       """.stripMargin

    val errMsg = intercept[LLJVMRuntimeException] {
      JvmAssembler.compile(illegalCode)
    }.getMessage
    assert(errMsg.contains(
      "Illegal bytecode found: Error at instruction 0: Expected J, but found I"))
  }
}
