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

import io.github.maropu.lljvm.util.ArrayUtils
import io.github.maropu.lljvm.util.python.PyArrayHolder

class PyArrayHolderSuite extends LLJVMFuncSuite {

  test("1d python array") {
    val pyArray = new PyArrayHolder().`with`(Array(1, 2, 3, 4, 5))
    assert(Seq("1d python array", "nitem=5", "itemsize=4", "shape=[5]", "stride=[4]")
      .forall(pyArray.toDebugString.contains))
  }

  test("2d python array") {
    val pyArray = new PyArrayHolder().`with`(Array(1, 2, 3, 4, 5)).reshape(5, 1)
    assert(Seq("2d python array", "nitem=5", "itemsize=4", "shape=[5,1]", "stride=[4,4]")
      .forall(pyArray.toDebugString.contains))
  }

  test("reshape between 1d and 2d") {
    val pyArray = new PyArrayHolder().`with`(Array(1, 2, 3, 4))
    assert(Seq("1d python array", "nitem=4", "itemsize=4", "shape=[4]", "stride=[4]")
      .forall(pyArray.toDebugString.contains))
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[2,2]", "stride=[8,4]")
      .forall(pyArray.reshape(2, 2).toDebugString.contains))
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[4,1]", "stride=[4,4]")
      .forall(pyArray.reshape(4, 1).toDebugString.contains))
    assert(Seq("2d python array", "nitem=4", "itemsize=4", "shape=[1,4]", "stride=[16,4]")
      .forall(pyArray.reshape(1, 4).toDebugString.contains))
    assert(Seq("1d python array", "nitem=4", "itemsize=4", "shape=[4]", "stride=[4]")
      .forall(pyArray.reshape(4).toDebugString.contains))
  }

  test("duplicate array data") {
    val ar = Array(1, 2, 3, 4, 5)
    val pyArray = new PyArrayHolder().`with`(ar)
    // Checks if `dataAddr` is not an object address of `ar`
    assert(!pyArray.toDebugString.contains(ArrayUtils.addressOf(ar).toString))
  }
}
