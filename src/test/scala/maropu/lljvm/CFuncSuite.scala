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

  test("x + y") {
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc1.bc",
      f = "_cfunc1",
      sig = Seq(jInt.TYPE, jInt.TYPE),
      args = Seq(new jInt(4), new jInt(5)),
      expected = 9
    )
  }

  test("pow(x, y)") {
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc2.bc",
      f = "_cfunc2",
      sig = Seq(jDouble.TYPE, jDouble.TYPE),
      args = Seq(new jDouble(2.0), new jDouble(3.0)),
      expected = 8.0
    )
  }

  test("2 * y + pow(y, x)") {
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc3.bc",
      f = "_cfunc3",
      sig = Seq(jDouble.TYPE, jDouble.TYPE),
      args = Seq(new jDouble(100.0), new jDouble(1.0)),
      expected = 3.0
    )
  }

  test("sum by simple for") {
    val doubleArray = Array(0.1, 3.9, 5.0, 8.3, 0.7, 5.0, 9.9, 1.1)
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc4-for.bc",
      f = "_cfunc4_for",
      sig = Seq(jLong.TYPE, jLong.TYPE),
      args = Seq(new jLong(ArrayUtils.addressOf(doubleArray)), new jLong(doubleArray.size)),
      expected = 34.0
    )
  }

  test("sum by simple while") {
    val intArray = Array(3, 1, 2, 8, 7, 2, 8, 9, 1, 3, 5, 8)
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc4-while.bc",
      f = "_cfunc4_while",
      sig = Seq(jLong.TYPE, jLong.TYPE),
      args = Seq(new jLong(ArrayUtils.addressOf(intArray)), new jLong(intArray.size)),
      expected = 57
    )
  }

  test("simple if") {
     TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc5.bc",
      f = "_cfunc5",
      sig = Seq(jInt.TYPE),
      args = Seq(new jInt(0)),
      expected = 1
    )
  }

  test("ternary operator") {
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc6.bc",
      f = "_cfunc6",
      sig = Seq(jInt.TYPE),
      args = Seq(new jInt(1)),
      expected = 0
    )
  }

  test("function call chains") {
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc7.bc",
      f = "_cfunc7",
      sig = Seq(jDouble.TYPE, jDouble.TYPE),
      args = Seq(new jDouble(4.0), new jDouble(1.0)),
      expected = 7.0
    )
  }
}
