/*
* Copyright (c) 2009 David Roberts <d@vidr.cc>
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

package maropu.lljvm.runtime;

import org.netlib.blas.*;

import maropu.lljvm.LLJVMRuntimeException;
import maropu.lljvm.unsafe.Platform;

final class NumbaRuntime {

  private NumbaRuntime() {}

  /******************************************************************
   * External field values for Numba runtime.
   ******************************************************************
   */
  public static long _PyExc_StopIteration = 3;
  public static long _PyExc_SystemError = -1;


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
    long base = VMemory.allocateStack((int) (meminfoSize + size + align * 2));
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
    return base;
  }

  public static void _NRT_MemInfo_call_dtor(long addr) {
    // Do nothing
  }

  private static String toChar(byte b) {
    return new String(new byte[] { b });
  }

  private static void sgemm(
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

  private static void dgemm(
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
    // TODO: Need to support 's', 'd', 'c', and 'z'
    switch (kind) {
      case 115: // 's': float
        sgemm(transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
        break;
      case 100: // 'd': double
        dgemm(transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in _numba_xxgemm: kind=" + toChar(kind));
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
    return 0;
  }

  // Vector * vector: result = dx * dy
  public static int _numba_xxdot(byte kind, byte conjugate, long n, long dx, long dy, long result) {
    return 0;
  }

  public static void _numba_gil_ensure(long x) {}
  public static void _numba_gil_release(long x) {}
  public static void _numba_do_raise(long x) {}
  public static long _numba_unpickle(long x, int y) {
    return 0;
  }

  public static void _Py_FatalError(long x) {}
  public static void _Py_DecRef(long x) {}

  public static long _PyExc_StopIteration() {
    return 0;
  }
  public static long _PyExc_SystemError() {
    return 0;
  }

  public static long _PyString_FromString(long x) {
    return 0;
  }

  public static void _PyErr_SetNone(long x) {}
  public static void _PyErr_SetString(long x, long y) {}
  public static void _PyErr_WriteUnraisable(long x) {}
  public static void _PyErr_Clear() {}
}
