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

package io.github.maropu.lljvm.runtime

import org.scalatest.{BeforeAndAfterAll, FunSuite}

import io.github.maropu.lljvm.LLJVMRuntimeException

class FunctionSuite extends FunSuite with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Function.clear()
    VMemory.createStackFrame()
  }

  override def afterAll(): Unit = {
    Function.clear()
    VMemory.destroyStackFrame()
    super.afterAll()
  }

  test("non-existent functions") {
    val errMsg = intercept[LLJVMRuntimeException] {
      Function.invoke_void("", "unknown function", 0)
    }.getMessage
    assert(errMsg === "Cannot resolve an external function for `unknown function`")
  }

  ignore("put/remove functions") {}
}
