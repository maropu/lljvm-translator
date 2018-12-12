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
# Python script for compling Python functions with numba

import math
import numpy as np
import platform
import scipy

from numba import cfunc, jit

# LLVM v6.0.0, Python v2.7.15, Numba v0.40.0, and Scipy v1.1.0
# were used to run this script.

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

# Prints versions
print("Python version: " + platform.python_version())
print("NumPy version: " + np.__version__)
print("SciPy version: " + scipy.__version__)

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

from range_test import *
write_bitcode_with_cfunc(range_test, "float32[:](float32[:], int32)", "-cfunc-float32-1d")
write_bitcode_with_cfunc(range_test, "float32[:,:](float32[:,:], int32)", "-cfunc-float32-2d")
write_bitcode_with_cfunc(range_test, "float64[:](float64[:], int32)", "-cfunc-float64-1d")
write_bitcode_with_cfunc(range_test, "float64[:,:](float64[:,:], int32)", "-cfunc-float64-2d")

from func_call_test import *
write_bitcode_with_cfunc(func_call_test, "float32(float32, float32)", "-cfunc-float32")
write_bitcode_with_cfunc(func_call_test, "float64(float64, float64)", "-cfunc-float64")

from transpose1_test import *
write_bitcode_with_cfunc(transpose1_test, "float32[:,:](float32[:,:])", "-cfunc-float32")
write_bitcode_with_cfunc(transpose1_test, "float64[:,:](float64[:,:])", "-cfunc-float64")

from transpose2_test import *
write_bitcode_with_cfunc(transpose2_test, "float32[:,:](float32[:,:])", "-cfunc-float32")
write_bitcode_with_cfunc(transpose2_test, "float64[:,:](float64[:,:])", "-cfunc-float64")

# from numpy_item_test  import *
# write_bitcode_with_cfunc(numpy_item1_test, "int32(int32[:,:], int32)", "-cfunc-int32")
# write_bitcode_with_cfunc(numpy_item2_test, "int32(int32[:,:], int32, int32)", "-cfunc-int32")

# from numpy_tolist_test  import *
# write_bitcode_with_cfunc(numpy_tolist_test, "int64[:]()", "-cfunc-int64")

from numpy_array_test  import *
write_bitcode_with_cfunc(numpy_array_test, "int32[:,:]()", "-cfunc-int32")

from numpy_arange_test  import *
write_bitcode_with_cfunc(numpy_arange_test, "int64[:,:]()", "-cfunc-int64")

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

from numpy_random1_test import *
write_bitcode_with_cfunc(numpy_random1_test, "float64()", "-cfunc-float64")

from numpy_random2_test import *
write_bitcode_with_cfunc(numpy_random2_test, "float64[:](int64)", "-cfunc-float64")

from numpy_random3_test import *
# write_bitcode_with_cfunc(numpy_random3_test, "int64[:,:]()", "-cfunc-int64")

from numba_examples.linear_regression import *
# write_bitcode_with_cfunc(linear_regression, "float32[:,:](float32[:,:], float32[:,:], float32[:,:], int32, float32)", "-numba-cfunc-float32")
write_bitcode_with_cfunc(linear_regression, "float64[:,:](float64[:,:], float64[:,:], float64[:,:], int64, float64)", "-numba-cfunc-float64")

from numba_examples.logistic_regression import *
# write_bitcode_with_cfunc(logistic_regression, "float32[:,:](float32[:,:], float32[:,:], float32[:,:], int32)", "-numba-cfunc-float32")
write_bitcode_with_cfunc(logistic_regression, "float64[:,:](float64[:,:], float64[:,:], float64[:,:], int64)", "-numba-cfunc-float64")

from numba_examples.blur_image import *
# write_bitcode_with_cfunc(filter2d, "float32[:,:](float32[:,:], float32[:,:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(filter2d, "float64[:,:](float64[:,:], float64[:,:])", "-numba-cfunc-float64")

from numba_examples.bubblesort import *
write_bitcode_with_cfunc(bubblesort, "void(float32[:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(bubblesort, "void(float64[:])", "-numba-cfunc-float64")

from numba_examples.mergesort import *
# write_bitcode_with_cfunc(mergesort, "float32[:](float32[:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(mergesort, "float64[:](float64[:])", "-numba-cfunc-float64")

from numba_examples.kernel_density_estimation import *
write_bitcode_with_cfunc(kde, "float32(float32[:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(kde, "float64(float64[:])", "-numba-cfunc-float64")

from numba_examples.laplace2d import *
write_bitcode_with_cfunc(jacobi_relax_core, "float32(float32[:,:], float32[:,:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(jacobi_relax_core, "float64(float64[:,:], float64[:,:])", "-numba-cfunc-float64")

from numba_examples.mandel import *
# write_bitcode_with_cfunc(create_fractal, "float32[:,:](float32, float32, float32, float32, float32[:,:], int32)", "-numba-cfunc-float32")
write_bitcode_with_cfunc(create_fractal, "float64[:,:](float64, float64, float64, float64, float64[:,:], int64)", "-numba-cfunc-float64")

from numba_examples.pi import *
write_bitcode_with_cfunc(calc_pi, "float32(int32)", "-numba-cfunc-float32")
write_bitcode_with_cfunc(calc_pi, "float64(int64)", "-numba-cfunc-float64")

from numba_examples.sum import *
write_bitcode_with_cfunc(sum2d, "float32(float32[:,:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(sum2d, "float64(float64[:,:])", "-numba-cfunc-float64")

from numba_examples.ra24 import *
write_bitcode_with_cfunc(ra_numba, "float32[:](int32, float32[:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(ra_numba, "float64[:](int64, float64[:])", "-numba-cfunc-float64")

from numba_examples.movemean import *
write_bitcode_with_cfunc(move_mean, "void(float32[:], int32[:], float32[:])", "-numba-cfunc-float32")
write_bitcode_with_cfunc(move_mean, "void(float64[:], int64[:], float64[:])", "-numba-cfunc-float64")

