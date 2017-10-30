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

import java.lang.{Double => jDouble, Integer => jInt}

import scala.collection.JavaConverters._

import org.scalatest.FunSuite

class TestClass {

  def methodX(a: Int, b: Double): Unit = {}
  def methodY(a: String): Unit = {}
  def methodZ(a: String): Unit = {}
  def otherMethod(): Unit = {}
}

class LLJVMUtilsSuite extends FunSuite {

  test("getAllMethods") {
    val methods = LLJVMUtils.getAllMethods(classOf[TestClass]).asScala.map(_.getName)
    assert(methods.toSet === Set("methodX", "methodY", "methodZ", "otherMethod"))
  }

  test("getMethod") {
    val errMsg = intercept[LLJVMRuntimeException] {
      LLJVMUtils.getMethod(classOf[TestClass], "unknownMethod", jInt.TYPE, classOf[String])
    }.getMessage
    assert(errMsg === "Method not found: unknownMethod(int, class java.lang.String)")

    val m1 = LLJVMUtils.getMethod(classOf[TestClass], "methodX", jInt.TYPE, jDouble.TYPE)
    assert(m1.getName === "methodX")
    assert(m1.getParameterTypes.toSeq === Seq(Integer.TYPE, jDouble.TYPE))

    val m2 = LLJVMUtils.getMethod(classOf[TestClass], "methodY", classOf[String])
    assert(m2.getName === "methodY")
    assert(m2.getParameterTypes.toSeq === Seq(classOf[String]))

    val m3 = LLJVMUtils.getMethod(classOf[TestClass], "methodZ", classOf[String])
    assert(m3.getName === "methodZ")
    assert(m3.getParameterTypes.toSeq === Seq(classOf[String]))

    val m4 = LLJVMUtils.getMethod(classOf[TestClass], "otherMethod")
    assert(m4.getName === "otherMethod")
    assert(m4.getParameterTypes.toSeq === Seq.empty)
  }

  test("findMethods") {
    val methods = LLJVMUtils.findMethods(classOf[TestClass], "method", classOf[String])
    assert(methods.asScala.map(_.getName()).toSet === Set("methodY", "methodZ"))
  }
}
