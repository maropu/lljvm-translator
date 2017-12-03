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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LLJVMUtils {

  public static void checkLLVMBitcodeFormat(byte[] bitcode) {
    // A format of LLVM bitcode is as follows:
    //  - https://llvm.org/docs/BitCodeFormat.html
    if (bitcode.length < 4 ||
        !(bitcode[0] == -34 && bitcode[1] == -64 && bitcode[2] == 23 && bitcode[3] == 11)) {
      throw new LLJVMRuntimeException("Corrupt LLVM bitcode found");
    }

    // try {
    //   LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
    //   lljvmApi.veryfyBitcode(bitcode);
    // } catch (Exception e) {
    //   throw new LLJVMRuntimeException(e.getMessage());
    // }
  }

  public static String asBitcode(byte[] bitcode) throws LLJVMRuntimeException {
    try {
      LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
      return lljvmApi.asBitcode(bitcode);
    } catch (Exception e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
  }

  public static String asBytecode(byte[] bitcode) throws LLJVMRuntimeException {
    checkLLVMBitcodeFormat(bitcode);
    String bytecode = null;
    try {
      LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
      bytecode = lljvmApi.parseBitcode(bitcode);
    } catch (Exception e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
    assert(bytecode != null);
    return bytecode;
  }

  public static List<Method> getAllMethods(Class<?> clazz) throws LLJVMRuntimeException {
    List<Method> methods = new ArrayList<>();
    try {
      for (Method m : clazz.getDeclaredMethods()) {
        if (Modifier.isPublic(m.getModifiers())) {
          methods.add(m);
        }
      }
    } catch (Throwable e) { // All the error states caught here
      throw new LLJVMRuntimeException("Illegal bytecode found: " + e.getMessage());
    }
    return methods;
  }

  private static String joinString(Object[] list, String delim) {
    int len = list.length;
    if (len == 0)
      return "";
    StringBuilder sb = new StringBuilder(list[0].toString());
    for (int i = 1; i < len; i++) {
      sb.append(delim);
      sb.append(list[i].toString());
    }
    return sb.toString();
  }

  public static Method getMethod(Class<?> clazz, String methodName, Class<?>... signature)
      throws LLJVMRuntimeException {
    try {
      for (Method m : clazz.getDeclaredMethods()) {
        if (Modifier.isPublic(m.getModifiers()) &&
            m.getName().equals(methodName) && Arrays.equals(m.getParameterTypes(), signature)) {
          return m;
        }
      }
    } catch (Throwable e) { // All the error states caught here
      throw new LLJVMRuntimeException("Illegal bytecode found: " + e.getMessage());
    }
    String notFoundMethod = String.format("%s(%s)", methodName, joinString(signature, ", "));
    throw new LLJVMRuntimeException("Method not found: " + notFoundMethod);
  }

  public static List<Method> findMethods(Class<?> clazz, String methodName, Class<?>... signature) {
    List<Method> methods = new ArrayList<>();
    try {
      for (Method m : clazz.getDeclaredMethods()) {
        if (Modifier.isPublic(m.getModifiers()) &&
            m.getName().contains(methodName) && Arrays.equals(m.getParameterTypes(), signature)) {
          methods.add(m);
        }
      }
    } catch (Throwable e) { // All the error states caught here
      throw new LLJVMRuntimeException("Illegal bytecode found: " + e.getMessage());
    }
    return methods;
  }
}
