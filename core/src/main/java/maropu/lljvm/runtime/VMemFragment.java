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

public class VMemFragment {
  private final long base;
  private final long numBytes;

  private Stack<Long> stackFrames = new Stack<>();
  private long currentOffset;

  public VMemFragment(long baseAddr, long numBytes) {
    this.base = baseAddr;
    this.numBytes = numBytes;
    this.currentOffset = 0;
  }

  public void createStackFrame() {
    stackFrames.push(currentOffset);
  }

  public void destroyStackFrame() {
    assert(!stackFrames.isEmpty());
    this.currentOffset = stackFrames.pop();
  }

  public long getCapacity() {
    return numBytes;
  }

  public long getRemainingBytes() {
    return getCapacity() - currentOffset;
  }

  // For memory releases
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
