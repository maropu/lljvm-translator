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

import java.lang.{Double => jDouble, Float => jFloat, Integer => jInt, Long => jLong}

import org.scalatest.FunSuite

class PyFuncSuite extends FunSuite {

  test("x + y") {
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc1-int32.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc1_241Eii",
      signature = Seq(jInt.TYPE, jInt.TYPE),
      arguments = Seq(new jInt(4), new jInt(5)),
      expected = 9
    )
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc1-int64.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc1_242Exx",
      signature = Seq(jLong.TYPE, jLong.TYPE),
      arguments = Seq(new jLong(3), new jLong(2)),
      expected = 5
    )
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc1-float32.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc1_243Eff",
      signature = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(7.0f)),
      expected = 8.0f
    )
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc1-float64.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc1_244Edd",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(2.0), new jDouble(2.0)),
      expected = 4.0
    )
  }

  test("math.pow(x, y)") {
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc2-float32.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc2_245Eff",
      signature = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(1.0f), new jFloat(4.0f)),
      expected = 1.0f
    )
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc2-float64.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc2_246Edd",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(3.0), new jDouble(2.0)),
      expected = 9.0
    )
  }

  test("2 * y + math.log10(x)") {
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc3-float32.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc3_247Eff",
      signature = Seq(jFloat.TYPE, jFloat.TYPE),
      arguments = Seq(new jFloat(100.0f), new jFloat(1.0f)),
      expected = 4.0f
    )
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc3-float64.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc3_248Edd",
      signature = Seq(jDouble.TYPE, jDouble.TYPE),
      arguments = Seq(new jDouble(1000.0), new jDouble(3.0)),
      expected = 9.0
    )
  }

  test("simple if") {
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc4-int32.bc",
      functionName = "_cfunc__ZN8__main__11pyfunc4_249Ei",
      signature = Seq(jInt.TYPE),
      arguments = Seq(new jInt(1)),
      expected = 0
    )
  }

  test("ternary operator") {
    TestUtils.doTest(
      bitcode = "llvm-pyfunc-bitcode/pyfunc5-int32.bc",
      functionName = "_cfunc__ZN8__main__12pyfunc5_2410Ei",
      signature = Seq(jInt.TYPE),
      arguments = Seq(new jInt(0)),
      expected = 1
    )
  }
}
