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

package io.github.maropu.lljvm.util;

import java.io.File;
import java.util.UUID;

import io.github.maropu.lljvm.LLJVMRuntimeException;

public class ProcessUtils {

  public static void checkIfCmdInstalled(String cmd) {
    try {
      ProcessBuilder builder = new ProcessBuilder(cmd);
      Process p = builder.start();
      p.waitFor();
    } catch (Throwable t) {
      throw new LLJVMRuntimeException(cmd + " not installed in your platform");
    }
  }

  public static String makeTempDir() {
    File tempDir = new File(System.getProperty("java.io.tmpdir"));
    if (!tempDir.exists()) {
      tempDir.mkdirs();
    }
    ShutdownHookManager.addShutdownHook(new Thread(() -> tempDir.delete()));
    return tempDir.getAbsolutePath();
  }

  public static String getTempFileName(String prefix) {
    return String.format("%s-%s", prefix, UUID.randomUUID().toString());
  }
}
