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

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import io.github.maropu.lljvm.util.JVMAssembler;
import io.github.maropu.lljvm.util.analysis.BytecodeVerifier;

/**
 * A custom class loader to handle classes generated on-runtime.
 */
public class LLJVMClassLoader extends ClassLoader {

  // This class is possibly accessed by `ReflectionUtils`
  public static ThreadLocal<LLJVMClassLoader> currentClassLoader =
      new ThreadLocal<LLJVMClassLoader>() {

    @Override
    public LLJVMClassLoader initialValue() {
      return new LLJVMClassLoader();
    }
  };

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

  public Class<?> loadClassFromBytecode(byte[] bytecode) {
    LLJVMUtils.checkIfLLJVMRuntimeInitialized();
    BytecodeVerifier.verify(bytecode);
    return defineClass(JVMAssembler.LLJVM_GENERATED_CLASSNAME, bytecode, 0, bytecode.length);
  }

  public Class<?> loadClassFromBitcode(byte[] bitcode) throws IOException, LLJVMRuntimeException {
    LLJVMUtils.checkIfLLJVMRuntimeInitialized();
    byte[] bytecode = JVMAssembler.compile(LLJVMUtils.asJVMAssemblyCode(bitcode));
    return defineClass(JVMAssembler.LLJVM_GENERATED_CLASSNAME, bytecode, 0, bytecode.length);
  }

  public Class<?> loadClassFromBytecodeFile(String classFile)
      throws IOException, LLJVMRuntimeException {
    byte[] bytecode = Files.readAllBytes(new File(classFile).toPath());
    return loadClassFromBytecode(bytecode);
  }

  public Class<?> loadClassFromBitcodeFile(String bitcodeFile)
      throws IOException, LLJVMRuntimeException {
    byte[] bitcode = Files.readAllBytes(new File(bitcodeFile).toPath());
    return loadClassFromBitcode(bitcode);
  }
}
