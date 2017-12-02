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

import maropu.lljvm.ArrayUtils;
import maropu.lljvm.LLJVMClassLoader;
import maropu.lljvm.LLJVMUtils;

public class PySum {

  private final Method method;

  public PySum() {
    final byte[] bytecode = resourceToBytes("benchmark/pysum-float64.class");
    Class<?> clazz = new LLJVMClassLoader().loadClassFromBytecode("GeneratedClass", bytecode);
    this.method = LLJVMUtils.getMethod(
      clazz, "_cfunc__ZN8__main__9pySum_241E5ArrayIdLi1E1A7mutable7alignedEi",
      Long.TYPE, Integer.TYPE);
  }

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

  public double run(double[] ar) {
    try {
      return (double) method.invoke(null, ArrayUtils.pyAyray(ar), ar.length);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  public static void main(String[] args) {
    double [] values = new double [1024];
    Random random = new Random();
    for (int i = 0; i < values.length; i++) {
      values[i] = random.nextInt(Integer.MAX_VALUE / 32);
    }

    // Run the gen'd function
    final PySum pysum = new PySum();
    int nIterations = 100000;
    System.out.println(String.format("Run PySum %s times", nIterations));
    for (int i = 0; i < nIterations; i++) {
      pysum.run(values);
    }
  }
}
