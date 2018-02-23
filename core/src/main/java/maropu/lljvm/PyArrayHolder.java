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

import lljvm.unsafe.Platform;

/**
 * A placeholder for one or two dimensional arrays in Python.
 */
public class PyArrayHolder implements AutoCloseable {
  private final long holderAddr;
  private final long meminfoHolderAddr;
  private final long shapeHolderAddr;
  private final long strideHolderAddr;
  private final boolean isArrayOwner;

  public PyArrayHolder(long addr) {
    this.holderAddr = addr;
    this.meminfoHolderAddr = Platform.getLong(null, holderAddr);
    this.shapeHolderAddr = Platform.getLong(null, holderAddr + 40);
    this.strideHolderAddr = 0; // Not used
    this.isArrayOwner = false;
  }

  public PyArrayHolder() {
    // We assume that the aggregate(array/struct) type of python input arrays is
    // `{ i8*, i8*, i64, i64, ty*, [n x i64], [n x i64] }`.
    long holderSize = 72;
    this.holderAddr = Platform.allocateMemory(holderSize);
    this.meminfoHolderAddr = Platform.allocateMemory(8);
    this.shapeHolderAddr = Platform.allocateMemory(16);
    this.strideHolderAddr = Platform.allocateMemory(16);
    this.isArrayOwner = true;
    Platform.setMemory(null, holderAddr, holderSize, (byte) 0);
    Platform.setMemory(null, shapeHolderAddr, 16, (byte) 0);
    Platform.setMemory(null, strideHolderAddr, 16, (byte) 0);
    Platform.putLong(null, holderAddr,  meminfoHolderAddr);
  }

  public long addr() {
    return holderAddr;
  }

  public int[] shape() {
    int x = (int) Platform.getLong(null, shapeHolderAddr);
    int y = (int) Platform.getLong(null, shapeHolderAddr + 8);
    return new int[] {x, y};
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

  private boolean is1d(long x, long y) {
    return y == 1;
  }

  public PyArrayHolder reshape(long x, long y) {
    long nitem = Platform.getLong(null, nitemsAddr());
    long itemsize = Platform.getLong(null, itemsizeAddr());
    if (nitem != x * y) {
      throw new LLJVMRuntimeException("Total size of new array must be unchanged");
    }
    if (is1d(x, y)) { // 1-d array
      Platform.putLong(null, holderAddr + 40, shapeHolderAddr);
      Platform.putLong(null, holderAddr + 48, strideHolderAddr);
      _resize(itemsize, 0);
    } else { // 2-d array
      Platform.putLong(null, holderAddr + 40, shapeHolderAddr);
      Platform.putLong(null, holderAddr + 56, strideHolderAddr);
      _resize(x * itemsize, itemsize);
    }
    _reshape(x, y);
    return this;
  }

  private void _reshape(long x, long y) {
    Platform.putLong(null, shapeHolderAddr, x);
    Platform.putLong(null, shapeHolderAddr + 8, y);
  }

  private void _resize(long x, long y) {
    Platform.putLong(null, strideHolderAddr, x);
    Platform.putLong(null, strideHolderAddr + 8, y);
  }

  private void setArrayData(long arrayAddr, long length, long size) {
    assert(isArrayOwner);
    Platform.putLong(null, nitemsAddr(), length);
    Platform.putLong(null, itemsizeAddr(), size);
    Platform.putLong(null, dataAddr(), arrayAddr);
    reshape(length, 1);
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
      Platform.freeMemory(meminfoHolderAddr);
      Platform.freeMemory(shapeHolderAddr);
      Platform.freeMemory(strideHolderAddr);
    }
  }
}
