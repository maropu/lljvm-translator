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

import io.github.maropu.lljvm.util.python.PyArrayHolder
import org.xerial.snappy.OSInfo

abstract class PyFuncTest extends LLJVMFuncSuite {

  val basePath = "pyfunc"

  var pyArray1: PyArrayHolder = _
  var pyArray2: PyArrayHolder = _
  var pyArray3: PyArrayHolder = _

  override def beforeAll(): Unit = {
    super.beforeAll()
    pyArray1 = PyArrayHolder.create()
    pyArray2 = PyArrayHolder.create()
    pyArray3 = PyArrayHolder.create()
  }

  override def afterAll(): Unit = {
    pyArray1.close()
    pyArray2.close()
    pyArray3.close()
    super.afterAll()
  }

  private val OS = OSInfo.getOSName

  // On Linux/x86_64 platforms, it fails to import `scipy.linalg.cython_blas`.
  // This issue seems to happen in Python v2.7, but doesn't in v3.x:
  // https://bugzilla.redhat.com/show_bug.cgi?id=1667914
  protected def testOnlyOnMac(title: String)(f: => Any): Unit = {
    if (OS.equals("Mac")) {
      test(title)(f)
    } else {
      ignore(s"$title - skipped in $OS")(f)
    }
  }

  def intArray(addr: Long): Array[Int] = {
    PyArrayHolder.create(addr).intArray()
  }

  def longArray(addr: Long): Array[Long] = {
    PyArrayHolder.create(addr).longArray()
  }

  def floatArray(addr: Long): Array[Float] = {
    PyArrayHolder.create(addr).floatArray()
  }

  def doubleArray(addr: Long): Array[Double] = {
    PyArrayHolder.create(addr).doubleArray()
  }
}
