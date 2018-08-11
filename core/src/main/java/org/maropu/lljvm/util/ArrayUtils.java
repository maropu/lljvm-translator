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

package org.maropu.lljvm.util;

import org.maropu.lljvm.LLJVMLoader;
import org.maropu.lljvm.LLJVMNative;
import org.maropu.lljvm.LLJVMRuntimeException;
import org.maropu.lljvm.unsafe.Platform;
import org.maropu.lljvm.util.python.PyArrayHolder;

public class ArrayUtils {
  // Variables below are used to decompress COOPs in Hotspot
  //  - https://wiki.openjdk.java.net/display/HotSpot/CompressedOops
  private static long narrowOffsetBase = 0L;
  private static int narrowOffsetShift = 0;
  private static boolean isCompressedOop = false;
  private static boolean isJavaArrayAddrSupported;
  private static String jvmName;

  static {
    final String jvmVendor = System.getProperty("java.vendor");
    final int addrSize = Platform.addressSize() * 8;
    if (jvmVendor.contains("Oracle")) {
      jvmName = String.format("OpenJDK/Oracle %d-bit JDK", addrSize);
      // Supports 64-bit Hotspot only
      isJavaArrayAddrSupported = (addrSize == 64);
    } else {
      jvmName = String.format("Unknown %d-bit Java Runtime (vendor=%s)", addrSize, jvmVendor);
      isJavaArrayAddrSupported = false;
    }
    try {
      if (isJavaArrayAddrSupported) {
        final LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
        final byte[] byteArray = "".getBytes();
        final long rawAddr = lljvmApi.addressOf(byteArray) - Platform.BYTE_ARRAY_OFFSET;
        final long jvmAddr = _addressOf(byteArray);
        // If OOPs compressed, we turn on a compressed mode here
        isCompressedOop = rawAddr != jvmAddr;
        if (isCompressedOop) {
          final long rt = rawAddr / jvmAddr;
          // Must be an exponent of 2
          assert(Long.bitCount(rt) == 1);
          narrowOffsetShift = Long.numberOfTrailingZeros(rt);
          narrowOffsetBase = rawAddr - (jvmAddr << narrowOffsetShift);
        }
      }
    } catch (Throwable t) {
      isJavaArrayAddrSupported = false;
    }
  }

  // We assume multiple threads possibly access this
  private static ThreadLocal<PyArrayHolder> pyArrayHolderAddr = new ThreadLocal<PyArrayHolder>() {
    @Override public PyArrayHolder initialValue() {
      return new PyArrayHolder();
    }
  };

  private static long _addressOf(Object o) {
    // If not supported, just throws an exception
    if (!isJavaArrayAddrSupported) {
      throw new LLJVMRuntimeException(
        String.format("Java array addressing is not supported in %s", jvmName));
    }

    final Object[] holder = new Object[]{ o };
    long baseOffset = Platform.arrayBaseOffset(Object[].class);
    // In 64-bit JVMs, we need to check if compressed OOPs enabled
    if (isCompressedOop) {
      long coop = Platform.getInt(holder, baseOffset) & 0x00000000FFFFFFFFL;
      long narrowBase = narrowOffsetBase;
      long narrowShift = narrowOffsetShift;
      // Decompress the compressed OOP here
      return narrowBase + (coop << narrowShift);
    } else {
      return Platform.getLong(holder, baseOffset);
    }
  }

  public static boolean isIsJavaArrayAddrSupported() {
    return isJavaArrayAddrSupported;
  }

  public static long addressOf(boolean[] ar) {
    return _addressOf(ar) + Platform.BOOLEAN_ARRAY_OFFSET;
  }

  public static long addressOf(byte[] ar) {
    return _addressOf(ar) + Platform.BYTE_ARRAY_OFFSET;
  }

  public static long addressOf(short[] ar) {
    return _addressOf(ar) + Platform.SHORT_ARRAY_OFFSET;
  }

  public static long addressOf(int[] ar) {
    return _addressOf(ar) + Platform.INT_ARRAY_OFFSET;
  }

  public static long addressOf(long[] ar) {
    return _addressOf(ar) + Platform.LONG_ARRAY_OFFSET;
  }

  public static long addressOf(float[] ar) {
    return _addressOf(ar) + Platform.FLOAT_ARRAY_OFFSET;
  }

  public static long addressOf(double[] ar) {
    return _addressOf(ar) + Platform.DOUBLE_ARRAY_OFFSET;
  }
}
