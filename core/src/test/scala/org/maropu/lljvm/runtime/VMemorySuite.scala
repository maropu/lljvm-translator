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

package org.maropu.lljvm.runtime

import java.util.concurrent.Executors

import scala.util.Random

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.scalatest.concurrent.TimeLimits
import org.scalatest.time.SpanSugar._

import org.maropu.lljvm.LLJVMRuntimeException

class VMemorySuite extends FunSuite with BeforeAndAfterAll with TimeLimits {

  override def beforeAll(): Unit = {
    super.beforeAll()
    VMemory.createStackFrame()
  }

  override def afterAll(): Unit = {
    VMemory.destroyStackFrame()
    super.afterAll()
  }

  test("read/write primitive types") {
    val addr1 = VMemory.allocateStack(1)
    VMemory.store(addr1, false)
    assert(VMemory.load_i1(addr1) === false)

    val addr2 = VMemory.allocateStack(1)
    VMemory.store(addr2, 3.toByte)
    assert(VMemory.load_i8(addr2) === 3)

    val addr3 = VMemory.allocateStack(2)
    VMemory.store(addr3, 8.toShort)
    assert(VMemory.load_i16(addr3) === 8)

    val addr4 = VMemory.allocateStack(4)
    VMemory.store(addr4, 23)
    assert(VMemory.load_i32(addr4) === 23)

    val addr5 = VMemory.allocateStack(8)
    VMemory.store(addr5, 122L)
    assert(VMemory.load_i64(addr5) === 122)

    val addr6 = VMemory.allocateStack(4)
    VMemory.store(addr6, 1.0f)
    assert(VMemory.load_f32(addr6) === 1.0)

    val addr7 = VMemory.allocateStack(8)
    VMemory.store(addr7, 8.0)
    assert(VMemory.load_f64(addr7) === 8.0)
  }

  // This test should lie in the end of this suite
  test("Throw an exception if not enough memory") {
    val errMsg = intercept[LLJVMRuntimeException] {
      while (true) {
        VMemory.allocateStack(8)
      }
    }.getMessage
    assert(errMsg === "Not enough memory in the stack")
  }

  ignore("multi-threading tests") {
    val testThread1 = new Runnable() {
      override def run(): Unit = {
        for (i <- 0 until 1024) {
          VMemory.createStackFrame()
          val xAddr = VMemory.allocateStack(4)
          VMemory.store(xAddr, i)
          val yAddr = VMemory.allocateStack(4)
          VMemory.store(yAddr, 2 * i)
          val result = VMemory.load_i32(xAddr) + VMemory.load_i32(yAddr)
          assert(result === 3 * i)
          VMemory.destroyStackFrame()
        }
      }
    }

    val testThread2 = new Runnable() {
      override def run(): Unit = {
        for (i <- 0 until 1024) {
          VMemory.resetHeap()
          val xAddr = VMemory.allocateData(4)
          VMemory.store(xAddr, 3 * i)
          val yAddr = VMemory.allocateData(4)
          VMemory.store(yAddr, 2 * i)
          val result = VMemory.load_i32(xAddr) + VMemory.load_i32(yAddr)
          assert(result === 5 * i)
        }
      }
    }

    val testThread3 = new Runnable() {
      override def run(): Unit = {
        for (i <- 0 until 1024) {
          VMemory.resetHeap()
          VMemory.createStackFrame()
          val xAddr = VMemory.allocateData(4)
          VMemory.store(xAddr, i + 1)
          val yAddr = VMemory.allocateStack(4)
          VMemory.store(yAddr, 2 * i + 5)
          val result = VMemory.load_i32(xAddr) + VMemory.load_i32(yAddr)
          assert(result === 3 * i + 6)
          VMemory.destroyStackFrame()
        }
      }
    }

    val testThread4 = new Runnable() {
      override def run(): Unit = {
        for (i <- 0 until 1024) {
          VMemory.createStackFrame()
          val baseAddr = VMemory.allocateStack(4 * 512)
          var curAddr = baseAddr
          var expectedValue: Int = 0
          for (j <- 0 until 512) {
            val v = Random.nextInt(10)
            curAddr = VMemory.pack(curAddr, v)
            expectedValue += v
          }
          val argTypes = Array.fill[Class[_]](512)(Integer.TYPE)
          val actualValue = VMemory.unpack(baseAddr, argTypes)
            .map(_.asInstanceOf[Integer])
            .reduce(_ + _)
          assert(actualValue === expectedValue)
          VMemory.destroyStackFrame()
        }
      }
    }

    val testThread5 = new Runnable() {
      override def run(): Unit = {
        for (i <- 0 until 1024) {
          VMemory.resetHeap()
          val baseAddr = VMemory.allocateData(4 * 512)
          var curAddr = baseAddr
          var expectedValue: Int = 0
          for (j <- 0 until 512) {
            val v = Random.nextInt(10)
            curAddr = VMemory.pack(curAddr, v)
            expectedValue += v
          }
          val argTypes = Array.fill[Class[_]](512)(Integer.TYPE)
          val actualValue = VMemory.unpack(baseAddr, argTypes)
            .map(_.asInstanceOf[Integer])
            .reduce(_ + _)
          assert(actualValue === expectedValue)
        }
      }
    }

    failAfter(10.seconds) {
      val service = Executors.newFixedThreadPool(5)
      val threads = Seq(testThread1, testThread2, testThread3, testThread4, testThread5)
      threads.foreach(t => service.submit(t))
      service.shutdown()
    }
  }
}
