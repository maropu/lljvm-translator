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

import java.util.Stack;

import com.google.common.annotations.VisibleForTesting;
import io.github.maropu.lljvm.LLJVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.maropu.lljvm.LLJVMLoader;
import io.github.maropu.lljvm.LLJVMNative;
import io.github.maropu.lljvm.LLJVMRuntimeException;
import io.github.maropu.lljvm.util.Pair;
import io.github.maropu.lljvm.util.ReflectionUtils;
import io.github.maropu.lljvm.unsafe.Platform;

/**
 * Virtual memory for storing/loading values to/from specified 64bit addresses. This class should
 * be thread-safe because multiple threads possibly access the memory.
 */
public final class VMemory {

  private static final Logger logger = LoggerFactory.getLogger(VMemory.class);

  // 8-byte alignment for this memory model
  private static final int ALIGNMENT = 8;

  private VMemory() {}

  private static class VMemFragment {
    private final long base;
    private final long numBytes;

    private long currentOffset;

    VMemFragment(long baseAddr, long numBytes) {
      this.base = baseAddr;
      this.numBytes = numBytes;
      this.currentOffset = 0;
    }

    long getCapacity() {
      return numBytes;
    }

    long getRemainingBytes() {
      return getCapacity() - currentOffset;
    }

    long getBaseAddr() {
      return base;
    }

    long getCurrentAddr() {
      return base + currentOffset;
    }

    void setCurrentAddr(long addr) {
      assert(base <= addr && addr <= base + numBytes);
      this.currentOffset = addr - base;
    }
  }

  /**
   * Return the least address greater than offset which is a multiple of align.
   */
  private static long alignOffsetUp(long offset, int align) {
    return ((offset - 1) & ~(align - 1)) + align;
  }

  // For stack
  private static ThreadLocal<Pair<VMemFragment, Stack<Long>>> _stack =
       new ThreadLocal<Pair<VMemFragment, Stack<Long>>>() {

     @Override
     public Pair<VMemFragment, Stack<Long>> initialValue() {
       final String sizeStr = System.getProperty(
         "maropu.lljvm.runtime.vmem.stacksize", String.valueOf(256 * 1024 * 1024));
       int size = Integer.parseInt(sizeStr);
       long baseAddr = Platform.allocateMemory(size + ALIGNMENT);
       return new Pair<>(new VMemFragment(baseAddr , size), new Stack<Long>());
    }

    @Override
    public void remove() {
      Platform.freeMemory(this.get().getKey().getBaseAddr());
      super.remove();
    }
  };

  private static Pair<VMemFragment, Stack<Long>> currentStack() {
    assert(_stack.get() != null);
    return _stack.get();
  }

  public static void createStackFrame() {
    Pair<VMemFragment, Stack<Long>> stack = currentStack();
    VMemFragment mem = stack.getKey();
    Stack<Long> stackFrames = stack.getValue();
    stackFrames.push(mem.getCurrentAddr());
  }

  public static void destroyStackFrame() {
    Pair<VMemFragment, Stack<Long>> stack = currentStack();
    VMemFragment mem = stack.getKey();
    Stack<Long> stackFrames = stack.getValue();
    mem.setCurrentAddr(stackFrames.pop());
  }

  /**
   * Allocate a memory block of the given size within the stack.
   */
  public static long allocateStack(int required) {
    VMemFragment stack = currentStack().getKey();
    if (stack.getRemainingBytes() < required + ALIGNMENT) {
      throw new LLJVMRuntimeException("Not enough memory in the stack");
    }
    long addr = alignOffsetUp(stack.getCurrentAddr(), ALIGNMENT);
    stack.setCurrentAddr(addr + required);
    return addr;
  }

  @VisibleForTesting
  public static boolean verifyStackAddress(long base, long size) {
    VMemFragment stack = currentStack().getKey();
    return stack.getBaseAddr() <= base && (base + size) < stack.getBaseAddr() + stack.getCapacity();
  }


  // For heap
  private static ThreadLocal<VMemFragment> _heap = new ThreadLocal<VMemFragment>() {

     @Override
     public VMemFragment initialValue() {
       final String sizeStr = System.getProperty(
         "maropu.lljvm.runtime.vmem.heapsize", String.valueOf(256 * 1024 * 1024));
       int size = Integer.parseInt(sizeStr);
       long baseAddr = Platform.allocateMemory(size + ALIGNMENT);
       return new VMemFragment(baseAddr , size);
    }

    @Override
    public void remove() {
      Platform.freeMemory(this.get().getBaseAddr());
      super.remove();
    }
  };

  private static VMemFragment currentHeap() {
    assert(_heap.get() != null);
    return _heap.get();
  }

  /**
   * Allocate a memory block for global variables.
   */
  public static long allocateData(int required) {
    if (currentHeap().getRemainingBytes() < required + ALIGNMENT) {
      throw new LLJVMRuntimeException("Not enough memory in the heap");
    }
    long addr = alignOffsetUp(currentHeap().getCurrentAddr(), ALIGNMENT);
    currentHeap().setCurrentAddr(addr + required);
    return addr;
  }

  public static void resetHeap() {
    currentHeap().setCurrentAddr(currentHeap().getBaseAddr());
  }

  private static final LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();

  private static String toMemoryAccessString(long base, long size) {
    return "[0x" + Long.toHexString(base) + ", 0x" + Long.toHexString(base + size) + ")";
  }

  private static boolean verifyMemoryAccess(long base, long size) {
    if (LLJVMUtils.isTesting()) {
      logger.debug("Trying to access memory in " + toMemoryAccessString(base, size));
      // TODO: Can't we check memory accesses w/o JNI calls?
      return lljvmApi.verifyMemoryAddress(base, size);
    } else {
      return true;
    }
  }

  // LLJVM might have some bugs if this exception thrown
  static class InvalidMemoryAccessException extends IllegalArgumentException {
    public InvalidMemoryAccessException(long base, long size) {
      super("Invalid memory access detected: " + toMemoryAccessString(base, size));
    }
  }

  /**
   * Store a double precision floating point number at the given address.
   */
  public static void store(long addr, double value) {
    if (!verifyMemoryAccess(addr, 8)) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
    try {
      Platform.putDouble(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
  }

  /**
   * Store an array of bytes at the given address.
   */
  public static void store(long addr, byte[] bytes) {
    if (!verifyMemoryAccess(addr, bytes.length)) {
      throw new InvalidMemoryAccessException(addr, bytes.length);
    }
    try {
      // TODO: make more efficient by using put(byte[])
      for (int i = 0; i < bytes.length; i++) {
        Platform.putByte(null, addr + i, bytes[i]);
      }
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, bytes.length);
    }
  }

  /**
   * Load a boolean value from the given address.
   */
  public static boolean load_i1(long addr) {
    if (!verifyMemoryAccess(addr, 1)) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
    try {
      return Platform.getBoolean(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
  }

  /**
   * Load a byte from the given address.
   */
  public static byte load_i8(long addr) {
    if (!verifyMemoryAccess(addr, 1)) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
    try {
      return Platform.getByte(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
  }

  /**
   * Load a 16-bit integer from the given address.
   */
  public static short load_i16(long addr) {
    if (!verifyMemoryAccess(addr, 2)) {
      throw new InvalidMemoryAccessException(addr, 2);
    }
    try {
      return Platform.getShort(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 2);
    }
  }

  /**
   * Load a 32-bit integer from the given address.
   */
  public static int load_i32(long addr) {
    if (!verifyMemoryAccess(addr, 4)) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
    try {
      return Platform.getInt(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
  }

  /**
   * Load a 64-bit integer from the given address.
   */
  public static long load_i64(long addr) {
    if (!verifyMemoryAccess(addr, 8)) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
    try {
      return Platform.getLong(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
  }

  /**
   * Load a single precision floating point number from the given address.
   */
  public static float load_f32(long addr) {
    if (!verifyMemoryAccess(addr, 4)) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
    try {
      return Platform.getFloat(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
  }

  /**
   * Load a double precision floating point number from the given address.
   */
  public static double load_f64(long addr) {
    if (!verifyMemoryAccess(addr, 8)) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
    try {
      return Platform.getDouble(null, addr);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
  }

  /**
   * Load a value of the given type from the given address.
   */
  public static Object load(long addr, Class<?> type) {
    if (type == boolean.class) {
      return load_i1(addr);
    } else if (type == byte.class) {
      return load_i8(addr);
    } else if (type == short.class) {
      return load_i16(addr);
    } else if (type == int.class) {
      return load_i32(addr);
    } else if (type == long.class) {
      return load_i64(addr);
    } else if (type == float.class) {
      return load_f32(addr);
    } else if (type == double.class) {
      return load_f64(addr);
    }
    throw new IllegalArgumentException("Unknown type");
  }

  /**
   * Store a boolean value at the given address.
   */
  public static void store(long addr, boolean value) {
    if (!verifyMemoryAccess(addr, 1)) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
    try {
      Platform.putBoolean(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
  }

  /**
   * Store a byte at the given address.
   */
  public static void store(long addr, byte value) {
    if (!verifyMemoryAccess(addr, 1)) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
    try {
      Platform.putByte(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 1);
    }
  }

  /**
   * Store a 16-bit integer at the given address.
   */
  public static void store(long addr, short value) {
    if (!verifyMemoryAccess(addr, 2)) {
      throw new InvalidMemoryAccessException(addr, 2);
    }
    try {
      Platform.putShort(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 2);
    }
  }

  /**
   * Store a 32-bit integer at the given address.
   */
  public static void store(long addr, int value) {
    if (!verifyMemoryAccess(addr, 4)) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
    try {
      Platform.putInt(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
  }

  /**
   * Store a 64-bit integer at the given address.
   */
  public static void store(long addr, long value) {
    if (!verifyMemoryAccess(addr, 8)) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
    try {
      Platform.putLong(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 8);
    }
  }

  /**
   * Store a single precision floating point number at the given address.
   */
  public static void store(long addr, float value) {
    if (!verifyMemoryAccess(addr, 4)) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
    try {
      Platform.putFloat(null, addr, value);
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, 4);
    }
  }

  /**
   * Store a boolean value at the given address, inserting any required padding before the value,
   * returning the first address following the value.
   */
  public static long pack(long addr, boolean value) {
    addr = alignOffsetUp(addr, 1);
    store(addr, value);
    return addr + 1;
  }

  /**
   * Store a byte at the given address, inserting any required padding before the value,
   * returning the first address following the value.
   */
  public static long pack(long addr, byte value) {
    addr = alignOffsetUp(addr, 1);
    store(addr, value);
    return addr + 1;
  }

  /**
   * Store a 16-bit integer at the given address, inserting any required padding before the value,
   * returning the first address following the value.
   */
  public static long pack(long addr, short value) {
    addr = alignOffsetUp(addr, 2);
    store(addr, value);
    return addr + 2;
  }

  /**
   * Store a 32-bit integer at the given address, inserting any required padding before the value,
   * returning the first address following the value.
   */
  public static long pack(long addr, int value) {
    addr = alignOffsetUp(addr, 4);
    store(addr, value);
    return addr + 4;
  }

  /**
   * Store a 64-bit integer at the given address, inserting any required padding before the value,
   * returning the first address following the value.
   */
  public static long pack(long addr, long value) {
    addr = alignOffsetUp(addr, 8);
    store(addr, value);
    return addr + 8;
  }

  /**
   * Store a single precision floating point number at the given address, inserting any required
   * padding before the value, returning the first address following the value.
   */
  public static long pack(long addr, float value) {
    addr = alignOffsetUp(addr, 4);
    store(addr, value);
    return addr + 4;
  }

  /**
   * Store a double precision floating point number at the given address, inserting any required
   * padding before the value, returning the first address following the value.
   */
  public static long pack(long addr, double value) {
    addr = alignOffsetUp(addr, 8);
    store(addr, value);
    return addr + 8;
  }

  /**
   * Store a string at the given address, returning the first address
   * following the null terminator.
   */
  public static long pack(long addr, String string) {
    final byte[] bytes = string.getBytes();
    store(addr, bytes);
    store(addr + bytes.length, (byte) 0);
    return addr + bytes.length + 1;
  }

  /**
   * Store an array of chars at the given address, treating it as an array of
   * bytes i.e. each char is cast to a byte before being stored.
   */
  public static long pack(long addr, char[] value) {
    if (!verifyMemoryAccess(addr, value.length)) {
      throw new InvalidMemoryAccessException(addr, value.length);
    }
    try {
      for (int i = 0; i < value.length; i++) {
        Platform.putByte(null, addr + i, (byte) value[i]);
      }
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(addr, value.length);
    }
    return addr + value.length;
  }

  /**
   * Unpack a packed list of values from the given address, according to the given list of types.
   */
  public static Object[] unpack(long addr, Class<?>[] types) {
    Object[] values = new Object[types.length];
    for (int i = 0; i < types.length; i++) {
      final Class<?> type = types[i];
      final int size = ReflectionUtils.sizeOf(type);
      addr = alignOffsetUp(addr, size);
      values[i] = load(addr, type);
      addr += size;
    }
    return values;
  }

  /**
   * Fill the first len bytes of memory area dest with the constant byte val.
   */
  public static void memset(long dest, byte val, int len, int align) {
    if (!verifyMemoryAccess(dest, len)) {
      throw new InvalidMemoryAccessException(dest, len);
    }
    try {
      for (long i = dest; i < dest + len; i++) {
        Platform.putByte(null, i, val);
      }
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(dest, len);
    }
  }

  /**
   * Fill the first len bytes of memory area dest with the constant byte val.
   */
  public static void memset(long dest, byte val, long len, int align) {
    memset(dest, val, (int) len, align);
  }

  /**
   * Copy data from src to dest byte-to-byte.
   */
  public static void memcpy(long dest, long src, long len) {
    if (!verifyMemoryAccess(src, len)) {
      throw new InvalidMemoryAccessException(src, len);
    }
    if (!verifyMemoryAccess(dest, len)) {
      throw new InvalidMemoryAccessException(dest, len);
    }
    byte[] bytes = new byte[(int) len];
    try {
      for (long i = 0; i < len; i++) {
        bytes[(int) i] = Platform.getByte(null, src + i);
      }
    } catch (Throwable t) {
      throw new InvalidMemoryAccessException(src, len);
    }
    for (long i = 0; i < len; i++) {
      try {
        Platform.putByte(null, dest + i, bytes[(int) i]);
      } catch (Throwable t) {
        throw new InvalidMemoryAccessException(dest, len);
      }
    }
  }

  /**
   * Fill the first len bytes of memory area dest with 0.
   */
  public static long zero(long dest, long len) {
    memset(dest, (byte) 0, len, 1);
    return dest + len;
  }
}
