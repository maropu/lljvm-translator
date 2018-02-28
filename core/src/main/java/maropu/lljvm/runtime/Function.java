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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import maropu.lljvm.LLJVMRuntimeException;
import maropu.lljvm.util.Pair;
import maropu.lljvm.util.ReflectionUtils;

public class Function {

  private static Map<String, Method> externalFuncPointers = new ConcurrentHashMap<>();
  private static LoadingCache<Pair<String, String>, Method> methodCache =
      CacheBuilder.newBuilder()
        .maximumSize(100)
        .build(new CacheLoader<Pair<String, String>, Method>() {

    @Override
    public Method load(Pair<String, String> methodSignature) throws LLJVMRuntimeException {
      final String targetSignature = methodSignature.getKey() + "/" + methodSignature.getValue();
      try {
        Class<?> clazz = ReflectionUtils.getClass(methodSignature.getKey());
        for (Method method : ReflectionUtils.getStaticMethods(clazz)) {
          String candidate = ReflectionUtils.getQualifiedSignature(method);
          if (targetSignature.equals(candidate)) {
            return method;
          }
        }
      } catch (Exception e) {
        // Throws an exception in the end
      }
      throw new LLJVMRuntimeException("Method not found: " + targetSignature);
    }
  });

  static {
    for (Method m : ReflectionUtils.getPublicStaticMethods(NumbaRuntime.class)) {
      externalFuncPointers.put(ReflectionUtils.getSignature(m), m);
    }
  }

  private Function() {}

  public static void put(String methodSignature, Method m) {
    externalFuncPointers.put(methodSignature, m);
  }

  public static void remove(String methodSignature) {
    externalFuncPointers.remove(methodSignature);
  }

  private static Object invoke(Method method, long args) {
    Class<?>[] paramTypes = method.getParameterTypes();
    try {
      if (args != 0) {
        return method.invoke(null, VMemory.unpack(args, paramTypes));
      } else {
        return method.invoke(null, null);
      }
    } catch (Exception e) {
      throw new LLJVMRuntimeException("Cannot invoke a method via reflection");
    }
  }

  private static Object invoke(String className, String methodSignature, long args) {
    if (!className.isEmpty()) {
      synchronized (methodCache) {
        try {
          Method method = methodCache.get(new Pair<>(className, methodSignature));
          return invoke(method, args);
        } catch (ExecutionException e) {
          throw new LLJVMRuntimeException("Cannot load a method from the cache");
        }
      }
    } else {
      // Invokes an external function
      if (externalFuncPointers.containsKey(methodSignature)) {
        try {
          Method method = externalFuncPointers.get(methodSignature);
          return invoke(method, args);
        } catch (Exception e) {
          throw new LLJVMRuntimeException(
            "Cannot invoke an external function for `" + methodSignature + "`");
        }
      } else {
        throw new LLJVMRuntimeException(
          "Cannot resolve an external function for `" + methodSignature + "`");
      }
    }
  }

  public static void invoke_void(String className, String methodSignature, long args) {
    invoke(className, methodSignature, args);
  }

  public static void invoke_void(String className, String methodSignature) {
    invoke(className, methodSignature, 0);
  }

  public static boolean invoke_i1(String className, String methodSignature, long args) {
    return (Boolean) invoke(className, methodSignature, args);
  }

  public static byte invoke_i8(String className, String methodSignature, long args) {
    return (Byte) invoke(className, methodSignature, args);
  }

  public static short invoke_i16(String className, String methodSignature, long args) {
    return (Short) invoke(className, methodSignature, args);
  }

  public static int invoke_i32(String className, String methodSignature, long args) {
    return (Integer) invoke(className, methodSignature, args);
  }

  public static long invoke_i64(String className, String methodSignature, long args) {
    return (Long) invoke(className, methodSignature, args);
  }

  public static float invoke_f32(String className, String methodSignature, long args) {
    return (Float) invoke(className, methodSignature, args);
  }

  public static double invoke_f64(String className, String methodSignature, long args) {
    return (Double) invoke(className, methodSignature, args);
  }
}
