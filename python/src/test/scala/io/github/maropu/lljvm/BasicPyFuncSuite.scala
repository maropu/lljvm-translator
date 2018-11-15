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

class BasicPyFuncSuite extends PyFuncTest {

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
}
