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

import java.nio.charset.StandardCharsets;

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
    // Since `MemInfo` allocated on the stack, we need to do nothing
    long dtor = Platform.getLong(null, addr + 8);
    long dtor_info = Platform.getLong(null, addr + 16);
    assert(dtor == 0L && dtor_info == 0L);
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
    switch (kind) {
      case 115: // 's': float
        sgemm(transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
        break;
      case 100: // 'd': double
        dgemm(transa, transb, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in numba_xxgemm: kind=" + toChar(kind));
    }
    return 0;
  }

  private static void sgemv(
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

  private static void dgemv(
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
        sgemv(trans, m, n, alpha, a, lda, x, beta, y);
        break;
      case 100: // 'd': double
        dgemv(trans, m, n, alpha, a, lda, x, beta, y);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in numba_xxgemv: kind=" + toChar(kind));
    }
    return 0;
  }

  private static void fdot(long n, long x, long y, long result) {
    float[] fx = new float[(int) n];
    float[] fy = new float[(int) n];
    Platform.copyMemory(null, x, fx, Platform.FLOAT_ARRAY_OFFSET, 4 * n);
    Platform.copyMemory(null, y, fy, Platform.FLOAT_ARRAY_OFFSET, 4 * n);
    float value = Sdot.sdot((int) n, fx, 0, 1, fy, 0, 1);
    Platform.putFloat(null, result, value);
  }

  private static void ddot(long n, long x, long y, long result) {
    double[] dx = new double[(int) n];
    double[] dy = new double[(int) n];
    Platform.copyMemory(null, x, dx, Platform.DOUBLE_ARRAY_OFFSET, 8 * n);
    Platform.copyMemory(null, y, dy, Platform.DOUBLE_ARRAY_OFFSET, 8 * n);
    double value = Ddot.ddot((int) n, dx, 0, 1, dy, 0, 1);
    Platform.putDouble(null, result, value);
  }

  // Vector * vector: result = dx * dy
  public static int _numba_xxdot(byte kind, byte conjugate, long n, long x, long y, long result) {
    switch (kind) {
      case 115: // 's': float
        fdot(n, x, y, result);
        break;
      case 100: // 'd': double
        ddot(n, x, y, result);
        break;
      default:
        throw new LLJVMRuntimeException(
          "Unsupported kind in numba_xxdot: kind=" + toChar(kind));
    }
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
