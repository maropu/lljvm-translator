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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.maropu.lljvm.LLJVMRuntimeException;

public final class FieldValue {

  private static final Logger logger = LoggerFactory.getLogger(FieldValue.class);

  private static Map<String, Object> externalFieldValues = new ConcurrentHashMap<>();

  private FieldValue() {}

  public static void put(String fieldName, Object value) {
    logger.debug("Numba Runtime field added: name=" + fieldName + " value=" + value);
    externalFieldValues.put(fieldName, value);
  }

  public static void remove(String fieldName) {
    externalFieldValues.remove(fieldName);
  }

  public static boolean exist(String fieldName) {
    return externalFieldValues.containsKey(fieldName);
  }

  public static void clear() {
    externalFieldValues.clear();
  }

  private static Object _get(String fieldName, Class<?> tpe) {
    Object fieldValue = null;
    if (externalFieldValues.containsKey(fieldName)) {
      fieldValue = externalFieldValues.get(fieldName);
    } else {
      throw new LLJVMRuntimeException(
        "Cannot resolve an external field for `" + fieldName + "`");
    }
    if (fieldValue.getClass() != tpe) {
      final String actualType = fieldValue.getClass().getSimpleName();
      final String expectedType = tpe.getSimpleName();
      throw new LLJVMRuntimeException(
        "Field '" + fieldName + "' found, but the type is " + actualType +
          " (expected: " + expectedType + ")");
    }
    logger.debug("Class field value referenced: name=" + fieldName + " value=" + fieldValue);
    return fieldValue;
  }

  public static boolean get_i1(String fieldName) {
    return (Boolean) _get(fieldName, java.lang.Boolean.class);
  }

  public static byte get_i8(String fieldName) {
    return (Byte) _get(fieldName, java.lang.Byte.class);
  }

  public static short get_i16(String fieldName) {
    return (Short) _get(fieldName, java.lang.Short.class);
  }

  public static int get_i32(String fieldName) {
    return (Integer) _get(fieldName, java.lang.Integer.class);
  }

  public static long get_i64(String fieldName) {
    return (Long) _get(fieldName, java.lang.Long.class);
  }

  public static float get_f32(String fieldName) {
    return (Float) _get(fieldName, java.lang.Float.class);
  }

  public static double get_f64(String fieldName) {
    return (Double) _get(fieldName, java.lang.Double.class);
  }
}
