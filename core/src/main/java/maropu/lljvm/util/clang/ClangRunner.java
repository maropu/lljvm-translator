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

package maropu.lljvm.util.clang;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import maropu.lljvm.LLJVMRuntimeException;
import maropu.lljvm.util.ProcessUtils;

public class ClangRunner {

  public static byte[] exec(String code) {
    ProcessUtils.checkIfCmdInstalled("clang");
    final String tempDir = ProcessUtils.makeTempDir();
    final String tempFilename = ProcessUtils.getTempFileName("cfunc");
    final File srcFile = new File(tempDir, tempFilename + ".c");
    final File bitcodeFile = new File(tempDir, tempFilename + ".bc");
    try (OutputStream os = new FileOutputStream(srcFile)) {
      os.write(code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }

    final String[] command = {"clang", "-c", "-O0", "-emit-llvm", "-o",
      bitcodeFile.getAbsolutePath(), srcFile.getAbsolutePath()};

    StringBuilder stderr = new StringBuilder();
    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      Process p = builder.start();
      p.waitFor();

      try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
        for(String line = br.readLine(); line != null; line = br.readLine()) {
          stderr.append(line);
          stderr.append("\n");
        }
      }
    } catch (Exception e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }

    // If no output exists, it throws an exception with compiler error messages
    if (bitcodeFile.exists()) {
      try {
        return Files.readAllBytes(bitcodeFile.toPath());
      } catch (IOException e) {
        throw new LLJVMRuntimeException(e.getMessage());
      }
    } else {
      throw new LLJVMRuntimeException(stderr.toString());
    }
  }
}
