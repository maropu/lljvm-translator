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

import lljvm.unsafe.Platform
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

  test("sum by simple loop") {
    val arraySize = 10
    val basePtr = Platform.allocateMemory(8 * arraySize)
    (0 until arraySize).foreach { i => Platform.putDouble(null, basePtr + 8 * i, 1.0) }
    TestUtils.doTest(
      id = "llvm-cfunc-bitcode/cfunc4-for.bc",
      f = "_cfunc4_for",
      sig = Seq(jLong.TYPE, jLong.TYPE),
      args = Seq(new jLong(basePtr), new jLong(arraySize)),
      expected = 1.0 * arraySize
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
}
