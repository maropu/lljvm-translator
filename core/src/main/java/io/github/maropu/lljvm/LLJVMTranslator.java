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

package io.github.maropu.lljvm;

import java.io.*;
import java.nio.file.Files;

import io.github.maropu.lljvm.util.JvmAssembler;

/**
 * A driver code to translate LLVM bitcode to JVM bytecode.
 */
public class LLJVMTranslator {

  public static void main(String args[]) {
    // Validate an input file name
    if (args.length < 1) {
      System.err.println("Need to specify input LLVM bitcode");
      System.exit(-1);
    }
    File bitcodeFile = new File(args[0]).getAbsoluteFile();
    if (!bitcodeFile.exists()) {
      System.err.println(bitcodeFile + " doesn't exist");
      System.exit(-1);
    }

    // Generate JVM assembly code from the given LLVM code
    String jvmAsm = null;
    try {
      byte[] bitcode = Files.readAllBytes(bitcodeFile.toPath());
      jvmAsm = LLJVMLoader.loadLLJVMApi().asJVMAssemblyCode(bitcode, 0);
    } catch (Throwable t) {
      t.printStackTrace(System.err);
      System.exit(-1);
    }

    String bitcodeFilename = bitcodeFile.getName();
    String basename = bitcodeFilename.substring(0, bitcodeFilename.lastIndexOf('.'));
    String outputDir = bitcodeFile.getParentFile().getAbsolutePath();

    try (OutputStream os = new FileOutputStream(new File(outputDir, basename + ".class"))) {
      os.write(JvmAssembler.compile(jvmAsm));
    } catch (Throwable t) {
      t.printStackTrace(System.err);
      System.exit(-1);
    }
  }
}
