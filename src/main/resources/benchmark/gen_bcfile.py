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

from numba import cfunc

def pySum(x, s):
  sum = 0
  for i in range(s):
    sum += x[i]
  return sum

# Write a python function as LLVM bitcode
with open("pysum-float64.bc", "wb") as fout:
  f = cfunc("float64(float64[:], int32)")(pySum)
  fout.write(f._library._final_module.as_bitcode())
