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
# Python initialization script

import ctypes
import llvmlite
import numba
import numpy
import platform
import scipy
import subprocess
import sys

from lljvm.debug import *

# Currently, Mac platforms only supported
if sys.platform != 'darwin':
  sys.exit('Mac platforms only supported now, but %s found' % sys.platform)

# Verifes the Python version
if (sys.version_info[0:2] != (2, 7)):
  sys.exit('Python version must be 2.7.x, but Python %s found' % platform.python_version())

# Checks if numba installed
if not 'numba' in sys.modules.keys():
  sys.exit('Numba needs to be installed first')

# Gets the LLVM version
llvm_version = subprocess.Popen("llvm-config --version",
  stdout=subprocess.PIPE,
  shell=True).communicate()[0]

print(r"""
     __   __      ___   ____  ___
    / /  / /  __ / / | / /  |/  /
   / /__/ /__/ // /| |/ / /|_/ /
  /____/____/\___/ |___/_/  /_/   version %s

 LLJVM debugging helper functions imported:
  - NumPy version: %s
  - SciPy version: %s
  - Numba version: %s
  - LLVMLite version: %s
  - LLVM version: %s""" % (
  # TODO: Needs to get the version from the shared library
  '0.2.0-EXPERIMENTAL',
  numpy.__version__,
  scipy.__version__,
  numba.__version__,
  llvmlite.__version__,
  llvm_version
))

