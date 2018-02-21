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

import java.lang.{Double => jDouble, Integer => jInt, Long => jLong}

import org.scalatest.FunSuite

class CFuncSuite extends FunSuite {

  val basePath = "llvm-cfunc-bitcode"

  test("x + y") {
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc1.bc",
      source = s"$basePath/cfunc1.c",
      functionName = "_cfunc1",
      signature = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(4), new jInt(5)),
      expected = Some(9)
    )
  }

  test("pow(x, y)") {
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc2.bc",
      source = s"$basePath/cfunc2.c",
      functionName = "_cfunc2",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(2.0), new jDouble(3.0)),
      expected = Some(8.0)
    )
  }

  test("2 * y + pow(y, x)") {
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc3.bc",
      source = s"$basePath/cfunc3.c",
      functionName = "_cfunc3",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(100.0), new jDouble(1.0)),
      expected = Some(3.0)
    )
  }

  test("sum by simple for") {
    val longArray = Array(3, 5, 8, 2, 1).map(_.toLong)
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc4_for1.bc",
      source = s"$basePath/cfunc4_for1.c",
      functionName = "_cfunc4_for1",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(ArrayUtils.addressOf(longArray)), new jLong(longArray.size)),
      expected = Some(19)
    )

    val doubleArray = Array(2.0, 1.0)
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc4_for2.bc",
      source = s"$basePath/cfunc4_for2.c",
      functionName = "_cfunc4_for2",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(ArrayUtils.addressOf(doubleArray)), new jLong(doubleArray.size)),
      expected = Some(84.0)
    )
  }

  test("sum by simple while") {
    val intArray = Array(3, 1, 2, 8, 7, 2, 8, 9, 1, 3, 5, 8)
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc4_while1.bc",
      source = s"$basePath/cfunc4_while1.c",
      functionName = "_cfunc4_while1",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(ArrayUtils.addressOf(intArray)), new jLong(intArray.size)),
      expected = Some(57)
    )

    val floatArray = Array(5, 1, 1, 0, 3, 2, 9, 1, 2, 3).map(_.toFloat)
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc4_while2.bc",
      source = s"$basePath/cfunc4_while2.c",
      functionName = "_cfunc4_while2",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(ArrayUtils.addressOf(floatArray)), new jLong(floatArray.size)),
      expected = Some(27.0)
    )
  }

  test("simple if") {
     TestUtils.doTest(
      bitcode = s"$basePath/cfunc5.bc",
      source = s"$basePath/cfunc5.c",
      functionName = "_cfunc5",
      signature = Seq(jInt.TYPE),
      arguments = Seq(new jInt(0)),
      expected = Some(1)
    )
  }

  test("ternary operator") {
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc6.bc",
      source = s"$basePath/cfunc6.c",
      functionName = "_cfunc6",
      signature = Seq(jInt.TYPE),
      arguments = Seq(new jInt(1)),
      expected = Some(0)
    )
  }

  test("function call chains") {
    TestUtils.doTest(
      bitcode = s"$basePath/cfunc7.bc",
      source = s"$basePath/cfunc7.c",
      functionName = "_cfunc7",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(4.0), new jDouble(1.0)),
      expected = Some(7.0)
    )
  }
}
