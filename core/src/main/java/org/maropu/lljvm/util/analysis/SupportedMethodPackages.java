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

package org.maropu.lljvm.util.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.maropu.lljvm.LLJVMRuntimeException;

public class SupportedMethodPackages {

  private final static List<String> supportedMethodPackages = new ArrayList<String>() {{
    add("java/lang");
    add("org/maropu/lljvm/runtime");
  }};

  public static void checkIfPackageSupported(String packageName) {
    boolean supported = false;
    for (Iterator<String> i = supportedMethodPackages.iterator();
         !supported && i.hasNext(); ) {
      if (packageName.startsWith(i.next())) {
        supported = true;
      }
    }
    if (!supported) {
      throw new LLJVMRuntimeException(String.format("Package(%s) not supported", packageName));
    }
  }
}
