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

package maropu.lljvm.runtime;

import java.util.Stack;

import maropu.lljvm.LLJVMRuntimeException;
import maropu.lljvm.unsafe.Platform;
import maropu.lljvm.util.Pair;
import maropu.lljvm.util.ReflectionUtils;

/**
 * Virtual memory for storing/loading values to/from specified 64bit addresses. This class should
 * be thread-safe because multiple threads possibly access the memory.
 */
public class VMemory {

  // 8-byte alignment for this memory model
  private static final int ALIGNMENT = 8;

  private static class VMemFragment {
    private final long base;
    private final long numBytes;

    private long currentOffset;

    public VMemFragment(long baseAddr, long numBytes) {
      this.base = baseAddr;
      this.numBytes = numBytes;
      this.currentOffset = 0;
    }

    public long getCapacity() {
      return numBytes;
    }

    public long getRemainingBytes() {
      return getCapacity() - currentOffset;
    }

    public long getBaseAddr() {
      return base;
    }

    public long getCurrentAddr() {
      return base + currentOffset;
  }

    public void setCurrentAddr(long addr) {
      assert(base <= addr && addr <= base + numBytes);
      this.currentOffset = addr - base;
    }
  }

  // For stack
  private static ThreadLocal<Pair<VMemFragment, Stack<Long>>> _stack =
       new ThreadLocal<Pair<VMemFragment, Stack<Long>>>() {

     @Override public Pair<VMemFragment, Stack<Long>> initialValue() {
       final String sizeStr = System.getProperty(
         "maropu.lljvm.runtime.vmem.stacksize", String.valueOf(8 * 1024 * 1024));
       int size = Integer.parseInt(sizeStr);
       long baseAddr = Platform.allocateMemory(size + ALIGNMENT);
       return new Pair<>(new VMemFragment(baseAddr , size), new Stack<Long>());
    }

    @Override public void remove() {
      Platform.freeMemory(this.get().getKey().getBaseAddr());
      super.remove();
    }
  };

  // For heap
  private static ThreadLocal<VMemFragment> _heap = new ThreadLocal<VMemFragment>() {

     @Override public VMemFragment initialValue() {
       final String sizeStr = System.getProperty(
         "maropu.lljvm.runtime.vmem.heapsize", String.valueOf(2 * 1024 * 1024));
       int size = Integer.parseInt(sizeStr);
       long baseAddr = Platform.allocateMemory(size + ALIGNMENT);
       return new VMemFragment(baseAddr , size);
    }

    @Override public void remove() {
      Platform.freeMemory(this.get().getBaseAddr());
      super.remove();
    }
  };

  private VMemory() {}

  private static Pair<VMemFragment, Stack<Long>> currentStack() {
    assert(_stack.get() != null);
    return _stack.get();
  }

  private static VMemFragment currentHeap() {
    assert(_heap.get() != null);
    return _heap.get();
  }

  /**
   * Return the least address greater than offset which is a multiple of align.
   */
  private static long alignOffsetUp(long offset, int align) {
    return ((offset - 1) & ~(align - 1)) + align;
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

  public static void resetHeap() {
    currentHeap().setCurrentAddr(currentHeap().getBaseAddr());
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
   * Store a double precision floating point number at the given address.
   */
  public static void store(long addr, double value) {
    try {
      Platform.putDouble(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store an array of bytes at the given address.
   */
  public static void store(long addr, byte[] bytes) {
    // TODO: make more efficient by using put(byte[])
    for (int i = 0; i < bytes.length; i++)
      store(addr + i, bytes[i]);
  }

  /**
   * Load a boolean value from the given address.
   */
  public static boolean load_i1(long addr) {
    try {
      return Platform.getBoolean(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a byte from the given address.
   */
  public static byte load_i8(long addr) {
    try {
      return Platform.getByte(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a 16-bit integer from the given address.
   */
  public static short load_i16(long addr) {
    try {
      return Platform.getShort(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a 32-bit integer from the given address.
   */
  public static int load_i32(long addr) {
    try {
      return Platform.getInt(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a 64-bit integer from the given address.
   */
  public static long load_i64(long addr) {
    try {
      return Platform.getLong(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a single precision floating point number from the given address.
   */
  public static float load_f32(long addr) {
    try {
      float d = Platform.getFloat(null, addr);
      return d;
      // return Platform.getFloat(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Load a double precision floating point number from the given address.
   */
  public static double load_f64(long addr) {
    try {
      return Platform.getDouble(null, addr);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
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
    try {
      Platform.putBoolean(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a byte at the given address.
   */
  public static void store(long addr, byte value) {
    try {
      Platform.putByte(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a 16-bit integer at the given address.
   */
  public static void store(long addr, short value) {
    try {
      Platform.putShort(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a 32-bit integer at the given address.
   */
  public static void store(long addr, int value) {
    try {
      Platform.putInt(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a 64-bit integer at the given address.
   */
  public static void store(long addr, long value) {
    try {
      Platform.putLong(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
    }
  }

  /**
   * Store a single precision floating point number at the given address.
   */
  public static void store(long addr, float value) {
    try {
      Platform.putFloat(null, addr, value);
    } catch (NullPointerException e) {
      throw new SegmentationFault(addr);
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
    for(int i = 0; i < value.length; i++)
      store(addr + i, (byte) value[i]);
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
    for(long i = dest; i < dest + len; i++)
      store(i, val);
  }

  /**
   * Fill the first len bytes of memory area dest with the constant byte val.
   */
  public static void memset(long dest, byte val, long len, int align) {
    memset(dest, val, (int) len, align);
  }
}
