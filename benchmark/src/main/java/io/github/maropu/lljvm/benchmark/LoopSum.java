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

package io.github.maropu.lljvm.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import io.github.maropu.lljvm.LLJVMClassLoader;
import io.github.maropu.lljvm.LLJVMUtils;
import io.github.maropu.lljvm.unsafe.Platform;
import io.github.maropu.lljvm.util.python.PyArrayHolder;

// -- c4.2xlarge:
// -- openjdk 1.8.0_151
// Benchmark             Mode  Cnt     Score    Error  Units
// LoopSum.pySum1        avgt   10  5213.111 ? 61.576  ns/op
// LoopSum.pySum2        avgt   10  1013.178 ?  1.960  ns/op
// LoopSum.javaSum       avgt   10   940.134 ?  0.041  ns/op
// LoopSum.heapBufSum    avgt   10  1864.531 ?  1.131  ns/op
// LoopSum.directBufSum  avgt   10   964.012 ?  0.133  ns/op
// LoopSum.unsafeBufSum  avgt   10   938.124 ?  0.104  ns/op
//
// -- openjdk 9.0.1
// Benchmark             Mode  Cnt     Score     Error  Units
// LoopSum.pySum1        avgt   10  5852.799 ?  32.757  ns/op
// LoopSum.pySum2        avgt   10  1008.902 ?   3.445  ns/op
// LoopSum.javaSum       avgt   10   938.333 ?   0.133  ns/op
// LoopSum.heapBufSum    avgt   10   966.682 ?   0.077  ns/op
// LoopSum.directBufSum  avgt   10   964.405 ?   0.176  ns/op
// LoopSum.unsafeBufSum  avgt   10   934.953 ?   0.069  ns/op
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, jvmArgsAppend = {
  "-XX:+UseSuperWord",
  "-XX:+UnlockDiagnosticVMOptions",
  "-XX:CompileCommand=print,*LoopSum.*",
  // "-XX:PrintAssembly", // Print all the assembly
  "-XX:PrintAssemblyOptions=intel"})
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class LoopSum {
  // Set a small value for CPU-intensive tests
  final static int SIZE = 1024;
  final static PyArrayHolder pyArray = PyArrayHolder.create();

  @State(Scope.Thread)
  public static class Context {
    public final float[] javaArray = new float[SIZE];
    public final ByteBuffer heapBuf = ByteBuffer.allocate(4 * SIZE);
    public final ByteBuffer directBuf = ByteBuffer.allocateDirect(4 * SIZE);
    public final long unsafeBuf = Platform.allocateMemory(4 * SIZE);

    // For python functions
    public Method pyAdd;
    public Method pySum;

    private byte[] resourceToBytes(String resource) {
      ByteArrayOutputStream outStream = new ByteArrayOutputStream();
      try (InputStream inStream = Thread.currentThread().getContextClassLoader()
          .getResourceAsStream(resource)) {
        boolean reading = true;
        while (reading) {
          int in = inStream.read();
          if (in == -1) {
            reading = false;
          } else {
            outStream.write(in);
          }
        }
        outStream.flush();
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
      return outStream.toByteArray();
    }

    @Setup
    public void setup() {
      // Initialize the input with the same data
      Random random = new Random();
      for (int i = 0; i < SIZE; i++) {
        float value = random.nextFloat() % 32;
        javaArray[i] = value;
        heapBuf.putFloat(4 * i, value);
        directBuf.putFloat(4 * i, value);
        Platform.putFloat(null, unsafeBuf + 4 * i, value);
      }

      // For python functions
      try {
        Class<?> clazz1 = new LLJVMClassLoader()
          .loadClassFromBitcode(resourceToBytes("benchmark/pyAdd-float32.bc"));
        this.pyAdd = LLJVMUtils.getMethod(clazz1, Float.TYPE, Float.TYPE);
        Class<?> clazz2 = new LLJVMClassLoader()
          .loadClassFromBitcode(resourceToBytes("benchmark/pySum-float32.bc"));
        this.pySum = LLJVMUtils.getMethod(clazz2, Long.TYPE);
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE) // This makes looking at assembly easier
  public float pySum1(Context context) {
    try {
      // def pyAdd(a, b):
      //   return a + b
      float sum = 0;
      for (int i = 0; i < SIZE; i++) {
        sum = (float) context.pyAdd.invoke(null, sum, context.javaArray[i]);
      }
    return sum;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0f;
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public float pySum2(Context context) {
    try {
      // def pySum(x):
      //   (s, ) = x.shape
      //   sum = 0.0
      //   for i in range(s):
      //     sum += x[i]
      //   return sum
      return (float) context.pySum.invoke(null, pyArray.with(context.javaArray));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0f;
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public float javaSum(Context context) {
    float sum = 0;
    for (int i = 0; i < SIZE; i++) {
      sum += context.javaArray[i];
    }
    return sum;
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public float heapBufSum(Context context) {
    float sum = 0;
    for (int i = 0; i < SIZE; i++) {
      sum += context.heapBuf.getFloat(4 * i);
    }
    return sum;
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public float directBufSum(Context context) {
    float sum = 0;
    for (int i = 0; i < SIZE; i++) {
      sum += context.directBuf.getFloat(4 * i);
    }
    return sum;
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public float unsafeBufSum(Context context) {
    float sum = 0;
    for (int i = 0; i < SIZE; i++) {
      sum += Platform.getFloat(null, context.unsafeBuf + 4 * i);
    }
    return sum;
  }
}
