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

import java.lang.{Long => jLong}
import java.lang.reflect.InvocationTargetException

// TODO: Adds more tests for NumPy linalg, see: https://docs.scipy.org/doc/numpy-1.15.1/reference/routines.linalg.html
class NumPyLinalgSuite  extends PyFuncTest {

  test("dot - vv") { // Vector * Vector case
    Seq(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384).foreach { n =>
      // float32(float32[:], float32[:])
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

      // float64(float64[:], float64[:])
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
      // float32[:,:](float32[:,:], float32[:,:])
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

      // float64[:,:](float64[:,:], float64[:,:])
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
    assert(errMsg.contains("Numba runtime exception: <Numba C callback 'numpy_dot_test'>"))
  }

  test("vdot") {
    Seq(2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384).foreach { n =>
      // float32(float32[:], float32[:])
      // TODO: reconsiders the current API design: `.reshape(4, 1)` != `.reshape(4)`
      val floatX = pyArray1.`with`(Array.fill[Float](n)(1.0f)).reshape(n)
      val floatY = pyArray2.`with`(Array.fill[Float](n)(2.0f)).reshape(n)
      TestUtils.doTest[Float](
        bitcode = s"$basePath/numpy_vdot_test-cfunc-vv-float32.bc",
        source = s"$basePath/numpy_vdot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(floatX.addr()), new jLong(floatY.addr())),
        expected = Some(2.0f * n)
      )

      // float64(float64[:], float64[:])
      val doubleX = pyArray1.`with`(Array.fill[Double](n)(3.0)).reshape(n)
      val doubleY = pyArray2.`with`(Array.fill[Double](n)(1.0)).reshape(n)
      TestUtils.doTest[Double](
        bitcode = s"$basePath/numpy_vdot_test-cfunc-vv-float64.bc",
        source = s"$basePath/numpy_vdot_test.py",
        argTypes = Seq(jLong.TYPE, jLong.TYPE),
        arguments = Seq(new jLong(doubleX.addr()), new jLong(doubleY.addr())),
        expected = Some(3.0 * n)
      )
    }
  }
}
