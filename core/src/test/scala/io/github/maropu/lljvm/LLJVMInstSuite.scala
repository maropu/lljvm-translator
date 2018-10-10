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

import java.nio.charset.{StandardCharsets => Charsets}

import io.github.maropu.lljvm.util.JVMAssembler
import io.github.maropu.lljvm.util.analysis.BytecodeVerifier

class LLJVMInstSuite extends LLJVMFuncSuite {

  private val basePath = "llvm-insts"

  // LLVM 7 instructions are listed in an URL below:
  // - https://releases.llvm.org/7.0.0/docs/LangRef.html#instruction-reference
  Seq("add", "sub", "mul"
    // TODO: Adds tests for instructions below:
    // "ret", "br", "switch", "invoke", "resume", "unreachable", "fadd", "fsub", "fmul", "udiv",
    // "sdiv", "fdiv", "urem", "srem", "frem", "shl", "lshr", "ashr", "and", "or", "xor",
    // "extractelement", "insertelement", "shufflevector", "extractvalue", "insertvalue",
    // "alloca", "load", "store", "fence", "atomicrmw", "getelementptr", "trunc", "zext",
    // "sext", "fptrunc", "fpext", "fptoui", "fptosi", "uitofp", "sitofp", "ptrtoint",
    // "inttoptr", "bitcast", "icmp", "fcmp", "phi", "select", "call", "va_arg"
  ).foreach { inst =>
    test(s"basic LLJVM translation test - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/$inst.bc")
      val jvmAsm = TestUtils.asJVMAssemblyCode(bitcode)
      BytecodeVerifier.verify(JVMAssembler.compile(jvmAsm))
      logDebug(
        s"""
           |========== LLVM Assembly Code =========
           |${TestUtils.asLLVMAssemblyCode(bitcode)}
           |========== JVM Assembly Code =========
           |$jvmAsm
         """.stripMargin)
    }
  }

  Seq("cmpxchg"
    // TODO: Adds tests for instructions below:
    // "addrspacecast", "indirectbr"
  ).foreach { inst =>
    test(s"basic LLJVM translation test - unsupported - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/$inst.bc")
      val errMsg = intercept[LLJVMRuntimeException] {
        TestUtils.asJVMAssemblyCode(bitcode)
      }.getMessage
      assert(errMsg.contains("Unsupported LLVM instruction:"))
      logDebug(
        s"""
           |========== LLVM Assembly Code =========
           |${TestUtils.asLLVMAssemblyCode(bitcode)}
           |========== JVM Assembly Code =========
           |<unsupported>
         """.stripMargin)
    }
  }

  Seq("resume"
    // TODO: Adds tests for instructions below:
    // "catchswitch", "catchret", "cleanupret", "landingpad", "catchpad", "cleanuppad"
  ).foreach { inst =>
    test(s"basic LLJVM translation test - unsupported - $inst") {
      val bitcode = TestUtils.resourceToBytes(s"$basePath/$inst.bc")
      val errMsg = intercept[LLJVMRuntimeException] {
        TestUtils.asJVMAssemblyCode(bitcode)
      }.getMessage
      assert(errMsg.contains("Unsupported LLVM exception handling instruction:"))
      logDebug(
        s"""
           |========== LLVM Assembly Code =========
           |${TestUtils.asLLVMAssemblyCode(bitcode)}
           |========== JVM Assembly Code =========
           |<unsupported>
         """.stripMargin)
    }
  }

  // The instructions for exception handling (resume, catchswitch, catchret, cleanupret,
  // landingpad, catchpad, and cleanuppad) are not supported in LLJVM.
  ignore("exception handling (unsupported)") {
    val bitcode = TestUtils.resourceToBytes(s"$basePath/exception.bc")
    val errMsg = intercept[LLJVMRuntimeException] {
      TestUtils.asJVMAssemblyCode(bitcode)
    }.getMessage
    assert(errMsg.contains("Unsupported LLVM exception handling instruction:"))
    logDebug(
      s"""
         |========== Source Code ==========
         |${new String(TestUtils.resourceToBytes(s"$basePath/exception.cc"), Charsets.UTF_8)}
         |========== LLVM Assembly Code =========
         |${TestUtils.asLLVMAssemblyCode(bitcode)}
         |========== JVM Assembly Code =========
         |<unsupported>
       """.stripMargin)
  }
}
