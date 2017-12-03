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

package maropu.lljvm.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lljvm.unsafe.Platform;
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

import maropu.lljvm.ArrayUtils;
import maropu.lljvm.LLJVMClassLoader;
import maropu.lljvm.LLJVMUtils;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, jvmArgsAppend = {
  "-XX:+UseSuperWord",
  "-XX:+UnlockDiagnosticVMOptions",
  "-XX:CompileCommand=print,*LoopVectorization.*",
  // "-XX:PrintAssembly", // Print all the assembly
  "-XX:PrintAssemblyOptions=intel"})
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class LoopSum {
  // Set a small value for CPU-intensive tests
  final static int SIZE = 1024;

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
      } catch (Exception e) {
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
          .loadClassFromBitcode("GeneratedClass", resourceToBytes("benchmark/pyAdd-float32.bc"));
        this.pyAdd = LLJVMUtils.getMethod(
          clazz1, "_cfunc__ZN8__main__9pyAdd_241Eff",
          Float.TYPE, Float.TYPE);
        Class<?> clazz2 = new LLJVMClassLoader()
          .loadClassFromBitcode("GeneratedClass", resourceToBytes("benchmark/pySum-float32.bc"));
        this.pySum = LLJVMUtils.getMethod(
          clazz2, "_cfunc__ZN8__main__9pySum_242E5ArrayIfLi1E1A7mutable7alignedEi",
          Long.TYPE, Integer.TYPE);
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
      // return a + b
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
      // def pySum(x, s):
      // sum = 0
      // for i in range(s):
      // sum += x[i]
      // return sum
      return (float) context.pySum.invoke(null, ArrayUtils.pyAyray(context.javaArray), SIZE);
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
