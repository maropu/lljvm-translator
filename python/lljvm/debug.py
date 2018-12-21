#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# Helper functions for debugging uses

import ctypes
import io
import os
import sys

from numba import cfunc

# Loads a shared library for LLJVM
if sys.platform == 'linux' or sys.platform == 'linux2':
  lljvmlibname = 'Linux/x86_64/liblljvm.so'
elif sys.platform == 'darwin':
  lljvmlibname = 'Mac/x86_64/liblljvm.dylib'
else:
  sys.exit('OS must be linux or darwin, but %s found' % sys.platform)

lljvmlibdir = os.getenv('PYTHON_LLJVM_SHARED_LIB_DIR')
lljvmapi = ctypes.cdll.LoadLibrary('%s/%s' % (lljvmlibdir, lljvmlibname))

# Prints LLVM assembly code for a given Python function
def print_llvm(pyfunc, sig):
  f = cfunc(sig, nopython=False, cache=False)(pyfunc)
  print f.inspect_llvm()

# Prints JVM assembly code (Jasmin) for a given Python function
def print_jvm(pyfunc, sig, optLevel=0, sizeLevel=0, debugLevel=0):
  f = cfunc(sig, nopython=False, cache=False)(pyfunc)
  bitcode = f._library._final_module.as_bitcode()
  lljvmapi.printAsJVMAssemblyCode(bitcode, len(bitcode), optLevel, sizeLevel, debugLevel)

