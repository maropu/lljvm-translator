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

package maropu.lljvm;

import java.io.IOException;

/**
 * A JNI interface of the LLJVM implementation.
 */
public class LLJVMNative {

  // Calculate the memory address of a given array
  public native long addressOf(byte[] ar) throws LLJVMRuntimeException;

  // Parse an input LLVM bitcode and transform it to JVM bytecode
  public native String parseBitcode(byte[] bitcode) throws IOException, LLJVMRuntimeException;

  // Return a human-readable LLVM bitcode
  public native void veryfyBitcode(byte[] bitcode) throws IOException, LLJVMRuntimeException;

  // Return a human-readable LLVM bitcode
  public native String asLLVMAssemblyCode(byte[] bitcode) throws IOException;

  // This exception is mainly used in native code
  public void throwException(String message) throws LLJVMRuntimeException {
    throw new LLJVMRuntimeException(message);
  }
}
