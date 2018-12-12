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

import org.netlib.blas.*;
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
    // add("_numba_attempt_nocopy_reshape");

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

  private static String toChar(byte b) {
    return new String(new byte[] { b });
  }

  // TODO: Needs to call BLAS native implementations if possible
  private static void _fdot_jvm_impl(long n, long x, long y, long result) {
    float[] fx = new float[(int) n];
    float[] fy = new float[(int) n];
    Platform.copyMemory(null, x, fx, Platform.FLOAT_ARRAY_OFFSET, 4 * n);
    Platform.copyMemory(null, y, fy, Platform.FLOAT_ARRAY_OFFSET, 4 * n);
    float value = Sdot.sdot((int) n, fx, 0, 1, fy, 0, 1);
    Platform.putFloat(null, result, value);
  }

  private static void _ddot_jvm_impl(long n, long x, long y, long result) {
    double[] dx = new double[(int) n];
    double[] dy = new double[(int) n];
    Platform.copyMemory(null, x, dx, Platform.DOUBLE_ARRAY_OFFSET, 8 * n);
    Platform.copyMemory(null, y, dy, Platform.DOUBLE_ARRAY_OFFSET, 8 * n);
    double value = Ddot.ddot((int) n, dx, 0, 1, dy, 0, 1);
    Platform.putDouble(null, result, value);
  }

  private static void _sgemv_jvm_impl(
      byte trans,
      long m,
      long n,
      long alpha,
      long a,
      long lda,
      long x,
      long beta,
      long y) {
    float falpha = Platform.getFloat(null, alpha);
    float fbeta = Platform.getFloat(null, beta);
    float[] fa = new float[(int) (m * n)];
    float[] fx = new float[(int) (n * 1)];
    float[] fy = new float[(int) (m * 1)];
    Platform.copyMemory(null, a, fa, Platform.FLOAT_ARRAY_OFFSET, 4 * (m * n));
    Platform.copyMemory(null, x, fx, Platform.FLOAT_ARRAY_OFFSET, 4 * (n * 1));
    Sgemv.sgemv(toChar(trans), (int) m, (int) n,
      falpha, fa, 0, (int) lda, fx, 0, 1, fbeta, fy, 0, 1);
    Platform.copyMemory(fy, Platform.FLOAT_ARRAY_OFFSET, null, y, 4 * (m * 1));
  }

  private static void _dgemv_jvm_impl(
      byte trans,
      long m,
      long n,
      long alpha,
      long a,
      long lda,
      long x,
      long beta,
      long y) {
    double dalpha = Platform.getDouble(null, alpha);
    double dbeta = Platform.getDouble(null, beta);
    double[] da = new double[(int) (m * n)];
    double[] dx = new double[(int) (n * 1)];
    double[] dy = new double[(int) (m * 1)];
    Platform.copyMemory(null, a, da, Platform.DOUBLE_ARRAY_OFFSET, 8 * (m * n));
    Platform.copyMemory(null, x, dx, Platform.DOUBLE_ARRAY_OFFSET, 8 * (n * 1));
    Dgemv.dgemv(toChar(trans), (int) m, (int) n,
      dalpha, da, 0, (int) lda, dx, 0, 1, dbeta, dy, 0, 1);
    Platform.copyMemory(dy, Platform.FLOAT_ARRAY_OFFSET, null, y,8 * (m * 1));
  }

  private static void _sgemm_jvm_impl(
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
    float falpha = Platform.getFloat(null, alpha);
    float fbeta = Platform.getFloat(null, beta);
    float[] fa = new float[(int) (m * n)];
    float[] fb = new float[(int) (n * k)];
    float[] fc = new float[(int) (m * k)];
    Platform.copyMemory(null, a, fa, Platform.FLOAT_ARRAY_OFFSET, 4 * (m * n));
    Platform.copyMemory(null, b, fb, Platform.FLOAT_ARRAY_OFFSET, 4 * (n * k));
    Sgemm.sgemm(toChar(transa), toChar(transb), (int) m, (int) n, (int) k,
      falpha, fa, 0, (int) lda, fb, 0, (int) ldb, fbeta, fc, 0, (int) ldc);
    Platform.copyMemory(fc, Platform.FLOAT_ARRAY_OFFSET, null, c, 4 * (m * k));
  }

  private static void _dgemm_jvm_impl(
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
    double falpha = Platform.getDouble(null, alpha);
    double fbeta = Platform.getDouble(null, beta);
    double[] da = new double[(int) (m * n)];
    double[] db = new double[(int) (n * k)];
    double[] dc = new double[(int) (m * k)];
    Platform.copyMemory(null, a, da, Platform.DOUBLE_ARRAY_OFFSET, 8 * (m * n));
    Platform.copyMemory(null, b, db, Platform.DOUBLE_ARRAY_OFFSET, 8 * (n * k));
    Dgemm.dgemm(toChar(transa), toChar(transb), (int) m, (int) n, (int) k,
      falpha, da, 0, (int) lda, db, 0, (int) ldb, fbeta, dc, 0, (int) ldc);
    Platform.copyMemory(dc, Platform.DOUBLE_ARRAY_OFFSET, null, c, 8 * (m * k));
  }

  // Vector * vector: result = dx * dy
  public static int _numba_xxdot(byte kind, byte conjugate, long n, long x, long y, long result) {
    switch (kind) {
      case 115: // 's': float
        _fdot_jvm_impl(n, x, y, result);
        break;
      case 100: // 'd': double
        _ddot_jvm_impl(n, x, y, result);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in numba_xxdot: kind=" + toChar(kind));
    }
    return 0;
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
    switch (kind) {
      case 115: // 's': float
        _sgemv_jvm_impl(trans, m, n, alpha, a, lda, x, beta, y);
        break;
      case 100: // 'd': double
        _dgemv_jvm_impl(trans, m, n, alpha, a, lda, x, beta, y);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in numba_xxgemv: kind=" + toChar(kind));
    }
    return 0;
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
    switch (kind) {
      case 115: // 's': float
        _sgemm_jvm_impl(transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
        break;
      case 100: // 'd': double
        _dgemm_jvm_impl(transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in numba_xxgemm: kind=" + toChar(kind));
    }
    return 0;
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

  // TODO: Needs to implement
  // _numba_attempt_nocopy_reshape(JJJJJJJI)I
  public static int _numba_attempt_nocopy_reshape() {
    return 0;
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
