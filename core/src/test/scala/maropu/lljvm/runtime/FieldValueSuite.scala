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

import maropu.lljvm.LLJVMRuntimeException
import org.scalatest.FunSuite

class FieldValueSuite extends FunSuite {

  test("field values for Numba") {
    assert(FieldValue.get_i64("_PyExc_StopIteration") === 0L)
    assert(FieldValue.get_i64("_PyExc_SystemError") === 0L)
  }

  test("non-existent values") {
    val errMsg = intercept[LLJVMRuntimeException] {
      FieldValue.get_i64("unknown value")
    }.getMessage
    assert(errMsg === "Cannot resolve an external field for `unknown value`")
  }

  test("put/remove field values") {
    FieldValue.put("value1", 3.0)
    assert(FieldValue.get_f64("value1") === 3.0)
    FieldValue.remove("value1")
    val errMsg = intercept[LLJVMRuntimeException] {
      FieldValue.get_f64("value1")
    }.getMessage
    assert(errMsg === "Cannot resolve an external field for `value1`")
  }
}
