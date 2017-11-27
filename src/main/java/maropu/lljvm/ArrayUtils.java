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

  private static boolean isCompressedOop() {
    try {
      final Class<?> beanClazz = Class.forName("com.sun.management.HotSpotDiagnosticMXBean");
      final Object hotSpotBean = ManagementFactory.newPlatformMXBeanProxy(
        ManagementFactory.getPlatformMBeanServer(),
        "com.sun.management:type=HotSpotDiagnostic",
        beanClazz);
      final Method getVMOption = hotSpotBean.getClass().getMethod("getVMOption", String.class);
      final Object vmOption = getVMOption.invoke(hotSpotBean, "UseCompressedOops");
      return Boolean.valueOf(vmOption.getClass().getMethod("getValue").invoke(vmOption).toString());
    } catch (Exception e) {
      return false;
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
        long oop = Platform.getLong(holder, baseOffset);
        // In 64bit JVMs, we need to check if compressed OOPs enabled
        if (isCompressedOop()) {
          return (oop & 0xFFFFFFFF) << 3;
        } else {
          return oop;
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
}
