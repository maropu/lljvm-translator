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

// TODO: Adds more tests for NumPy random, see: https://docs.scipy.org/doc/numpy/reference/routines.random.html
class NumPyRandomSuite extends PyFuncTest {

  test("ranf") {
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

    val rvalues2 = doubleArray(result)
    rvalues2.indices.foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }
  }

  test("randint") {
    // int64(int32)
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random9_test-cfunc-int64.bc",
      source = s"$basePath/numpy_random9_test.py",
      funcName = "_cfunc__ZN18numpy_random9_test23numpy_random9_test_2468Eii",
      argTypes = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(6), new jInt(9))
    )
    assert(result1 - 6 < 3)

    // int64[:](int32, int32, int32)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random3_test-cfunc-int64.bc",
      source = s"$basePath/numpy_random3_test.py",
      funcName = "_cfunc__ZN18numpy_random3_test23numpy_random3_test_2456Eiii",
      argTypes = Seq(jInt.TYPE, jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(3), new jInt(5), new jInt(4))
    )

    val pyArray2 = PyArrayHolder.create(result2, 1)
    assert(Seq("1d python array", "nitem=4", "itemsize=8", "shape=[4]", "stride=[8]")
      .forall(pyArray2.toDebugString.contains))
    val longArray2 = pyArray2.longArray()
    (0 until longArray2.size).foreach { x =>
      assert(longArray2(x) - 3 < 2)
    }
  }

  test("rand") {
    // float64[:](int32)
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random4_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random4_test.py",
      funcName = "_cfunc__ZN18numpy_random4_test23numpy_random4_test_2457Ei",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(5))
    )

    val pyArray1 = PyArrayHolder.create(result1, 1)
    assert(Seq("1d python array", "nitem=5", "itemsize=8", "shape=[5]", "stride=[8]")
      .forall(pyArray1.toDebugString.contains))
    val rvalues1 = pyArray1.doubleArray()
    rvalues1.indices.foreach { x =>
      val value = rvalues1(x)
      (x + 1 until rvalues1.size).foreach { y =>
        assert(Math.abs(value - rvalues1(y)) > Double.MinValue)
      }
    }

    // float64[:,:](int32, int32)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random5_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random5_test.py",
      funcName = "_cfunc__ZN18numpy_random5_test23numpy_random5_test_2459Eii",
      argTypes = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(3), new jInt(5))
    )

    val pyArray2 = PyArrayHolder.create(result2, 2)
    assert(Seq("2d python array", "nitem=15", "itemsize=8", "shape=[3,5]", "stride=[40,8]")
      .forall(pyArray2.toDebugString.contains))
    val rvalues2 = pyArray2.doubleArray()
    rvalues2.indices.foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }
  }

  // TODO: Needs to fix; invalid constant value: Type=ArrayTyID (file=../const.cc line=138)
  ignore("randn") {
    // float64()
    def getRandomValue(): Double = {
      TestUtils.doTestWithFuncName[Double](
        bitcode = s"$basePath/numpy_random10_test-cfunc-float64.bc",
        source = s"$basePath/numpy_random10_test.py",
        funcName = "XXX")
    }
    val rvalues1 = (0 until 10).map(_ => getRandomValue())
    rvalues1.indices.foreach { x =>
      val value = rvalues1(x)
      (x + 1 until rvalues1.size).foreach { y =>
        assert(Math.abs(value - rvalues1(y)) > Double.MinValue)
      }
    }

    // float64[:](int32)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random6_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random6_test.py",
      funcName = "XXX",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(3))
    )

    val pyArray2 = PyArrayHolder.create(result2, 1)
    assert(Seq("1d python array", "nitem=3", "itemsize=8", "shape=[3]", "stride=[8]")
      .forall(pyArray2.toDebugString.contains))
    val rvalues2 = pyArray2.doubleArray()
    rvalues2.indices.foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }

    // float64[:,:](int32, int32)
    val result3 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random7_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random7_test.py",
      funcName = "XXX",
      argTypes = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(4), new jInt(2))
    )

    val pyArray3 = PyArrayHolder.create(result3, 2)
    assert(Seq("2d python array", "nitem=8", "itemsize=8", "shape=[4,2]", "stride=[16,8]")
      .forall(pyArray3.toDebugString.contains))
    val rvalues3 = pyArray3.doubleArray()
    rvalues3.indices.foreach { x =>
      val value = rvalues3(x)
      (x + 1 until rvalues3.size).foreach { y =>
        assert(Math.abs(value - rvalues3(y)) > Double.MinValue)
      }
    }
  }

  test("random_sample") {
    // float64()
    def getRandomValue(): Double = {
      TestUtils.doTestWithFuncName[Double](
        bitcode = s"$basePath/numpy_random11_test-cfunc-float64.bc",
        source = s"$basePath/numpy_random11_test.py",
        funcName = "_cfunc__ZN19numpy_random11_test24numpy_random11_test_2470E")
    }
    val rvalues1 = (0 until 10).map(_ => getRandomValue())
    rvalues1.indices.foreach { x =>
      val value = rvalues1(x)
      (x + 1 until rvalues1.size).foreach { y =>
        assert(Math.abs(value - rvalues1(y)) > Double.MinValue)
      }
    }

    // float64[:](int32)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random12_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random12_test.py",
      funcName = "_cfunc__ZN19numpy_random12_test24numpy_random12_test_2471Ei",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(7))
    )

    val pyArray2 = PyArrayHolder.create(result2, 1)
    assert(Seq("1d python array", "nitem=7", "itemsize=8", "shape=[7]", "stride=[8]")
      .forall(pyArray2.toDebugString.contains))
    val rvalues2 = pyArray2.doubleArray()
    rvalues2.indices.foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }
  }

  test("random") {
    // float64()
    def getRandomValue(): Double = {
      TestUtils.doTestWithFuncName[Double](
        bitcode = s"$basePath/numpy_random13_test-cfunc-float64.bc",
        source = s"$basePath/numpy_random13_test.py",
        funcName = "_cfunc__ZN19numpy_random13_test24numpy_random13_test_2472E")
    }
    val rvalues1 = (0 until 10).map(_ => getRandomValue())
    rvalues1.indices.foreach { x =>
      val value = rvalues1(x)
      (x + 1 until rvalues1.size).foreach { y =>
        assert(Math.abs(value - rvalues1(y)) > Double.MinValue)
      }
    }

    // float64[:](int32)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random14_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random14_test.py",
      funcName = "_cfunc__ZN19numpy_random14_test24numpy_random14_test_2473Ei",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(7))
    )

    val pyArray2 = PyArrayHolder.create(result2, 1)
    assert(Seq("1d python array", "nitem=7", "itemsize=8", "shape=[7]", "stride=[8]")
      .forall(pyArray2.toDebugString.contains))
    val rvalues2 = pyArray2.doubleArray()
    rvalues2.indices.foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }
  }

  test("sample") {
    // float64()
    def getRandomValue(): Double = {
      TestUtils.doTestWithFuncName[Double](
        bitcode = s"$basePath/numpy_random15_test-cfunc-float64.bc",
        source = s"$basePath/numpy_random15_test.py",
        funcName = "_cfunc__ZN19numpy_random15_test24numpy_random15_test_2474E")
    }
    val rvalues1 = (0 until 10).map(_ => getRandomValue())
    rvalues1.indices.foreach { x =>
      val value = rvalues1(x)
      (x + 1 until rvalues1.size).foreach { y =>
        assert(Math.abs(value - rvalues1(y)) > Double.MinValue)
      }
    }

    // float64[:](int32)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random16_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random16_test.py",
      funcName = "_cfunc__ZN19numpy_random16_test24numpy_random16_test_2475Ei",
      argTypes = Seq(jInt.TYPE),
      arguments = Seq(new jInt(7))
    )

    val pyArray2 = PyArrayHolder.create(result2, 1)
    assert(Seq("1d python array", "nitem=7", "itemsize=8", "shape=[7]", "stride=[8]")
      .forall(pyArray2.toDebugString.contains))
    val rvalues2 = pyArray2.doubleArray()
    rvalues2.indices.foreach { x =>
      val value = rvalues2(x)
      (x + 1 until rvalues2.size).foreach { y =>
        assert(Math.abs(value - rvalues2(y)) > Double.MinValue)
      }
    }
  }

  test("choice") {
    // int64(int32)
    def chooseValue(): Long = {
      TestUtils.doTestWithFuncName[Long](
        bitcode = s"$basePath/numpy_random17_test-cfunc-int64.bc",
        source = s"$basePath/numpy_random17_test.py",
        funcName = "_cfunc__ZN19numpy_random17_test24numpy_random17_test_2476Ei",
        argTypes = Seq(jInt.TYPE),
        arguments = Seq(new jInt(7))
      )
    }
    assert((0 until 10).map(_ => chooseValue()).forall { v => v >= 0 && v < 7 })

    // float64(float64[:])
    val result2 = TestUtils.doTestWithFuncName[Double](
      bitcode = s"$basePath/numpy_random18_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random18_test.py",
      funcName = "_cfunc__ZN19numpy_random18_test24numpy_random18_test_2480E5ArrayIdLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(Array(1.0, 2.0)).addr()))
    )
    assert(Seq(1.0, 2.0).contains(result2))
  }

  // TODO: Needs to fix; invalid memory access errors
  ignore("shuffle") {
    // void(float32[:])
    val ar1 = Array(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 7.0f, 8.0f, 9.0f)
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random19_test-cfunc-float32.bc",
      source = s"$basePath/numpy_random19_test.py",
      funcName = "_cfunc__ZN19numpy_random19_test24numpy_random19_test_2484E5ArrayIfLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(ar1).addr()))
    )
    val expected1 = Array(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 7.0f, 8.0f, 9.0f)
    val resultArray1 = floatArray(result1)
    assert(expected1.toSet !== resultArray1.toSet)
    assert(expected1 !== resultArray1)

    // void(float64[:,:])
    val ar2 = Array(5.0, 7.0, 8.0, 9.0)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random19_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random19_test.py",
      funcName = "XXX",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(ar2).reshape(2, 2). addr()))
    )
    val expected2 = Array(5.0, 7.0, 8.0, 9.0)
    val resultArray2 = doubleArray(result2)
    assert(expected2.toSet !== resultArray2.toSet)
    assert(expected2 !== resultArray2)
  }

  // TODO: Needs to fix; can't find a function in LLJVM runtime: _NRT_Allocate(J)J
  ignore("permutation") {
    // void(float32[:])
    val ar1 = Array(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 7.0f, 8.0f, 9.0f)
    val result1 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random20_test-cfunc-float32.bc",
      source = s"$basePath/numpy_random20_test.py",
      funcName = "_cfunc__ZN19numpy_random20_test24numpy_random20_test_2488E5ArrayIfLi1E1A7mutable7alignedE",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(ar1).addr()))
    )
    val expected1 = Array(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 7.0f, 8.0f, 9.0f)
    val resultArray1 = floatArray(result1)
    assert(expected1.toSet === resultArray1.toSet)
    assert(expected1 !== resultArray1)

    // void(float64[:,:])
    val ar2 = Array(5.0, 7.0, 8.0, 9.0)
    val result2 = TestUtils.doTestWithFuncName[Long](
      bitcode = s"$basePath/numpy_random20_test-cfunc-float64.bc",
      source = s"$basePath/numpy_random20_test.py",
      funcName = "XXX",
      argTypes = Seq(jLong.TYPE),
      arguments = Seq(new jLong(pyArray1.`with`(ar2).reshape(2, 2). addr()))
    )
    val expected2 = Array(5.0, 7.0, 8.0, 9.0)
    val resultArray2 = doubleArray(result2)
    assert(expected2.toSet === resultArray2.toSet)
    assert(expected2 !== resultArray2)
  }

  // TODO: Needs to fix; Can't find a function in LLJVM runtime: _sqrt(D)D
  ignore("distribution - binomial") {
    // float64(int64, float64)
    val result = TestUtils.doTestWithFuncName[Double](
      bitcode = s"$basePath/numpy_random21_test-cfunc-float32.bc",
      source = s"$basePath/numpy_random21_test.py",
      funcName = "XXX",
      argTypes = Seq(jLong.TYPE, jDouble.TYPE),
      arguments = Seq(new jLong(1), new jDouble(1.0))
    )
    assert(result > 0.0)
  }
}
