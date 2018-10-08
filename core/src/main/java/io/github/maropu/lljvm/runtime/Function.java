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

package io.github.maropu.lljvm.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.maropu.lljvm.LLJVMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.maropu.lljvm.LLJVMRuntimeException;
import io.github.maropu.lljvm.util.Pair;
import io.github.maropu.lljvm.util.ReflectionUtils;

public class Function {

  private static final Logger logger = LoggerFactory.getLogger(FieldValue.class);

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

  private Function() {}

  public static void put(String methodSignature, Method m) {
    externalFuncPointers.put(methodSignature, m);
  }

  public static void remove(String methodSignature) {
    externalFuncPointers.remove(methodSignature);
  }

  public static void clear() {
    externalFuncPointers.clear();
  }

  // TODO: Needs to verify a return type of the given `method`
  private static Object _invoke(Method method, long args) {
    Class<?>[] paramTypes = method.getParameterTypes();
    try {
      if (args != 0) {
        return method.invoke(null, VMemory.unpack(args, paramTypes));
      } else {
        return method.invoke(null, null);
      }
    } catch (IllegalAccessException e) {
      throw new LLJVMRuntimeException("Cannot invoke a method via reflection");
    } catch (InvocationTargetException e) {
      throw new LLJVMRuntimeException(e.getCause().getMessage());
    }
  }

  private static void validateMethod(Method method, String signature, Class<?>[] returnType) {
    boolean validated = false;
    for (Class<?> tpe : returnType) {
      if (tpe == method.getReturnType()) {
        validated = true;
      }
    }
    if (!validated) {
      throw new LLJVMRuntimeException("Method `" + method.getName() + "` found, " +
        "but the return type is " + method.getReturnType() +
        " (expected: " + LLJVMUtils.joinString(returnType, "/") + ")");
    }
    logger.debug("Function invoked: signature=" + signature + " returnType=" +
      method.getReturnType().getSimpleName());
  }

  private static Object _invoke(String className, String methodSignature, long args, Class<?>... returnType) {
    if (!className.isEmpty()) {
      // Invokes a static method in a class loaded via a classloader
      synchronized (methodCache) {
        try {
          Method method = methodCache.get(new Pair<>(className, methodSignature));
          validateMethod(method, methodSignature, returnType);
          return _invoke(method, args);
        } catch (ExecutionException e) {
          throw new LLJVMRuntimeException("Cannot load a method from the cache");
        }
      }
    } else {
      // Invokes an external function registered in `externalFucPointers`
      if (externalFuncPointers.containsKey(methodSignature)) {
        Method method = externalFuncPointers.get(methodSignature);
        validateMethod(method, methodSignature, returnType);
        return _invoke(method, args);
      } else {
        throw new LLJVMRuntimeException(
          "Cannot resolve an external function for `" + methodSignature + "`");
      }
    }
  }

  public static void invoke_void(String className, String methodSignature, long args) {
    _invoke(className, methodSignature, args, Void.TYPE, Void.class);
  }

  public static void invoke_void(String className, String methodSignature) {
    invoke_void(className, methodSignature, 0);
  }

  public static boolean invoke_i1(String className, String methodSignature, long args) {
    return (Boolean) _invoke(className, methodSignature, args, Boolean.TYPE, Boolean.class);
  }

  public static boolean invoke_i1(String className, String methodSignature) {
    return invoke_i1(className, methodSignature, 0);
  }

  public static byte invoke_i8(String className, String methodSignature, long args) {
    return (Byte) _invoke(className, methodSignature, args, Byte.TYPE, Byte.class);
  }

  public static byte invoke_i8(String className, String methodSignature) {
    return invoke_i8(className, methodSignature, 0);
  }

  public static short invoke_i16(String className, String methodSignature, long args) {
    return (Short) _invoke(className, methodSignature, args, Short.TYPE);
  }

  public static short invoke_i16(String className, String methodSignature) {
    return invoke_i16(className, methodSignature, 0);
  }

  public static int invoke_i32(String className, String methodSignature, long args) {
    return (Integer) _invoke(className, methodSignature, args, Integer.TYPE, Integer.class);
  }

  public static int invoke_i32(String className, String methodSignature) {
    return invoke_i32(className, methodSignature, 0);
  }

  public static long invoke_i64(String className, String methodSignature, long args) {
    return (Long) _invoke(className, methodSignature, args, Long.TYPE, Long.class);
  }

  public static long invoke_i64(String className, String methodSignature) {
    return invoke_i64(className, methodSignature, 0);
  }

  public static float invoke_f32(String className, String methodSignature, long args) {
    return (Float) _invoke(className, methodSignature, args, Float.TYPE, Float.class);
  }

  public static float invoke_f32(String className, String methodSignature) {
    return invoke_f32(className, methodSignature, 0);
  }

  public static double invoke_f64(String className, String methodSignature, long args) {
    return (Double) _invoke(className, methodSignature, args, Double.TYPE, Double.class);
  }

  public static double invoke_f64(String className, String methodSignature) {
    return invoke_f64(className, methodSignature, 0);
  }
}
