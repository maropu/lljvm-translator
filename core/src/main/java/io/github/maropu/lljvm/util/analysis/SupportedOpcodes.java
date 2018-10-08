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

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import io.github.maropu.lljvm.LLJVMRuntimeException;

public class SupportedOpcodes {

  // A while list for supported opcodes
  private final static Set<Integer> supportedOpcodes = new HashSet<Integer>() {{
    add(Opcodes.NOP);
    add(Opcodes.ACONST_NULL);
    add(Opcodes.ICONST_M1);
    add(Opcodes.ICONST_0);
    add(Opcodes.ICONST_1);
    add(Opcodes.ICONST_2);
    add(Opcodes.ICONST_3);
    add(Opcodes.ICONST_4);
    add(Opcodes.ICONST_5);
    add(Opcodes.LCONST_0);
    add(Opcodes.LCONST_1);
    add(Opcodes.FCONST_0);
    add(Opcodes.FCONST_1);
    add(Opcodes.FCONST_2);
    add(Opcodes.DCONST_0);
    add(Opcodes.DCONST_1);
    add(Opcodes.BIPUSH);
    add(Opcodes.SIPUSH);
    add(Opcodes.LDC);
    add(Opcodes.ILOAD);
    add(Opcodes.LLOAD);
    add(Opcodes.FLOAD);
    add(Opcodes.DLOAD);
    add(Opcodes.ALOAD);
    add(Opcodes.IALOAD);
    add(Opcodes.LALOAD);
    add(Opcodes.FALOAD);
    add(Opcodes.DALOAD);
    add(Opcodes.AALOAD);
    add(Opcodes.BALOAD);
    add(Opcodes.CALOAD);
    add(Opcodes.SALOAD);
    add(Opcodes.ISTORE);
    add(Opcodes.LSTORE);
    add(Opcodes.FSTORE);
    add(Opcodes.DSTORE);
    add(Opcodes.ASTORE);
    add(Opcodes.IASTORE);
    add(Opcodes.LASTORE);
    add(Opcodes.FASTORE);
    add(Opcodes.DASTORE);
    add(Opcodes.AASTORE);
    add(Opcodes.BASTORE);
    add(Opcodes.CASTORE);
    add(Opcodes.SASTORE);
    add(Opcodes.POP);
    add(Opcodes.POP2);
    add(Opcodes.DUP);
    add(Opcodes.DUP_X1);
    add(Opcodes.DUP_X2);
    add(Opcodes.DUP2);
    add(Opcodes.DUP2_X1);
    add(Opcodes.DUP2_X2);
    add(Opcodes.SWAP);
    add(Opcodes.IADD);
    add(Opcodes.LADD);
    add(Opcodes.FADD);
    add(Opcodes.DADD);
    add(Opcodes.ISUB);
    add(Opcodes.LSUB);
    add(Opcodes.FSUB);
    add(Opcodes.DSUB);
    add(Opcodes.IMUL);
    add(Opcodes.LMUL);
    add(Opcodes.FMUL);
    add(Opcodes.DMUL);
    add(Opcodes.IDIV);
    add(Opcodes.LDIV);
    add(Opcodes.FDIV);
    add(Opcodes.DDIV);
    add(Opcodes.IREM);
    add(Opcodes.LREM);
    add(Opcodes.FREM);
    add(Opcodes.DREM);
    add(Opcodes.INEG);
    add(Opcodes.LNEG);
    add(Opcodes.FNEG);
    add(Opcodes.DNEG);
    add(Opcodes.ISHL);
    add(Opcodes.LSHL);
    add(Opcodes.ISHR);
    add(Opcodes.LSHR);
    add(Opcodes.IUSHR);
    add(Opcodes.LUSHR);
    add(Opcodes.IAND);
    add(Opcodes.LAND);
    add(Opcodes.IOR);
    add(Opcodes.LOR);
    add(Opcodes.IXOR);
    add(Opcodes.LXOR);
    add(Opcodes.IINC);
    add(Opcodes.I2L);
    add(Opcodes.I2F);
    add(Opcodes.I2D);
    add(Opcodes.L2I);
    add(Opcodes.L2F);
    add(Opcodes.L2D);
    add(Opcodes.F2I);
    add(Opcodes.F2L);
    add(Opcodes.F2D);
    add(Opcodes.D2I);
    add(Opcodes.D2L);
    add(Opcodes.D2F);
    add(Opcodes.I2B);
    add(Opcodes.I2C);
    add(Opcodes.I2S);
    add(Opcodes.LCMP);
    add(Opcodes.FCMPL);
    add(Opcodes.FCMPG);
    add(Opcodes.DCMPL);
    add(Opcodes.DCMPG);
    add(Opcodes.IFEQ);
    add(Opcodes.IFNE);
    add(Opcodes.IFLT);
    add(Opcodes.IFGE);
    add(Opcodes.IFGT);
    add(Opcodes.IFLE);
    add(Opcodes.IF_ICMPEQ);
    add(Opcodes.IF_ICMPNE);
    add(Opcodes.IF_ICMPLT);
    add(Opcodes.IF_ICMPGE);
    add(Opcodes.IF_ICMPGT);
    add(Opcodes.IF_ICMPLE);
    add(Opcodes.IF_ACMPEQ);
    add(Opcodes.IF_ACMPNE);
    add(Opcodes.GOTO);
    add(Opcodes.JSR);
    add(Opcodes.RET);
    // add(Opcodes.TABLESWITCH);
    // add(Opcodes.LOOKUPSWITCH);
    add(Opcodes.IRETURN);
    add(Opcodes.LRETURN);
    add(Opcodes.FRETURN);
    add(Opcodes.DRETURN);
    add(Opcodes.ARETURN);
    add(Opcodes.RETURN);
    add(Opcodes.GETSTATIC);
    add(Opcodes.PUTSTATIC);
    // add(Opcodes.GETFIELD);
    // add(Opcodes.PUTFIELD);
    add(Opcodes.INVOKEVIRTUAL);
    add(Opcodes.INVOKESPECIAL);
    add(Opcodes.INVOKESTATIC);
    // add(Opcodes.INVOKEINTERFACE);
    // add(Opcodes.INVOKEDYNAMIC);
    // add(Opcodes.NEW);
    // add(Opcodes.NEWARRAY);
    // add(Opcodes.ANEWARRAY);
    // add(Opcodes.ARRAYLENGTH);
    add(Opcodes.ATHROW);
    // add(Opcodes.CHECKCAST);
    // add(Opcodes.INSTANCEOF);
    // add(Opcodes.MONITORENTER);
    // add(Opcodes.MONITOREXIT);
    // add(Opcodes.MULTIANEWARRAY);
    // add(Opcodes.IFNULL);
    // add(Opcodes.IFNONNULL);
  }};

  public static void checkIfOpcodeSupported(int opcode) {
    if (!supportedOpcodes.contains(opcode)) {
      throw new LLJVMRuntimeException(String.format("OPCODE(%d) not supported", opcode));
    }
  }
}
