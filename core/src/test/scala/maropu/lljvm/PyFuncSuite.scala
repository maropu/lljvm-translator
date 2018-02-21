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

import java.lang.{Long => jLong, Float => jFloat, Integer => jInt, Double => jDouble}

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import lljvm.unsafe.Platform

class PyFuncSuite extends FunSuite with BeforeAndAfterAll {

  private val basePath = "llvm-pyfunc-bitcode"

  // scalastyle:off line.size.limit

  test("x + y") {
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc1-cfunc-int32.bc",
      source = s"$basePath/pyfunc1.py",
      functionName = "_cfunc__ZN7pyfunc111pyfunc1_241Eii",
      signature = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(4), new jInt(5)),
      expected = Some(9)
    )
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc1-cfunc-int64.bc",
      source = s"$basePath/pyfunc1.py",
      functionName = "_cfunc__ZN7pyfunc111pyfunc1_242Exx",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(3), new jLong(2)),
      expected = Some(5)
    )
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc1-cfunc-float32.bc",
      source = s"$basePath/pyfunc1.py",
      functionName = "_cfunc__ZN7pyfunc111pyfunc1_243Eff",
      signature = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(7.0f)),
      expected = Some(8.0f)
    )
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc1-cfunc-float64.bc",
      source = s"$basePath/pyfunc1.py",
      functionName = "_cfunc__ZN7pyfunc111pyfunc1_244Edd",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(2.0), new jDouble(2.0)),
      expected = Some(4.0)
    )
  }

  test("math.pow(x, y)") {
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc2-cfunc-float32.bc",
      source = s"$basePath/pyfunc2.py",
      functionName = "_cfunc__ZN7pyfunc211pyfunc2_245Eff",
      signature = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(4.0f)),
      expected = Some(1.0f)
    )
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc2-cfunc-float64.bc",
      source = s"$basePath/pyfunc2.py",
      functionName = "_cfunc__ZN7pyfunc211pyfunc2_246Edd",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(3.0), new jDouble(2.0)),
      expected = Some(9.0)
    )
  }

  test("2 * y + math.log10(x)") {
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc3-cfunc-float32.bc",
      source = s"$basePath/pyfunc3.py",
      functionName = "_cfunc__ZN7pyfunc311pyfunc3_247Eff",
      signature = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(100.0f), new jFloat(1.0f)),
      expected = Some(4.0f)
    )
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc3-cfunc-float64.bc",
      source = s"$basePath/pyfunc3.py",
      functionName = "_cfunc__ZN7pyfunc311pyfunc3_248Edd",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(1000.0), new jDouble(3.0)),
      expected = Some(9.0)
    )
  }

  test("simple if") {
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc4-cfunc-int32.bc",
      source = s"$basePath/pyfunc4.py",
      functionName = "_cfunc__ZN7pyfunc411pyfunc4_249Ei",
      signature = Seq(jInt.TYPE),
      arguments = Seq(new jInt(1)),
      expected = Some(0)
    )
  }

  test("ternary operator") {
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc5-cfunc-int32.bc",
      source = s"$basePath/pyfunc5.py",
      functionName = "_cfunc__ZN7pyfunc512pyfunc5_2410Ei",
      signature = Seq(jInt.TYPE),
      arguments = Seq(new jInt(0)),
      expected = Some(1)
    )
  }

  test("sum by simple for") {
    val floatArray1 = Array(1.0, 8.0, 2.0, 3.0, 4.0, 1.0, 1.0, 2.0).map(_.toFloat)
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc6_for1-cfunc-float32.bc",
      source = s"$basePath/pyfunc6_for1.py",
      functionName = "_cfunc__ZN12pyfunc6_for117pyfunc6_for1_2411E5ArrayIfLi1E1A7mutable7alignedEi",
      signature = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(pyArray1.addressOf(floatArray1)), new jInt(floatArray1.size)),
      expected = Some(22.0)
    )
    val doubleArray1 = Array(2.0, 1.0, 5.0, 13.0, 4.0)
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc6_for1-cfunc-float64.bc",
      source = s"$basePath/pyfunc6_for1.py",
      functionName = "_cfunc__ZN12pyfunc6_for117pyfunc6_for1_2412E5ArrayIdLi1E1A7mutable7alignedEi",
      signature = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(pyArray1.addressOf(doubleArray1)), new jInt(doubleArray1.size)),
      expected = Some(25.0)
    )
    val floatArray2 = Array(4.0, -5.0, 2.0, 8.0).map(_.toFloat)
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc6_for2-cfunc-float32.bc",
      source = s"$basePath/pyfunc6_for2.py",
      functionName =
        "_cfunc__ZN12pyfunc6_for217pyfunc6_for2_2413E5ArrayIfLi1E1A7mutable7alignedE",
      signature = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.addressOf(floatArray2))),
      expected = Some(9.0)
    )
    val doubleArray2 = Array(5.0, 3.0, -9.0, 5.0, 1.0, 2.0, 2.0, 9.0, 1.0, 3.0)
    TestUtils.doTest(
      bitcode = s"$basePath/pyfunc6_for2-cfunc-float64.bc",
      source = s"$basePath/pyfunc6_for2.py",
      functionName =
        "_cfunc__ZN12pyfunc6_for217pyfunc6_for2_2414E5ArrayIdLi1E1A7mutable7alignedE",
      signature = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.addressOf(doubleArray2))),
      expected = Some(22.0)
    )
  }

  private var pyArray1: PyArrayHolder = _
  private var pyArray2: PyArrayHolder = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    pyArray1 = new PyArrayHolder()
    pyArray2 = new PyArrayHolder()
  }

  override def afterAll(): Unit = {
    pyArray1.close()
    pyArray2.close()
    super.afterAll()
  }

  test("numpy.power") {
    val floatX = Array(1.0f, 2.0f, 3.0f, 4.0f)
    val floatY = Array(1.0f, 2.0f, 3.0f, 4.0f)
    val result1 = TestUtils.doTest[Long](
      bitcode = s"$basePath/pyfunc8-cfunc-float32.bc",
      source = s"$basePath/pyfunc8.py",
      functionName = "_cfunc__ZN7pyfunc812pyfunc8_2415E5ArrayIfLi1E1A7mutable7alignedE5ArrayIfLi1E1A7mutable7alignedE",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.addressOf(floatX)), new jLong(pyArray2.addressOf(floatY)))
    )
    val resultData1 = Platform.getLong(null, result1 + 32)
    assert(Platform.getFloat(null, resultData1) === 1.0f)
    assert(Platform.getFloat(null, resultData1 + 4) === 8.0f)
    assert(Platform.getFloat(null, resultData1 + 8) === 27.0f)
    assert(Platform.getFloat(null, resultData1 + 12) === 64.0f)

    val doubleX = Array(1.0, 2.0, 3.0, 4.0)
    val doubleY = Array(1.0, 2.0, 3.0, 4.0)
    val result2 = TestUtils.doTest[Long](
      bitcode = s"$basePath/pyfunc8-cfunc-float64.bc",
      source = s"$basePath/pyfunc8.py",
      functionName = "_cfunc__ZN7pyfunc812pyfunc8_2418E5ArrayIdLi1E1A7mutable7alignedE5ArrayIdLi1E1A7mutable7alignedE",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.addressOf(doubleX)), new jLong(pyArray2.addressOf(doubleY)))
    )
    val resultData2 = Platform.getLong(null, result2 + 32)
    assert(Platform.getDouble(null, resultData2) === 1.0)
    assert(Platform.getDouble(null, resultData2 + 8) === 8.0)
    assert(Platform.getDouble(null, resultData2 + 16) === 27.0)
    assert(Platform.getDouble(null, resultData2 + 24) === 64.0)
  }

  // TODO: Currently, we cannot use 2-d arrays in the gen'd code
  ignore("logistic regression") {}

  // scalastyle:on line.size.limit
}
