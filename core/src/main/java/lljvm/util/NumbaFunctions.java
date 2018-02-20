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

package lljvm.util;

import lljvm.unsafe.Platform;

public final class NumbaFunctions {
    static long _NRT_MemInfo_alloc_safe_aligned(long size, int align) {
        long memInfoSize = 40;
        long base = Platform.allocateMemory(memInfoSize + size + align * 2);
        long data = base + memInfoSize;
        long rem = data % align;
        if (rem != 0) {
            long offset = align - rem;
            data += offset;
        }
        Platform.putLong(null, base, 1);
        Platform.putLong(null, base + 8, 0);
        Platform.putLong(null, base + 16, 0);
        Platform.putLong(null, base + 24, data);
        Platform.putLong(null, base + 32, size);
        return base;
    }

    static void _NRT_MemInfo_call_dtor(long addr) {
        Platform.freeMemory(addr);
    }

    static void _numba_gil_ensure(long x) {}
    static void _numba_gil_release(long x) {}
    static void _numba_do_raise(long x) {}
    static long _numba_unpickle(long x, int y) {
        return 0;
    }

    static void _Py_DecRef(long x) {}
    static long _PyString_FromString(long x) {
        return 0;
    }

    static void _PyErr_SetNone(long x) {}
    static void _PyErr_SetString(long x, long y) {}
    static void _PyErr_WriteUnraisable(long x) {}
    static void _PyErr_Clear() {}
}
