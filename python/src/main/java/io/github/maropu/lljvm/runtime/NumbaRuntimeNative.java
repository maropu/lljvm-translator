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

package io.github.maropu.lljvm.runtime;

import io.github.maropu.lljvm.LLJVMRuntimeException;

public class NumbaRuntimeNative {

  // Initialize a Python interpreter and the others for Numba operations
  public native void initialize() throws LLJVMRuntimeException;

  // For NumPy arange
  public native int _numba_attempt_nocopy_reshape(long nd, long dims, long strides, long newnd, long newdims, long newstrides, long itemsize, int is_f_order);

  // For NumPy random
  public native long numba_get_np_random_state() throws LLJVMRuntimeException;
  public native void numba_rnd_shuffle(long state) throws LLJVMRuntimeException;

  // For NumPy dot
  public native int numba_xxdot(byte kind, byte conjugate, long n, long x, long y, long result) throws LLJVMRuntimeException;
  public native int numba_xxgemv(byte kind, byte trans, long m, long n, long alpha, long a, long lda, long x, long beta, long y) throws LLJVMRuntimeException;
  public native int numba_xxgemm(byte kind, byte transa, byte transb, long m, long n, long k, long alpha, long a, long lda, long b, long ldb, long beta, long c, long ldc) throws LLJVMRuntimeException;
}
