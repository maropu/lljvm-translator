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

package maropu.lljvm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import maropu.lljvm.LLJVMRuntimeException;

public class ClangRunner {

  private static void checkIfClangInstalled() {
    try {
      ProcessRunner.exec("clang");
    } catch (Throwable t) {
      throw new LLJVMRuntimeException("clang not installed in your platform");
    }
  }

  private static String makeTempDir() {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    if (!tempDir.exists()) {
      tempDir.mkdirs();
    }
    ShutdownHookManager.addShutdownHook(new Thread(() -> tempDir.delete()));
    return tempDir.getAbsolutePath();
  }

  private static String getTempFileName() {
    return String.format("clang-cfunc-%s", UUID.randomUUID().toString());
  }

  public static byte[] exec(String code) {
    checkIfClangInstalled();
    final String tempDir = makeTempDir();
    final String tempFilename = getTempFileName();
    final File srcFile = new File(tempDir, tempFilename + ".c");
    final File bitcodeFile = new File(tempDir, tempFilename + ".bc");
    try (OutputStream os = new FileOutputStream(srcFile)) {
      os.write(code.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
    ProcessRunner.exec(
      "clang", "-c", "-O0", "-emit-llvm", "-o", bitcodeFile.getAbsolutePath(),
      srcFile.getAbsolutePath());
    try {
      return Files.readAllBytes(bitcodeFile.toPath());
    } catch (IOException e) {
      throw new LLJVMRuntimeException(e.getMessage());
    }
  }
}
