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

import java.lang.{Integer => jInt, Long => jLong}
import java.lang.reflect.InvocationTargetException

import io.github.maropu.lljvm.util.python.PyArrayHolder

// TODO: Adds more tests for NumPy operations, see: https://docs.scipy.org/doc/numpy/reference/routines.html
class NumPySuite extends PyFuncTest {

  test("transpose") {
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f)).reshape(2, 2)
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/transpose1_test-cfunc-float32.bc",
      source = s"$basePath/transpose1_test.py",
      funcName = "_cfunc__ZN15transpose1_test20transpose1_test_2423E5ArrayIfLi2E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()))
    )
    val transposed1 = new PyArrayHolder(result1, 2)
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[8,4]")
      .forall(floatX.toDebugString.contains))
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[4,8]")
      .forall(transposed1.toDebugString.contains))
    assert(transposed1.floatArray() === Seq(1.0f, 2.0f, 3.0f, 4.0f))

    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/transpose2_test-cfunc-float32.bc",
      source = s"$basePath/transpose2_test.py",
      funcName = "_cfunc__ZN15transpose2_test20transpose2_test_2425E5ArrayIfLi2E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()))
    )
    val transposed2 = new PyArrayHolder(result2, 2)
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[4,8]")
      .forall(transposed2.toDebugString.contains))
    assert(transposed2.floatArray() === Seq(1.0f, 2.0f, 3.0f, 4.0f))

    val doubleX = pyArray1.`with`(Array(6.0, 5.0, 4.0, 3.0, 2.0, 1.0)).reshape(3, 2)
    val result3 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/transpose1_test-cfunc-float64.bc",
      source = s"$basePath/transpose1_test.py",
      funcName = "_cfunc__ZN15transpose1_test20transpose1_test_2424E5ArrayIdLi2E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()))
    )
    val transposed3 = new PyArrayHolder(result3, 2)
    assert(Seq("2d python array", "nitem=6", "itemsize=8", "shape=[3,2]", "stride=[16,8]")
      .forall(doubleX.toDebugString.contains))
    assert(Seq("2d python array", "nitem=6", "itemsize=8", "shape=[2,3]", "stride=[8,16]")
      .forall(transposed3.toDebugString.contains))
    assert(transposed3.doubleArray() === Seq(6.0, 5.0, 4.0, 3.0, 2.0, 1.0))

    val result4 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/transpose2_test-cfunc-float64.bc",
      source = s"$basePath/transpose2_test.py",
      funcName = "_cfunc__ZN15transpose2_test20transpose2_test_2426E5ArrayIdLi2E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()))
    )
    val transposed4 = new PyArrayHolder(result4, 2)
    assert(Seq("2d python array", "nitem=6", "itemsize=8", "shape=[2,3]", "stride=[8,16]")
      .forall(transposed4.toDebugString.contains))
    assert(transposed4.doubleArray() === Seq(6.0, 5.0, 4.0, 3.0, 2.0, 1.0))
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

  test("array (NOT SUPPORTED)") {
    val errMsg = intercept[LLJVMRuntimeException] {
      // int32[:,:]()
      TestUtils.doTest[Long](
        bitcode = s"$basePath/numpy_array_test-cfunc-int32.bc",
        source = s"$basePath/numpy_array_test.py"
      )
    }.getMessage
    assert(errMsg.contains("bitcast not supported in function operands: Name=__dtor_list_int64"))
  }

  test("arange") {
    // int64[:,:]()
    val result = TestUtils.doTest[Long](
      bitcode = s"$basePath/numpy_arange_test-cfunc-int64.bc",
      source = s"$basePath/numpy_arange_test.py"
    )
    val longArray = new PyArrayHolder(result, 2)
    assert(Seq("2d python array", "nitem=9", "itemsize=8", "shape=[3,3]", "stride=[24,8]")
      .forall(longArray.toDebugString.contains))
    assert(longArray.longArray() === Seq(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L))
  }

  test("random") {
    val rvalues1 = (0 until 100).map { _ =>
      // float64()
      TestUtils.doTestWithFuncName[Double](
        bitcode = s"$basePath/numpy_random1_test-cfunc-float64.bc",
        source = s"$basePath/numpy_random1_test.py",
        funcName = "_cfunc__ZN17numpy_random_test23numpy_random1_test_2454E"
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
    val result = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random2_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random2_test.py",
      funcName = "_cfunc__ZN17numpy_random_test23numpy_random2_test_2455Ex",
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

  test("ones") {
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_ones1_test-cfunc-float64.bc",
      source = s"$basePath/numpy_ones1_test.py",
      funcName = "",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(3))
    )
    val ones1 = new PyArrayHolder(result1)
    assert(ones1.doubleArray() === Seq(1.0, 1.0, 1.0))
    assert(Seq("1d python array", "nitem=3", "itemsize=8", "shape=[3]", "stride=[8]")
      .forall(ones1.toDebugString.contains))

    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_ones2_test-cfunc-float64.bc",
      source = s"$basePath/numpy_ones2_test.py",
      funcName = "",
      argTypes = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(2), new jLong(2))
    )
    val ones2 = new PyArrayHolder(result2, 2)
    assert(ones2.doubleArray() === Seq(1.0, 1.0, 1.0, 1.0))
    assert(Seq("2d python array", "nitem=4", "itemsize=8", "shape=[2,2]", "stride=[16,8]")
      .forall(ones2.toDebugString.contains))
  }

  test("ones_like") {
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f))
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_ones_like_test-cfunc-float32.bc",
      source = s"$basePath/numpy_ones_like_test.py",
      funcName = "_cfunc__ZN20numpy_ones_like_test25numpy_ones_like_test_2460E5ArrayIfLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()))
    )
    val ones_like1 = new PyArrayHolder(result1)
    assert(ones_like1.floatArray() === Seq(1.0f, 1.0f, 1.0f))
    assert(Seq("1d python array", "nitem=3", "itemsize=4", "shape=[3]", "stride=[4]")
      .forall(ones_like1.toDebugString.contains))

    val doubleX = pyArray1.`with`(Array(4.0, 5.0, 6.0, 7.0)).reshape(2, 2)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_ones_like_test-cfunc-float64.bc",
      source = s"$basePath/numpy_ones_like_test.py",
      funcName = "_cfunc__ZN20numpy_ones_like_test25numpy_ones_like_test_2462E5ArrayIdLi2E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()))
    )
    val ones_like2 = new PyArrayHolder(result2, 2)
    assert(ones_like2.doubleArray() === Seq(1.0, 1.0, 1.0, 1.0))
    assert(Seq("2d python array", "nitem=4", "itemsize=8", "shape=[2,2]", "stride=[16,8]")
      .forall(ones_like2.toDebugString.contains))
  }

  test("power") {
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

  test("dot - vv") { // Vector * Vector case
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

  test("dot - mv") { // Matrix * Vector case
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

  test("dot - mm") { // Matrix * Matrix case
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

  test("dot - throws an exception when hitting incompatible shapes") {
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
}
