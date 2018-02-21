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

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStreamReader}
import java.lang.{Double => jDouble, Integer => jInt, Long => jLong}

import jasmin.ClassFile
import org.scalatest.FunSuite

import lljvm.unsafe.Platform

class JasminSuite extends FunSuite {

  test("asBytecode") {
    val bitcode = TestUtils.resourceToBytes("llvm-cfunc-bitcode/cfunc1.bc")
    TestUtils.compareCode(LLJVMUtils.asBytecode(bitcode),
      s""".class public final GeneratedClass
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
         |
         |        ; allocate global variables
         |
         |        ; initialise global variables
         |        return
         |.end method
         |
         |
         |.method public static _cfunc1(II)I
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
         |label1:
         |;  %1 = alloca i32, align 4
         |        bipush 4
         |        invokestatic lljvm/runtime/VMemory/allocateStack(I)J
         |        lstore_2 ; _2
         |;  %2 = alloca i32, align 4
         |        bipush 4
         |        invokestatic lljvm/runtime/VMemory/allocateStack(I)J
         |        lstore 4 ; _4
         |;  store i32 %x, i32* %1, align 4
         |        lload_2 ; _2
         |        iload_0 ; _x
         |        invokestatic lljvm/runtime/VMemory/store(JI)V
         |;  store i32 %y, i32* %2, align 4
         |        lload 4 ; _4
         |        iload_1 ; _y
         |        invokestatic lljvm/runtime/VMemory/store(JI)V
         |;  %3 = load i32, i32* %1, align 4
         |        lload_2 ; _2
         |        invokestatic lljvm/runtime/VMemory/load_i32(J)I
         |        istore 6 ; _6
         |;  %4 = load i32, i32* %2, align 4
         |        lload 4 ; _4
         |        invokestatic lljvm/runtime/VMemory/load_i32(J)I
         |        istore 7 ; _7
         |;  %5 = add nsw i32 %3, %4
         |        iload 6 ; _6
         |        iload 7 ; _7
         |        iadd
         |        istore 8 ; _8
         |;  ret i32 %5
         |        iload 8 ; _8
         |        ireturn
         |        .limit stack 16
         |        .limit locals 9
         |end_method:
         |.end method
       """.stripMargin)
  }

  test("plus") {
    val code =
      s""".class public final GeneratedClass
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

    val classFile = new ClassFile()
    val in = new InputStreamReader(new ByteArrayInputStream(code.getBytes))
    classFile.readJasmin(in, "GeneratedClass", true)

    val out = new ByteArrayOutputStream()
    classFile.write(out)
    assert(out.size > 0)

    val clazz = TestUtils.loadClassFromBytecode("GeneratedClass", out.toByteArray)
    val method = LLJVMUtils.getMethod(clazz, "plus", Seq(jInt.TYPE, jInt.TYPE): _*)
    val obj = clazz.newInstance()
    val args = Seq(new jInt(1), new jInt(2))
    assert(method.invoke(obj, args: _*) === 3)
  }

  test("pow") {
    val code =
      s""".class public final GeneratedClass
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
         |begin_method:
         |        invokestatic lljvm/runtime/Memory/createStackFrame()V
         |label2:
         |        dload_0 ; _a
         |        dload_2 ; _b
         |        invokestatic java/lang/Math/pow(DD)D
         |        dstore 4 ; _4
         |        invokestatic lljvm/runtime/Memory/destroyStackFrame()V
         |        dload 4 ; _4
         |        dreturn
         |        .limit stack 16
         |        .limit locals 6
         |end_method:
         |.end method
       """.stripMargin

    val classFile = new ClassFile()
    val in = new InputStreamReader(new ByteArrayInputStream(code.getBytes))
    classFile.readJasmin(in, "GeneratedClass", true)

    val out = new ByteArrayOutputStream()
    classFile.write(out)
    assert(out.size > 0)

    val clazz = TestUtils.loadClassFromBytecode("GeneratedClass", out.toByteArray)
    val method = LLJVMUtils.getMethod(clazz, "pow", Seq(jDouble.TYPE, jDouble.TYPE): _*)
    val obj = clazz.newInstance()
    val args = Seq(new jDouble(2.0), new jDouble(2.0))
    assert(method.invoke(obj, args: _*) === 4.0)
  }

  test("log10") {
    val code =
      s""".class public final GeneratedClass
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
         |begin_method:
         |        invokestatic lljvm/runtime/Memory/createStackFrame()V
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
         |        invokestatic lljvm/runtime/Memory/destroyStackFrame()V
         |        dload 8 ; __47_i
         |        dreturn
         |        .limit stack 16
         |        .limit locals 10
         |end_method:
         |.end method
       """.stripMargin

    val classFile = new ClassFile()
    val in = new InputStreamReader(new ByteArrayInputStream(code.getBytes))
    classFile.readJasmin(in, "GeneratedClass", true)

    val out = new ByteArrayOutputStream()
    classFile.write(out)
    assert(out.size > 0)

    val clazz = TestUtils.loadClassFromBytecode("GeneratedClass", out.toByteArray)
    val method = LLJVMUtils.getMethod(clazz, "log10", Seq(jDouble.TYPE, jDouble.TYPE): _*)
    val obj = clazz.newInstance()
    val args = Seq(new jDouble(100.0), new jDouble(2.0))
    assert(method.invoke(obj, args: _*) === 6.0)
  }

  ignore("jasmin assembly tests from resources") {
    val code = TestUtils.resourceToBytes("test.jasmin")
    val classFile = new ClassFile()
    val in = new InputStreamReader(new ByteArrayInputStream(code))
    classFile.readJasmin(in, "GeneratedClass", true)
    val out = new ByteArrayOutputStream()
    classFile.write(out)
    assert(out.size > 0)

    val clazz = TestUtils.loadClassFromBytecode("GeneratedClass", out.toByteArray)
    val method = LLJVMUtils.getMethod(clazz, "_cfunc__ZN7pyfunc812pyfunc8_2415E5ArrayIfLi1E1A7mutable7alignedE5ArrayIfLi1E1A7mutable7alignedE", Seq(jLong.TYPE, jLong.TYPE): _*)
    val obj = clazz.newInstance()
    assert(obj.getClass.getSimpleName === "GeneratedClass")
    val floatX = Array(1.0f, 2.0f, 3.0f, 4.0f)
    val floatY = Array(1.0f, 2.0f, 3.0f, 4.0f)
    val pyArrayX = new PyArrayHolder()
    val pyArrayY = new PyArrayHolder()
    val args = Seq(new jLong(pyArrayX.addressOf(floatX)), new jLong(pyArrayY.addressOf(floatY)))
    val result = method.invoke(obj, args: _*).asInstanceOf[Long]
    assert(Platform.getFloat(null, result) === 1.0f)
    assert(Platform.getFloat(null, result + 4) === 8.0f)
    assert(Platform.getFloat(null, result + 8) === 27.0f)
    assert(Platform.getFloat(null, result + 12) === 64.0f)
  }
}
