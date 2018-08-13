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

import java.util.{HashMap => jMap}

import org.codehaus.janino.ClassBodyEvaluator
import org.scalatest.FunSuite

import io.github.maropu.lljvm.util.JvmAssembler

abstract class JaninoClass {
   def plus(a: Int, b: Int): Int
}

class JaninoSuite extends FunSuite {

  test("invoke gen'd function inside janino-compiled class") {
    val code =
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
         |        iload_0
         |        iload_1
         |        iadd
         |        ireturn
         |
         |.end method
       """.stripMargin

    val bytecode = JvmAssembler.compile(code)
    val classMap = new jMap[String, Class[_]]()
    classMap.put(JvmAssembler.LLJVM_GENERATED_CLASSNAME, TestUtils.loadClassFromBytecode(bytecode))
    val classLoader = new LLJVMClassLoader(classMap)

    // Call gen'd function in janino-compiled class
    val evaluator = new ClassBodyEvaluator()
    evaluator.setParentClassLoader(classLoader)
    evaluator.setClassName("maropu.TestClass")
    evaluator.setExtendedClass(classOf[JaninoClass])
    evaluator.cook("generated.java",
      s"""public int plus(int a, int b) {
         |  return ${JvmAssembler.LLJVM_GENERATED_CLASSNAME}.plus(a, b);
         |}
       """.stripMargin
    )
    val cls = evaluator.getClazz.newInstance().asInstanceOf[JaninoClass]
    assert(cls.plus(1, 1) === 2)
  }
}
