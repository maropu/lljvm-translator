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

import math

from numba import cfunc

# A helper function to write a python function as LLVM bitcode
def write_pyfunc_as_bitcode(pyfunc, sig, filename_suffix=""):
  with open(pyfunc.__name__ + filename_suffix + ".bc", "wb") as fout:
    f = cfunc(sig)(pyfunc)
    fout.write(f._library._final_module.as_bitcode())

from pyfunc1 import *
write_pyfunc_as_bitcode(pyfunc1, "int32(int32, int32)", "-int32")
write_pyfunc_as_bitcode(pyfunc1, "int64(int64, int64)", "-int64")
write_pyfunc_as_bitcode(pyfunc1, "float32(float32, float32)", "-float32")
write_pyfunc_as_bitcode(pyfunc1, "float64(float64, float64)", "-float64")

from pyfunc2 import *
write_pyfunc_as_bitcode(pyfunc2, "float32(float32, float32)", "-float32")
write_pyfunc_as_bitcode(pyfunc2, "float64(float64, float64)", "-float64")

from pyfunc3 import *
write_pyfunc_as_bitcode(pyfunc3, "float32(float32, float32)", "-float32")
write_pyfunc_as_bitcode(pyfunc3, "float64(float64, float64)", "-float64")

from pyfunc4 import *
write_pyfunc_as_bitcode(pyfunc4, "int32(int32)", "-int32")

from pyfunc5 import *
write_pyfunc_as_bitcode(pyfunc5, "int32(int32)", "-int32")

from pyfunc6_for1 import *
write_pyfunc_as_bitcode(pyfunc6_for1, "float32(float32[:], int32)", "-float32")
write_pyfunc_as_bitcode(pyfunc6_for1, "float64(float64[:], int32)", "-float64")

from pyfunc6_for2 import *
write_pyfunc_as_bitcode(pyfunc6_for2, "float32(float32[:])", "-float32")
write_pyfunc_as_bitcode(pyfunc6_for2, "float64(float64[:])", "-float64")

from pyfunc7 import *
write_pyfunc_as_bitcode(pyfunc7, "float32(float32[:])", "-float32")
write_pyfunc_as_bitcode(pyfunc7, "float64(float64[:])", "-float64")

