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

import lljvm.unsafe.Platform;
import maropu.lljvm.LLJVMClassLoader;
import maropu.lljvm.LLJVMUtils;

// -- c4.2xlarge:
// -- openjdk 1.8.0_151
// Benchmark             Mode  Cnt     Score    Error  Units
// LoopAdd.pyAdd         avgt   10  6592.447 ? 49.295  ns/op
// LoopAdd.javaAdd       avgt   10   216.251 ?  0.226  ns/op
// LoopAdd.heapBufAdd    avgt   10  4865.125 ?  0.702  ns/op
// LoopAdd.directBufAdd  avgt   10  1236.636 ?  1.683  ns/op
// LoopAdd.unsafeBufAdd  avgt   10   369.959 ?  0.020  ns/op
//
// -- openjdk 9.0.1
// Benchmark             Mode  Cnt     Score     Error  Units
// LoopAdd.pyAdd         avgt   10  6744.503 ? 126.727  ns/op
// LoopAdd.javaAdd       avgt   10   165.704 ?   0.020  ns/op
// LoopAdd.heapBufAdd    avgt   10  1040.211 ?   0.665  ns/op
// LoopAdd.directBufAdd  avgt   10  1220.033 ?   1.314  ns/op
// LoopAdd.unsafeBufAdd  avgt   10   370.250 ?   0.367  ns/op
@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, jvmArgsAppend = {
  "-XX:+UseSuperWord",
  "-XX:+UnlockDiagnosticVMOptions",
  "-XX:CompileCommand=print,*LoopAdd.*",
  // "-XX:PrintAssembly", // Print all the assembly
  "-XX:PrintAssemblyOptions=intel"})
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class LoopAdd {
  // Set a small value for CPU-intensive tests
  final static int SIZE = 1024;

  @State(Scope.Thread)
  public static class Context {
    public final float[] javaArray1 = new float[SIZE];
    public final float[] javaArray2 = new float[SIZE];
    public final float[] javaArray3 = new float[SIZE];

    public final ByteBuffer heapBuf1 = ByteBuffer.allocate(4 * SIZE);
    public final ByteBuffer heapBuf2 = ByteBuffer.allocate(4 * SIZE);
    public final ByteBuffer heapBuf3 = ByteBuffer.allocate(4 * SIZE);

    public final ByteBuffer directBuf1 = ByteBuffer.allocateDirect(4 * SIZE);
    public final ByteBuffer directBuf2 = ByteBuffer.allocateDirect(4 * SIZE);
    public final ByteBuffer directBuf3 = ByteBuffer.allocateDirect(4 * SIZE);

    public final long unsafeBuf1 = Platform.allocateMemory(4 * SIZE);
    public final long unsafeBuf2 = Platform.allocateMemory(4 * SIZE);
    public final long unsafeBuf3 = Platform.allocateMemory(4 * SIZE);

    // For python functions
    public Method pyAdd;

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
        javaArray1[i] = value;
        javaArray2[i] = value;
        heapBuf1.putFloat(4 * i, value);
        heapBuf2.putFloat(4 * i, value);
        directBuf1.putFloat(4 * i, value);
        directBuf2.putFloat(4 * i, value);
        Platform.putFloat(null, unsafeBuf1 + 4 * i, value);
        Platform.putFloat(null, unsafeBuf2 + 4 * i, value);
      }

      // For python functions
      try {
        Class<?> clazz1 = new LLJVMClassLoader()
          .loadClassFromBitcode("GeneratedClass", resourceToBytes("benchmark/pyAdd-float32.bc"));
        this.pyAdd = LLJVMUtils.getMethod(
          clazz1, "_cfunc__ZN8__main__9pyAdd_241Eff",
          Float.TYPE, Float.TYPE);
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE) // This makes looking at assembly easier
  public void pyAdd(Context context) {
    try {
      // def pyAdd(a, b):
      // return a + b
      for (int i = 0; i < SIZE; i++) {
        context.javaArray3[i] =
          (float) context.pyAdd.invoke(null, context.javaArray1[i], context.javaArray2[i]);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public void javaAdd(Context context) {
    for (int i = 0; i < SIZE; i++) {
      context.javaArray3[i] += context.javaArray1[i] + context.javaArray2[i];
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public void heapBufAdd(Context context) {
    for (int i = 0; i < SIZE; i++) {
      float v = context.heapBuf1.getFloat(4 * i) + context.heapBuf2.getFloat(4 * i);
      context.heapBuf3.putFloat(4 * i, v);
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public void directBufAdd(Context context) {
    for (int i = 0; i < SIZE; i++) {
      float v = context.directBuf1.getFloat(4 * i) + context.directBuf2.getFloat(4 * i);
      context.directBuf3.putFloat(4 * i, v);
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE)
  public void unsafeBufAdd(Context context) {
    for (int i = 0; i < SIZE; i++) {
      float v = Platform.getFloat(null, context.unsafeBuf1 + 4 * i)
        + Platform.getFloat(null, context.unsafeBuf1 + 4 * i);
      Platform.putFloat(null, context.unsafeBuf3 + 4 * i, v);
    }
  }
}
