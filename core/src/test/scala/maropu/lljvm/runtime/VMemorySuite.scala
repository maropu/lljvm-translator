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

package maropu.lljvm.runtime

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import maropu.lljvm.LLJVMRuntimeException

class VMemorySuite extends FunSuite with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    super.beforeAll()
    VMemory.createStackFrame()
  }

  override def afterAll(): Unit = {
    VMemory.destroyStackFrame()
    super.afterAll()
  }

  test("read/write primitive types") {
    val addr1 = VMemory.allocateStack(1)
    VMemory.store(addr1, false)
    assert(VMemory.load_i1(addr1) === false)

    val addr2 = VMemory.allocateStack(1)
    VMemory.store(addr2, 3.toByte)
    assert(VMemory.load_i8(addr2) === 3)

    val addr3 = VMemory.allocateStack(2)
    VMemory.store(addr3, 8.toShort)
    assert(VMemory.load_i16(addr3) === 8)

    val addr4 = VMemory.allocateStack(4)
    VMemory.store(addr4, 23)
    assert(VMemory.load_i32(addr4) === 23)

    val addr5 = VMemory.allocateStack(8)
    VMemory.store(addr5, 122L)
    assert(VMemory.load_i64(addr5) === 122)

    val addr6 = VMemory.allocateStack(4)
    VMemory.store(addr6, 1.0f)
    assert(VMemory.load_f32(addr6) === 1.0)

    val addr7 = VMemory.allocateStack(8)
    VMemory.store(addr7, 8.0)
    assert(VMemory.load_f64(addr7) === 8.0)
  }

  // This test should lie in the end of this suite
  test("Throw an exception if not enough memory") {
    val errMsg = intercept[LLJVMRuntimeException] {
      while (true) {
        VMemory.allocateStack(8)
      }
    }.getMessage
    assert(errMsg === "Not enough memory in the stack")
  }
}
