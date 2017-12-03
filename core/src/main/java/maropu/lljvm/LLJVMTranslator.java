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

import java.io.*;
import java.nio.file.Files;

import jasmin.ClassFile;

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
    File bitcodeFile = new File(args[0]);
    if (!bitcodeFile.exists()) {
      System.err.println(bitcodeFile + " doesn't exist");
      System.exit(-1);
    }

    // Generate Jasmin code from the given LLVM code
    String jasminCode = null;
    try {
      byte[] bitcode = Files.readAllBytes(bitcodeFile.toPath());
      jasminCode = LLJVMLoader.loadLLJVMApi().parseBitcode(bitcode);
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(-1);
    }

    String bitcodeFilename = bitcodeFile.getName();
    String baseFilename = bitcodeFilename.substring(0, bitcodeFilename.lastIndexOf('.'));
    String outputDir = bitcodeFile.getParentFile().getAbsolutePath();

    try (Reader in = new InputStreamReader(new ByteArrayInputStream(jasminCode.getBytes()));
          OutputStream os = new FileOutputStream(new File(outputDir, baseFilename + ".class"))) {
      // Compile the code and write JVM bytecode
      ClassFile classFile = new ClassFile();
      classFile.readJasmin(in, "GeneratedClass", false);
      classFile.write(os);
      os.close();
    } catch (Exception e) {
      e.printStackTrace(System.err);
      System.exit(-1);
    }
  }
}
