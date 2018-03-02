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

from numba import cfunc, jit

# A helper function to write a python function as LLVM bitcode
# by using the `cfunc` decorator.
def write_bitcode_with_cfunc(pyfunc, sig, filename_suffix=""):
  with open(pyfunc.__name__ + filename_suffix + ".bc", "wb") as fout:
    f = cfunc(sig, nopython=False, cache=False)(pyfunc)
    # print f.inspect_llvm()
    fout.write(f._library._final_module.as_bitcode())

# TODO: Gen'd LLVM bitcode by the `jit` decorator is not supported yet
# because we cannot resolve external references in it.
def write_bitcode_with_jit(pyfunc, sig, filename_suffix=""):
  with open(pyfunc.__name__ + filename_suffix + ".bc", "wb") as fout:
    f = jit(sig)(pyfunc)
    # print f.inspect_llvm()
    fout.write(f.overloads[f.signatures[0]].library._final_module.as_bitcode())

from add_test import *
write_bitcode_with_cfunc(add_test, "int32(int32, int32)", "-cfunc-int32")
write_bitcode_with_cfunc(add_test, "int64(int64, int64)", "-cfunc-int64")
write_bitcode_with_cfunc(add_test, "float32(float32, float32)", "-cfunc-float32")
write_bitcode_with_cfunc(add_test, "float64(float64, float64)", "-cfunc-float64")

from math_pow_test import *
write_bitcode_with_cfunc(math_pow_test, "float32(float32, float32)", "-cfunc-float32")
write_bitcode_with_cfunc(math_pow_test, "float64(float64, float64)", "-cfunc-float64")

from math_log10_test import *
write_bitcode_with_cfunc(math_log10_test, "float32(float32, float32)", "-cfunc-float32")
write_bitcode_with_cfunc(math_log10_test, "float64(float64, float64)", "-cfunc-float64")

from if1_test import *
write_bitcode_with_cfunc(if1_test, "int32(int32)", "-cfunc-int32")

from if2_test import *
write_bitcode_with_cfunc(if2_test, "int32(int32)", "-cfunc-int32")

from for1_test import *
write_bitcode_with_cfunc(for1_test, "float32(float32[:], int32)", "-cfunc-float32")
write_bitcode_with_cfunc(for1_test, "float64(float64[:], int32)", "-cfunc-float64")

from for2_test import *
write_bitcode_with_cfunc(for2_test, "float32(float32[:])", "-cfunc-float32")
write_bitcode_with_cfunc(for2_test, "float64(float64[:])", "-cfunc-float64")

from numpy_power_test  import *
write_bitcode_with_cfunc(numpy_power_test, "float32[:](float32[:], float32[:])", "-cfunc-float32")
write_bitcode_with_cfunc(numpy_power_test, "float64[:](float64[:], float64[:])", "-cfunc-float64")

from numpy_dot_test  import *
# Matrix * Matrix case
write_bitcode_with_cfunc(numpy_dot_test, "float32[:,:](float32[:,:], float32[:,:])", "-cfunc-mm-float32")
write_bitcode_with_cfunc(numpy_dot_test, "float64[:,:](float64[:,:], float64[:,:])", "-cfunc-mm-float64")
# Matrix * Vector case
write_bitcode_with_cfunc(numpy_dot_test, "float32[:](float32[:,:], float32[:])", "-cfunc-mv-float32")
write_bitcode_with_cfunc(numpy_dot_test, "float64[:](float64[:,:], float64[:])", "-cfunc-mv-float64")
# Vector * Vector case
write_bitcode_with_cfunc(numpy_dot_test, "float32(float32[:], float32[:])", "-cfunc-vv-float32")
write_bitcode_with_cfunc(numpy_dot_test, "float64(float64[:], float64[:])", "-cfunc-vv-float64")

from numba_examples.linear_regression import *
# write_bitcode_with_cfunc(linear_regression, "float32[:](float32[:], float32[:,:], float32[:], int32, float32)", "-numba-cfunc-float32")
write_bitcode_with_cfunc(linear_regression, "float64[:](float64[:], float64[:,:], float64[:], int64, float64)", "-numba-cfunc-float64")

from numba_examples.logistic_regression.logistic_regression import *
# write_bitcode_with_cfunc(logistic_regression, "float32[:](float32[:], float32[:,:], float32[:], int32)", "-numba-cfunc-float32")
write_bitcode_with_cfunc(logistic_regression, "float64[:](float64[:], float64[:,:], float64[:], int64)", "-numba-cfunc-float64")

