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

package org.maropu.lljvm.util.python;

import org.maropu.lljvm.LLJVMRuntimeException;
import org.maropu.lljvm.unsafe.Platform;
import org.maropu.lljvm.util.ArrayUtils;

/**
 * A placeholder for one or two dimensional arrays in Python.
 */
public class PyArrayHolder implements AutoCloseable {
  private final long holderAddr;
  private final long meminfoAddr;
  private final long parentAddr; // Not used now
  private final boolean isArrayOwner;

  // This value depends on a shape of arrays
  private long strideAddrOffset;

  public PyArrayHolder(long addr) {
    this.holderAddr = addr;
    this.meminfoAddr = Platform.getLong(null, holderAddr);
    this.parentAddr = Platform.getLong(null, holderAddr + 8);
    this.strideAddrOffset = 0; // Not used
    this.isArrayOwner = false;
  }

  public PyArrayHolder() {
    // We assume that the aggregate(array/struct) type of python input arrays is
    // `{ i8*, i8*, i64, i64, ty*, [n x i64], [n x i64] }`.
    long holderSize = 72;
    this.holderAddr = Platform.allocateMemory(holderSize);

    // A pointer to allocated memory info; we assume the total size is 40B and the structure
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
    this.meminfoAddr = Platform.allocateMemory(meminfoSize);
    // TODO: Needs to merge code to initialize `MemInfo` in `NumbaRuntime`
    Platform.setMemory(null, meminfoAddr, meminfoSize, (byte) 0);
    Platform.putLong(null, meminfoAddr, 1L); // starts with 1 refct

    this.parentAddr = Platform.allocateMemory(8);

    // 1-d array by default, e.g., `{ i8*, i8*, i64, i64, ty*, [1 x i64], [1 x i64] }`
    this.strideAddrOffset = 8;
    Platform.setMemory(null, holderAddr, holderSize, (byte) 0);
    Platform.setMemory(null, meminfoAddr, meminfoSize, (byte) 0);
    Platform.putLong(null, holderAddr, meminfoAddr);

    this.isArrayOwner = true;
  }

  public long addr() {
    return holderAddr;
  }

  public int length() {
    return (int) Platform.getLong(null, nitemsAddr());
  }

  private long meminfoAddr() {
    return holderAddr;
  }

  private long parentAddr() {
    return holderAddr + 8;
  }

  private long nitemsAddr() {
    return holderAddr + 16;
  }

  private long itemsizeAddr() {
    return holderAddr + 24;
  }

  private long dataAddr() {
    return holderAddr + 32;
  }

  private long shapeAddr() {
    return holderAddr + 40;
  }

  private long strideAddr() {
    return shapeAddr() + strideAddrOffset;
  }

  private boolean is1d(long x, long y) {
    return y == 1;
  }

  public PyArrayHolder reshape(long x, long y) {
    long nitem = Platform.getLong(null, nitemsAddr());
    long itemsize = Platform.getLong(null, itemsizeAddr());
    if (nitem != x * y) {
      throw new LLJVMRuntimeException("Total size of new array must be unchanged");
    }

    // Updates shape and stride for 2-d arrays
    strideAddrOffset = 16;
    Platform.putLong(null, shapeAddr(), x);
    Platform.putLong(null, shapeAddr() + 8, y);
    Platform.putLong(null, strideAddr(), y * itemsize);
    Platform.putLong(null, strideAddr() + 8, itemsize);
    return this;
  }

  private void setArrayData(long arrayAddr, long length, long size) {
    assert(isArrayOwner);
    Platform.putLong(null, nitemsAddr(), length);
    Platform.putLong(null, itemsizeAddr(), size);
    Platform.putLong(null, dataAddr(), arrayAddr);

    // Updates `MemInfo`
    Platform.putLong(null, meminfoAddr + 24, arrayAddr);
    Platform.putLong(null, meminfoAddr + 32, length * size);

    // reshape(length, 1);
    strideAddrOffset = 8;
    Platform.putLong(null, shapeAddr(), length);
    Platform.putLong(null, strideAddr(), size);
  }

  public PyArrayHolder with(boolean[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 1);
    return this;
  }

  public PyArrayHolder with(byte[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 1);
    return this;
  }

  public PyArrayHolder with(short[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 2);
    return this;
  }

  public PyArrayHolder with(int[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 4);
    return this;
  }

  public PyArrayHolder with(long[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 8);
    return this;
  }

  public PyArrayHolder with(float[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 4);
    return this;
  }

  public PyArrayHolder with(double[] ar) {
    setArrayData(ArrayUtils.addressOf(ar), ar.length, 8);
    return this;
  }

  public boolean[] booleanArray() {
    boolean[] data = new boolean[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getBoolean(null, baseAddr + i);
    }
    return data;
  }

  public byte[] byteArray() {
    byte[] data = new byte[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getByte(null, baseAddr + i);
    }
    return data;
  }

  public short[] shortArray() {
    short[] data = new short[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getShort(null, baseAddr + i * 2);
    }
    return data;
  }

  public int[] intArray() {
    int[] data = new int[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getInt(null, baseAddr + i * 4);
    }
    return data;
  }

  public long[] longArray() {
    long[] data = new long[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getLong(null, baseAddr + i * 8);
    }
    return data;
  }

  public float[] floatArray() {
    float[] data = new float[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getFloat(null, baseAddr + i * 4);
    }
    return data;
  }

  public double[] doubleArray() {
    double[] data = new double[length()];
    long baseAddr = Platform.getLong(null, dataAddr());
    for (int i = 0; i < length(); i++) {
      data[i] = Platform.getDouble(null, baseAddr + i * 8);
    }
    return data;
  }

  @Override
  public void close() throws Exception {
    if (isArrayOwner) {
      Platform.freeMemory(holderAddr);
      Platform.freeMemory(meminfoAddr);
      Platform.freeMemory(parentAddr);
    }
  }
}
