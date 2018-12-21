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

import java.lang.{Boolean => jBoolean, Double => jDouble, Float => jFloat, Integer => jInt, Long => jLong, Short => jShort}
import java.nio.charset.{StandardCharsets => Charsets}

import scala.reflect.ClassTag

import io.github.maropu.lljvm.runtime.VMemory
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
    // Checks if the input/output addresses are different
    // between each other based on SSA.
    assert(!args.contains(result))

    def getValue(i: Int): Any = implicitly[ClassTag[T]].runtimeClass match {
      case t if t == jBoolean.TYPE =>
        Platform.getBoolean(null, result + i)
      case t if t == jShort.TYPE =>
        Platform.getShort(null, result + 2 * i)
      case t if t == jInt.TYPE =>
        Platform.getInt(null, result + 4 * i)
      case t if t == jLong.TYPE =>
        Platform.getLong(null, result + 8 * i)
      case t if t == jFloat.TYPE =>
        Platform.getFloat(null, result + 4 * i)
      case t if t == jDouble.TYPE =>
        Platform.getDouble(null, result + 8 * i)
    }
    expected.zipWithIndex.foreach { case (v, i) =>
      assert(getValue(i) === v, s": test=$name index=$i")
    }
  }

  // LLVM 7 instructions are listed in an URL below:
  // - https://releases.llvm.org/7.0.0/docs/LangRef.html#instruction-reference
  Seq[(String, (Class[_], Any) => Unit)](
    ("add", (clazz, obj) => {
      val add = LLJVMUtils.getMethod(clazz, "_add", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(1), new jInt(3))
      assert(add.invoke(obj, args: _*) === 4)
    }),

    ("sub", (clazz, obj) => {
      val sub = LLJVMUtils.getMethod(clazz, "_sub", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(8), new jInt(7))
      assert(sub.invoke(obj, args: _*) === 1)
    }),

    ("mul", (clazz, obj) => {
      val mul = LLJVMUtils.getMethod(clazz, "_mul", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(4), new jInt(2))
      assert(mul.invoke(obj, args: _*) === 8)
    }),

    ("ret", (clazz, obj) => {
      val ret = LLJVMUtils.getMethod(clazz, "_ret")
      assert(ret.invoke(obj) === 0)
    }),

    ("br", (clazz, obj) => {
      val br = LLJVMUtils.getMethod(clazz, "_br", Seq(jBoolean.TYPE): _*)
      assert(br.invoke(obj, Seq(new jBoolean(true)): _*) === 1)
      assert(br.invoke(obj, Seq(new jBoolean(false)): _*) === 0)
    }),

    ("switch", (clazz, obj) => {
      val switch = LLJVMUtils.getMethod(clazz, "_switch", Seq(jInt.TYPE): _*)
      assert(switch.invoke(obj, Seq(new jInt(0)): _*) === 1)
      assert(switch.invoke(obj, Seq(new jInt(1)): _*) === 2)
      assert(switch.invoke(obj, Seq(new jInt(-1)): _*) === 3)
    }),

    ("call", (clazz, obj) => {
      val call = LLJVMUtils.getMethod(clazz, "_call", Seq(jDouble.TYPE): _*)
      assert(call.invoke(obj, Seq(new jDouble(2.0)): _*) === 4.0)
    }),

    ("unreachable", (clazz, obj) => {}),

    ("fadd", (clazz, obj) => {
      val fadd = LLJVMUtils.getMethod(clazz, "_fadd", Seq(jDouble.TYPE, jDouble.TYPE): _*)
      val args = Seq(new jDouble(9.2), new jDouble(0.8))
      assert(fadd.invoke(obj, args: _*) === 10.0)
    }),

    ("fsub", (clazz, obj) => {
      {
        val fsub1 = LLJVMUtils.getMethod(clazz, "_fsub1", Seq(jFloat.TYPE, jFloat.TYPE): _*)
        val args = Seq(new jFloat(5.5), new jFloat(1.5))
        assert(fsub1.invoke(obj, args: _*) === 4.0f)
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
      vectorTypeTest("_fsub9", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(1.0f, 4.0f, 2.0f, 0.0f))) :: Nil,
        expected = 1.0f :: Nil)
      // left undef value case
      vectorTypeTest("_fsub10", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(1.0f, 4.0f, 2.0f, 0.0f))) :: Nil,
        expected = -1.0f :: -4.0f :: -2.0f :: 0.0f :: Nil)
      // right undef value case
      vectorTypeTest("_fsub11", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(3.0f, 1.0f, 0.0f, 2.0f))) :: Nil,
        expected = 3.0f :: 1.0f :: 0.0f :: 2.0f :: Nil)
      // both undef value case
      vectorTypeTest("_fsub12", clazz, obj,
        args = new jLong(0) :: Nil,
        expected = 0.0f :: 0.0f :: 0.0f :: 0.0f :: Nil)
    }),

    ("fmul", (clazz, obj) => {
      val fmul = LLJVMUtils.getMethod(clazz, "_fmul", Seq(jFloat.TYPE, jFloat.TYPE): _*)
      val args = Seq(new jFloat(1.5), new jFloat(4.0))
      assert(fmul.invoke(obj, args: _*) === 6.0f)
    }),

    ("fdiv", (clazz, obj) => {
      val fdiv = LLJVMUtils.getMethod(clazz, "_fdiv", Seq(jDouble.TYPE, jDouble.TYPE): _*)
      val args = Seq(new jDouble(6.0), new jDouble(2.0))
      assert(fdiv.invoke(obj, args: _*) === 3.0)
    }),

    ("sdiv", (clazz, obj) => {
      val sdiv = LLJVMUtils.getMethod(clazz, "_sdiv", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(12), new jInt(2))
      assert(sdiv.invoke(obj, args: _*) === 6)
    }),

    ("udiv", (clazz, obj) => {
      val udiv = LLJVMUtils.getMethod(clazz, "_udiv", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(30), new jInt(15))
      assert(udiv.invoke(obj, args: _*) === 2)
    }),

    ("frem", (clazz, obj) => {
      val frem = LLJVMUtils.getMethod(clazz, "_frem", Seq(jDouble.TYPE, jDouble.TYPE): _*)
      val args = Seq(new jDouble(10.5), new jDouble(3.0))
      assert(frem.invoke(obj, args: _*) === 1.5)
    }),

    ("srem", (clazz, obj) => {
      val srem = LLJVMUtils.getMethod(clazz, "_srem", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(7), new jInt(4))
      assert(srem.invoke(obj, args: _*) === 3)
    }),

    ("urem", (clazz, obj) => {
      val urem = LLJVMUtils.getMethod(clazz, "_urem", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(13), new jInt(2))
      assert(urem.invoke(obj, args: _*) === 1)
    }),

    ("shl", (clazz, obj) => {
      val shl = LLJVMUtils.getMethod(clazz, "_shl", Seq(jInt.TYPE): _*)
      assert(shl.invoke(obj, Seq(new jInt(2)): _*) === 8)
    }),

    ("ashr", (clazz, obj) => {
      val ashr = LLJVMUtils.getMethod(clazz, "_ashr", Seq(jInt.TYPE): _*)
      assert(ashr.invoke(obj, Seq(new jInt(8)): _*) === 2)
    }),

    ("lshr", (clazz, obj) => {
      val lshr = LLJVMUtils.getMethod(clazz, "_lshr", Seq(jInt.TYPE): _*)
      assert(lshr.invoke(obj, Seq(new jInt(32)): _*) === 8)
    }),

    ("and", (clazz, obj) => {
      val and = LLJVMUtils.getMethod(clazz, "_and", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(15), new jInt(3))
      assert(and.invoke(obj, args: _*) === 3)
    }),

    ("or", (clazz, obj) => {
      val or = LLJVMUtils.getMethod(clazz, "_or", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(15), new jInt(3))
      assert(or.invoke(obj, args: _*) === 15)
    }),

    ("xor", (clazz, obj) => {
      val xor = LLJVMUtils.getMethod(clazz, "_xor", Seq(jInt.TYPE, jInt.TYPE): _*)
      val args = Seq(new jInt(15), new jInt(3))
      assert(xor.invoke(obj, args: _*) === 12)
    }),

    ("alloca", (clazz, obj) => { // "load" and "store"
      // alloca1 - i32
      val alloca1 = LLJVMUtils.getMethod(clazz, "_alloca1", Seq(jInt.TYPE): _*)
      assert(alloca1.invoke(obj, Seq(new jInt(6)): _*) === 10)

      // alloca2 - { i32*, i64*, <3 x i32> }*
      val alloca2 = LLJVMUtils.getMethod(clazz, "_alloca2")
      val result2 = alloca2.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result2, 28))

      // alloca3 - { i32*, <2 x i32> }*
      val alloca3 = LLJVMUtils.getMethod(clazz, "_alloca3")
      val result3 = alloca3.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result3, 16))

      // alloca4 - { i32*, i64*, { i32, i64} }*
      val alloca4 = LLJVMUtils.getMethod(clazz, "_alloca4")
      val result4 = alloca4.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result4, 28))

      // alloca5 - { i32*, i64*, [3 x i32] }*
      val alloca5 = LLJVMUtils.getMethod(clazz, "_alloca5")
      val result5 = alloca5.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result5, 28))

      // alloca6 - [3 x i32]*
      val alloca6 = LLJVMUtils.getMethod(clazz, "_alloca6")
      assert(VMemory.verifyStackAddress(alloca6.invoke(obj).asInstanceOf[Long], 12))

      // alloca7 - [3 x i32]*
      val alloca7 = LLJVMUtils.getMethod(clazz, "_alloca7")
      assert(VMemory.verifyStackAddress(alloca7.invoke(obj).asInstanceOf[Long], 12))

      // alloca8 - <3 x i32>*
      val alloca8 = LLJVMUtils.getMethod(clazz, "_alloca8")
      assert(VMemory.verifyStackAddress(alloca8.invoke(obj).asInstanceOf[Long], 12))

      // alloca9 - <3 x i32>*
      val alloca9 = LLJVMUtils.getMethod(clazz, "_alloca9")
      assert(VMemory.verifyStackAddress(alloca9.invoke(obj).asInstanceOf[Long], 12))

      // alloca10 - <4 x double>*
      val alloca10 = LLJVMUtils.getMethod(clazz, "_alloca10")
      val result10 = alloca10.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result10, 32))
      assert(Platform.getDouble(null, result10) === 1.0)
      assert(Platform.getDouble(null, result10 + 8) === 2.0)
      assert(Platform.getDouble(null, result10 + 16) === 3.0)
      assert(Platform.getDouble(null, result10 + 24) === 4.0)

      // alloca11 - <4 x double>*
      val alloca11 = LLJVMUtils.getMethod(clazz, "_alloca11")
      val result11 = alloca11.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result11, 32))
      assert(Platform.getDouble(null, result11) === 5.0)
      assert(Platform.getDouble(null, result11 + 8) === 0.0)  // undef
      assert(Platform.getDouble(null, result11 + 16) === 0.0) // undef
      assert(Platform.getDouble(null, result11 + 24) === 0.0) // undef

      // alloca12 - <4 x double>*
      val alloca12 = LLJVMUtils.getMethod(clazz, "_alloca12")
      val result12 = alloca12.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result12, 32))
      assert(Platform.getDouble(null, result12) === 0.0)      // undef
      assert(Platform.getDouble(null, result12 + 8) === 0.0)  // undef
      assert(Platform.getDouble(null, result12 + 16) === 0.0) // undef
      assert(Platform.getDouble(null, result12 + 24) === 0.0) // undef

      // alloca13 - <4 x double>* - aggregate zero
      val alloca13 = LLJVMUtils.getMethod(clazz, "_alloca13")
      val result13 = alloca13.invoke(obj).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result13, 32))
      assert(Platform.getDouble(null, result13) === 0.0)
      assert(Platform.getDouble(null, result13 + 8) === 0.0)
      assert(Platform.getDouble(null, result13 + 16) === 0.0)
      assert(Platform.getDouble(null, result13 + 24) === 0.0)

      // alloca14 - <4 x double>*
      val alloca14 = LLJVMUtils.getMethod(clazz, "_alloca14", Seq(jLong.TYPE): _*)
      val args14 = ArrayUtils.addressOf(Array(3.0, 1.0, 2.0, 0.0))
      val result14 = alloca14.invoke(obj, Seq(new jLong(args14)): _*).asInstanceOf[Long]
      assert(VMemory.verifyStackAddress(result14, 32))
      assert(Platform.getDouble(null, result14) === 3.0)
      assert(Platform.getDouble(null, result14 + 8) === 1.0)
      assert(Platform.getDouble(null, result14 + 16) === 2.0)
      assert(Platform.getDouble(null, result14 + 24) === 0.0)

      // Inf/NaN for float values
      val plus_finf = LLJVMUtils.getMethod(clazz, "_plus_finf")
      assert(plus_finf.invoke(obj) === Float.PositiveInfinity)
      val minus_finf = LLJVMUtils.getMethod(clazz, "_minus_finf")
      assert(minus_finf.invoke(obj) === Float.NegativeInfinity)
      val fnan = LLJVMUtils.getMethod(clazz, "_fnan")
      assert(fnan.invoke(obj).asInstanceOf[Float].isNaN)

      // Inf/NaN for double values
      val plus_dinf = LLJVMUtils.getMethod(clazz, "_plus_dinf")
      assert(plus_dinf.invoke(obj) === Double.PositiveInfinity)
      val minus_dinf = LLJVMUtils.getMethod(clazz, "_minus_dinf")
      assert(minus_dinf.invoke(obj) === Double.NegativeInfinity)
      val dnan = LLJVMUtils.getMethod(clazz, "_dnan")
      assert(dnan.invoke(obj).asInstanceOf[Double].isNaN)
    }),

    ("fence", (clazz, obj) => {}),

    ("getelementptr", (clazz, obj) => {
      // getelementptr1 - i32*
      val getelementptr1 = LLJVMUtils.getMethod(clazz, "_getelementptr1", Seq(jLong.TYPE): _*)
      val addr1 = ArrayUtils.addressOf(Array(0, 2, 4, -6, 8, 10))
      assert(getelementptr1.invoke(obj, Seq(new jLong(addr1)): _*) === -6)

      // getelementptr2 - { i32, i32 }*
      val getelementptr2 = LLJVMUtils.getMethod(clazz, "_getelementptr2", Seq(jLong.TYPE): _*)
      val x2 = Array(1, 3, 5, 7)
      val addr2 = ArrayUtils.addressOf(x2)
      assert(getelementptr2.invoke(obj, Seq(new jLong(addr2)): _*) === 7)

      // getelementptr3 - { i32, { i32, i32 } }*
      val getelementptr3 = LLJVMUtils.getMethod(clazz, "_getelementptr3", Seq(jLong.TYPE): _*)
      val x3 = Array(0, 2, 4, 6, 8, 10)
      val addr3 = ArrayUtils.addressOf(x3)
      assert(getelementptr3.invoke(obj, Seq(new jLong(addr3)): _*) === 8)

      // getelementptr4 - { i32, [2 x i32] } }*
      val getelementptr4 = LLJVMUtils.getMethod(clazz, "_getelementptr4", Seq(jLong.TYPE): _*)
      assert(getelementptr4.invoke(obj, Seq(new jLong(addr3)): _*) === 8)

      // getelementptr5 - { i32, <2 x i32> } }*
      val getelementptr5 = LLJVMUtils.getMethod(clazz, "_getelementptr5", Seq(jLong.TYPE): _*)
      assert(getelementptr5.invoke(obj, Seq(new jLong(addr3)): _*) === 8)

      // getelementptr6 - i32*
      val x6 = Array(4, -1, 9, 3)
      val addr6 = ArrayUtils.addressOf(x6)
      val getelementptr6 = LLJVMUtils.getMethod(clazz, "_getelementptr6", Seq(jLong.TYPE): _*)
      assert(getelementptr6.invoke(obj, Seq(new jLong(addr6)): _*) === -1)

      // getelementptr7 - i32*
      val getelementptr7 = LLJVMUtils.getMethod(clazz, "_getelementptr7", Seq(jLong.TYPE): _*)
      assert(getelementptr7.invoke(obj, Seq(new jLong(addr6)): _*) === -1)

      // getelementptr8 - i32*
      val getelementptr8 = LLJVMUtils.getMethod(clazz, "_getelementptr8", Seq(jLong.TYPE): _*)
      assert(getelementptr8.invoke(obj, Seq(new jLong(addr6)): _*) === 4)

      // getelementptr9 - i32*
      val getelementptr9 = LLJVMUtils.getMethod(clazz, "_getelementptr9", Seq(jLong.TYPE): _*)
      assert(getelementptr9.invoke(obj, Seq(new jLong(addr6)): _*) === -1)

      // getelementptr10 - i32*
      val getelementptr10 = LLJVMUtils.getMethod(clazz, "_getelementptr10", Seq(jLong.TYPE): _*)
      assert(getelementptr10.invoke(obj, Seq(new jLong(addr6)): _*) === 3)

      // getelementptr11 - i1*
      val x11 = Array(false, false, true, false)
      val addr11 = ArrayUtils.addressOf(x11)
      val getelementptr11 = LLJVMUtils.getMethod(clazz, "_getelementptr11", Seq(jLong.TYPE): _*)
      assert(getelementptr11.invoke(obj, Seq(new jLong(addr11)): _*) === true)

      // getelementptr12 - i32*
      val x12 = Array(3, 2)
      val addr12 = ArrayUtils.addressOf(x12)
      val getelementptr12 = LLJVMUtils.getMethod(clazz, "_getelementptr12", Seq(jLong.TYPE): _*)
      assert(getelementptr12.invoke(obj, Seq(new jLong(addr12)): _*) === 3)

      // getelementptr13 - { i32, [5 x i32] } }*
      val argTypes13 = Seq(jLong.TYPE, jLong.TYPE)
      val getelementptr13 = LLJVMUtils.getMethod(clazz, "_getelementptr13", argTypes13: _*)
      val x13 = Array(0, 2, 4, 6, 8, 10)
      val addr13 = ArrayUtils.addressOf(x13)
      assert(getelementptr13.invoke(obj, Seq(new jLong(addr13), new jLong(2)): _*) === 6)

      // getelementptr14 - { i32, [5 x i32] } }*
      val argTypes14 = Seq(jLong.TYPE, jInt.TYPE)
      val getelementptr14 = LLJVMUtils.getMethod(clazz, "_getelementptr14", argTypes14: _*)
      assert(getelementptr14.invoke(obj, Seq(new jLong(addr13), new jInt(1)): _*) === 4)

      // getelementptr15 - [4 x i32]*
      val getelementptr15 = LLJVMUtils.getMethod(clazz, "_getelementptr15", Seq(jLong.TYPE): _*)
      val x15 = Array(1, 3, 5, 7)
      val addr15 = ArrayUtils.addressOf(x15)
      assert(getelementptr15.invoke(obj, Seq(new jLong(addr15)): _*) === 3)

      // getelementptr16 - [4 x i32]*
      val argTypes16 = Seq(jLong.TYPE, jLong.TYPE)
      val getelementptr16 = LLJVMUtils.getMethod(clazz, "_getelementptr16", argTypes16: _*)
      assert(getelementptr16.invoke(obj, Seq(new jLong(addr15), new jLong(2)): _*) === 5)

      // getelementptr17 - [4 x i32]*
      val argTypes17 = Seq(jLong.TYPE, jInt.TYPE)
      val getelementptr17 = LLJVMUtils.getMethod(clazz, "_getelementptr17", argTypes17: _*)
      assert(getelementptr17.invoke(obj, Seq(new jLong(addr15), new jInt(3)): _*) === 7)
    }),

    ("sext", (clazz, obj) => {
      val sext = LLJVMUtils.getMethod(clazz, "_sext", Seq(jInt.TYPE): _*)
      assert(sext.invoke(obj, Seq(new jInt(3)): _*) === 3L)
    }),

    ("zext", (clazz, obj) => {
      val zext = LLJVMUtils.getMethod(clazz, "_zext", Seq(jInt.TYPE): _*)
      assert(zext.invoke(obj, Seq(new jInt(3)): _*) === 3L)
    }),

    ("fptosi", (clazz, obj) => {
      val fptosi = LLJVMUtils.getMethod(clazz, "_fptosi", Seq(jFloat.TYPE): _*)
      assert(fptosi.invoke(obj, Seq(new jFloat(2.3)): _*) === 2)
    }),

    ("fptoui", (clazz, obj) => {
      val fptoui = LLJVMUtils.getMethod(clazz, "_fptoui", Seq(jFloat.TYPE): _*)
      assert(fptoui.invoke(obj, Seq(new jFloat(2.3)): _*) === 2)
    }),

    ("sitofp", (clazz, obj) => {
      val sitofp = LLJVMUtils.getMethod(clazz, "_sitofp", Seq(jInt.TYPE): _*)
      assert(sitofp.invoke(obj, Seq(new jInt(6)): _*) === 6.0f)
    }),

    ("uitofp", (clazz, obj) => {
      val uitofp = LLJVMUtils.getMethod(clazz, "_uitofp", Seq(jInt.TYPE): _*)
      assert(uitofp.invoke(obj, Seq(new jInt(6)): _*) === 6.0f)
    }),

    ("fpext", (clazz, obj) => {
      val fpext = LLJVMUtils.getMethod(clazz, "_fpext", Seq(jFloat.TYPE): _*)
      assert(fpext.invoke(obj, Seq(new jFloat(2.5f)): _*) === 2.5)
    }),

    ("ptrtoint", (clazz, obj) => {
      val ptrtoint = LLJVMUtils.getMethod(clazz, "_ptrtoint", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0, 1, 2))
      assert(ptrtoint.invoke(obj, Seq(new jLong(addr)): _*) === addr)
    }),

    ("inttoptr", (clazz, obj) => {
      val inttoptr = LLJVMUtils.getMethod(clazz, "_inttoptr", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0, 1, 2))
      assert(inttoptr.invoke(obj, Seq(new jLong(addr)): _*) === addr)
    }),

    ("trunc", (clazz, obj) => {
      val trunc = LLJVMUtils.getMethod(clazz, "_trunc", Seq(jLong.TYPE): _*)
      assert(trunc.invoke(obj, Seq(new jLong(-7)): _*) === -7.toShort)
    }),

    ("fptrunc", (clazz, obj) => {
      val fptrunc = LLJVMUtils.getMethod(clazz, "_fptrunc", Seq(jDouble.TYPE): _*)
      assert(fptrunc.invoke(obj, Seq(new jDouble(1.0)): _*) === 1.0f)
    }),

    ("bitcast", (clazz, obj) => {
      val bitcast = LLJVMUtils.getMethod(clazz, "_bitcast", Seq(jLong.TYPE): _*)
      val addr = ArrayUtils.addressOf(Array(0.0, 4.0, 8.0))
      assert(bitcast.invoke(obj, Seq(new jLong(addr)): _*) === addr)
    }),

    ("icmp", (clazz, obj) => {
      {
        val icmp1 = LLJVMUtils.getMethod(clazz, "_icmp1", Seq(jInt.TYPE, jInt.TYPE): _*)
        val args = Seq(new jInt(2), new jInt(3))
        assert(icmp1.invoke(obj, args: _*) === true)
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
        expected = true :: Nil)
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
        expected = false :: Nil)
      // left undef value case
      vectorTypeTest("_icmp10", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-1, 1, 1, -1))) :: Nil,
      expected = false :: true :: true :: false :: Nil)
      // right undef value case
      vectorTypeTest("_icmp11", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(-1, 1, 1, -1))) :: Nil,
      expected = true :: false :: false :: true :: Nil)
      // both undef value case
      vectorTypeTest("_icmp12", clazz, obj,
        args = new jLong(0L) :: Nil,
        expected = false :: false :: false :: false :: Nil)
    }),

    ("fcmp", (clazz, obj) => {
      val fcmp = LLJVMUtils.getMethod(clazz, "_fcmp", Seq(jFloat.TYPE, jFloat.TYPE): _*)
      val args = Seq(new jFloat(1.0f), new jFloat(3.0f))
      assert(fcmp.invoke(obj, args: _*) === false)
    }),

    ("phi", (clazz, obj) => {
      val phi = LLJVMUtils.getMethod(clazz, "_phi", Seq(jInt.TYPE): _*)
      assert(phi.invoke(obj, Seq(new jInt(1)): _*) === 3)
      assert(phi.invoke(obj, Seq(new jInt(0)): _*) === -3)
    }),

    ("select", (clazz, obj) => {
      val select = LLJVMUtils.getMethod(clazz, "_select", Seq(jBoolean.TYPE): _*)
      assert(select.invoke(obj, Seq(new jBoolean(true)): _*) === 1.0)
      assert(select.invoke(obj, Seq(new jBoolean(false)): _*) === 9.0)
    }),

    ("extractelement", (clazz, obj) => {
      val extractelement = LLJVMUtils.getMethod(clazz, "_extractelement", Seq(jLong.TYPE): _*)
      val ar = Array(-5, 6, -7, 8)
      assert(extractelement.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar))): _*) === -7)
    }),

    ("insertelement", (clazz, obj) => {
      // insertelement1 - <4 x i32>
      val insertelement1 = LLJVMUtils.getMethod(clazz, "_insertelement1", Seq(jLong.TYPE): _*)
      val ar1 = Array(-5, 6, -7, 8)
      val addr1 = ArrayUtils.addressOf(ar1)
      val resultAddr1 = insertelement1.invoke(obj, Seq(new jLong(addr1)): _*).asInstanceOf[Long]
      assert(resultAddr1 !== addr1)
      assert(ar1 === Array(-5, 6, -7, 8))
      assert(Platform.getInt(null, resultAddr1) === -5)
      assert(Platform.getInt(null, resultAddr1 + 4) === 6)
      assert(Platform.getInt(null, resultAddr1 + 8) === -7)
      assert(Platform.getInt(null, resultAddr1 + 12) === 4)

      // insertelement2 - <3 x i64>
      val insertelement2 = LLJVMUtils.getMethod(clazz, "_insertelement2", Seq(jLong.TYPE): _*)
      val ar2 = Array(1L, -3L, 0L)
      val addr2 = ArrayUtils.addressOf(ar2)
      assert(insertelement2.invoke(obj, Seq(new jLong(addr2)): _*) === 1L)
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
      vectorTypeTest("_shufflevector5", clazz, obj,
        args = new jLong(ArrayUtils.addressOf(Array(1L, 2L, 3L, 4L))) :: Nil,
        expected = 3L :: 4L :: 0L :: 0L :: Nil)
    }),

    ("extractvalue", (clazz, obj) => {
      // extractvalue1 -- { i32, i32 }
      val extractvalue1 = LLJVMUtils.getMethod(clazz, "_extractvalue1", Seq(jLong.TYPE): _*)
      val addr1 = ArrayUtils.addressOf(Array(3, -4))
      assert(extractvalue1.invoke(obj, Seq(new jLong(addr1)): _*) === -4)

      // extractvalue2 -- [2 x i64]
      val extractvalue2 = LLJVMUtils.getMethod(clazz, "_extractvalue2", Seq(jLong.TYPE): _*)
      val addr2 = ArrayUtils.addressOf(Array(8L, -6L))
      assert(extractvalue2.invoke(obj, Seq(new jLong(addr2)): _*) === 2)

      // extractvalue3 -- { i32, <2 x i32>, <3 x i32> }
      val extractvalue3 = LLJVMUtils.getMethod(clazz, "_extractvalue3", Seq(jLong.TYPE): _*)
      val ar3 = Array(1, 2, 3, 4, 5, 6)
      val addr3 = ArrayUtils.addressOf(ar3)
      assert(extractvalue3.invoke(obj, Seq(new jLong(addr3)): _*) === 21)

      // extractvalue4 -- { i32, [2 x i32], <2 x i32>, { i32, i32 } }
      val extractvalue4 = LLJVMUtils.getMethod(clazz, "_extractvalue4", Seq(jLong.TYPE): _*)
      val ar4 = Array(1, 2, 3, 4, 5, 6, 7)
      val addr4 = ArrayUtils.addressOf(ar4)
      assert(extractvalue4.invoke(obj, Seq(new jLong(addr4)): _*) === 28)
    }),

    ("insertvalue", (clazz, obj) => {
      // insertvalue1 -- { double, double }
      val insertvalue1 = LLJVMUtils.getMethod(clazz, "_insertvalue1", Seq(jLong.TYPE): _*)
      val ar1 = Array(1.0, 2.0)
      val addr1 = ArrayUtils.addressOf(ar1)
      val resultAddr1 = insertvalue1.invoke(obj, Seq(new jLong(addr1)): _*).asInstanceOf[Long]
      assert(resultAddr1 !== addr1)
      assert(ar1(0) === 1.0)
      assert(ar1(1) === 2.0)
      assert(Platform.getDouble(null, resultAddr1) === 1.0)
      assert(Platform.getDouble(null, resultAddr1 + 8) === 7.0)

      // insertvalue2 -- { i32, double, i32 }
      val insertvalue2 = LLJVMUtils.getMethod(clazz, "_insertvalue2", Seq(jLong.TYPE): _*)
      val ar2 = Array[Byte](16)
      val addr2 = ArrayUtils.addressOf(ar2)
      Platform.putInt(null, addr2, 1)
      Platform.putDouble(null, addr2 + 4, 4.0)
      Platform.putInt(null, addr2 + 12, 2)
      val resultAddr2 = insertvalue2.invoke(obj, Seq(new jLong(addr2)): _*).asInstanceOf[Long]
      assert(resultAddr2 !== addr2)
      assert(Platform.getInt(null, addr2) === 1)
      assert(Platform.getDouble(null, addr2 + 4) === 4.0)
      assert(Platform.getInt(null, addr2 + 12) === 2)
      assert(Platform.getInt(null, resultAddr2) === 5)
      assert(Platform.getDouble(null, resultAddr2 + 4) === 4.0)
      assert(Platform.getInt(null, resultAddr2 + 12) === 2)

      // insertvalue3 -- { i32, { i32 }, i32 }
      val insertvalue3 = LLJVMUtils.getMethod(
        clazz, "_insertvalue3", Seq(jLong.TYPE, jLong.TYPE): _*)
      val ar3_x = Array(4, -1, 9)
      val ar3_y = Array(7)
      val addr3_x = ArrayUtils.addressOf(ar3_x)
      val addr3_y = ArrayUtils.addressOf(ar3_y)
      val resultAddr3 = insertvalue3.invoke(
        obj, Seq(new jLong(addr3_x), new jLong(addr3_y)): _*).asInstanceOf[Long]
      assert(resultAddr3 !== addr3_x)
      assert(ar3_x(0) === 4)
      assert(ar3_x(1) === -1)
      assert(ar3_x(2) === 9)
      assert(Platform.getInt(null, resultAddr3) === 4)
      assert(Platform.getInt(null, resultAddr3 + 4) === 7)
      assert(Platform.getInt(null, resultAddr3 + 8) === 9)

      // insertvalue4 -- { i32, [2 x i32] }
      val ar4_x = Array(8, 1, 2)
      val addr4_x = ArrayUtils.addressOf(ar4_x)
      val ar4_y = Array(3, 4)
      val addr4_y = ArrayUtils.addressOf(ar4_y)
      val insertvalue4 = LLJVMUtils.getMethod(
        clazz, "_insertvalue4", Seq(jLong.TYPE, jLong.TYPE): _*)
      val resultAddr4 = insertvalue4.invoke(
        obj, Seq(new jLong(addr4_x), new jLong(addr4_y)): _*).asInstanceOf[Long]
      assert(resultAddr4 !== addr4_x)
      assert(resultAddr4 !== addr4_y)
      assert(ar4_x(0) === 8)
      assert(ar4_x(1) === 1)
      assert(ar4_x(2) === 2)
      assert(Platform.getInt(null, resultAddr4) === 8)
      assert(Platform.getInt(null, resultAddr4 + 4) === 3)
      assert(Platform.getInt(null, resultAddr4 + 8) === 4)

      // insertvalue5 -- { i32, [3 x i64] }
      val ar5 = Array[Byte](28)
      val ar5_ar = Array(1L, 2L, 3L)
      val addr5 = ArrayUtils.addressOf(ar5)
      Platform.putInt(null, addr5, 0)
      Platform.putLong(null, addr5 + 4, 1L)
      Platform.putLong(null, addr5 + 12, 2L)
      Platform.putLong(null, addr5 + 20, 3L)
      val insertvalue5 = LLJVMUtils.getMethod(clazz, "_insertvalue5", Seq(jLong.TYPE): _*)
      val resultAddr5 = insertvalue5.invoke(obj, Seq(new jLong(addr5)): _*).asInstanceOf[Long]
      assert(resultAddr5 !== addr5)
      assert(Platform.getInt(null, addr5) === 0)
      assert(Platform.getLong(null, addr5 + 4) === 1L)
      assert(Platform.getLong(null, addr5 + 12) === 2L)
      assert(Platform.getLong(null, addr5 + 20) === 3L)
      assert(Platform.getInt(null, resultAddr5) === 4)
      assert(Platform.getLong(null, resultAddr5 + 4) === 1L)
      assert(Platform.getLong(null, resultAddr5 + 12) === 2L)
      assert(Platform.getLong(null, resultAddr5 + 20) === 3L)

      // insertvalue6 -- { i32, i32, i32 }
      val insertvalue6 = LLJVMUtils.getMethod(clazz, "_insertvalue6")
      val resultAddr6 = insertvalue6.invoke(obj).asInstanceOf[Long]
      assert(Platform.getInt(null, resultAddr6 + 4) === 3)

      // insertvalue7 -- { i32, <3 x i32> }
      val x7 = Array(0, 1, 2, 3)
      val addr7 = ArrayUtils.addressOf(x7)
      val insertvalue7 = LLJVMUtils.getMethod(clazz, "_insertvalue7", Seq(jLong.TYPE): _*)
      val resultAddr7 = insertvalue7.invoke(obj, Seq(new jLong(addr7)): _*).asInstanceOf[Long]
      assert(resultAddr7 !== addr7)
      assert(x7(0) === 0)
      assert(x7(1) === 1)
      assert(x7(2) === 2)
      assert(x7(3) === 3)
      assert(Platform.getInt(null, resultAddr7) === 4)
      assert(Platform.getInt(null, resultAddr7 + 4) === 1)
      assert(Platform.getInt(null, resultAddr7 + 8) === 2)
      assert(Platform.getInt(null, resultAddr7 + 12) === 3)

      // insertvalue8 -- { i64, <2 x i32> }
      val ar8_x = Array[Byte](16)
      val addr8_x = ArrayUtils.addressOf(ar8_x)
      Platform.putLong(null, addr8_x, 3L)
      Platform.putInt(null, addr8_x + 8, 0)
      Platform.putInt(null, addr8_x + 12, 2)
      val ar8_y = Array(1, 3)
      val addr8_y = ArrayUtils.addressOf(ar8_y)
      val insertvalue8 = LLJVMUtils.getMethod(
        clazz, "_insertvalue8", Seq(jLong.TYPE, jLong.TYPE): _*)
      val resultAddr8 = insertvalue8.invoke(
        obj, Seq(new jLong(addr8_x), new jLong(addr8_y)): _*).asInstanceOf[Long]
      assert(resultAddr8 !== addr8_x)
      assert(resultAddr8 !== addr8_y)
      assert(Platform.getLong(null, addr8_x) === 3L)
      assert(Platform.getInt(null, addr8_x + 8) === 0)
      assert(Platform.getInt(null, addr8_x + 12) === 2)
      assert(Platform.getLong(null, resultAddr8) === 3L)
      assert(Platform.getInt(null, resultAddr8 + 8) === 1)
      assert(Platform.getInt(null, resultAddr8 + 12) === 3)

      // insertvalue9 -- [3 x i32]
      val ar9 = Array(2, 1, 0)
      val addr9 = ArrayUtils.addressOf(ar9)
      val insertvalue9 = LLJVMUtils.getMethod(clazz, "_insertvalue9", Seq(jLong.TYPE): _*)
      val resultAddr9 = insertvalue9.invoke(obj, Seq(new jLong(addr9)): _*).asInstanceOf[Long]
      assert(resultAddr9 !== addr9)
      assert(ar9(0) === 2)
      assert(ar9(1) === 1)
      assert(ar9(2) === 0)
      assert(Platform.getInt(null, resultAddr9) === 2)
      assert(Platform.getInt(null, resultAddr9 + 4) === 1)
      assert(Platform.getInt(null, resultAddr9 + 8) === 9)

      // insertvalue10 -- { i32, i32 }
      val ar10 = Array(1, 1)
      val addr10 = ArrayUtils.addressOf(ar10)
      val insertvalue10 = LLJVMUtils.getMethod(clazz, "_insertvalue10", Seq(jLong.TYPE): _*)
      assert(insertvalue10.invoke(obj, Seq(new jLong(addr10)): _*) === 9)

      // insertvalue11 -- { i32, <1 x i32>, double, [1 x float], { i64 } }
      val ar11 = Array[Byte](28)
      val addr11 = ArrayUtils.addressOf(ar11)
      Platform.putInt(null, addr11, 0)
      Platform.putInt(null, addr11 + 4, 0)
      Platform.putDouble(null, addr11 + 8, 0.0)
      Platform.putFloat(null, addr11 + 16, 0.0f)
      Platform.putLong(null, addr11 + 20, 0L)
      val ar11_vec = Array(5)
      val addr11_vec = ArrayUtils.addressOf(ar11_vec)
      val ar11_ar = Array(1.0f)
      val addr11_ar = ArrayUtils.addressOf(ar11_ar)
      val ar11_st = Array(4L)
      val addr11_st = ArrayUtils.addressOf(ar11_st)
      val argTypes11 = Seq(jLong.TYPE, jInt.TYPE, jLong.TYPE, jDouble.TYPE, jLong.TYPE, jLong.TYPE)
      val insertvalue11 = LLJVMUtils.getMethod(clazz, "_insertvalue11", argTypes11: _*)
      val args11 = Seq(new jLong(addr11), new jInt(1), new jLong(addr11_vec), new jDouble(3.0),
        new jLong(addr11_ar), new jLong(addr11_st))
      val resultAddr11 = insertvalue11.invoke(obj, args11: _*).asInstanceOf[Long]
      assert(resultAddr11 !== addr11)
      assert(Platform.getInt(null, addr11) === 0)
      assert(Platform.getInt(null, addr11 + 4) === 0)
      assert(Platform.getDouble(null, addr11 + 8) === 0.0)
      assert(Platform.getFloat(null, addr11 + 16) === 0.0f)
      assert(Platform.getLong(null, addr11 + 20) === 0L)
      assert(Platform.getInt(null, resultAddr11) === 1)
      assert(Platform.getInt(null, resultAddr11 + 4) === 5)
      assert(Platform.getDouble(null, resultAddr11 + 8) === 3.0)
      assert(Platform.getFloat(null, resultAddr11 + 16) === 1.0f)
      assert(Platform.getLong(null, resultAddr11 + 20) === 4L)

      // insertvalue12 -- { i32, <1 x i32>, double, [1 x float], { i64 } }
      val argTypes12 = Seq(jInt.TYPE, jLong.TYPE, jDouble.TYPE, jLong.TYPE, jLong.TYPE)
      val insertvalue12 = LLJVMUtils.getMethod(clazz, "_insertvalue12", argTypes12: _*)
      val args12 = Seq(new jInt(1), new jLong(addr11_vec), new jDouble(3.0), new jLong(addr11_ar),
        new jLong(addr11_st))
      val resultAddr12 = insertvalue12.invoke(obj, args12: _*).asInstanceOf[Long]
      assert(resultAddr12 !== addr11)
      assert(Platform.getInt(null, addr11) === 0)
      assert(Platform.getInt(null, addr11 + 4) === 0)
      assert(Platform.getDouble(null, addr11 + 8) === 0.0)
      assert(Platform.getFloat(null, addr11 + 16) === 0.0f)
      assert(Platform.getLong(null, addr11 + 20) === 0L)
      assert(Platform.getInt(null, resultAddr12) === 1)
      assert(Platform.getInt(null, resultAddr12 + 4) === 5)
      assert(Platform.getDouble(null, resultAddr12 + 8) === 3.0)
      assert(Platform.getFloat(null, resultAddr12 + 16) === 1.0f)
      assert(Platform.getLong(null, resultAddr12 + 20) === 4L)
    }),

    ("atomicrmw", (clazz, obj) => {
      // add
      val add1 = LLJVMUtils.getMethod(clazz, "_add1", Seq(jLong.TYPE): _*)
      val ar1 = Array(3)
      assert(add1.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar1))): _*) === 3)
      assert(ar1(0) === 4)

      val add2 = LLJVMUtils.getMethod(clazz, "_add2", Seq(jLong.TYPE, jInt.TYPE): _*)
      assert(add2.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar1)), new jInt(2)): _*) === 4)
      assert(ar1(0) === 6)

      val add3 = LLJVMUtils.getMethod(clazz, "_add3", Seq(jLong.TYPE): _*)
      assert(add3.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar1))): _*) === 6)
      assert(ar1(0) === 6)

      // sub
      val sub = LLJVMUtils.getMethod(clazz, "_sub", Seq(jLong.TYPE): _*)
      val ar2 = Array(3)
      assert(sub.invoke(obj, Seq(new jLong(ArrayUtils.addressOf(ar2))): _*) === 3)
      assert(ar2(0) === 2)

      // TODO: Adds tests for instructions below:
      // atomicrmw (xchg, and, nand, or, xor, max, min, umax, umin)
    }),

    ("intrinsics", (clazz, obj) => {
      // math intrinsic functions
      Seq(("log2", 32.0f, 5.0f), ("round", 3.8f, 4.0f)).foreach { case (func, input, expected) =>
        val method1 = LLJVMUtils.getMethod(clazz, s"_${func}_f32", Seq(jFloat.TYPE): _*)
        assert(method1.invoke(obj, Seq(new jFloat(input)): _*) === expected)
        val method2 = LLJVMUtils.getMethod(clazz, s"_${func}_f64", Seq(jDouble.TYPE): _*)
        assert(method2.invoke(obj, Seq(new jDouble(input)): _*) === expected)
      }
    })
  ).foreach { case (inst, testFunc) =>

    test(s"basic LLJVM translation test - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/supported/$inst.bc")
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

  Seq("alloca1", "alloca2", "getelementptr1", "atomicrmw1", "atomicrmw2", "atomicrmw3",
    "atomicrmw4", "atomicrmw5", "atomicrmw6", "atomicrmw7", "atomicrmw8", "atomicrmw9",
    "cmpxchg"
    // TODO: Adds tests for instructions below:
    // "addrspacecast", "indirectbr", "va_arg"
  ).foreach { inst =>
    test(s"basic LLJVM translation test - unsupported - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/unsupported/$inst.bc")
      intercept[LLJVMRuntimeException] {
        TestUtils.asJVMAssemblyCode(bitcode)
      }
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
      val bitcode = TestUtils.resourceToBytes(s"$basePath/unsupported/$inst.bc")
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
    val path = s"$basePath/unsupported/exception.bc"
    val bitcode = TestUtils.resourceToBytes(path)
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.asJVMAssemblyCode(bitcode)
    }.getMessage
    assert(errMsg.contains("Unsupported") && !errMsg.contains("!!UNREACHABLE!!") )
    logDebug(
      s"""
         |========== Source Code ==========
         |${new String(TestUtils.resourceToBytes(path), Charsets.UTF_8)}
         |========== LLVM Assembly Code =========
         |${TestUtils.asLLVMAssemblyCode(bitcode)}
         |========== JVM Assembly Code =========
         |<unsupported>
       """.stripMargin)
  }
}
