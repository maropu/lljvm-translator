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

package lljvm.runtime;

import java.lang.System;

import lljvm.unsafe.Platform;

/**
 * Virtual memory for storing/loading values to/from specified 64bit addresses. This class should
 * be thread-safe because multiple threads possibly access stack frames.
 */
public class VMemory {
  public static final String KEY_LLJVM_RUNTIME_VMEM_STACKSIZE = "lljvm.runtime.vmem.stacksize";
  public static final String DEFAULT_STACKSIZE = "2097152"; // 64KiB x 32 = 2MiB

  public static final int ALIGNMENT = 8; // 8-byte alignment

  // We assume multiple threads possibly access this
  private static ThreadLocal<VMemFragment> vmem = new ThreadLocal<>();

  static {
    long stackSize = Integer.parseInt(
      System.getProperty(KEY_LLJVM_RUNTIME_VMEM_STACKSIZE, DEFAULT_STACKSIZE));
    // TODO: How we do we release the allocated memory?
    long base = Platform.allocateMemory(stackSize + ALIGNMENT);
    vmem.set(new VMemFragment(alignOffsetUp(base, ALIGNMENT), stackSize));
  }

  /**
   * Return the least address greater than offset which is a multiple of align.
   */
  private static long alignOffsetUp(long offset, int align) {
    return ((offset - 1) & ~(align - 1)) + align;
  }

  /**
   * Allocate a memory block of the given size within the stack.
   */
  public static long allocateStack(int size) {
    assert(vmem.get() != null);
    VMemFragment vm = vmem.get();
    if (vm.getRemainingBytes() > size) {
      long addr = vm.getCurrentOffset();
      long nextOffset = alignOffsetUp(addr + size, ALIGNMENT);
      if (nextOffset < vm.getBase() + vm.getNumBytes()) {
        vm.setCurrentOffset(nextOffset);
      } else {
        vm.setCurrentOffset(vm.getBase());
      }
      return addr;
    }
    long addr = vm.getBase();
    long nextOffset = alignOffsetUp(addr + size, ALIGNMENT);
    vm.setCurrentOffset(nextOffset);
    return addr;
  }

  /**
   * Thrown if an application tries to access an invalid memory address, or
   * tries to write to a read-only location.
   */
  @SuppressWarnings("serial")
  public static class SegmentationFault extends IllegalArgumentException {
    public SegmentationFault(long addr) {
      super("Address = " + addr + " (0x" + Long.toHexString(addr) + ")");
    }
  }

  /**
   * Store a boolean value at the given address.
   */
  public static void store(long addr, boolean value) {
    try {
      Platform.putBoolean(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a byte at the given address.
   */
  public static void store(long addr, byte value) {
    try {
      Platform.putByte(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a 16-bit integer at the given address.
   */
  public static void store(long addr, short value) {
    try {
      Platform.putShort(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a 32-bit integer at the given address.
   */
  public static void store(long addr, int value) {
    try {
      Platform.putInt(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a 64-bit integer at the given address.
   */
  public static void store(long addr, long value) {
    try {
      Platform.putLong(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a single precision floating point number at the given address.
   */
  public static void store(long addr, float value) {
    try {
      Platform.putFloat(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a double precision floating point number at the given address.
   */
  public static void store(long addr, double value) {
    try {
      Platform.putDouble(null, addr, value);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a boolean value from the given address.
   */
  public static boolean load_i1(long addr) {
    try {
      return Platform.getBoolean(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a byte from the given address.
   */
  public static byte load_i8(long addr) {
    try {
      return Platform.getByte(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a 16-bit integer from the given address.
   */
  public static short load_i16(long addr) {
    try {
      return Platform.getShort(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a 32-bit integer from the given address.
   */
  public static int load_i32(long addr) {
    try {
      return Platform.getInt(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a 64-bit integer from the given address.
   */
  public static long load_i64(long addr) {
    try {
      return Platform.getLong(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a single precision floating point number from the given address.
   */
  public static float load_f32(long addr) {
    try {
      return Platform.getFloat(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a double precision floating point number from the given address.
   */
  public static double load_f64(long addr) {
    try {
      return Platform.getDouble(null, addr);
    } catch(NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }
}
