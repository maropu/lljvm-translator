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

package io.github.maropu.lljvm.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import io.github.maropu.lljvm.LLJVMClassLoader;

/**
 * Provides methods for obtaining reflective information about classes.
 * 
 * @author  David Roberts
 */
public final class ReflectionUtils {

  /**
   * Prevent this class from being instantiated.
   */
  private ReflectionUtils() {}

  /**
   * Returns the class with the specified binary name.
   *
   * @param name  the binary name of the class to return
   * @return      the class with the specified binary name
   */
  public static Class<?> getClass(String name) throws ClassNotFoundException {
    final String normalizedName = name.replace('/', '.');
    ClassLoader classLoader = null;
    try {
      classLoader = new URLClassLoader(new URL[] { new File(".").toURI().toURL() });
    } catch (MalformedURLException e) {
      classLoader = ClassLoader.getSystemClassLoader();
    }
    try {
      return classLoader.loadClass(normalizedName);
    } catch (Exception e) {
      return LLJVMClassLoader.currentClassLoader.get().loadClass(name);
    }
  }

  /**
   * Returns a list of the static methods contained in the given array of methods.
   *
   * @param allMethods  the array of methods to filter
   * @return            the list of static methods
   */
  private static List<Method> getStaticMethods(Method[] allMethods) {
    List<Method> methods = new ArrayList<>();
    for (Method method : allMethods) {
      if (Modifier.isStatic(method.getModifiers())) {
        methods.add(method);
      }
    }
    return methods;
  }

  /**
   * Returns a list of the public static methods provided by the specified class.
   *
   * @param cls  the class providing the methods
   * @return     the list of the public static methods provided by the specified class
   */
  public static List<Method> getPublicStaticMethods(Class<?> cls) {
    return getStaticMethods(cls.getMethods());
  }

  /**
   * Returns a list of the static methods provided by the specified class.
   *
   * @param cls  the class providing the methods
   * @return     the list of the static methods provided by the specified class
   */
  public static List<Method> getStaticMethods(Class<?> cls) {
    return getStaticMethods(cls.getDeclaredMethods());
  }

  /**
   * Returns a list of the public static fields provided by the specified
   * class.
   *
   * @param cls  the class providing the fields
   * @return     the list of the public static fields provided by the specified class
   */
  public static List<Field> getPublicStaticFields(Class<?> cls) {
    List<Field> fields = new ArrayList<>();
    for (Field field : cls.getFields()) {
      int modifiers = field.getModifiers();
      if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
        fields.add(field);
      }
    }
    return fields;
  }

  /**
   * Returns the type descriptor of the specified type.
   *
   * @param cls  the Class representing the specifed type
   * @return     the type descriptor of the specified type
   */
  public static String getDescriptor(Class<?> cls) {
    if (cls == void.class) return "V";
    if (cls == boolean.class) return "Z";
    if (cls == byte.class) return "B";
    if (cls == char.class) return "C";
    if (cls == short.class) return "S";
    if (cls == int.class) return "I";
    if (cls == long.class) return "J";
    if (cls == float.class) return "F";
    if (cls == double.class) return "D";
    if (cls.isArray()) {
      return cls.getName().replace('.', '/');
    }
    return "L" + cls.getName().replace('.', '/') + ";";
  }

  /**
   * Returns the type signature of the given method.
   *
   * @param method  the given method
   * @return        the type signature of the given method
   */
  public static String getSignature(Method method) {
    StringBuilder builder = new StringBuilder();
    builder.append(method.getName());
    builder.append("(");
    for(Class<?> param : method.getParameterTypes()) {
      builder.append(getDescriptor(param));
    }
    builder.append(")");
    builder.append(getDescriptor(method.getReturnType()));
    return builder.toString();
  }

  /**
   * Returns the qualified signature of the given method.
   *
   * @param method  the given method
   * @return        the qualified signature of the given method
   */
  public static String getQualifiedSignature(Method method) {
    return method.getDeclaringClass().getName().replace('.', '/') + "/" + getSignature(method);
  }

  public static int sizeOf(Class<?> cls) {
    if (cls == boolean.class) return 1;
    if (cls == byte.class) return 1;
    if (cls == char.class) return 2;
    if (cls == short.class) return 2;
    if (cls == int.class) return 4;
    if (cls == long.class) return 8;
    if (cls == float.class) return 4;
    if (cls == double.class) return 8;
    throw new IllegalArgumentException("Cannot request size of non-primitive type");
  }
}
