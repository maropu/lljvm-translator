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

package lljvm.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import lljvm.util.ReflectionUtils;
import maropu.lljvm.LLJVMRuntimeException;

/**
 * Virtual memory, with methods for storing/loading values to/from
 * specified addresses.
 * 
 * @author  David Roberts
 */
public final class Memory {
    private static final int ALIGNMENT = 8; // 8-byte alignment
    private static final int MEM_SIZE = 1<<30; // 1 GiB of virtual memory
    private static final int DATA_SIZE = 1<<20; // 1 MiB Data+BSS
    private static final int STACK_SIZE = 1<<20; // 1 MiB stack
    
    // 64 KiB pages
    private static final int PAGE_SHIFT = 16;
    private static final int PAGE_SIZE = 1<<PAGE_SHIFT;
    
    private static final ByteOrder ENDIANNESS = ByteOrder.LITTLE_ENDIAN;
    
    /** Array of pages */
    private static final ByteBuffer[] pages =
        new ByteBuffer[MEM_SIZE>>>PAGE_SHIFT];
    /** Current end of Data+BSS */
    private static long dataEnd = 0;
    /** Current end of the heap */
    private static long heapEnd = DATA_SIZE;
    /** Current frame pointer */
    private static long framePointer = MEM_SIZE;
    /** Current stack pointer */
    private static long stackPointer = framePointer;
    /** Current number of frames on the stack */
    private static int stackDepth = 0;
    
    /** The null pointer */
    public static final long NULL = allocateData();
    
    static {
        final int DATA_BOTTOM = 0>>>PAGE_SHIFT;
        final int DATA_END = (DATA_SIZE - 1)>>>PAGE_SHIFT;
        for(int i = DATA_BOTTOM; i <= DATA_END; i++)
            pages[i] = createPage();
        final int STACK_BOTTOM = (MEM_SIZE - STACK_SIZE)>>>PAGE_SHIFT;
        final int STACK_END = (MEM_SIZE - 1)>>>PAGE_SHIFT;
        for(int i = STACK_BOTTOM; i <= STACK_END; i++)
            pages[i] = createPage();
    }
    
    /**
     * Thrown if an application tries to access an invalid memory address, or
     * tries to write to a read-only location.
     */
    @SuppressWarnings("serial")
    public static class SegmentationFault extends IllegalArgumentException {
        public SegmentationFault(long addr) {
            super("Address = "+addr+" (0x"+Long.toHexString(addr)+")");
        }
    }

    /**
     * Prevent this class from being instantiated.
     */
    private Memory() {}
    
    /**
     * Create a new page.
     * @return  the new page
     */
    private static ByteBuffer createPage() {
        return ByteBuffer.allocateDirect(PAGE_SIZE).order(ENDIANNESS);
    }
    
    /**
     * Return the page of the given virtual memory address
     * 
     * @param addr  the virtual memory address
     * @return      the page of the given virtual memory address
     */
    private static ByteBuffer getPage(long addr) {
        try {
            // TODO: Support 64bit addresses
            return pages[(int)addr >>> PAGE_SHIFT];
        } catch(ArrayIndexOutOfBoundsException e) {
            throw new SegmentationFault(addr);
        }
    }

    /**
     * Return the offset within the page of the given virtual memory address
     * 
     * @param addr  the virtual memory address
     * @return      the offset of the given virtual memory address
     */
    private static long getOffset(long addr) {
        return addr & (PAGE_SIZE - 1);
    }
    
    /**
     * Returns the least address greater than offset which is a multiple of
     * align.
     * 
     * @param offset  the offset to align
     * @param align   the required alignment. Must be a power of two.
     * @return        the aligned offset
     */
    private static long alignOffsetUp(long offset, int align) {
        return ((offset-1) & ~(align-1)) + align;
    }

    /**
     * Returns the greatest address less than offset which is a multiple of
     * align.
     * 
     * @param offset  the offset to align
     * @param align   the required alignment. Must be a power of two.
     * @return        the aligned offset
     */
    private static long alignOffsetDown(long offset, int align) {
        return offset & ~(align-1);
    }
    
    /**
     * Create a new stack frame, storing the current frame pointer.
     */
    public static void createStackFrame() {
        final long prevFramePointer = framePointer;
        framePointer = stackPointer;
        storeStack(prevFramePointer);
        stackDepth++;
    }
    
    /**
     * Destroy the current stack frame, restoring the previous frame pointer.
     */
    public static void destroyStackFrame() {
        stackPointer = framePointer;
        framePointer = load_i32(stackPointer - ALIGNMENT);
        stackDepth--;
    }
    
    /**
     * Destroy the top n stack frames.
     * 
     * @param n  the number of stack frames to destroy
     */
    public static void destroyStackFrames(int n) {
        for(int i = 0; i < n; i++)
            destroyStackFrame();
    }
    
    /**
     * Return the number of stack frames currently on the stack.
     * 
     * @return  the number of stack frames currently on the stack
     */
    public static int getStackDepth() {
        return stackDepth;
    }
    
    /**
     * Allocate a block of the given size within the data segment.
     * 
     * @param size  the size of the block to allocate
     * @return      a pointer to the allocated block
     */
    public static long allocateData(int size) {
        final long addr = dataEnd;
        dataEnd = alignOffsetUp(dataEnd + size, ALIGNMENT);
        return addr;
    }
    
    /**
     * Allocate one byte within the data segment.
     * 
     * @return  a pointer to the allocated byte
     */
    public static long allocateData() {
        return allocateData(1);
    }
    
    /**
     * Allocate a block of the given size within the stack.
     * 
     * @param size  the size of the block to allocate
     * @return      a pointer to the allocated block
     */
    public static long allocateStack(int size) {
        stackPointer = alignOffsetDown(stackPointer - size, ALIGNMENT);
        return stackPointer;
    }
    
    /**
     * Allocate one byte within the stack.
     * 
     * @return  a pointer to the allocated byte
     */
    public static long allocateStack() {
        return allocateStack(1);
    }
    
    /**
     * Store a boolean value at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, boolean value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).put((int)getOffset(addr), (byte) (value ? 1 : 0));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store a byte at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, byte value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).put((int)getOffset(addr), value);
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store a 16-bit integer at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, short value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).putShort((int)getOffset(addr), value);
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store a 32-bit integer at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, int value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).putInt((int)getOffset(addr), value);
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store a 64-bit integer at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, long value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).putLong((int)getOffset(addr), value);
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store a single precision floating point number at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, float value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).putFloat((int)getOffset(addr), value);
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store a double precision floating point number at the given address.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     */
    public static void store(long addr, double value) {
        try {
            // TODO: Support 64bit addresses
            getPage(addr).putDouble((int)getOffset(addr), value);
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Store an array of bytes at the given address.
     * 
     * @param addr   the address at which to store the bytes
     * @param bytes  the bytes to be stored
     */
    public static void store(long addr, byte[] bytes) {
        // TODO: make more efficient by using put(byte[])
        for(int i = 0; i < bytes.length; i++)
            store(addr + i, bytes[i]);
    }
    
    /**
     * Store a string at the given address.
     * 
     * @param addr    the address at which to store the string
     * @param string  the string to be stored
     */
    public static void store(long addr, String string) {
        final byte[] bytes = string.getBytes();
        store(addr, bytes);
        Memory.store(addr + bytes.length, (byte) 0);
    }
    
    /**
     * Store a string at the given address, unless the string would occupy more
     * than size bytes (including the null terminator).
     * 
     * @param addr    the address at which to store the string
     * @param string  the string to be stored
     * @param size    the maximum size of the string
     * @return        addr on success, NULL on error
     */
    public static long store(long addr, String string, int size) {
        final byte[] bytes = string.getBytes();
        store(addr, bytes);
        Memory.store(addr + bytes.length, (byte) 0);
        return addr;
    }
    
    /**
     * Store a boolean value in the data segment, returning a pointer to the
     * value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(boolean value) {
        final long addr = allocateData(1);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a byte in the data segment, returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(byte value) {
        final long addr = allocateData(1);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a 16-bit integer in the data segment, returning a pointer to the
     * value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(short value) {
        final long addr = allocateData(2);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a 32-bit integer in the data segment, returning a pointer to the
     * value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(int value) {
        final long addr = allocateData(4);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a 64-bit integer in the data segment, returning a pointer to the
     * value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(long value) {
        final long addr = allocateData(8);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a single precision floating point number in the data segment,
     * returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(float value) {
        final long addr = allocateData(4);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a double precision floating point number in the data segment,
     * returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeData(double value) {
        final long addr = allocateData(8);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store an array of bytes in the data segment, returning a pointer to the
     * bytes.
     * 
     * @param bytes  the bytes to be stored
     * @return       a pointer to the bytes
     */
    public static long storeData(byte[] bytes) {
        final long addr = allocateData(bytes.length);
        store(addr, bytes);
        return addr;
    }
    
    /**
     * Store a string in the data segment, returning a pointer to the string.
     * 
     * @param string  the string to be stored
     * @return        a pointer to the string
     */
    public static long storeData(String string) {
        final byte[] bytes = string.getBytes();
        final long addr = allocateData(bytes.length+1);
        store(addr, bytes);
        Memory.store(addr + bytes.length, (byte) 0);
        return addr;
    }
    
    /**
     * Store a boolean value in the stack, returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(boolean value) {
        final long addr = allocateStack(1);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a byte in the stack, returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(byte value) {
        final long addr = allocateStack(1);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a 16-bit integer in the stack, returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(short value) {
        final long addr = allocateStack(2);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a 32-bit integer in the stack, returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(int value) {
        final long addr = allocateStack(4);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a 64-bit integer in the stack, returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(long value) {
        final long addr = allocateStack(8);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a single precision floating point number in the stack,
     * returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(float value) {
        final long addr = allocateStack(4);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store a double precision floating point number in the stack,
     * returning a pointer to the value.
     * 
     * @param value  the value to be stored
     * @return       a pointer to the value
     */
    public static long storeStack(double value) {
        final long addr = allocateStack(8);
        store(addr, value);
        return addr;
    }
    
    /**
     * Store an array of bytes in the stack, returning a pointer to the bytes.
     * 
     * @param bytes  the bytes to be stored
     * @return       a pointer to the bytes
     */
    public static long storeStack(byte[] bytes) {
        final long addr = allocateStack(bytes.length);
        store(addr, bytes);
        return addr;
    }
    
    /**
     * Store an array of strings in the stack, terminated by a null pointer.
     * 
     * @param strings  the array of strings to be stored
     * @return         a pointer to the array
     */
    public static long storeStack(String[] strings) {
        final long addr = allocateStack(strings.length * 4 + 4);
        for(int i = 0; i < strings.length; i++)
            store(addr + i * 4, storeStack(strings[i]));
        store(addr + strings.length * 4, NULL);
        return addr;
    }
    
    /**
     * Store a string in the stack, returning a pointer to the string.
     * 
     * @param string  the string to be stored
     * @return        a pointer to the string
     */
    public static long storeStack(String string) {
        final byte[] bytes = string.getBytes();
        final long addr = allocateStack(bytes.length+1);
        store(addr, bytes);
        Memory.store(addr + bytes.length, (byte) 0);
        return addr;
    }
    
    /**
     * Load a boolean value from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static boolean load_i1(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).get((int)getOffset(addr)) != 0;
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a byte from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static byte load_i8(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).get((int)getOffset(addr));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a 16-bit integer from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static short load_i16(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).getShort((int)getOffset(addr));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a 32-bit integer from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static int load_i32(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).getInt((int)getOffset(addr));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a 64-bit integer from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static long load_i64(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).getLong((int)getOffset(addr));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a single precision floating point number from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static float load_f32(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).getFloat((int)getOffset(addr));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a double precision floating point number from the given address.
     * 
     * @param addr  the address from which to load the value
     * @return      the value at the given address
     */
    public static double load_f64(long addr) {
        try {
            // TODO: Support 64bit addresses
            return getPage(addr).getDouble((int)getOffset(addr));
        } catch(NullPointerException e) {
            throw new SegmentationFault(addr);
        }
    }
    
    /**
     * Load a string from the given address.
     * 
     * @param addr  the address from which to load the string
     * @return      the string at the given address
     */
    public static String load_string(long addr) {
        byte[] bytes = new byte[16];
        int i = 0;
        while((bytes[i++] = load_i8(addr++)) != 0)
            if(i >= bytes.length) bytes = Arrays.copyOf(bytes, i*2);
        return new String(Arrays.copyOf(bytes, i));
    }
    
    /**
     * Load a value of the given type from the given address.
     * 
     * @param addr  the address from which to load the value
     * @param type  the type of value to load. Must be a primitive type other
     *              than char.
     * @return      the value at the given address
     */
    public static Object load(long addr, Class<?> type) {
        if(type == boolean.class) return load_i1(addr);
        if(type == byte.class)    return load_i8(addr);
        if(type == short.class)   return load_i16(addr);
        if(type == int.class)     return load_i32(addr);
        if(type == long.class)    return load_i64(addr);
        if(type == float.class)   return load_f32(addr);
        if(type == double.class)  return load_f64(addr);
        throw new IllegalArgumentException("Unrecognised type");
    }
    
    /**
     * Store a boolean value at the given address, inserting any required
     * padding before the value, returning the first address following the
     * value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, boolean value) {
        addr = alignOffsetUp(addr, 1);
        store(addr, value);
        return addr + 1;
    }
    
    /**
     * Store a byte at the given address, inserting any required padding before
     * the value, returning the first address following the value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, byte value) {
        addr = alignOffsetUp(addr, 1);
        store(addr, value);
        return addr + 1;
    }
    
    /**
     * Store a 16-bit integer at the given address, inserting any required
     * padding before the value, returning the first address following the
     * value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, short value) {
        addr = alignOffsetUp(addr, 2);
        store(addr, value);
        return addr + 2;
    }
    
    /**
     * Store a 32-bit integer at the given address, inserting any required
     * padding before the value, returning the first address following the
     * value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, int value) {
        addr = alignOffsetUp(addr, 4);
        store(addr, value);
        return addr + 4;
    }
    
    /**
     * Store a 64-bit integer at the given address, inserting any required
     * padding before the value, returning the first address following the
     * value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, long value) {
        addr = alignOffsetUp(addr, 8);
        store(addr, value);
        return addr + 8;
    }
    
    /**
     * Store a single precision floating point number at the given address,
     * inserting any required padding before the value, returning the first
     * address following the value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, float value) {
        addr = alignOffsetUp(addr, 4);
        store(addr, value);
        return addr + 4;
    }
    
    /**
     * Store a double precision floating point number at the given address,
     * inserting any required padding before the value, returning the first
     * address following the value.
     * 
     * @param addr   the address at which to store the value
     * @param value  the value to be stored
     * @return       the first address following the value
     */
    public static long pack(long addr, double value) {
        addr = alignOffsetUp(addr, 8);
        store(addr, value);
        return addr + 8;
    }
    
    /**
     * Store a string at the given address, returning the first address
     * following the null terminator.
     * 
     * @param addr    the address at which to store the string
     * @param string  the string to be stored
     * @return        the first address following the null terminator
     */
    public static long pack(long addr, String string) {
        final byte[] bytes = string.getBytes();
        store(addr, bytes);
        Memory.store(addr + bytes.length, (byte) 0);
        return addr + bytes.length + 1;
    }
    
    /**
     * Store an array of chars at the given address, treating it as an array of
     * bytes i.e. each char is cast to a byte before being stored.
     * 
     * @param addr   the address at which to store the array
     * @param chars  the array of chars
     * @return       the first address following the stored array
     */
    public static long pack(long addr, char[] chars) {
        for(int i = 0; i < chars.length; i++)
            Memory.store(addr + i, (byte) chars[i]);
        return addr + chars.length;
    }
    
    /**
     * Unpack a naturally-aligned value of the given size from the given
     * address. The given address is updated to point to the first address
     * following the value.
     * 
     * @param addrp  a pointer to the address
     * @param size   the size of the value in bytes. Must be a power of 2.
     * @return       the address of the first naturally-aligned value of the
     *               given size following the given address
     */
    public static long unpack(long addrp, int size) {
        long addr = Memory.load_i32(addrp);
        addr = alignOffsetUp(addr, size);
        Memory.store(addrp, addr + size);
        return addr;
    }
    
    /**
     * Unpack a packed list of values from the given address, according to
     * the given list of types.
     * 
     * @param addr   the address from which to load the values
     * @param types  the array of types. All elements must be primitive types
     *               other than char.
     * @return       an array of unpacked values
     */
    public static Object[] unpack(long addr, Class<?>[] types) {
        Object[] values = new Object[types.length];
        for(int i = 0; i < types.length; i++) {
            final Class<?> type = types[i];
            final int size = ReflectionUtils.sizeOf(type);
            addr = alignOffsetUp(addr, size);
            values[i] = load(addr, type);
            addr += size;
        }
        return values;
    }
    
    /**
     * Copy len bytes from memory area src to memory area dest. The memory
     * areas should not overlap.
     * 
     * @param dest   the destination memory area
     * @param src    the source memory area
     * @param len    the number of bytes to copy
     * @param align  the alignment of the source and destination pointers,
     *               unless align is equal to 0 or 1
     */
    public static void memcpy(long dest, long src, int len, int align) {
        // TODO: make more efficient by using put(ByteBuffer)
        for(int i = 0; i < len; i++)
            store(dest + i, load_i8(src + i));
    }
    
    /**
     * Copy len bytes from memory area src to memory area dest. The memory
     * areas should not overlap.
     * 
     * @param dest   the destination memory area
     * @param src    the source memory area
     * @param len    the number of bytes to copy
     * @param align  the alignment of the source and destination pointers,
     *               unless align is equal to 0 or 1
     */
    public static void memcpy(long dest, long src, long len, int align) {
        memcpy(dest, src, (int) len, align);
    }
    
    /**
     * Copy len bytes from memory area src to memory area dest. The memory
     * areas may overlap.
     * 
     * @param dest   the destination memory area
     * @param src    the source memory area
     * @param len    the number of bytes to copy
     * @param align  the alignment of the source and destination pointers,
     *               unless align is equal to 0 or 1
     */
    public static void memmove(long dest, long src, int len, int align) {
        // TODO: make more efficient by using put(ByteBuffer)
        if(dest < src)
            for(int i = 0; i < len; i++)
                store(dest + i, load_i8(src + i));
        else
            for(int i = len - 1; i >= 0; i--)
                store(dest + i, load_i8(src + i));
    }
    
    /**
     * Copy len bytes from memory area src to memory area dest. The memory
     * areas may overlap.
     * 
     * @param dest   the destination memory area
     * @param src    the source memory area
     * @param len    the number of bytes to copy
     * @param align  the alignment of the source and destination pointers,
     *               unless align is equal to 0 or 1
     */
    public static void memmove(long dest, long src, long len, int align) {
        memmove(dest, src, (int) len, align);
    }
    
    /**
     * Fill the first len bytes of memory area dest with the constant byte val.
     * 
     * @param dest   the destination memory area
     * @param val    the constant byte fill value
     * @param len    the number of bytes to set
     * @param align  the alignment of the source and destination pointers,
     *               unless align is equal to 0 or 1
     */
    public static void memset(long dest, byte val, int len, int align) {
        // TODO: make more efficient by setting larger blocks at a time
        for(long i = dest; i < dest + len; i++)
            store(i, val);
    }
    
    /**
     * Fill the first len bytes of memory area dest with the constant byte val.
     * 
     * @param dest   the destination memory area
     * @param val    the constant byte fill value
     * @param len    the number of bytes to set
     * @param align  the alignment of the source and destination pointers,
     *               unless align is equal to 0 or 1
     */
    public static void memset(long dest, byte val, long len, int align) {
        memset(dest, val, (int) len, align);
    }
    
    /**
     * Fill the first len bytes of memory area dest with 0.
     * 
     * @param dest  the destination memory area
     * @param len   the number of bytes to set
     * @return      the address of the first byte following the block
     */
    public static long zero(long dest, long len) {
        memset(dest, (byte) 0, len, 1);
        return dest + len;
    }
}
