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

// TODO: Needs to support over 2-dimension arrays
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
    this.strideHolderAddr = Platform.getLong(null, holderAddr + 48);
    this.isArrayOwner = false;
  }

  public PyArrayHolder() {
    // We assume that the aggregate(array/struct) type of python input arrays is
    // `{ i8*, i8*, i64, i64, ty*, [1 x i64], [1 x i64] }`.
    int elemNumInAggType = 7;
    long holderSize = elemNumInAggType * 8;
    this.holderAddr = Platform.allocateMemory(holderSize);
    this.meminfoHolderAddr = Platform.allocateMemory(8);
    this.shapeHolderAddr = Platform.allocateMemory(8);
    this.strideHolderAddr = Platform.allocateMemory(8);
    this.isArrayOwner = true;
    Platform.setMemory(null, holderAddr, holderSize, (byte) 0);
    Platform.putLong(null, holderAddr,  meminfoHolderAddr);
    Platform.putLong(null, holderAddr + 40, shapeHolderAddr);
    Platform.putLong(null, holderAddr + 48, strideHolderAddr);
  }

  public long addr() {
    return holderAddr;
  }

  public int length() {
    return (int) Platform.getLong(null, shapeHolderAddr);
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
    return holderAddr + 48;
  }

  private void _setArrayData(long arrayAddr, long shape, long stride) {
    assert(isArrayOwner);
    Platform.putLong(null, dataAddr(), arrayAddr);
    Platform.putLong(null, shapeHolderAddr, shape);
    Platform.putLong(null, strideHolderAddr, stride);
  }

  public long with(boolean[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 1);
    return holderAddr;
  }

  public long with(byte[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 1);
    return holderAddr;
  }

  public long with(short[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 2);
    return holderAddr;
  }

  public long with(int[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 4);
    return holderAddr;
  }

  public long with(long[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 8);
    return holderAddr;
  }

  public long with(float[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 4);
    return holderAddr;
  }

  public long with(double[] ar) {
    _setArrayData(ArrayUtils.addressOf(ar), ar.length, 8);
    return holderAddr;
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
