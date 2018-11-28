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

import java.lang.{Double => jDouble, Integer => jInt, Long => jLong}

import io.github.maropu.lljvm.util.python.PyArrayHolder

// TODO: Adds more tests for Numba examples, see: https://github.com/numba/numba/tree/master/examples
class NumbaExampleSuite extends PyFuncTest {

  // TODO: Needs to fix issues about illegal memory accesses
  ignore("numba - linear regression ()NEEDS TO BE FIXED)") {
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
        new jLong(10),             // iterations
        new jDouble(0.1))          // alphaN
    )
    assert(result !== 0)
    val resultArray = doubleArray(result)
    (0 until 2).foreach { i =>
      assert(Math.abs(resultArray(i)) > 0.0)
    }
  }

  // TODO: Needs to fix issues about illegal memory accesses
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
    assert(result !== 0)
    val resultArray = doubleArray(result)
    (0 until 2).foreach { i =>
      assert(Math.abs(resultArray(i)) > 0.0)
    }
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
        "_cfunc__ZN14numba_examples10bubblesort15bubblesort_2463E5ArrayIfLi1E1A7mutable7alignedE",
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
        "_cfunc__ZN14numba_examples10bubblesort15bubblesort_2464E5ArrayIdLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()))
    )
    assert(doubleX.doubleArray() === Seq(1.0, 2.0, 3.0, 4.0))
  }

  test("numba - kernel density estimation") {
    // float32(float32[:])
    val floatX = pyArray1.`with`(Array(1.0f, 2.0f, 3.0f, 4.0f))
    val result1 = TestUtils.doTestWithFuncName[Float](
      bitcode = s"$basePath/kde-numba-cfunc-float32.bc",
      source = s"$basePath/numba_examples/kernel_density_estimation.py",
      funcName = "_cfunc__ZN14numba_examples25kernel_density_estimation8kde_2469E5ArrayIfLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(floatX.addr()))
    )
    // TODO: Needs to check the correct answer
    assert(Math.abs(result1) > 0.0f)

    // float64(float64[:])
    val doubleX = pyArray1.`with`(Array(1.0, 2.0, 3.0, 4.0))
    val result2 = TestUtils.doTestWithFuncName[Double](
      bitcode = s"$basePath/kde-numba-cfunc-float64.bc",
      source = s"$basePath/numba_examples/kernel_density_estimation.py",
      funcName = "_cfunc__ZN14numba_examples25kernel_density_estimation8kde_2475E5ArrayIdLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(doubleX.addr()))
    )
    // TODO: Needs to check the correct answer
    assert(Math.abs(result2) > 0.0)
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

  ignore("numba - mandel") {
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
  test("numba - ra24") {
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
    // TODO: Needs to check the correct answer
    assert(result1 !== 0)
    val resultArray1 = floatArray(result1)
    (0 until 4).foreach { i =>
      assert(Math.abs(resultArray1(i)) > 0.0f)
    }

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
    // TODO: Needs to check the correct answer
    assert(result2 !== 0)
    val resultArray2 = floatArray(result2)
    (0 until 4).foreach { i =>
      assert(Math.abs(resultArray2(i)) > 0.0f)
    }
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
