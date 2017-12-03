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

public class PyArrayHolder {
  // TODO: Need to free memory below
  private final long holderAddr;
  private final long arrayAddr;
  private final long shapeHolderAddr;
  private final long strideHolderAddr;

  public PyArrayHolder() {
    // We assume that the aggregate(struct) type of python input arrays is
    // `{ i8*, i8*, i64, i64, double*, [1 x i64], [1 x i64] }`.
    int elemNumInAggType = 7;
    this.holderAddr = Platform.allocateMemory(elemNumInAggType * 8);
    this.shapeHolderAddr = Platform.allocateMemory(8);
    this.strideHolderAddr = Platform.allocateMemory(8);
    this.arrayAddr = holderAddr + 4 * 8;
    Platform.putLong(null, holderAddr + 5 * 8, shapeHolderAddr);
    Platform.putLong(null, holderAddr + 6 * 8, strideHolderAddr);
  }

  public long getHolderAddr() {
    return holderAddr;
  }

  public long getArrayAddr() {
    return arrayAddr;
  }

  public long getShapeHolderAddr() {
    return shapeHolderAddr;
  }

  public long getStrideHolderAddr() {
    return strideHolderAddr;
  }
}
