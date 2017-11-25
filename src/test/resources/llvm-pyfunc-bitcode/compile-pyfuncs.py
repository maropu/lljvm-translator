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

def pyfunc1(x, y):
  return x + y

write_pyfunc_as_bitcode(pyfunc1, "int32(int32, int32)", "-int32")
write_pyfunc_as_bitcode(pyfunc1, "int64(int64, int64)", "-int64")
write_pyfunc_as_bitcode(pyfunc1, "float32(float32, float32)", "-float32")
write_pyfunc_as_bitcode(pyfunc1, "float64(float64, float64)", "-float64")

def pyfunc2(x, y):
  return math.pow(x, y)

write_pyfunc_as_bitcode(pyfunc2, "float32(float32, float32)", "-float32")
write_pyfunc_as_bitcode(pyfunc2, "float64(float64, float64)", "-float64")

def pyfunc3(x, y):
  return 2 * y + math.log10(x)

write_pyfunc_as_bitcode(pyfunc3, "float32(float32, float32)", "-float32")
write_pyfunc_as_bitcode(pyfunc3, "float64(float64, float64)", "-float64")

def pyfunc4(x):
  if x > 0:
    return 0
  else:
    return 1

write_pyfunc_as_bitcode(pyfunc4, "int32(int32)", "-int32")

def pyfunc5(x):
  value = x > 0 if 0 else 1
  return value

write_pyfunc_as_bitcode(pyfunc5, "int32(int32)", "-int32")

def pyfunc6(x, s):
  sum = 0
  for i in range(s):
    sum += x[i]
  return sum

write_pyfunc_as_bitcode(pyfunc6, "float32(float32[:], int32)", "-float32")
write_pyfunc_as_bitcode(pyfunc6, "float64(float64[:], int32)", "-float64")

