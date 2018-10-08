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

import java.io.{File, FileOutputStream}
import java.lang.{Integer => jInt}

import org.scalatest.FunSuite

class LLJVMTranslatorSuite extends FunSuite {

  test("simple test") {
    val file = TestUtils.createTempDir()
    val bitcode = TestUtils.resourceToBytes("cfunc-add-int32.bc")
    val inputFile = new File(file, "code.bc")
    val os = new FileOutputStream(inputFile)
    os.write(bitcode)
    os.close()

    LLJVMTranslator.main(Array(inputFile.getAbsolutePath))
    val outputFile = new File(file, "code.class")
    val classLoader = new LLJVMClassLoader()
    val clazz = classLoader.loadClassFromBytecodeFile(outputFile.getAbsolutePath)
    val method = LLJVMUtils.getMethod(clazz, "_add_test", jInt.TYPE, jInt.TYPE)
    assert(method.invoke(null, new jInt(4), new jInt(1)) === 5)
  }
}
