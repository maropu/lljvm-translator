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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import maropu.lljvm.LLJVMRuntimeException;
import maropu.lljvm.util.ReflectionUtils;

public class FieldValue {

  private static Map<String, Object> externalFieldValues = new ConcurrentHashMap<>();

  static {
    for (Field f : ReflectionUtils.getPublicStaticFields(NumbaRuntime.class)) {
      try {
        externalFieldValues.put(f.getName(), f.get(null));
      } catch (IllegalAccessException e) {
        // Just ignores it
      }
    }
  }

  public static void put(String fieldName, Object value) {
    externalFieldValues.put(fieldName, value);
  }

  public static void remove(String fieldName) {
    externalFieldValues.remove(fieldName);
  }

  private static Object _get(String fieldName) {
    if (externalFieldValues.containsKey(fieldName)) {
      return externalFieldValues.get(fieldName);
    } else {
      throw new LLJVMRuntimeException(
        "Cannot resolve an external field for `" + fieldName + "`");
    }
  }

  public static byte get_i8(String fieldName) {
    return (byte) _get(fieldName);
  }

  public static short get_i16(String fieldName) {
    return (short) _get(fieldName);
  }

  public static int get_i32(String fieldName) {
    return (int) _get(fieldName);
  }

  public static long get_i64(String fieldName) {
    return (long) _get(fieldName);
  }

  public static float get_f32(String fieldName) {
    return (float) _get(fieldName);
  }

  public static double get_f64(String fieldName) {
    return (double) _get(fieldName);
  }
}
