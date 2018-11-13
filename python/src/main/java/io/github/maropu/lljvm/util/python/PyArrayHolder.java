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

package io.github.maropu.lljvm.util.python;

import io.github.maropu.lljvm.LLJVMRuntimeException;
import io.github.maropu.lljvm.unsafe.Platform;
import io.github.maropu.lljvm.util.ArrayUtils;

/**
 * A placeholder for one or two dimensional arrays in Python.
 */
public class PyArrayHolder implements AutoCloseable {
  private static final int MAX_DIMENSION = 2;

  private final long holderAddr;
  private final long meminfoAddr;
  private final long parentAddr; // Not used now
  private final boolean isArrayOwner;

  // Number of dimensions
  private int numDim;

  public PyArrayHolder(long addr, int dim) {
    assert(dim == 1 || dim == 2);
    this.holderAddr = addr;
    this.meminfoAddr = Platform.getLong(null, holderAddr);
    this.parentAddr = Platform.getLong(null, holderAddr + 8);
    this.numDim = dim; // Not used
    this.isArrayOwner = false;
  }

  public PyArrayHolder(long addr) {
    this(addr, 1);
  }

  public PyArrayHolder() {
    // We assume that the aggregate(array/struct) type of python input n-d arrays is
    // `{ i8*, i8*, i64, i64, ty*, [n x i64], [n x i64] }`.
    long diminfoSize = 8 * (2 * MAX_DIMENSION);
    long pyArrayHeaderSize = 40 + diminfoSize;
    long meminfoSize = 40;
    long parentAddrSize = 8;
    long holderSize = pyArrayHeaderSize + meminfoSize + parentAddrSize;
    this.holderAddr = Platform.allocateMemory(holderSize);

    Platform.setMemory(null, holderAddr, holderSize, (byte) 0);

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
    this.meminfoAddr = holderAddr + pyArrayHeaderSize;
    this.parentAddr = meminfoAddr + meminfoSize;
    // 1-d array by default, that is, `{ i8*, i8*, i64, i64, ty*, [1 x i64], [1 x i64] }`
    this.numDim = 1;

    Platform.putLong(null, holderAddr, meminfoAddr);
    Platform.putLong(null, meminfoAddr, 1L); // starts with 1 refct

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
    return holderAddr + 40 + numDim * 8;
  }

  private boolean is1d() {
    return numDim == 1;
  }

  private boolean is2d() {
    return numDim == 2;
  }

  public PyArrayHolder reshape(long x, long y) {
    long nitem = Platform.getLong(null, nitemsAddr());
    long itemsize = Platform.getLong(null, itemsizeAddr());
    if (nitem != x * y) {
      throw new LLJVMRuntimeException("Total size of new array must be unchanged");
    }

    // Updates shape and stride for 2-d arrays
    numDim = 2;
    Platform.putLong(null, shapeAddr(), x);
    Platform.putLong(null, shapeAddr() + 8, y);
    Platform.putLong(null, strideAddr(), y * itemsize);
    Platform.putLong(null, strideAddr() + 8, itemsize);
    return this;
  }

  // TODO: reconsiders the current API design: `.reshape(4, 1)` != `.reshape(4)`
  public PyArrayHolder reshape(long length) {
    long nitem = Platform.getLong(null, nitemsAddr());
    long itemsize = Platform.getLong(null, itemsizeAddr());
    if (nitem != length) {
      throw new LLJVMRuntimeException("Total size of new array must be unchanged");
    }
    long arrayAddr = Platform.getLong(null, dataAddr());
    setArrayData(arrayAddr, length, itemsize);
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
    numDim = 1;
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

  public String toDebugString() {
    assert(is1d() || is2d());
    long nitem = Platform.getLong(null, nitemsAddr());
    long itemsize = Platform.getLong(null, itemsizeAddr());
    long dataAddr = Platform.getLong(null, dataAddr());
    StringBuilder builder = new StringBuilder();
    if (is1d()) {
      long shape = Platform.getLong(null, shapeAddr());
      long stride = Platform.getLong(null, strideAddr());
      builder.append("1d python array(");
      builder.append("addr=" + holderAddr + ", ");
      builder.append("nitem=" + nitem + ", ");
      builder.append("itemsize=" + itemsize + ", ");
      builder.append("dataAddr=" + dataAddr + ", ");
      builder.append("shape=[" + shape + "], ");
      builder.append("stride=[" + stride + "]");
      builder.append(")");
    } else if (is2d()) {
      long shape1 = Platform.getLong(null, shapeAddr());
      long shape2 = Platform.getLong(null, shapeAddr() + 8);
      long stride1 = Platform.getLong(null, strideAddr());
      long stride2 = Platform.getLong(null, strideAddr() + 8);
      builder.append("2d python array(");
      builder.append("addr=" + holderAddr + ", ");
      builder.append("nitem=" + nitem + ", ");
      builder.append("itemsize=" + itemsize + ", ");
      builder.append("dataAddr=" + dataAddr + ", ");
      builder.append("shape=[" + shape1 + "," + shape2 + "], ");
      builder.append("stride=[" + stride1 + "," + stride2 + "]");
      builder.append(")");
    }
    return builder.toString();
  }

  // TODO: Better to release the allocated via the weak reference logic?
  @Override
  public void close() {
    if (isArrayOwner) {
      Platform.freeMemory(holderAddr);
    }
  }
}
