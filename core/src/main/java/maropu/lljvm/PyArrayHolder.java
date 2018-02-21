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

public class PyArrayHolder implements AutoCloseable {
  private final long holderAddr;
  private final long meminfoAddr;
  private final long parentAddr;
  private final long nitemsAddr;
  private final long itemsizeAddr;
  private final long dataAddr;
  private final long shapeHolderAddr;
  private final long strideHolderAddr;

  public PyArrayHolder() {
    // We assume that the aggregate(array/struct) type of python input arrays is
    // `{ i8*, i8*, i64, i64, ty*, [1 x i64], [1 x i64] }`.
    int elemNumInAggType = 7;
    this.holderAddr = Platform.allocateMemory(elemNumInAggType * 8);
    this.shapeHolderAddr = Platform.allocateMemory(8);
    this.strideHolderAddr = Platform.allocateMemory(8);
    this.meminfoAddr = holderAddr;
    this.parentAddr = holderAddr + 8;
    this.nitemsAddr = holderAddr + 16;
    this.itemsizeAddr = holderAddr + 24;
    this.dataAddr = holderAddr + 32;
    Platform.putLong(null, meminfoAddr, 0);
    Platform.putLong(null, parentAddr, 0);
    Platform.putLong(null, nitemsAddr, 0);
    Platform.putLong(null, itemsizeAddr, 0);
    Platform.putLong(null, dataAddr, 0);
    Platform.putLong(null, holderAddr + 40, shapeHolderAddr);
    Platform.putLong(null, holderAddr + 48, strideHolderAddr);
  }

  private void _init(long arrayAddr, long shape, long stride) {
    Platform.putLong(null, dataAddr, arrayAddr);
    Platform.putLong(null, shapeHolderAddr, shape);
    Platform.putLong(null, strideHolderAddr, stride);
  }

  public long addressOf(boolean[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 1);
    return holderAddr;
  }

  public long addressOf(byte[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 1);
    return holderAddr;
  }

  public long addressOf(short[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 2);
    return holderAddr;
  }

  public long addressOf(int[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 4);
    return holderAddr;
  }

  public long addressOf(long[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 8);
    return holderAddr;
  }

  public long addressOf(float[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 4);
    return holderAddr;
  }

  public long addressOf(double[] ar) {
    _init(ArrayUtils.addressOf(ar), ar.length, 8);
    return holderAddr;
  }

  @Override
  public void close() throws Exception {
    Platform.freeMemory(holderAddr);
  }
}
