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

package io.github.maropu.lljvm.util.analysis;

import org.objectweb.asm.*;

import io.github.maropu.lljvm.LLJVMRuntimeException;

/**
 * Since this is a base class to verify code in [[BytecodeVerifier]], all the methods are guarded
 * by throwing a runtime exception.
 */
public abstract class WhilelistBaseClassVerifier extends ClassVisitor {

  WhilelistBaseClassVerifier(int apiCode) {
    super(apiCode);
  }

  public void visitSource(String source, String debug) {
    throw new LLJVMRuntimeException("SOURCE not supported");
  }

  public ModuleVisitor visitModule(String name, int access, String version) {
    throw new LLJVMRuntimeException("MODULE not supported: " + name);
  }

  public void visitOuterClass(String owner, String name, String descriptor) {
    throw new LLJVMRuntimeException("OUTERCLASS not supported: " + name);
  }

  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("ANNOTATION not supported");
  }

  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("TYPEANNOTATION not supported");
  }

  public void visitAttribute(Attribute attribute) {
    throw new LLJVMRuntimeException("ATTRIBUTE not supported");
  }

  public void visitInnerClass(String name, String outerName, String innerName, int access) {
    throw new LLJVMRuntimeException("INNERCLASS not supported: " + name);
  }

  public FieldVisitor visitField(
      int access, String name, String descriptor, String signature, Object value) {
    throw new LLJVMRuntimeException("FIELD not supported: " + name);
  }

  public MethodVisitor visitMethod(
      int access, String name, String descriptor, String signature, String[] exceptions) {
    throw new LLJVMRuntimeException("METHOD not supported: " + name + descriptor);
  }

  public void visitEnd() {
    throw new LLJVMRuntimeException("END not supported");
  }
}
