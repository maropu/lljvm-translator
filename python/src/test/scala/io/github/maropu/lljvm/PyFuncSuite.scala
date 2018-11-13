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

import java.lang.{Double => jDouble, Float => jFloat, Integer => jInt, Long => jLong}
import java.lang.reflect.InvocationTargetException

import io.github.maropu.lljvm.util.python.PyArrayHolder

class PyFuncSuite extends LLJVMFuncSuite {

  private val basePath = "pyfunc"

  private var pyArray1: PyArrayHolder = _
  private var pyArray2: PyArrayHolder = _
  private var pyArray3: PyArrayHolder = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    pyArray1 = new PyArrayHolder()
    pyArray2 = new PyArrayHolder()
    pyArray3 = new PyArrayHolder()
  }

  override def afterAll(): Unit = {
    pyArray1.close()
    pyArray2.close()
    pyArray3.close()
    super.afterAll()
  }

  test("add") {
    // int32(int32, int32)
    TestUtils.doTest(
      bitcode = s"$basePath/add_test-cfunc-int32.bc",
      source = s"$basePath/add_test.py",
      argTypes = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(4), new jInt(5)),
      expected = Some(9)
    )

    // int64(int64, int64)
    TestUtils.doTest(
      bitcode = s"$basePath/add_test-cfunc-int64.bc",
      source = s"$basePath/add_test.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(3), new jLong(2)),
      expected = Some(5)
    )

    // float32(float32, float32)
    TestUtils.doTest(
      bitcode = s"$basePath/add_test-cfunc-float32.bc",
      source = s"$basePath/add_test.py",
      argTypes = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(7.0f)),
      expected = Some(8.0f)
    )

    // float64(float64, float64)
    TestUtils.doTest(
      bitcode = s"$basePath/add_test-cfunc-float64.bc",
      source = s"$basePath/add_test.py",
      argTypes = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(2.0), new jDouble(2.0)),
      expected = Some(4.0)
    )
  }

  test("pow") {
    // float32(float32, float32)
    TestUtils.doTest(
      bitcode = s"$basePath/math_pow_test-cfunc-float32.bc",
      source = s"$basePath/math_pow_test.py",
      argTypes = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(4.0f)),
      expected = Some(1.0f)
    )

    // float64(float64, float64)
    TestUtils.doTest(
      bitcode = s"$basePath/math_pow_test-cfunc-float64.bc",
      source = s"$basePath/math_pow_test.py",
      argTypes = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(3.0), new jDouble(2.0)),
      expected = Some(9.0)
    )
  }

  test("log10") {
    // float32(float32, float32)
    TestUtils.doTest(
      bitcode = s"$basePath/math_log10_test-cfunc-float32.bc",
      source = s"$basePath/math_log10_test.py",
      argTypes = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(100.0f), new jFloat(1.0f)),
      expected = Some(4.0f)
    )

    // float64(float64, float64)
    TestUtils.doTest(
      bitcode = s"$basePath/math_log10_test-cfunc-float64.bc",
      source = s"$basePath/math_log10_test.py",
      argTypes = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(1000.0), new jDouble(3.0)),
      expected = Some(9.0)
    )
  }

  test("if") {
    // int32(int32)
    TestUtils.doTest(
      bitcode = s"$basePath/if1_test-cfunc-int32.bc",
      source = s"$basePath/if1_test.py",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(1)),
      expected = Some(0)
    )

    // int32(int32)
    TestUtils.doTest(
      bitcode = s"$basePath/if2_test-cfunc-int32.bc",
      source = s"$basePath/if2_test.py",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(0)),
      expected = Some(1)
    )
  }

  test("function call chains") {
    // float32(float32, float32)
    TestUtils.doTest(
      bitcode = s"$basePath/func_call_test-cfunc-float32.bc",
      source = s"$basePath/func_call_test.py",
      argTypes = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(7.0f)),
      expected = Some(15.0f)
    )

    // float64(float64, float64)
    TestUtils.doTest(
      bitcode = s"$basePath/func_call_test-cfunc-float64.bc",
      source = s"$basePath/func_call_test.py",
      argTypes = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(2.0), new jDouble(2.0)),
      expected = Some(8.0)
    )
  }

  def intArray(addr: Long): Array[Int] = {
    new PyArrayHolder(addr).intArray()
  }

  def longArray(addr: Long): Array[Long] = {
    new PyArrayHolder(addr).longArray()
  }

  def floatArray(addr: Long): Array[Float] = {
    new PyArrayHolder(addr).floatArray()
  }

  def doubleArray(addr: Long): Array[Double] = {
    new PyArrayHolder(addr).doubleArray()
  }

  test("transpose") {
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f)).reshape(2, 2)
    val result1 = TestUtils.doTest[Long](
      bitcode = s"$basePath/transpose1_test-cfunc-float32.bc",
      source = s"$basePath/transpose1_test.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()))
    )
    val transposed1 = new PyArrayHolder(result1, 2)
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[8,4]")
      .forall(floatX.toDebugString.contains))
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[4,8]")
      .forall(transposed1.toDebugString.contains))
    assert(transposed1.floatArray() === Seq(1.0f, 2.0f, 3.0f, 4.0f))

    // val result2 = TestUtils.doTest[Long](
    //   bitcode = s"$basePath/transpose2_test-cfunc-float32.bc",
    //   source = s"$basePath/transpose2_test.py",
    //   argTypes = Seq(jLong.TYPE),
    //   arguments = Seq(new jLong(floatX.addr()))
    // )
    // val transposed2 = new PyArrayHolder(result2, 2)
    // assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[4,8]")
    //   .forall(transposed2.toDebugString.contains))
    // assert(transposed2.floatArray() === Seq(1.0f, 2.0f, 3.0f, 4.0f))

    // val doubleX = pyArray1.`with`(Array(6.0, 5.0, 4.0, 3.0, 2.0, 1.0)).reshape(3, 2)
    // val result3 = TestUtils.doTest[Long](
    //   bitcode = s"$basePath/transpose1_test-cfunc-float64.bc",
    //   source = s"$basePath/transpose1_test.py",
    //   argTypes = Seq(jLong.TYPE),
    //   arguments = Seq(new jLong(doubleX.addr()))
    // )
    // val transposed3 = new PyArrayHolder(result3, 2)
    // assert(Seq("2d python array", "nitem=6", "itemsize=8", "shape=[3,2]", "stride=[16,8]")
    //   .forall(doubleX.toDebugString.contains))
    // assert(Seq("2d python array", "nitem=6", "itemsize=8", "shape=[3,2]", "stride=[8,16]")
    //   .forall(transposed3.toDebugString.contains))
    // assert(transposed3.doubleArray() === Seq(6.0, 5.0, 4.0, 3.0, 2.0, 1.0))

    // val result4 = TestUtils.doTest[Long](
    //   bitcode = s"$basePath/transpose2_test-cfunc-float64.bc",
    //   source = s"$basePath/transpose2_test.py",
    //   argTypes = Seq(jLong.TYPE),
    //   arguments = Seq(new jLong(doubleX.addr()))
    // )
    // val transposed4 = new PyArrayHolder(result4, 2)
    // assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[3,2]", "stride=[4,12]")
    //   .forall(transposed4.toDebugString.contains))
    // assert(transposed4.doubleArray() === Seq(6.0, 5.0, 4.0, 3.0, 2.0, 1.0))
  }

  test("loop") {
    // float32(float32[:]
    val floatArray1 = Array(1.0, 8.0, 2.0, 3.0, 4.0, 1.0, 1.0, 2.0).map(_.toFloat)
    TestUtils.doTest(
      bitcode = s"$basePath/for1_test-cfunc-float32.bc",
      source = s"$basePath/for1_test.py",
      argTypes = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(floatArray1).addr()), new jInt(floatArray1.size)),
      expected = Some(22.0)
    )

    // float64(float64[:]
    val doubleArray1 = Array(2.0, 1.0, 5.0, 13.0, 4.0)
    TestUtils.doTest(
      bitcode = s"$basePath/for1_test-cfunc-float64.bc",
      source = s"$basePath/for1_test.py",
      argTypes = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(doubleArray1).addr()), new jInt(doubleArray1.size)),
      expected = Some(25.0)
    )

    // float32(float32[:]
    val floatArray2 = Array(4.0, -5.0, 2.0, 8.0).map(_.toFloat)
    TestUtils.doTest(
      bitcode = s"$basePath/for2_test-cfunc-float32.bc",
      source = s"$basePath/for2_test.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(floatArray2).addr())),
      expected = Some(9.0)
    )

    // float64(float64[:]
    val doubleArray2 = Array(5.0, 3.0, -9.0, 5.0, 1.0, 2.0, 2.0, 9.0, 1.0, 3.0)
    TestUtils.doTest(
      bitcode = s"$basePath/for2_test-cfunc-float64.bc",
      source = s"$basePath/for2_test.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(doubleArray2).addr())),
      expected = Some(22.0)
    )
  }

  test("range") {
    // float32[:](float32[:], int32)
    val floatX = pyArray1.`with`(Array(3.0f, 5.0f, 3.0f, 4.0f))
    val result1 = TestUtils.doTest[Long](
      bitcode = s"$basePath/range_test-cfunc-float32-1d.bc",
      source = s"$basePath/range_test.py",
      argTypes = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(floatX.addr()), new jInt(3))
    )
    assert(floatArray(result1) === Seq(0.0f, 2.0f, 0.0f, 1.0f))

    // float32[:,:](float32[:,:], int32)
    val result2 = TestUtils.doTest[Long](
      bitcode = s"$basePath/range_test-cfunc-float32-2d.bc",
      source = s"$basePath/range_test.py",
      argTypes = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(floatX.reshape(4, 1).addr()), new jInt(1))
    )
    assert(floatArray(result2) === Seq(-1.0f, 1.0f, -1.0f, 0.0f))

    // float64[:](float64[:], int32)
    val doubleX = pyArray1.`with`(Array(0.0, 3.0, 8.0, 9.0))
    val result3 = TestUtils.doTest[Long](
      bitcode = s"$basePath/range_test-cfunc-float64-1d.bc",
      source = s"$basePath/range_test.py",
      argTypes = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(doubleX.addr()), new jInt(6))
    )
    assert(doubleArray(result3) === Seq(-6.0, -3.0, 2.0, 3.0))

    // float64[:,:](float64[:,:], int32)
    val result4 = TestUtils.doTest[Long](
      bitcode = s"$basePath/range_test-cfunc-float64-2d.bc",
      source = s"$basePath/range_test.py",
      argTypes = Seq(jLong.TYPE, jInt.TYPE),
      arguments = Seq(new jLong(doubleX.reshape(4, 1).addr()), new jInt(1))
    )
    assert(doubleArray(result4) === Seq(-7.0, -4.0, 1.0, 2.0))
  }

  ignore("NumPy arange") {
    // int64[:,:]()
    // TODO: !!UNREACHABLE!! (file=../types.cc line=52) Unsupported integer width: Bits=63
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/numpy_arange_test-cfunc-int64.bc",
      source = s"$basePath/numpy_arange_test.py"
    )
    assert(longArray(result) === Seq(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L))
  }

  test("NumPy power") {
    // float32[:](float32[:], float32[:])
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f))
    val floatY = pyArray2.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f))
    val result1 = TestUtils.doTest[Long](
      bitcode = s"$basePath/numpy_power_test-cfunc-float32.bc",
      source = s"$basePath/numpy_power_test.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()), new jLong(floatY.addr()))
    )
    assert(floatArray(result1) === Seq(1.0f, 8.0f, 27.0f, 64.0f))

    // float64[:](float64[:], float64[:])
    val doubleX = pyArray1.`with`(Array(1.0, 2.0, 3.0, 4.0))
    val doubleY = pyArray2.`with`(Array(1.0, 2.0, 3.0, 4.0))
    val result2 = TestUtils.doTest[Long](
      bitcode = s"$basePath/numpy_power_test-cfunc-float64.bc",
      source = s"$basePath/numpy_power_test.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()), new jLong(doubleY.addr()))
    )
    assert(doubleArray(result2) === Seq(1.0, 8.0, 27.0, 64.0))
  }

  test("NumPy dot - vv") { // Vector * Vector case
    Seq(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384).foreach { n =>
      // float32[:,:](float32[:,:], float32[:,:])
      // TODO: reconsiders the current API design: `.reshape(4, 1)` != `.reshape(4)`
      val floatX = pyArray1.`with`(Array.fill[Float](n)(1.0f)).reshape(n)
      val floatY = pyArray2.`with`(Array.fill[Float](n)(2.0f)).reshape(n)
      TestUtils.doTest[Float](
        bitcode = s"$basePath/numpy_dot_test-cfunc-vv-float32.bc",
        source = s"$basePath/numpy_dot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(floatX.addr()), new jLong(floatY.addr())),
        expected = Some(2.0f * n)
      )

      // float64[:,:](float64[:,:], float64[:,:])
      val doubleX = pyArray1.`with`(Array.fill[Double](n)(3.0)).reshape(n)
      val doubleY = pyArray2.`with`(Array.fill[Double](n)(1.0)).reshape(n)
      TestUtils.doTest[Double](
        bitcode = s"$basePath/numpy_dot_test-cfunc-vv-float64.bc",
        source = s"$basePath/numpy_dot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(doubleX.addr()), new jLong(doubleY.addr())),
        expected = Some(3.0 * n)
      )
    }
  }

  test("NumPy dot - mv") { // Matrix * Vector case
    Seq(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048 /*, 4096 */).foreach { n =>
      // float32[:](float32[:,:], float32[:])
      // TODO: if the length is 8192+, it throws an exception because stack is not enough
      val floatX = pyArray1.`with`(Array.fill[Float](n * n)(1.0f)).reshape(n, n)
      val floatY = pyArray2.`with`(Array.fill[Float](n)(2.0f)).reshape(n)
      val result1 = TestUtils.doTest[Long](
        bitcode = s"$basePath/numpy_dot_test-cfunc-mv-float32.bc",
        source = s"$basePath/numpy_dot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(floatX.addr()), new jLong(floatY.addr()))
      )
      assert(floatArray(result1) === Array.fill[Float](n)(2.0f * n))

      // float64[:](float64[:,:], float64[:])
      val doubleX = pyArray1.`with`(Array.fill[Double](n * n)(3.0)).reshape(n, n)
      val doubleY = pyArray2.`with`(Array.fill[Double](n)(1.0)).reshape(n)
      val result2 = TestUtils.doTest[Long](
        bitcode = s"$basePath/numpy_dot_test-cfunc-mv-float64.bc",
        source = s"$basePath/numpy_dot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(doubleX.addr()), new jLong(doubleY.addr()))
      )
      assert(doubleArray(result2) === Array.fill[Double](n)(3.0 * n))
    }
  }

  test("NumPy dot - mm") { // Matrix * Matrix case
    Seq(2, 4, 8, 16, 32, 64, 128, 256, 1024, 2048).foreach { n =>
      // float32(float32[:], float32[:])
      // TODO: if the length is 4096+, it throws an exception because stack is not enough
      val floatX = pyArray1.`with`(Array.fill[Float](n * n)(1.0f)).reshape(n, n)
      val floatY = pyArray2.`with`(Array.fill[Float](n * n)(1.0f)).reshape(n, n)
      val result1 = TestUtils.doTest[Long](
        bitcode = s"$basePath/numpy_dot_test-cfunc-mm-float32.bc",
        source = s"$basePath/numpy_dot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(floatX.addr()), new jLong(floatY.addr()))
      )
      assert(floatArray(result1) === Array.fill[Float](n * n)(1.0f * n))

      // float64(float64[:], float64[:])
      val doubleX = pyArray1.`with`(Array.fill[Double](n * n)(1.0)).reshape(n, n)
      val doubleY = pyArray2.`with`(Array.fill[Double](n * n)(1.0)).reshape(n, n)
      val result2 = TestUtils.doTest[Long](
        bitcode = s"$basePath/numpy_dot_test-cfunc-mm-float64.bc",
        source = s"$basePath/numpy_dot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(doubleX.addr()), new jLong(doubleY.addr()))
      )
      assert(doubleArray(result2) === Array.fill[Double](n * n)(1.0 * n))
    }
  }

  test("NumPy dot - throws an exception when hitting incompatible shapes") {
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f)).reshape(4, 1)
    val floatY = pyArray2.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f)).reshape(2, 2)
    val errMsg = intercept[InvocationTargetException] {
      val method = LLJVMUtils.getMethod(
        TestUtils.loadClassFromBitcodeInResource(s"$basePath/numpy_dot_test-cfunc-mm-float32.bc"),
        jLong.TYPE, jLong.TYPE)
      method.invoke(null, new jLong(floatX.addr()), new jLong(floatY.addr()))
    }.getCause.getMessage
    assert(errMsg.contains("Numba runtime exception <Numba C callback 'numpy_dot_test'>"))
  }

  ignore("NumPy random") {
    val rvalues1 = (0 until 100).map { _ =>
      // float64()
      TestUtils.doTest[Double](
        bitcode = s"$basePath/numpy_random1_test-cfunc-float64.bc",
        source = s"$basePath/numpy_random1_test.py"
      )
    }
    // Checks if generated values are different from each other
    rvalues1.indices.foreach { x =>
      val value = rvalues1(x)
      (x + 1 until rvalues1.size).foreach { y =>
        assert(Math.abs(value - rvalues1(y)) > Double.MinValue)
      }
    }

    // float64[:](int64)
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/numpy_random2_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random2_test.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(100))
    )
    val rvalues2 = new PyArrayHolder(result).doubleArray()
    (0 until rvalues2.size).foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }
  }

  ignore("numba - linear regression (NEEDS TO BE FIXED)") {
    // float64[:](float64[:], float64[:,:], float64[:], int64, float64)
    val doubleX = pyArray1.`with`(Array(1.0, 1.0)).reshape(2, 1)
    val doubleY = pyArray2.`with`(Array(1.0, 1.0, 1.0, 1.0)).reshape(2, 2)
    val doubleZ = pyArray3.`with`(Array(1.0, 1.0)).reshape(2, 1)
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/linear_regression-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/linear_regression.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE, jLong.TYPE, jLong.TYPE, jDouble.TYPE),
      arguments = Seq(
        new jLong(doubleX.addr()), // Y
        new jLong(doubleY.addr()), // X
        new jLong(doubleZ.addr()), // w
        new jLong(50),             // iterations
        new jDouble(0.1))          // alphaN
    )
    assert(doubleArray(result) === Seq(0.0, 0.0))
  }

  ignore("numba - logistic regression (NEEDS TO BE FIXED)") {
    // float64[:](float64[:], float64[:,:], float64[:], int64)
    val doubleX = pyArray1.`with`(Array(1.0, 1.0)).reshape(2, 1)
    val doubleY = pyArray2.`with`(Array(1.0, 1.0, 1.0, 1.0)).reshape(2, 2)
    val doubleZ = pyArray3.`with`(Array(1.0, 1.0)).reshape(2, 1)
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/logistic_regression-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/logistic_regression.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE, jLong.TYPE, jLong.TYPE),
      arguments = Seq(
        new jLong(doubleX.addr()), // Y
        new jLong(doubleY.addr()), // X
        new jLong(doubleZ.addr()), // w
        new jLong(100))            // iterations
    )
    assert(doubleArray(result) === Seq(0.0, 0.0))
  }

  ignore("numba - blur image (NEEDS TO BE FIXED)") {
    // float64[:,:](float64[:,:], float64[:,:])
    val doubleX = pyArray1.`with`(Array(1.0, 1.0, 1.0, 1.0)).reshape(2, 2)
    val doubleY = pyArray2.`with`(Array(1.0, 1.0, 1.0, 1.0)).reshape(2, 2)
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/filter2d-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/blur_image.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(
        new jLong(doubleX.addr()), // image
        new jLong(doubleY.addr())) // filt
    )
    assert(doubleArray(result) === Seq(0.0, 0.0, 0.0, 0.0))
  }

  test("NumPy - bubble sort") {
    // void(float32[:])
    val floatX = new PyArrayHolder().`with`(Array(4.0f, 2.0f, 1.0f, 3.0f))
    TestUtils.doTestWithFuncName[Unit](
      bitcode = s"$basePath/bubblesort-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/bubblesort.py",
      funcName =
        "_cfunc__ZN14numba_examples10bubblesort15bubblesort_2462E5ArrayIfLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()))
    )
    assert(floatX.floatArray() === Seq(1.0f, 2.0f, 3.0f, 4.0f))

    // void(float64[:])
    val doubleX = new PyArrayHolder().`with`(Array(4.0, 3.0, 1.0, 2.0))
    TestUtils.doTestWithFuncName[Unit](
      bitcode = s"$basePath/bubblesort-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/bubblesort.py",
      funcName =
        "_cfunc__ZN14numba_examples10bubblesort15bubblesort_2463E5ArrayIdLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()))
    )
    assert(doubleX.doubleArray() === Seq(1.0, 2.0, 3.0, 4.0))
  }

  ignore("numba - kernel density estimation (NEEDS TO BE FIXED)") {
    // float32(float32[:])
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f))
    TestUtils.doTest[Float](
      bitcode = s"$basePath/kde-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/kernel_density_estimation.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr())),
      expected = Some(0.0f)
    )

    // float64(float64[:])
    val doubleX = pyArray1.`with`(Array(1.0, 2.0, 3.0, 4.0))
    TestUtils.doTest[Double](
      bitcode = s"$basePath/kde-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/kernel_density_estimation.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr())),
      expected = Some(0.0)
    )
  }

  ignore("numba - laplace2d (NEEDS TO BE FIXED)") {
    // float32(float32[:,:], float32[:,:])
    val floatX = pyArray1.`with`(Array(1.0f, 1.0f, 1.0f, 1.0f)).reshape(2, 2)
    val floatY = pyArray2.`with`(Array(3.0f, 1.0f, 2.0f, 4.0f)).reshape(2, 2)
    val result1 = TestUtils.doTest[Float](
      bitcode = s"$basePath/jacobi_relax_core-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/laplace2d.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()), new jLong(floatY.addr())),
      expected = Some(0.0f)
    )

    // float64(float64[:,:], float64[:,:])
    val doubleX = pyArray1.`with`(Array(0.0, 0.0, 0.0, 0.0)).reshape(2, 2)
    val doubleY = pyArray2.`with`(Array(3.0, 1.0, 2.0, 4.0)).reshape(2, 2)
    TestUtils.doTest[Double](
      bitcode = s"$basePath/jacobi_relax_core-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/laplace2d.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()), new jLong(doubleY.addr())),
      expected = Some(0.0f)
    )
  }

  ignore("numba - mandel (NEEDS TO BE FIXED)") {
    // float64[:,:](float64, float64, float64, float64, float64[:,:], int64)
    val doubleX = pyArray1.`with`(Array(1.0, 1.0, 1.0, 1.0))
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/create_fractal-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/mandel.py",
      argTypes =
        Seq(jDouble.TYPE, jDouble.TYPE, jDouble.TYPE, jDouble.TYPE, jLong.TYPE, jLong.TYPE),
      arguments = Seq(
        new jDouble(-1.0),         // min_x
        new jDouble(1.0),          // max_x
        new jDouble(-1.0),         // min_y
        new jDouble(1.0),          // max_y
        new jLong(doubleX.addr()), // image
        new jLong(10)              // iters
      )
    )
    assert(doubleArray(result) === Seq(2.0, 2.0, 3.0, 5.0))
  }

  ignore("numba - pi (NEEDS TO BE FIXED)") {
    // float32(int32)
    val result1 = TestUtils.doTest[Float](
      bitcode = s"$basePath/calc_pi-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/pi.py",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(512))
    )
    assert(result1 === 0.0f)

    // float64(int64)
    val result2 = TestUtils.doTestWithFuncName[Double](
      bitcode = s"$basePath/calc_pi-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/pi.py",
      funcName = "_cfunc__ZN14numba_examples2pi12calc_pi_2476Ex",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(512))
    )
    assert(result2 === 0.0)
  }

  test("NumPy - sum") {
    // float32(float32[:,:])
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f)).reshape(2, 3)
    TestUtils.doTest[Float](
      bitcode = s"$basePath/sum2d-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/sum.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr())),
      expected = Some(21.0f)
    )

    // float64(float64[:,:])
    val doubleX = pyArray1.`with`(Array(1.0, 2.0, 3.0, 4.0)).reshape(4, 1)
    TestUtils.doTest[Double](
      bitcode = s"$basePath/sum2d-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/sum.py",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr())),
      expected = Some(10.0)
    )
  }

  // TODO: Needs to implement unsupported LLVM instructions
  ignore("numba - ra24 (NEEDS TO BE FIXED)") {
    // float32[:](int32, float32[:])
    val floatX = pyArray1.`with`(Array(20.0f, 24.0f, 16.0f, 28.0f))
    val result1 = TestUtils.doTest[Long](
      bitcode = s"$basePath/ra_numba-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/ra24.py",
      argTypes = Seq(jInt.TYPE, jLong.TYPE),
      arguments = Seq(
        new jInt(4),             // doy
        new jLong(floatX.addr()) // lat
      )
    )
    assert(floatArray(result1) === Seq(0.0f, 0.0f, 0.0f, 0.0f))

    // float64[:](int64, float64[:])
    val doubleX = pyArray1.`with`(Array(20.0, 24.0, 16.0, 28.0))
    val result2 = TestUtils.doTest[Long](
      bitcode = s"$basePath/ra_numba-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/ra24.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(
        new jLong(4),             // doy
        new jLong(doubleX.addr()) // lat
      )
    )
    assert(doubleArray(result2) === Seq(0.0, 0.0, 0.0, 0.0))
  }

  test("numba - movemean") {
    // void(float32[:], int32[:], float32[:])
    val floatX = pyArray1.`with`(Array(4.0f, 2.0f, 6.0f, 4.0f, 8.0f, 2.0f, 4.0f, 0.0f))
    val floatY = pyArray2.`with`(Array(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f))
    val intX = pyArray3.`with`(Array(2))
    TestUtils.doTest[Long](
      bitcode = s"$basePath/move_mean-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/movemean.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE, jLong.TYPE),
      arguments = Seq(
        new jLong(floatX.addr()), // a
        new jLong(intX.addr()),   // window_arr
        new jLong(floatY.addr())  // out
      )
    )
    assert(floatY.floatArray() === Seq(4.0f, 3.0f, 4.0f, 5.0f, 6.0f, 5.0f, 3.0f, 2.0f))

    // void(float64[:], int64[:], float64[:])
    val doubleX = pyArray1.`with`(Array(4.0, 2.0, 6.0, 4.0, 8.0, 2.0, 4.0, 0.0))
    val doubleY = pyArray2.`with`(Array(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
    val longX = pyArray3.`with`(Array(2))
    TestUtils.doTest[Long](
      bitcode = s"$basePath/move_mean-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/movemean.py",
      argTypes = Seq(jLong.TYPE, jLong.TYPE, jLong.TYPE),
      arguments = Seq(
        new jLong(doubleX.addr()), // a
        new jLong(longX.addr()),   // window_arr
        new jLong(doubleY.addr())  // out
      )
    )
    assert(doubleY.doubleArray() === Seq(4.0, 3.0, 4.0, 5.0, 6.0, 5.0, 3.0, 2.0))
  }
}
