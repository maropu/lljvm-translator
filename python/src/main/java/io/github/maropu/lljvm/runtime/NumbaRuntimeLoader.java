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

package io.github.maropu.lljvm.runtime;

import java.io.*;
import java.util.UUID;

import org.xerial.snappy.OSInfo;

import io.github.maropu.lljvm.LLJVMRuntimeException;

public class NumbaRuntimeLoader {

  private static volatile NumbaRuntimeNative numbaRuntimeApi = null;
  private static boolean isLoaded = false;

  private static final String OS = OSInfo.getOSName();
  private static final String ARCH = OSInfo.getArchName();

  public synchronized static NumbaRuntimeNative loadNumbaRuntimeApi() throws LLJVMRuntimeException {
    checkIfPlatformSupported();

    if (numbaRuntimeApi != null) {
      return numbaRuntimeApi;
    }

    loadNativeLibrary();
    numbaRuntimeApi = new NumbaRuntimeNative();
    numbaRuntimeApi.initialize();
    return numbaRuntimeApi;
  }

  private static void checkIfPlatformSupported() {
    if (!((OS.equals("Linux") || OS.equals("Mac")) && ARCH.equals("x86_64"))) {
      final String errMsg = String.format(
        "Unsupported platform: os.name=%s and os.arch=%s", OS, ARCH);
      throw new LLJVMRuntimeException(errMsg);
    }
  }

  private synchronized static void loadNativeLibrary() {
    if (!isLoaded) {
      // Load extracted or specified LLJVM native library
      System.load(findNativeLibrary().getAbsolutePath());
      isLoaded = true;
    }
  }

  private static boolean contentsEquals(InputStream in1, InputStream in2) throws IOException {
    if (!(in1 instanceof BufferedInputStream)) {
      in1 = new BufferedInputStream(in1);
    }
    if (!(in2 instanceof BufferedInputStream)) {
      in2 = new BufferedInputStream(in2);
    }
    int ch = in1.read();
    while (ch != -1) {
      int ch2 = in2.read();
      if (ch != ch2) {
        return false;
      }
      ch = in1.read();
    }
    int ch2 = in2.read();
    return ch2 == -1;
  }

  private static File extractLibraryFile(
    String libFolderForCurrentOS, String libraryFileName, String targetFolder) {
    String nativeLibraryFilePath = libFolderForCurrentOS + "/" + libraryFileName;

    // Attach UUID to the native library file to ensure multiple class loaders can
    // read the libnumba-rt multiple times.
    String uuid = UUID.randomUUID().toString();
    String extractedLibFileName = String.format(
      "lljvm-%s-%s-%s", getVersion(), uuid, libraryFileName);
    File extractedLibFile = new File(targetFolder, extractedLibFileName);

    try {
      // Extract a native library file into the target directory
      InputStream reader = null;
      FileOutputStream writer = null;
      try {
        reader = NumbaRuntimeLoader.class.getResourceAsStream(nativeLibraryFilePath);
        try {
          writer = new FileOutputStream(extractedLibFile);

          byte[] buffer = new byte[8192];
          int bytesRead = 0;
          while ((bytesRead = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, bytesRead);
          }
        } finally {
          if (writer != null) {
            writer.close();
          }
        }
      } finally {
        if (reader != null) {
          reader.close();
        }
        // Delete the extracted lib file on JVM exit
        extractedLibFile.deleteOnExit();
      }

      // Set executable (x) flag to enable Java to load the native library
      extractedLibFile.setReadable(true);
      extractedLibFile.setWritable(true, true);
      extractedLibFile.setExecutable(true);

      // Check whether the contents are properly copied from the resource folder
      InputStream nativeIn = null;
      InputStream extractedLibIn = null;
      try {
        nativeIn = NumbaRuntimeLoader.class.getResourceAsStream(nativeLibraryFilePath);
        extractedLibIn = new FileInputStream(extractedLibFile);

        if (!contentsEquals(nativeIn, extractedLibIn)) {
          throw new LLJVMRuntimeException("Can't load the lljvm library");
        }
      } finally {
        if (nativeIn != null) {
          nativeIn.close();
        }
        if (extractedLibIn != null) {
          extractedLibIn.close();
        }
      }
      return new File(targetFolder, extractedLibFileName);
    } catch (IOException e) {
      e.printStackTrace(System.err);
      return null;
    }
  }

  private static File findNativeLibrary() {
    // Load an OS-dependent native library inside a jar file
    String nativeLibraryName = System.mapLibraryName("numba-rt");
    String nativeLibraryPath = String.format("/lib/%s/%s", OS, ARCH);
    // We assume that we have already checked if this platform is supported in advance
    assert(hasResource(nativeLibraryPath + "/" + nativeLibraryName));

    // Temporary folder for the native lib
    File tempFolder = new File(System.getProperty("java.io.tmpdir"));
    if (!tempFolder.exists()) {
      tempFolder.mkdirs();
    }

    // Extract and load a native library inside the jar file
    return extractLibraryFile(
            nativeLibraryPath, nativeLibraryName, tempFolder.getAbsolutePath());
  }

  private static boolean hasResource(String path) {
    return NumbaRuntimeLoader.class.getResource(path) != null;
  }

  private static String getVersion() {
    // TODO: Get the versin of lljvm-translator
    return "0.1.0";
  }
}
