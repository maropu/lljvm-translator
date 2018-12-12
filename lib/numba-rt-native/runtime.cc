/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.io/github/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "io_github_maropu_lljvm_runtime_NumbaRuntimeNative.h"

#include <Python.h>

// XXX
#define NUMBA_EXPORT_FUNC(_rettype) static _rettype
#define NUMBA_EXPORT_DATA(_vartype) static _vartype

#include "numba/numba/_random.c"

JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_initialize
    (JNIEnv *env, jobject self) {
  Py_Initialize();
}

JNIEXPORT jlong JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1get_1np_1random_1state
    (JNIEnv *env, jobject self) {
  return (jlong) numba_get_np_random_state();
}

JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1rnd_1shuffle
    (JNIEnv *env, jobject self, jlong state) {
  numba_rnd_shuffle((rnd_state_t *) state);
}

