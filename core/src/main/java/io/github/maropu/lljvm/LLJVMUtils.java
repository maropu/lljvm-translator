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

package io.github.maropu.lljvm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import io.github.maropu.lljvm.runtime.LLJVMRuntime;

public class LLJVMUtils {

  public static void checkIfLLJVMRuntimeInitialized() {
    if (!LLJVMRuntime.isInitialized) {
      throw new LLJVMRuntimeException(
        "To initialize the runtime, you need to call `LLJVMRuntime.initialize()` first");
    }
  }

  // TODO: Rechecks the LLVM bitcode format
  // For Numba-generated bitcode
  static final byte[] expectedBitcodeHeader1 = { -34, -64, 23, 11 };
  // For Clang-generated bitcode
  static final byte[] expectedBitcodeHeader2 = { 66, 67, -64, -34 };

  @VisibleForTesting
  public static void checkLLVMBitcodeFormat(byte[] bitcode) {
    // A format of LLVM bitcode is as follows:
    //  - https://llvm.org/docs/BitCodeFormat.html
    if (bitcode.length < 4 || !(
        // This magic number in only valid for `target triple = "x86_64-apple-macosx10.12.0"`?
        (bitcode[0] == expectedBitcodeHeader1[0] || bitcode[0] == expectedBitcodeHeader2[0]) &&
        (bitcode[1] == expectedBitcodeHeader1[1] || bitcode[1] == expectedBitcodeHeader2[1]) &&
        (bitcode[2] == expectedBitcodeHeader1[2] || bitcode[2] == expectedBitcodeHeader2[2]) &&
        (bitcode[3] == expectedBitcodeHeader1[3] || bitcode[3] == expectedBitcodeHeader2[3]))) {
      final byte b1 = (bitcode.length > 0)? bitcode[0] : (byte) 0;
      final byte b2 = (bitcode.length > 1)? bitcode[1] : (byte) 0;
      final byte b3 = (bitcode.length > 2)? bitcode[2] : (byte) 0;
      final byte b4 = (bitcode.length > 3)? bitcode[3] : (byte) 0;
      final String expectedHeader = "-34,-64,23,11 or 66,67,-64,-34";
      final String invalidHeader = String.format("%d,%d,%d,%d", b1, b2, b3, b4);
      throw new LLJVMRuntimeException(String.format(
        "Expected bitcode header is %s, but %s found", expectedHeader, invalidHeader));
    }
    try {
      LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
      lljvmApi.veryfyBitcode(bitcode);
    } catch (IOException e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
  }

  public static String asLLVMAssemblyCode(byte[] bitcode) throws LLJVMRuntimeException {
    try {
      LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
      // TODO: Uses `-O0` because of unresolved bugs
      return lljvmApi.asLLVMAssemblyCode(bitcode, 0, 0);
    } catch (IOException e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
  }

  public static String asJVMAssemblyCode(byte[] bitcode) throws LLJVMRuntimeException {
    checkLLVMBitcodeFormat(bitcode);
    String jvmAsm = null;
    try {
      LLJVMNative lljvmApi = LLJVMLoader.loadLLJVMApi();
      // TODO: Uses `-O0` because of unresolved bugs
      jvmAsm = lljvmApi.asJVMAssemblyCode(bitcode, 0, 0, 0);
    } catch (IOException e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
    assert(jvmAsm != null);
    return jvmAsm;
  }

  public static List<Method> getAllMethods(Class<?> clazz) throws LLJVMRuntimeException {
    List<Method> methods = new ArrayList<>();
    for (Method m : clazz.getDeclaredMethods()) {
      if (Modifier.isPublic(m.getModifiers())) {
        methods.add(m);
      }
    }
    return methods;
  }

  public static String joinString(List<String> list, String delim) {
    int len = list.size();
    if (len == 0)
      return "";
    StringBuilder sb = new StringBuilder(list.get(0));
    for (int i = 1; i < len; i++) {
      sb.append(delim);
      sb.append(list.get(i));
    }
    return sb.toString();
  }

  public static String joinString(Object[] list, String delim) {
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

  public static Object invoke(Class<?> clazz, String methodName, Object... args) {
    Class[] argTypes = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      if (args[i] instanceof java.lang.Short) {
        argTypes[i] = java.lang.Short.TYPE;
      } else if (args[i] instanceof java.lang.Integer) {
        argTypes[i] = java.lang.Integer.TYPE;
      } else if (args[i] instanceof java.lang.Long) {
        argTypes[i] = java.lang.Long.TYPE;
      } else if (args[i] instanceof java.lang.Float) {
        argTypes[i] = java.lang.Float.TYPE;
      } else if (args[i] instanceof java.lang.Double) {
        argTypes[i] = java.lang.Double.TYPE;
      } else if (args[i] instanceof java.lang.Boolean) {
        argTypes[i] = java.lang.Boolean.TYPE;
      } else {
        throw new LLJVMRuntimeException(
          "Unsupported argument type: " + args[i].getClass().getSimpleName());
      }
    }
    Method method = getMethod(clazz, methodName, argTypes);
    try {
      return method.invoke(null, args);
    } catch (Exception e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
  }

  public static Object invoke(Class<?> clazz, Object... args) {
    return invoke(clazz, "", args);
  }

  private static void throwNotFoundMethodException(String name,  Class<?>... argTypes) {
    final String notFoundMethod = String.format("%s(%s)", name, joinString(argTypes, ", "));
    throw new LLJVMRuntimeException("Method not found: " + notFoundMethod);
  }

  public static Method getMethod(Class<?> clazz, String methodName, Class<?>... argTypes)
      throws LLJVMRuntimeException {
    for (Method m : clazz.getDeclaredMethods()) {
      if (Modifier.isPublic(m.getModifiers()) && Arrays.equals(m.getParameterTypes(), argTypes)) {
        if (methodName.isEmpty() || m.getName().equals(methodName)) {
          return m;
        }
      }
    }
    throwNotFoundMethodException(methodName, argTypes);
    return null; // Not hit here
  }

  public static Method getMethod(Class<?> clazz, Class<?>... argTypes)
      throws LLJVMRuntimeException {
    return getMethod(clazz, "", argTypes);
  }

  /**
   * Indicates whether Spark is currently running unit tests.
   */
  public static boolean isTesting() {
    return System.getenv().containsKey("LLJVM_TESTING") ||
      System.getProperties().containsKey("lljvm.testing");
  }
}
