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

package maropu.lljvm

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStreamReader}
import java.util.{HashMap => jMap}

import jasmin.ClassFile
import org.codehaus.janino.ClassBodyEvaluator
import org.scalatest.FunSuite

abstract class JaninoClass {
   def plus(a: Int, b: Int): Int
}

class JaninoSuite extends FunSuite {

  test("invoke gen'd function inside janino-compiled class") {
    val code =
      s""".class public final GeneratedFunc
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

    val classFile = new ClassFile()
    val in = new InputStreamReader(new ByteArrayInputStream(code.getBytes))
    classFile.readJasmin(in, "GeneratedFunc", true)

    val out = new ByteArrayOutputStream()
    classFile.write(out)
    assert(out.size > 0)

    val classMap = new jMap[String, Class[_]]()
    classMap.put("GeneratedFunc", TestUtils.loadClassFromBytecode("GeneratedFunc", out.toByteArray))
    val classLoader = new LLJVMClassLoader(classMap)

    // Call gen'd function in janino-compiled class
    val evaluator = new ClassBodyEvaluator()
    evaluator.setParentClassLoader(classLoader)
    evaluator.setClassName("maropu.TestClass")
    evaluator.setExtendedClass(classOf[JaninoClass])
    evaluator.cook("generated.java",
      s"""public int plus(int a, int b) {
         |  return GeneratedFunc.plus(a, b);
         |}
       """.stripMargin
    )
    val cls = evaluator.getClazz.newInstance().asInstanceOf[JaninoClass]
    assert(cls.plus(1, 1) === 2)
  }
}
