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

import io.github.maropu.lljvm.LLJVMFuncSuite

class NumbaRuntimeSuite extends LLJVMFuncSuite {

  test("field values for Numba") {
    assert(FieldValue.get_i64("_PyExc_StopIteration") === 0L)
    assert(FieldValue.get_i64("_PyExc_SystemError") === 0L)
  }

  test("functions for Numba") {
    // Invokes `_NRT_MemInfo_alloc_safe_aligned(12, 8)`
    val args1 = VMemory.allocateStack(12)
    VMemory.pack(VMemory.pack(args1, 12L), 8)
    val addr1 = Function.invoke_i64("", "_NRT_MemInfo_alloc_safe_aligned(JI)J", args1)
    assert(addr1 != 0L)

    // Invokes `_NRT_MemInfo_call_dtor(addr1)`
    val args2 = VMemory.allocateStack(8)
    VMemory.pack(args2, addr1)
    Function.invoke_void("", "_NRT_MemInfo_call_dtor(J)V", args2)

    // TODO: Adds tests for `_numba_xxgemm`, `_numba_xxgemv`, and `_numba_xxdot`
  }
}
