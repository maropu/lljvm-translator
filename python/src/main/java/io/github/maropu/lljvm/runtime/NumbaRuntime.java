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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.maropu.lljvm.LLJVMRuntimeException;
import io.github.maropu.lljvm.util.ReflectionUtils;
import io.github.maropu.lljvm.unsafe.Platform;

public final class NumbaRuntime implements RuntimeInterface {

  private static final Logger logger = LoggerFactory.getLogger(NumbaRuntime.class);

  private static final Set<String> fieldWhileList = new HashSet<String>() {{
    add("_PyExc_SystemError");
    add("_PyExc_StopIteration");
  }};

  private static final Set<String> methodWhileList = new HashSet<String>() {{
    // For internal memory operations
    add("_NRT_MemInfo_alloc_safe_aligned");
    add("_NRT_MemInfo_call_dtor");

    // For NumPy random
    add("_numba_get_np_random_state");
    add("_numba_rnd_shuffle");

    // For NumPy arange
    add("_numba_attempt_nocopy_reshape");

    // Math functions
    add("_tanf");
    add("_tan");
    add("_acos");

    // For Numpy dot
    add("_numba_xxdot");
    add("_numba_xxgemv");
    add("_numba_xxgemm");

    // For error handling (e.g., NumPy dot between incompatible shapes)
    add("_PyString_FromString");
    add("_PyErr_WriteUnraisable");
    add("_PyErr_Clear");
    add("_numba_unpickle");
    add("_numba_gil_ensure");
  }};

  private static final NumbaRuntimeNative runtimeApi = NumbaRuntimeLoader.loadNumbaRuntimeApi();

  // Injects the Numba environment into the LLJVM runtime
  @Override
  public void initialize() {
    for (Field f : ReflectionUtils.getPublicStaticFields(NumbaRuntime.class)) {
      if (fieldWhileList.contains(f.getName())) {
        try {
          logger.debug("Numba Runtime field added: name=" + f.getName() + " value=" + f.get(null));
          FieldValue.put(f.getName(), f.get(null));
        } catch (IllegalAccessException e) {
          // Just ignores it
        }
      }
    }
    for (Method m : ReflectionUtils.getPublicStaticMethods(NumbaRuntime.class)) {
      if (methodWhileList.contains(m.getName())) {
        final String signature = ReflectionUtils.getSignature(m);
        logger.debug("Numba Runtime method added: signature=" + signature);
        Function.put(signature, m);
      }
    }
  }

  /******************************************************************
   * External field values for Numba runtime.
   ******************************************************************
   */
  public static long _PyExc_SystemError = 0;
  public static long _PyExc_NameError = 0;
  public static long _PyExc_StopIteration = 0;
  public static long _PyExc_RuntimeError = 0;
  public static long __Py_NoneStruct = 0;


  /******************************************************************
   * External methods for Numba runtime.
   ******************************************************************
   */
  public static long _NRT_MemInfo_alloc_safe_aligned(long size, int align) {
    // We assume the total size of an allocated memory info is 40B and the structure
    // in `numba/runtime/nrt.c` is as follows;
    //
    // struct MemInfo {
    //   size_t             refct;
    //   NRT_dtor_function  dtor;
    //   void              *dtor_info;
    //   void              *data;
    //   size_t             size;
    // };
    long meminfoSize = 40;
    long base = VMemory.allocateData((int) (meminfoSize + size + align * 2));
    long data = base + meminfoSize;
    long rem = data % align;
    if (rem != 0) {
      long offset = align - rem;
      data += offset;
    }
    // Initialize `MemInfo`
    Platform.setMemory(null, base, meminfoSize, (byte) 0);
    Platform.putLong(null, base, 1L); // starts with 1 refct
    Platform.putLong(null, base + 24, data);
    Platform.putLong(null, base + 32, size);
    logger.debug("Method '_NRT_MemInfo_alloc_safe_aligned' invoked: size=" + size +
      " align=" + align + " addr=" + base);
    return base;
  }

  public static void _NRT_MemInfo_call_dtor(long addr) {
    // Since `MemInfo` allocated on the stack, we need to do nothing
    long dtor = Platform.getLong(null, addr + 8);
    long dtor_info = Platform.getLong(null, addr + 16);
    assert(dtor == 0L && dtor_info == 0L);
    logger.debug("Method '_NRT_MemInfo_call_dtor' invoked: addr=" + addr);
  }

  // Vector * vector: result = dx * dy
  public static int _numba_xxdot(byte kind, byte conjugate, long n, long x, long y, long result) {
    return runtimeApi.numba_xxdot(kind, conjugate, n, x, y, result);
  }

  // Matrix * vector: y = alpha * a * x + beta * y
  public static int _numba_xxgemv(
      byte kind,
      byte trans,
      long m,
      long n,
      long alpha,
      long a,
      long lda,
      long x,
      long beta,
      long y) {
    return runtimeApi.numba_xxgemv(kind, trans, m, n, alpha, a, lda, x, beta, y);
  }

  // Matrix * matrix: c = alpha * a * b + beta * c
  public static int _numba_xxgemm(
      byte kind,
      byte transa,
      byte transb,
      long m,
      long n,
      long k,
      long alpha,
      long a,
      long lda,
      long b,
      long ldb,
      long beta,
      long c,
      long ldc) {
    return runtimeApi.numba_xxgemm(
      kind, transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
  }

  /**
   * Returns a state for NumPy random functions.
   */
  public static long _numba_get_np_random_state() {
    // typedef struct {
    //     int index;
    //     /* unsigned int is sufficient on modern machines as we only need 32 bits */
    //     unsigned int mt[MT_N];
    //     int has_gauss;
    //     double gauss;
    //     int is_initialized;
    // } rnd_state_t;
    //
    // NUMBA_EXPORT_FUNC(void)
    // numba_rnd_init(rnd_state_t *state, unsigned int seed)
    // {
    //     unsigned int pos;
    //     seed &= 0xffffffffU;
    //
    //     /* Knuth's PRNG as used in the Mersenne Twister reference implementation */
    //     for (pos = 0; pos < MT_N; pos++) {
    //         state->mt[pos] = seed;
    //         seed = (1812433253U * (seed ^ (seed >> 30)) + pos + 1) & 0xffffffffU;
    //     }
    //     state->index = MT_N;
    //     state->has_gauss = 0;
    //     state->gauss = 0.0;
    //     state->is_initialized = 1;
    // }
    return runtimeApi.numba_get_np_random_state();
  }

  public static void _numba_rnd_shuffle(long state) {
    runtimeApi.numba_rnd_shuffle(state);
  }

  public static float _tanf(float d) {
    return (float) Math.tan(d);
  }

  public static double _tan(double d) {
    return Math.tan(d);
  }

  public static double _acos(double d) {
    return Math.acos(d);
  }

  public static int _numba_attempt_nocopy_reshape(
      long nd,
      long dims,
      long strides,
      long newnd,
      long newdims,
      long newstrides,
      long itemsize,
      int is_f_order) {
    return runtimeApi._numba_attempt_nocopy_reshape(
      nd, dims, strides, newnd, newdims, newstrides, itemsize, is_f_order);
  }

  public static void _numba_gil_ensure(long x) {
    // Do nothing
  }
  public static void _numba_gil_release(long x) {
    // Do nothing
  }

  public static void _numba_do_raise(long x) {}
  public static long _numba_unpickle(long x, int y) {
    return 0;
  }

  public static void _Py_DecRef(long x) {}

  public static long _PyString_FromString(long addr) {
    // Just passes through a C string pointer
    return addr;
  }

  private static String toString(long addr) {
    final int maxNumBytes = 1024;
    int numBytes = 0;
    while (Platform.getByte(null, addr + numBytes) != 0) {
      if (numBytes > maxNumBytes) break;
      numBytes++;
    }
    byte[] strBuf = new byte[numBytes];
    Platform.copyMemory(null, addr, strBuf, Platform.BYTE_ARRAY_OFFSET, numBytes);
    return new String(strBuf, StandardCharsets.UTF_8);
  }

  public static void _PyErr_SetNone(long errType) {}
  public static void _PyErr_SetString(long errType, long errMsg) {}

  public static void _Py_FatalError(long errMsg) {
    throw new LLJVMRuntimeException("Numba runtime exception " + toString(errMsg));
  }

  public static void _PyErr_WriteUnraisable(long errMsg) {
    throw new LLJVMRuntimeException("Numba runtime exception " + toString(errMsg));
  }

  public static void _PyErr_Clear() {
    // Do nothing
  }
}
