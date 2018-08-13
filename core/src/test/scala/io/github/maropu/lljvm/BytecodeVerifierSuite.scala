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

package io.github.maropu.lljvm

import java.nio.charset.StandardCharsets

import org.scalatest.FunSuite

import io.github.maropu.lljvm.util.JvmAssembler
import io.github.maropu.lljvm.util.analysis.BytecodeVerifier

class BytecodeVerifierSuite extends FunSuite {

  private def checkException(code: String, expectedMsg: String): Unit = {
    val bytecode = JvmAssembler.doCompile(code.getBytes(StandardCharsets.UTF_8), false)
    val errMsg = intercept[LLJVMRuntimeException] {
      BytecodeVerifier.verify(bytecode)
    }.getMessage
    assert(errMsg.contains(expectedMsg))
  }

  test("illegal class name") {
     val illegalCode =
      s""".class public final IllegalClassName
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static test(I)I
         |        iload_0
         |        ireturn
         |
         |.end method
       """.stripMargin

    checkException(illegalCode,
      s"Generated class name must be '${JvmAssembler.LLJVM_GENERATED_CLASSNAME}', " +
        "but 'IllegalClassName")
  }

  test("illegal bytecode") {
    val illegalCode =
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static plus(II)I
         |.limit stack 2
         |.limit locals 2
         |        lload_0 ; Push wrong type data onto the operand stack
         |        iload_1
         |        iadd
         |        ireturn
         |
         |.end method
       """.stripMargin

    checkException(illegalCode,
      "Illegal bytecode found: Error at instruction 0: Expected J, but found I")
  }

  test("illegal method call") {
    val illegalCode =
      s""".class public final ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}
         |.super java/lang/Object
         |
         |.method public <init>()V
         |        aload_0
         |        invokenonvirtual java/lang/Object/<init>()V
         |        return
         |.end method
         |
         |.method public static test()V
         |        invokestatic test/dummy/TestObject/func()V
         |        return
         |
         |.end method
       """.stripMargin

    checkException(illegalCode, "Package(test/dummy/TestObject) not supported")
  }
}
