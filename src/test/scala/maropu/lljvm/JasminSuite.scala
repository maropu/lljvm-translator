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
import java.lang.{Double => jDouble, Integer => jInt}

import scala.collection.JavaConverters._

import jasmin.ClassFile
import org.scalatest.FunSuite

class JasminSuite extends FunSuite {

  test("asBytecode") {
    val bitcode = TestUtils.resourceToBytes("llvm-pyfunc-bitcode/pyfunc1-int32.bc")
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
         |.method public static __ZN8__main__11pyfunc1_241Eii(IIIII)I
         |        iconst_0
         |        istore 5
         |begin_method:
         |        invokestatic lljvm/runtime/Memory/createStackFrame()V
         |label1:
         |;  %.17 = add i32 %arg.y, %arg.x
         |        iload 4 ; _arg_y
         |        iload_3 ; _arg_x
         |        iadd
         |        istore 5 ; __17
         |;  store i32 %.17, i32* %retptr, align 4
         |        iload_0 ; _retptr
         |        iload 5 ; __17
         |        invokestatic lljvm/runtime/Memory/store(II)V
         |;  ret i32 0
         |        invokestatic lljvm/runtime/Memory/destroyStackFrame()V
         |        iconst_0
         |        ireturn
         |        .limit stack 16
         |        .limit locals 6
         |end_method:
         |.end method
         |
         |.method public static _cfunc__ZN8__main__11pyfunc1_241Eii(II)I
         |        iconst_0
         |        istore 2
         |begin_method:
         |        invokestatic lljvm/runtime/Memory/createStackFrame()V
         |label2:
         |;  %.17.i = add i32 %.2, %.1
         |        iload_1 ; __2
         |        iload_0 ; __1
         |        iadd
         |        istore_2 ; __17_i
         |;  ret i32 %.17.i
         |        invokestatic lljvm/runtime/Memory/destroyStackFrame()V
         |        iload_2 ; __17_i
         |        ireturn
         |        .limit stack 16
         |        .limit locals 3
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
}
