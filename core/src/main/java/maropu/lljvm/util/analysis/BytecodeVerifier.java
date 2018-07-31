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

package maropu.lljvm.util.analysis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.CheckClassAdapter;

import maropu.lljvm.LLJVMRuntimeException;
import maropu.lljvm.util.JvmAssembler;

public class BytecodeVerifier {
  private static final int apiCode = Opcodes.ASM6;

  public static void verify(byte[] bytecode) throws LLJVMRuntimeException {
    final ClassReader cr = getClassReader(bytecode);
    verifyBytecode(cr);
    checkIfBytecodeSupported(cr);
  }

  // Gets an ASM class reader for given bytecode
  private static ClassReader getClassReader(byte[] bytecode) throws LLJVMRuntimeException {
    try {
      return new ClassReader(new ByteArrayInputStream(bytecode));
    } catch (IOException e) {
      throw new LLJVMRuntimeException(
        "Can't get an ASM class reader for given bytecode: " + e.getMessage());
    }
  }

  private static void verifyBytecode(ClassReader cr) throws LLJVMRuntimeException {
    ClassNode cn = new ClassNode();
    try {
      cr.accept(new CheckClassAdapter(cn, false), ClassReader.SKIP_DEBUG);
    } catch (IllegalArgumentException e) {
      throw new LLJVMRuntimeException("Illegal bytecode found: " + e.getMessage());
    }

    List<Type> interfaces = new ArrayList<>();
    for (Iterator<String> i = cn.interfaces.iterator(); i.hasNext(); ) {
      interfaces.add(Type.getObjectType(i.next()));
    }

    Type tpe = Type.getObjectType(cn.name);
    Type superTpe = cn.superName == null ? null : Type.getObjectType(cn.superName);
    Boolean isInterface = (cn.access & Opcodes.ACC_INTERFACE) != 0;
    for (Iterator<MethodNode> i = cn.methods.iterator(); i.hasNext(); ) {
      MethodNode method = i.next();
      SimpleVerifier verifier = new SimpleVerifier(tpe, superTpe, interfaces, isInterface);
      Analyzer<BasicValue> analyzer = new Analyzer<>(verifier);
      try {
        analyzer.analyze(cn.name, method);
      } catch (AnalyzerException e) {
        throw new LLJVMRuntimeException("Illegal bytecode found: " + e.getMessage());
      }
    }
  }

  private static void checkIfBytecodeSupported(ClassReader cr) {
    cr.accept(new SupportedBytecodeChecker(), ClassReader.SKIP_DEBUG);
  }

  private static class SupportedBytecodeChecker extends WhilelistBaseClassVerifier {

    SupportedBytecodeChecker() {
      super(apiCode);
    }

    @Override
    public void visit(
        int version, int access, String name, String signature, String superName,
        String[] interfaces) {
      final String clazzName = JvmAssembler.LLJVM_GENERATED_CLASSNAME;
      if (!name.equals(clazzName)) {
        throw new LLJVMRuntimeException(
          String.format("Generated class name must be '%s', but '%s' found", clazzName, name));
      }
    }

    @Override
    public MethodVisitor visitMethod(
        int access, String name, String descriptor, String signature, String[] exceptions) {

      return new WhilelistBaseMethodVerifier(apiCode) {

        @Override
        public void visitCode() {}

        @Override
        public void visitInsn(int opcode) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
        }

        @Override
        public void visitMethodInsn(
            int opcode, String owner, String name, String descriptor, boolean isInterface) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
          SupportedMethodPackages.checkIfPackageSupported(owner);
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
          SupportedOpcodes.checkIfOpcodeSupported(opcode);
        }

        @Override
        public void visitLabel(Label label) {}

        @Override
        public void visitLdcInsn(Object value) {}

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {}

        @Override
        public void visitEnd() {}
      };
    }

    @Override
    public void visitEnd() {}
  }
}
