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
# Helper functions for I/O

from numba import cfunc, jit

# A helper function to save a python function as LLVM bitcode
# by using the `cfunc` decorator.
def save_llvm_with_cfunc(pyfunc, sig, filename):
  with open(filename + ".bc", "wb") as fout:
    f = cfunc(sig, nopython=False, cache=False)(pyfunc)
    fout.write(f._library._final_module.as_bitcode())

# TODO: Gen'd LLVM bitcode by the `jit` decorator is not supported yet
# because we cannot resolve external references in it.
def save_llvm_with_jit(pyfunc, sig, filename):
  with open(filename + ".bc", "wb") as fout:
    f = jit(sig)(pyfunc)
    fout.write(f.overloads[f.signatures[0]].library._final_module.as_bitcode())

