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

package io.github.maropu.lljvm.runtime;

import io.github.maropu.lljvm.LLJVMRuntimeException;

/**
 * JNI interfaces of the Numba Runtime.
 *
 * TODO: Needs to check all the Numba v0.41.0 exported functions as listed below;
 *
 * // numba/_helperlib.c
 *  - NUMBA_EXPORT_FUNC(double) numba_fixed_fmod(double x, double y)
 *  - NUMBA_EXPORT_FUNC(float) numba_fixed_fmodf(float x,float y)
 *  - NUMBA_EXPORT_FUNC(void) numba_set_fnclex(void*fn)
 *  - NUMBA_EXPORT_FUNC(int64_t) numba_sdiv(int64_t a,int64_t b)
 *  - NUMBA_EXPORT_FUNC(uint64_t) numba_udiv(uint64_t a,uint64_t b)
 *  - NUMBA_EXPORT_FUNC(int64_t) numba_srem(int64_t a,int64_t b)
 *  - NUMBA_EXPORT_FUNC(uint64_t) numba_urem(uint64_t a, uint64_t b)
 *  - NUMBA_EXPORT_FUNC(double) numba_frexp(double x,int*exp)
 *  - NUMBA_EXPORT_FUNC(float) numba_frexpf(float x,int*exp)
 *  - NUMBA_EXPORT_FUNC(double) numba_ldexp(double x,int exp)
 *  - NUMBA_EXPORT_FUNC(double) numba_ldexp(double x, int exp)
 *  - NUMBA_EXPORT_FUNC(float) numba_ldexpf(float x, int exp)
 *  - NUMBA_EXPORT_FUNC(void) numba_cpow(Py_complex *a, Py_complex *b, Py_complex *out)
 *  - NUMBA_EXPORT_FUNC(void) numba_cpowf(npy_cfloat *a, npy_cfloat *b, npy_cfloat *out)
 *  - NUMBA_EXPORT_FUNC(double) numba_gamma(double x)
 *  - NUMBA_EXPORT_FUNC(float) numba_gammaf(float x)
 *  - NUMBA_EXPORT_FUNC(double) numba_lgamma(double x)
 *  - NUMBA_EXPORT_FUNC(float) numba_lgammaf(float x)
 *  - NUMBA_EXPORT_FUNC(double) numba_erf(double x)
 *  - NUMBA_EXPORT_FUNC(float) numba_erff(float x)
 *  - NUMBA_EXPORT_FUNC(double) numba_erfc(double x)
 *  - NUMBA_EXPORT_FUNC(float) numba_erfcf(float x)
 *  - NUMBA_EXPORT_FUNC(int) numba_signbitf(float a)
 *  - NUMBA_EXPORT_FUNC(int) numba_signbit(npy_double a)
 *  - NUMBA_EXPORT_FUNC(int) numba_complex_adaptor(PyObject* obj, Py_complex *out)
 *  - NUMBA_EXPORT_FUNC(void *) numba_extract_record_data(PyObject *recordobj, Py_buffer *pbuf)
 *  - NUMBA_EXPORT_FUNC(PyObject *) numba_recreate_record(void *pdata, int size, PyObject *dtype)
 *  - NUMBA_EXPORT_FUNC(int) numba_adapt_ndarray(PyObject *obj, arystruct_t* arystruct)
 *  - NUMBA_EXPORT_FUNC(int) numba_get_buffer(PyObject *obj, Py_buffer *buf)
 *  - NUMBA_EXPORT_FUNC(void) numba_adapt_buffer(Py_buffer *buf, arystruct_t *arystruct)
 *  - NUMBA_EXPORT_FUNC(void) numba_release_buffer(Py_buffer *buf)
 *  - NUMBA_EXPORT_FUNC(PyObject *) numba_ndarray_new(int nd, npy_intp *dims, npy_intp *strides, void* data, int type_num, int itemsize)
 *  - NUMBA_EXPORT_FUNC(int) numba_attempt_nocopy_reshape(npy_intp nd, const npy_intp *dims, const npy_intp *strides, npy_intp newnd, const npy_intp *newdims, npy_intp *newstrides, npy_intp itemsize, int is_f_order)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_import_cython_function(PyObject *self, PyObject *args)
 *  - NUMBA_EXPORT_FUNC(npy_int64) numba_extract_np_datetime(PyObject *td)
 *  - NUMBA_EXPORT_FUNC(npy_int64) numba_extract_np_timedelta(PyObject *td)
 *  - NUMBA_EXPORT_FUNC(PyObject *) numba_create_np_datetime(npy_int64 value, int unit_code)
 *  - NUMBA_EXPORT_FUNC(PyObject *) numba_create_np_timedelta(npy_int64 value, int unit_code)
 *  - NUMBA_EXPORT_FUNC(uint64_t) numba_fptoui(double x)
 *  - NUMBA_EXPORT_FUNC(uint64_t) numba_fptouif(float x)
 *  - NUMBA_EXPORT_FUNC(void) numba_gil_ensure(PyGILState_STATE *state)
 *  - NUMBA_EXPORT_FUNC(void) numba_gil_release(PyGILState_STATE *state)
 *  - NUMBA_EXPORT_FUNC(PyObject *) numba_py_type(PyObject *obj)
 *  - NUMBA_EXPORT_FUNC(void) numba_set_pyobject_private_data(PyObject *obj, void *ptr)
 *  - NUMBA_EXPORT_FUNC(void *) numba_get_pyobject_private_data(PyObject *obj)
 *  - NUMBA_EXPORT_FUNC(void) numba_reset_pyobject_private_data(PyObject *obj)
 *  - NUMBA_EXPORT_FUNC(int)
 *  - numba_unpack_slice(PyObject *obj, Py_ssize_t *start, Py_ssize_t *stop, Py_ssize_t *step)
 *  - NUMBA_EXPORT_FUNC(int) numba_fatal_error(void)
 *  - NUMBA_EXPORT_FUNC(int) numba_do_raise(PyObject *exc_packed)
 *  - NUMBA_EXPORT_FUNC(PyObject *) numba_unpickle(const char *data, int n)
 *  - NUMBA_EXPORT_FUNC(void *) numba_extract_unicode(PyObject *obj, Py_ssize_t *length, int *kind)
 *
 * // numba/_lapack.c
 *  - NUMBA_EXPORT_FUNC(int) numba_xxdot(char kind, char conjugate, Py_ssize_t n, void *dx, void *dy,  void *result)
 *  - NUMBA_EXPORT_FUNC(int) numba_xxgemv(char kind, char trans, Py_ssize_t m, Py_ssize_t n, void *alpha, void *a, Py_ssize_t lda, void *x, void *beta, void *y)
 *  - NUMBA_EXPORT_FUNC(int) numba_xxgemm(char kind, char transa, char transb, Py_ssize_t m, Py_ssize_t n, Py_ssize_t k, void *alpha, void *a, Py_ssize_t lda, void *b, Py_ssize_t ldb, void *beta, void *c, Py_ssize_t ldc)
 *  - NUMBA_EXPORT_FUNC(F_INT) numba_xxnrm2(char kind, Py_ssize_t n, void * x, Py_ssize_t incx, void * result)
 *  - NUMBA_EXPORT_FUNC(int) numba_xxgetrf(char kind, Py_ssize_t m, Py_ssize_t n, void *a, Py_ssize_t lda, F_INT *ipiv)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_xxgetri(char kind, Py_ssize_t n, void *a, Py_ssize_t lda, F_INT *ipiv)
 *  - NUMBA_EXPORT_FUNC(int) numba_xxpotrf(char kind, char uplo, Py_ssize_t n, void *a, Py_ssize_t lda)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_rgeev(char kind, char jobvl, char jobvr, Py_ssize_t n, void *a, Py_ssize_t lda, void *wr, void *wi, void *vl, Py_ssize_t ldvl, void *vr, Py_ssize_t ldvr)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_cgeev(char kind, char jobvl, char jobvr,  Py_ssize_t n, void *a, Py_ssize_t lda, void *w, void *vl, Py_ssize_t ldvl, void *vr, Py_ssize_t ldvr)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_xxxevd(char kind, char jobz, char uplo, Py_ssize_t n, void *a, Py_ssize_t lda, void *w)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_gesdd(char kind, char jobz, Py_ssize_t m, Py_ssize_t n, void *a, Py_ssize_t lda, void *s, void *u, Py_ssize_t ldu, void *vt, Py_ssize_t ldvt)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_geqrf(char kind, Py_ssize_t m, Py_ssize_t n, void *a, Py_ssize_t lda, void *tau)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_xxgqr(char kind, Py_ssize_t m, Py_ssize_t n, Py_ssize_t k, void *a, Py_ssize_t lda, void *tau)
 *  - NUMBA_EXPORT_FUNC(int) numba_ez_gelsd(char kind, Py_ssize_t m, Py_ssize_t n, Py_ssize_t nrhs, void *a, Py_ssize_t lda, void *b, Py_ssize_t ldb, void *S, double rcond, Py_ssize_t * rank)
 *  - NUMBA_EXPORT_FUNC(int) numba_xgesv(char kind, Py_ssize_t n, Py_ssize_t nrhs, void *a, Py_ssize_t lda, F_INT *ipiv, void *b, Py_ssize_t ldb)
 *
 * // numba/_random.c
 *  - NUMBA_EXPORT_FUNC(void) numba_rnd_shuffle(rnd_state_t *state)
 *  - NUMBA_EXPORT_FUNC(void) numba_rnd_init(rnd_state_t *state, unsigned int seed)
 *  - NUMBA_EXPORT_FUNC(void) numba_rnd_ensure_global_init(void)
 *  - NUMBA_EXPORT_FUNC(rnd_state_t *) numba_get_py_random_state(void)
 *  - NUMBA_EXPORT_FUNC(rnd_state_t *) numba_get_np_random_state(void)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_rnd_get_py_state_ptr(PyObject *self)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_rnd_get_np_state_ptr(PyObject *self)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_rnd_shuffle(PyObject *self, PyObject *arg)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_rnd_set_state(PyObject *self, PyObject *args)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_rnd_get_state(PyObject *self, PyObject *arg)
 *  - NUMBA_EXPORT_FUNC(PyObject *) _numba_rnd_seed(PyObject *self, PyObject *args)
 *  - NUMBA_EXPORT_FUNC(unsigned int) get_next_int32(rnd_state_t *state)
 *  - NUMBA_EXPORT_FUNC(double) get_next_double(rnd_state_t *state)
 *  - NUMBA_EXPORT_FUNC(double) loggam(double x)
 *  - NUMBA_EXPORT_FUNC(int64_t) numba_poisson_ptrs(rnd_state_t *state, double lam)
 *
 * // numba/runtime/_nrt_python.c
 *  - NUMBA_EXPORT_FUNC(NRT_MemInfo *) NRT_meminfo_new_from_pyobject(void *data, PyObject *ownerobj)
 *  - NUMBA_EXPORT_FUNC(int) NRT_adapt_ndarray_from_python(PyObject *obj, arystruct_t* arystruct)
 *  - NUMBA_EXPORT_FUNC(PyObject *) NRT_adapt_ndarray_to_python(arystruct_t* arystruct, int ndim, int writeable, PyArray_Descr *descr)
 *  - NUMBA_EXPORT_FUNC(void) NRT_adapt_buffer_from_python(Py_buffer *buf, arystruct_t *arystruct)
 */
public class NumbaRuntimeNative {

  // Initialize a Python interpreter and the others for Numba operations
  public native void initialize() throws LLJVMRuntimeException;

  // For NumPy arange
  public native int _numba_attempt_nocopy_reshape(
    long nd,
    long dims,
    long strides,
    long newnd,
    long newdims,
    long newstrides,
    long itemsize,
    int is_f_order);

  // For NumPy random
  public native long numba_get_np_random_state() throws LLJVMRuntimeException;
  public native void numba_rnd_shuffle(long state) throws LLJVMRuntimeException;

  // For NumPy dot
  public native int numba_xxdot(
    byte kind,
    byte conjugate,
    long n,
    long x,
    long y,
    long result) throws LLJVMRuntimeException;

  public native int numba_xxgemv(
    byte kind,
    byte trans,
    long m,
    long n,
    long alpha,
    long a,
    long lda,
    long x,
    long beta,
    long y) throws LLJVMRuntimeException;

  public native int numba_xxgemm(
    byte kind,
    byte transa,
    byte transb,
    long m,
    long n,
    long k,
    long alpha,
    long a,
    long lda,
    long b,
    long ldb,
    long beta,
    long c,
    long ldc) throws LLJVMRuntimeException;
}
