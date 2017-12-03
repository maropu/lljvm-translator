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

package maropu.lljvm;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;

import lljvm.unsafe.Platform;

public class ArrayUtils {

  // We assume multiple threads possibly access this
  private static ThreadLocal<PyArrayHolder> pyArrayHolderAddr = new ThreadLocal<PyArrayHolder>() {
    @Override public PyArrayHolder initialValue() {
      return new PyArrayHolder();
    }
  };

  // TODO: In JDK8, we can use `VMSupport` in JOL v0.4 or earlier versions? Otherwise,
  // we can use `HotSpot Serviceability Agent` in JDK9?
  //  - https://stackoverflow.com/questions/46597668/how-to-determine-if-java-heap-is-using-compressed-pointers-and-whether-or-not-re/46600331
  private static final long COMPRESSED_OOP_BASE = 0L;
  private static final int COMPRESSED_OOP_SHIFT = 3;

  private static boolean isCompressedOop = false;

  static {
    try {
      final Class<?> beanClazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
      final Object hotSpotBean = ManagementFactory.newPlatformMXBeanProxy(
        ManagementFactory.getPlatformMBeanServer(),
        "com.sun.management:type=HotSpotDiagnostic",
        beanClazz);
      final Method getVMOption = hotSpotBean.getClass().getMethod("getVMOption", String.class);
      final Object vmOption = getVMOption.invoke(hotSpotBean, "UseCompressedOops");
      isCompressedOop = Boolean.valueOf(vmOption.getClass().getMethod("getValue").invoke(vmOption).toString());
    } catch (Exception e) {
      // Just ignore it
    }
  }

  private static long _addressOf(Object o) {
    final Object[] holder = new Object[]{ o };
    long baseOffset = Platform.arrayBaseOffset(Object[].class);
    int addressSize = Platform.addressSize();
    switch (addressSize) {
      case 4:
        // In 32bit JVMs, addresses are stored as they are
        return Platform.getInt(holder, baseOffset);
      case 8:
        // In 64bit JVMs, we need to check if compressed OOPs enabled
        if (isCompressedOop) {
          long coop = Platform.getInt(holder, baseOffset) & 0x00000000FFFFFFFFL;
          long narrowBase = COMPRESSED_OOP_BASE;
          long narrowShift = COMPRESSED_OOP_SHIFT;
          // Decompress a compressed oop here
          //  - https://wiki.openjdk.java.net/display/HotSpot/CompressedOops
          return narrowBase + (coop << narrowShift);
        } else {
          return Platform.getLong(holder, baseOffset);
        }
      default:
        throw new LLJVMRuntimeException("Unsupported address size: " + addressSize);
    }
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

  private static long _pyArray(long arrayAddr, long shape, long stride) {
    PyArrayHolder holder = pyArrayHolderAddr.get();
    Platform.putLong(null, holder.getArrayAddr(), arrayAddr);
    Platform.putLong(null, holder.getStrideHolderAddr(), stride);
    return holder.getHolderAddr();
  }

  // For python arrays
  public static long pyAyray(boolean[] ar) {
    return _pyArray(addressOf(ar), ar.length, 1);
  }

  public static long pyAyray(byte[] ar) {
    return _pyArray(addressOf(ar), ar.length, 1);
  }

  public static long pyAyray(short[] ar) {
    return _pyArray(addressOf(ar), ar.length, 2);
  }

  public static long pyAyray(int[] ar) {
    return _pyArray(addressOf(ar), ar.length, 4);
  }

  public static long pyAyray(long[] ar) {
    return _pyArray(addressOf(ar), ar.length, 8);
  }

  public static long pyAyray(float[] ar) {
    return _pyArray(addressOf(ar), ar.length, 4);
  }

  public static long pyAyray(double[] ar) {
    return _pyArray(addressOf(ar), ar.length, 8);
  }
}
