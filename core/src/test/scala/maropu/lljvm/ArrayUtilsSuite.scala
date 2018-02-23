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

import org.scalatest.FunSuite

import maropu.lljvm.unsafe.Platform
import maropu.lljvm.util.ArrayUtils

class ArrayUtilsSuite extends FunSuite {

  test("boolean") {
    val booleanArray = Array(true, false, true, true, false, false, true)
    val baseAddr = ArrayUtils.addressOf(booleanArray)
    (0 until booleanArray.size).foreach { i =>
      assert(booleanArray(i) === Platform.getBoolean(null, baseAddr + i))
    }
  }

  test("byte") {
    val byteArray = Array(1, 3, 8, 3, 2, 5, 2, 8, 1, 1, 4).map(_.toByte)
    val baseAddr = ArrayUtils.addressOf(byteArray)
    (0 until byteArray.size).foreach { i =>
      assert(byteArray(i) === Platform.getByte(null, baseAddr + i))
    }
  }

  test("short") {
    val shortArray = Array(8, 9, 3, 5, 4, 8, 2, 3, 5, 6, 9).map(_.toShort)
    val baseAddr = ArrayUtils.addressOf(shortArray)
    (0 until shortArray.size).foreach { i =>
      assert(shortArray(i) === Platform.getShort(null, baseAddr + 2 * i))
    }
  }

  test("int") {
    val intArray = Array(1, 1, 8, 3, 3, 4, 1, 0, 7, 3, 4, 3)
    val baseAddr = ArrayUtils.addressOf(intArray)
    (0 until intArray.size).foreach { i =>
      assert(intArray(i) === Platform.getInt(null, baseAddr + 4 * i))
    }
  }

  test("long") {
    val longArray = Array(0, 0, 5, 8, 1, 9, 2, 3, 8, 7, 1, 2).map(_.toLong)
    val baseAddr = ArrayUtils.addressOf(longArray)
    (0 until longArray.size).foreach { i =>
      assert(longArray(i) === Platform.getInt(null, baseAddr + 8 * i))
    }
  }

  test("float") {
    val floatArray = Array(5, 8, 3, 2, 8, 3, 6, 6, 1, 8, 2, 5).map(_.toFloat)
    val baseAddr = ArrayUtils.addressOf(floatArray)
    (0 until floatArray.size).foreach { i =>
      assert(floatArray(i) === Platform.getFloat(null, baseAddr + 4 * i))
    }
  }

  test("double") {
    val doubleArray = Array(9, 8, 1, 0, 5, 3, 4, 4, 1, 8, 3, 1).map(_.toDouble)
    val baseAddr = ArrayUtils.addressOf(doubleArray)
    (0 until doubleArray.size).foreach { i =>
      assert(doubleArray(i) === Platform.getDouble(null, baseAddr + 8 * i))
    }
  }
}
