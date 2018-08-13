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
public abstract class WhilelistBaseMethodVerifier extends MethodVisitor {

  WhilelistBaseMethodVerifier(int apiCode) {
    super(apiCode);
  }

  public void visitParameter(String name, int access) {
    throw new LLJVMRuntimeException("PARAMETER not supported: " + name);
  }

  public AnnotationVisitor visitAnnotationDefault() {
    throw new LLJVMRuntimeException("ANNOTATIONDEFAULT not supported");
  }

  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("ANNOTATION not supported: " + descriptor);
  }

  public AnnotationVisitor visitTypeAnnotation(
      int typeRef, TypePath typePath, String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("TYPEANNOTATION not supported: " + typePath);
  }

  public AnnotationVisitor visitParameterAnnotation(int param, String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("PARAMETERANNOTATION not supported: " + descriptor);
  }

  public void visitAttribute(Attribute attribute) {
    throw new LLJVMRuntimeException("ATTRIBUTE not supported");
  }

  public void visitCode() {
    throw new LLJVMRuntimeException("CODE not supported");
  }

  public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
    throw new LLJVMRuntimeException("FRAME not supported");
  }

  public void visitInsn(int opcode) {
    throw new LLJVMRuntimeException("INSN not supported: " + opcode);
  }

  public void visitIntInsn(int opcode, int operand) {
    throw new LLJVMRuntimeException("INTINSN not supported: " + opcode);
  }

  public void visitVarInsn(int opcode, int var) {
    throw new LLJVMRuntimeException("VARINSN not supported: " + opcode);
  }

  public void visitTypeInsn(int opcode, String type) {
    throw new LLJVMRuntimeException("TYPEINSN not supported: " + opcode);
  }

  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    throw new LLJVMRuntimeException("FIELDINSN not supported: " + opcode);
  }

  public void visitMethodInsn(
      int opcode, String owner, String name, String descriptor, boolean isInterface) {
    throw new LLJVMRuntimeException(
      "INVOKENONVIRTUAL/SPECIAL/STATIC not supported: " + owner + "/" + name + descriptor);
  }

  public void visitInvokeDynamicInsn(
      String name, String descriptor, Handle method, Object... args) {
    throw new LLJVMRuntimeException("INVOKEDYNAMIC not supported: " + name + descriptor);
  }

  public void visitJumpInsn(int opcode, Label label) {
    throw new LLJVMRuntimeException("JUMPINSN not supported: " + opcode);
  }

  public void visitLabel(Label label) {
    throw new LLJVMRuntimeException("LABEL not supported: " + label);
  }

  public void visitLdcInsn(Object value) {
    throw new LLJVMRuntimeException("LDCINSN not supported: " + value);
  }

  public void visitIincInsn(int var, int increment) {
    throw new LLJVMRuntimeException("IINCINSN not supported");
  }

  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    throw new LLJVMRuntimeException("TABLESWITCHINSN not supported");
  }

  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    throw new LLJVMRuntimeException("LOOKUPSWITCHINSN not supported");
  }

  public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
    throw new LLJVMRuntimeException("MULTIANEWARRAYINSN not supported");
  }

  public AnnotationVisitor visitInsnAnnotation(
      int typeRef, TypePath typePath, String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("INSNANNOTATION not supported");
  }

  public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
    throw new LLJVMRuntimeException("TRYCATCHBLOCK not supported");
  }

  public AnnotationVisitor visitTryCatchAnnotation(
      int typeRef, TypePath typePath, String descriptor, boolean visible) {
    throw new LLJVMRuntimeException("TRYCATCHANNOTATION not supported");
  }

  public void visitLocalVariable(
      String name, String descriptor, String signature, Label start, Label end, int index) {
    throw new LLJVMRuntimeException("LOCALVARIABLE not supported");
  }

  public AnnotationVisitor visitLocalVariableAnnotation(
      int typeRef, TypePath typePath, Label[] start, Label[] end, int[] index, String descriptor,
      boolean visible) {
    throw new LLJVMRuntimeException("LOCALVARIABLEANNOTATION not supported");
  }

  public void visitLineNumber(int line, Label start) {
    throw new LLJVMRuntimeException("LINENUMBER not supported");
  }

  public void visitMaxs(int maxStack, int maxLocals) {
    throw new LLJVMRuntimeException("MAXS not supported");
  }

  public void visitEnd() {
    throw new LLJVMRuntimeException("END not supported");
  }
}
