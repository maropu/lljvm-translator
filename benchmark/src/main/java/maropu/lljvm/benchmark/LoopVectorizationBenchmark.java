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
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import maropu.lljvm.ArrayUtils;
import maropu.lljvm.LLJVMClassLoader;
import maropu.lljvm.LLJVMUtils;
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

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@Fork(value = 1, jvmArgsAppend = {
  "-XX:+UseSuperWord",
  "-XX:+UnlockDiagnosticVMOptions",
  "-XX:CompileCommand=print,*LoopVectorizationBenchmark.pySum",
  "-XX:PrintAssemblyOptions=intel"})
@Warmup(iterations = 5)
@Measurement(iterations = 10)
public class LoopVectorizationBenchmark {
  public static final int SIZE = 1024;


  @State(Scope.Thread)
  public static class Context {
    public final int[] values = new int[SIZE];

    public Method method;

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
      Random random = new Random();
      for (int i = 0; i < SIZE; i++) {
        values[i] = random.nextInt(Integer.MAX_VALUE / 32);
      }
      final byte[] bytecode = resourceToBytes("benchmark/pysum-float64.class");
      Class<?> clazz = new LLJVMClassLoader().loadClassFromBytecode("GeneratedClass", bytecode);
      this.method = LLJVMUtils.getMethod(
        clazz, "_cfunc__ZN8__main__9pySum_241E5ArrayIdLi1E1A7mutable7alignedEi",
        Long.TYPE, Integer.TYPE);
    }
  }

  @Benchmark
  @CompilerControl(CompilerControl.Mode.DONT_INLINE) // This makes looking at assembly easier
  public double pySum(Context context) {
    try {
      return (double) context.method.invoke(null, ArrayUtils.pyAyray(context.values), SIZE);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }
}
