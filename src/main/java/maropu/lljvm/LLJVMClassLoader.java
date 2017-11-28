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

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import jasmin.ClassFile;

/**
 * A custom class loader to handle classes generated on-runtime.
 */
public class LLJVMClassLoader extends ClassLoader {

  // This class is possibly accessed by `ReflectionUtils`
  public static ThreadLocal<LLJVMClassLoader> currentClassLoader = new ThreadLocal<>();

  static {
    currentClassLoader.set(new LLJVMClassLoader());
  }

  private final Map<String, Class<?>> generatedClassMap;
  private ClassLoader parent;

  public LLJVMClassLoader() {
    this.generatedClassMap = new HashMap<>();
    this.parent = Thread.currentThread().getContextClassLoader();
  }

  public LLJVMClassLoader(Map<String, Class<?>> classMap) {
    this.generatedClassMap = classMap;
    this.parent = Thread.currentThread().getContextClassLoader();
  }

  public LLJVMClassLoader(ClassLoader parent) {
    this.generatedClassMap = new HashMap<>();
    this.parent = parent;
  }

  public LLJVMClassLoader(Map<String, Class<?>> classMap, ClassLoader parent) {
    this.generatedClassMap = classMap;
    this.parent = parent;
  }

  @Override
  protected Class<?> findClass(String className) throws ClassNotFoundException {
    try {
      Method method = ClassLoader.class.getDeclaredMethod("findClass", String.class);
      method.setAccessible(true);
      return (Class<?>) method.invoke(parent, className);
    } catch (Exception e) {
      Class<?> clazz = generatedClassMap.get(className);
      if (clazz != null) {
        return clazz;
      }
      throw new ClassNotFoundException(className);
    }
  }

  public Class<?> loadClassFromBytecode(String className, byte[] bytecode) {
    return defineClass(className, bytecode, 0, bytecode.length);
  }

  public Class<?> loadClassFromBitcode(String className, byte[] bitcode)
      throws IOException, LLJVMRuntimeException {
    String bytecode = LLJVMUtils.asBytecode(bitcode);
    ClassFile classFile = new ClassFile();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (Reader in = new InputStreamReader(
        new ByteArrayInputStream(bytecode.getBytes(StandardCharsets.UTF_8)))) {
      classFile.readJasmin(in, className, false);
      classFile.write(out);
    } catch (Exception e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
    return loadClassFromBytecode(className, out.toByteArray());
  }

  public Class<?> loadClassFromBytecodeFile(String className, String classFile)
      throws IOException, LLJVMRuntimeException {
    byte[] bytecode = Files.readAllBytes(new File(classFile).toPath());
    return loadClassFromBytecode(className, bytecode);
  }

  public Class<?> loadClassFromBitcodeFile(String className, String bitcodeFile)
      throws IOException, LLJVMRuntimeException {
    byte[] bitcode = Files.readAllBytes(new File(bitcodeFile).toPath());
    return loadClassFromBitcode(className, bitcode);
  }
}
