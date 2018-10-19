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

import java.lang.{Boolean => jBoolean, Double => jDouble, Float => jFloat, Integer => jInt, Long => jLong}
import java.nio.charset.{StandardCharsets => Charsets}

import scala.reflect.ClassTag

import io.github.maropu.lljvm.unsafe.Platform
import io.github.maropu.lljvm.util.{ArrayUtils, JVMAssembler}
import io.github.maropu.lljvm.util.analysis.BytecodeVerifier

class LLJVMInstSuite extends LLJVMFuncSuite {

  private val basePath = "llvm-insts"

  private def vectorTypeTest[T: ClassTag](
      name: String,
      clazz: Class[_],
      obj: Any,
      args: Seq[AnyRef],
      argTypes: Seq[Class[_]] = jLong.TYPE :: Nil,
      expected: Seq[T]): Unit = {

    val method = LLJVMUtils.getMethod(clazz, name, argTypes: _*)
    val result = method.invoke(obj, args: _*).asInstanceOf[Long]

    def getValue(i: Int): Any = implicitly[ClassTag[T]].runtimeClass match {
      case t if t == jBoolean.TYPE =>
        Platform.getBoolean(null, result + i)
      case t if t == jInt.TYPE =>
        Platform.getInt(null, result + 4 * i)
      case t if t == jFloat.TYPE =>
        Platform.getFloat(null, result + 4 * i)
    }
    expected.zipWithIndex.foreach { case (v, i) =>
      assert(getValue(i) === v, s": test=$name index=$i")
    }
  }

  // LLVM 7 instructions are listed in an URL below:
  // - https://releases.llvm.org/7.0.0/docs/LangRef.html#instruction-reference
  Seq[(String, (Class[_], Any) => Unit)](
    ("add", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_add", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(1), new jInt(3))
      assert(method.invoke(obj, args: _*) === 4)
    }),

    ("sub", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_sub", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(8), new jInt(7))
      assert(method.invoke(obj, args: _*) === 1)
    }),

    ("mul", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_mul", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(4), new jInt(2))
      assert(method.invoke(obj, args: _*) === 8)
    }),

    ("ret", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_ret")
      assert(method.invoke(obj) === 0)
    }),

    ("br", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_br", Seq(jBoolean.TYPE): _*)
      assert(method.invoke(obj, Seq(new jBoolean(true)): _*) === 1)
      assert(method.invoke(obj, Seq(new jBoolean(false)): _*) === 0)
    }),

    ("switch", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_switch", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(0)): _*) === 1)
      assert(method.invoke(obj, Seq(new jInt(1)): _*) === 2)
      assert(method.invoke(obj, Seq(new jInt(-1)): _*) === 3)
    }),

    ("call", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_call", Seq(jDouble.TYPE): _*)
      assert(method.invoke(obj, Seq(new jDouble(2.0)): _*) === 4.0)
    }),

    ("unreachable", (clazz, obj) => {}),

    ("fadd", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fadd", Seq(jDouble.TYPE, jDouble.TYPE): _*)
      val args = Seq(new jDouble(9.2), new jDouble(0.8))
      assert(method.invoke(obj, args: _*) === 10.0)
    }),

    ("fsub", (clazz, obj) => {
      {
        val method = LLJVMUtils.getMethod(clazz, "_fsub1", Seq(jFloat.TYPE, jFloat.TYPE): _*)
        val args = Seq(new jFloat(5.5), new jFloat(1.5))
        assert(method.invoke(obj, args: _*) === 4.0f)
      }
      vectorTypeTest("_fsub2", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(3.0f, 2.0f, 0.0f, 1.0f))) :: Nil,
        expected = -3.0f :: -2.0f :: 0.0f :: -1.0f :: Nil)
      vectorTypeTest("_fsub3", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(0.0f, 3.0f, 1.0f, 2.0f))) :: Nil,
        expected = 0.0f :: -3.0f :: -1.0f :: -2.0f :: Nil)
      vectorTypeTest("_fsub4", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(2.0f, 1.0f, 0.0f, 3.0f))) :: Nil,
        expected = -3.0f :: -4.0f :: 0.0f :: -5.0f :: Nil)
      vectorTypeTest("_fsub5", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(1.0f, 4.0f, 2.0f, 0.0f))) :: Nil,
        expected = -1.0f :: -4.0f :: -2.0f :: 0.0f :: Nil)
      vectorTypeTest("_fsub6", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(3.0f, 2.0f, 0.0f, 1.0f))) :: Nil,
        expected = 3.0f :: 2.0f :: 0.0f :: 1.0f :: Nil)
      vectorTypeTest("_fsub7", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(0.0f, 3.0f, 1.0f, 2.0f))) :: Nil,
        expected = 0.0f :: 3.0f :: 1.0f :: 2.0f :: Nil)
      vectorTypeTest("_fsub8", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(2.0f, 1.0f, 0.0f, 3.0f))) :: Nil,
        expected = 3.0f :: 4.0f :: 0.0f :: 5.0f :: Nil)
      // TODO: Needs to fix this test failure
      // vectorTypeTest("_fsub9", clazz, obj,
      //   args = new jLong(ArrayUtils.addressOf(Array(1.0f, 4.0f, 2.0f, 0.0f))) :: Nil,
      //   expected = 0.0f :: 0.0f :: 0.0f :: 0.0f :: Nil)
    }),

    ("fmul", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fmul", Seq(jFloat.TYPE, jFloat.TYPE): _*)
      val args = Seq(new jFloat(1.5), new jFloat(4.0))
      assert(method.invoke(obj, args: _*) === 6.0f)
    }),

    ("fdiv", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fdiv", Seq(jDouble.TYPE, jDouble.TYPE): _*)
      val args = Seq(new jDouble(6.0), new jDouble(2.0))
      assert(method.invoke(obj, args: _*) === 3.0)
    }),

    ("sdiv", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_sdiv", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(12), new jInt(2))
      assert(method.invoke(obj, args: _*) === 6)
    }),

    ("udiv", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_udiv", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(30), new jInt(15))
      assert(method.invoke(obj, args: _*) === 2)
    }),

    ("frem", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_frem", Seq(jDouble.TYPE, jDouble.TYPE): _*)
      val args = Seq(new jDouble(10.5), new jDouble(3.0))
      assert(method.invoke(obj, args: _*) === 1.5)
    }),

    ("srem", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_srem", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(7), new jInt(4))
      assert(method.invoke(obj, args: _*) === 3)
    }),

    ("urem", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_urem", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(13), new jInt(2))
      assert(method.invoke(obj, args: _*) === 1)
    }),

    ("shl", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_shl", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(2)): _*) === 8)
    }),

    ("ashr", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_ashr", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(8)): _*) === 2)
    }),

    ("lshr", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_lshr", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(32)): _*) === 8)
    }),

    ("and", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_and", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(15), new jInt(3))
      assert(method.invoke(obj, args: _*) === 3)
    }),

    ("or", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_or", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(15), new jInt(3))
      assert(method.invoke(obj, args: _*) === 15)
    }),

    ("xor", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_xor", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(15), new jInt(3))
      assert(method.invoke(obj, args: _*) === 12)
    }),

    ("alloca", (clazz, obj) => { // "load" and "store"
      val method = LLJVMUtils.getMethod(clazz, "_alloca", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(6)): _*) === 10)
    }),

    ("fence", (clazz, obj) => {}),

    ("getelementptr", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_getelementptr", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0, 2, 4, -6, 8, 10))
      assert(method.invoke(obj, Seq(new jLong(addr)): _*) === -6)
    }),

    ("sext", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_sext", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(3)): _*) === 3L)
    }),

    ("zext", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_zext", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(3)): _*) === 3L)
    }),

    ("fptosi", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fptosi", Seq(jFloat.TYPE): _*)
      assert(method.invoke(obj, Seq(new jFloat(2.3)): _*) === 2)
    }),

    ("fptoui", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fptoui", Seq(jFloat.TYPE): _*)
      assert(method.invoke(obj, Seq(new jFloat(2.3)): _*) === 2)
    }),

    ("sitofp", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_sitofp", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(6)): _*) === 6.0f)
    }),

    ("uitofp", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_uitofp", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(6)): _*) === 6.0f)
    }),

    ("fpext", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fpext", Seq(jFloat.TYPE): _*)
      assert(method.invoke(obj, Seq(new jFloat(2.5f)): _*) === 2.5)
    }),

    ("ptrtoint", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_ptrtoint", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0, 1, 2))
      assert(method.invoke(obj, Seq(new jLong(addr)): _*) === addr)
    }),

    ("inttoptr", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_inttoptr", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0, 1, 2))
      assert(method.invoke(obj, Seq(new jLong(addr)): _*) === addr)
    }),

    ("trunc", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_trunc", Seq(jLong.TYPE): _*)
      assert(method.invoke(obj, Seq(new jLong(-7)): _*) === -7.toShort)
    }),

    ("fptrunc", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fptrunc", Seq(jDouble.TYPE): _*)
      assert(method.invoke(obj, Seq(new jDouble(1.0)): _*) === 1.0f)
    }),

    ("bitcast", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_bitcast", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0.0, 4.0, 8.0))
      assert(method.invoke(obj, Seq(new jLong(addr)): _*) === addr)
    }),

    ("icmp", (clazz, obj) => {
      {
        val method = LLJVMUtils.getMethod(clazz, "_icmp1", Seq(jInt.TYPE, jInt.TYPE): _*)
        val args = Seq(new jInt(2), new jInt(3))
        assert(method.invoke(obj, args: _*) === true)
      }
      vectorTypeTest("_icmp2", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-1, 1, -2, 3))) :: Nil,
        expected = true :: false :: true :: false :: Nil)
      vectorTypeTest("_icmp3", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-1, 1, -2, 3))) :: Nil,
        expected = true :: false :: true :: false :: Nil)
      vectorTypeTest("_icmp4", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(2, -4, 4, -6))) :: Nil,
        expected = false :: true :: true :: false :: Nil)
      vectorTypeTest("_icmp5", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(3, 5, 1, 8))) :: Nil,
        expected = true :: false :: true :: false :: Nil)
      vectorTypeTest("_icmp6", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-1, 1, -2, 3))) :: Nil,
        expected = false :: true :: false :: true :: Nil)
      vectorTypeTest("_icmp7", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-1, 1, -2, 3))) :: Nil,
        expected = false :: true :: false :: true :: Nil)
      vectorTypeTest("_icmp8", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(2, -4, 4, -6))) :: Nil,
        expected = true :: false :: false :: true :: Nil)
      vectorTypeTest("_icmp9", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(3, 5, 1, 8))) :: Nil,
        expected = false :: true :: false :: true :: Nil)
    }),

    ("fcmp", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_fcmp", Seq(jFloat.TYPE, jFloat.TYPE): _*)
      val args = Seq(new jFloat(1.0f), new jFloat(3.0f))
      assert(method.invoke(obj, args: _*) === false)
    }),

    ("phi", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_phi", Seq(jInt.TYPE): _*)
      assert(method.invoke(obj, Seq(new jInt(1)): _*) === 3)
      assert(method.invoke(obj, Seq(new jInt(0)): _*) === -3)
    }),

    ("select", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_select", Seq(jBoolean.TYPE): _*)
      assert(method.invoke(obj, Seq(new jBoolean(true)): _*) === 1.0)
      assert(method.invoke(obj, Seq(new jBoolean(false)): _*) === 9.0)
    }),

    ("extractelement", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_extractelement", Seq(jLong.TYPE): _*)
      val ar = Array(-5, 6, -7, 8)
      assert(method.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar))): _*) === -7)
    }),

    ("insertelement", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_insertelement", Seq(jLong.TYPE): _*)
      val ar = Array(-5, 6, -7, 8)
      val addr = ArrayUtils.addressOf(ar)
      assert(method.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar))): _*) === addr)
      assert(ar(3) === 4)
    }),

    ("shufflevector", (clazz, obj) => {
      vectorTypeTest("_shufflevector1", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-3, 6, -7, 8))) :: Nil,
        expected = 6 :: 8 :: -7 :: -3 :: Nil)
      vectorTypeTest("_shufflevector2", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(
          Array(1.0f, 1.0f, 1.0f, 1.0f, 2.0f, 2.0f, 2.0f, 2.0f))) :: Nil,
        expected = 0.0f :: 0.0f :: 0.0f :: 0.0f :: 0.0f :: 0.0f :: 0.0f :: 0.0f :: Nil)
      vectorTypeTest("_shufflevector3", clazz, obj,
        args = Seq(Array(5, 2, 7, 6), Array(3, 0, 1, 4)).map {
          ar => new jLong(ArrayUtils.addressOf(ar))
        },
        argTypes = jLong.TYPE :: jLong.TYPE :: Nil,
        expected = 5 :: 3 :: 2 :: 0 :: Nil)
      vectorTypeTest("_shufflevector4", clazz, obj,
        args = Seq(Array(7, 6, 5, 4), Array(3, 2, 1, 0)).map {
          ar => new jLong(ArrayUtils.addressOf(ar))
        },
        argTypes = jLong.TYPE :: jLong.TYPE :: Nil,
        expected = 4 :: 4 :: 6 :: 6 :: 2 :: 2 :: 0 :: 0 :: Nil)
    }),

    ("extractvalue", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_extractvalue", Seq(jLong.TYPE): _*)
      val ar = Array(3, -4)
      assert(method.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar))): _*) === -4)
    }),

    ("insertvalue", (clazz, obj) => {
      val method = LLJVMUtils.getMethod(clazz, "_insertvalue", Seq(jLong.TYPE): _*)
      val ar = Array(0.0, 0.0)
      val addr = ArrayUtils.addressOf(ar)
      assert(method.invoke(obj, Seq(new jLong(addr)): _*) === addr)
      assert(ar(1) === 7.0)
    })

    // ("atomicrmw", (clazz, obj) => {
    //   // add
    //   val method1 = LLJVMUtils.getMethod(clazz, "_add", Seq(jLong.TYPE): _*)
    //   val ar1 = Array(3)
    //   assert(method1.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar1))): _*) === 3)
    //   assert(ar1(0) === 4)
    //
    //   // sub
    //   val method2 = LLJVMUtils.getMethod(clazz, "_sub", Seq(jLong.TYPE): _*)
    //   val ar2 = Array(3)
    //   assert(method2.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar2))): _*) === 3)
    //   assert(ar2(0) === 2)
    //
    //   // TODO: Adds tests for instructions below:
    //   // atomicrmw (xchg, and, nand, or, xor, max, min, umax, umin)
    // })
  ).foreach { case (inst, testFunc) =>

    test(s"basic LLJVM translation test - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/$inst.bc")
      val jvmAsm = TestUtils.asJVMAssemblyCode(bitcode)
      logDebug( // Outputs this log in `core/target/unit-tests.log`
        s"""
           |========== LLVM Assembly Code =========
           |${TestUtils.asLLVMAssemblyCode(bitcode)}
           |========== JVM Assembly Code =========
           |$jvmAsm
         """.stripMargin)

      // Verifies the generated bytecode
      val bytecode = JVMAssembler.compile(jvmAsm, false)
      BytecodeVerifier.verify(bytecode)

      val clazz = TestUtils.loadClassFromBytecode(bytecode)
      val obj = clazz.newInstance()
      testFunc(clazz, obj)
    }
  }

  Seq("cmpxchg"
    // TODO: Adds tests for instructions below:
    // "addrspacecast", "indirectbr", "va_arg"
  ).foreach { inst =>
    test(s"basic LLJVM translation test - unsupported - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/$inst.bc")
      val errMsg = intercept[LLJVMRuntimeException] {
        TestUtils.asJVMAssemblyCode(bitcode)
      }.getMessage
      assert(errMsg.contains("Unsupported LLVM instruction:"))
      logDebug(
        s"""
           |========== LLVM Assembly Code =========
           |${TestUtils.asLLVMAssemblyCode(bitcode)}
           |========== JVM Assembly Code =========
           |<unsupported>
         """.stripMargin)
    }
  }

  Seq("resume"
    // TODO: Adds tests for instructions below:
    // "catchswitch", "catchret", "cleanupret", "landingpad", "catchpad", "cleanuppad"
  ).foreach { inst =>
    test(s"basic LLJVM translation test - unsupported - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/$inst.bc")
      val errMsg = intercept[LLJVMRuntimeException] {
        TestUtils.asJVMAssemblyCode(bitcode)
      }.getMessage
      assert(errMsg.contains("Unsupported LLVM exception handling instruction:"))
      logDebug(
        s"""
           |========== LLVM Assembly Code =========
           |${TestUtils.asLLVMAssemblyCode(bitcode)}
           |========== JVM Assembly Code =========
           |<unsupported>
         """.stripMargin)
    }
  }

  // The instructions for exception handling (invoke, resume, catchswitch, catchret, cleanupret,
  // landingpad, catchpad, and cleanuppad) are not supported in LLJVM.
  test("exception handling (unsupported)") {
    val bitcode = TestUtils.resourceToBytes(s"$basePath/exception.bc")
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.asJVMAssemblyCode(bitcode)
    }.getMessage
    assert(errMsg.contains("Unsupported"))
    logDebug(
      s"""
         |========== Source Code ==========
         |${new String(TestUtils.resourceToBytes(s"$basePath/exception.cc"), Charsets.UTF_8)}
         |========== LLVM Assembly Code =========
         |${TestUtils.asLLVMAssemblyCode(bitcode)}
         |========== JVM Assembly Code =========
         |<unsupported>
       """.stripMargin)
  }
}
