/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class io_github_maropu_lljvm_runtime_NumbaRuntimeNative */

#ifndef _Included_io_github_maropu_lljvm_runtime_NumbaRuntimeNative
#define _Included_io_github_maropu_lljvm_runtime_NumbaRuntimeNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    initialize
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_initialize
  (JNIEnv *, jobject);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    setSystemPath
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_setSystemPath
  (JNIEnv *, jobject, jstring);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    _numba_attempt_nocopy_reshape
 * Signature: (JJJJJJJI)I
 */
JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative__1numba_1attempt_1nocopy_1reshape
  (JNIEnv *, jobject, jlong, jlong, jlong, jlong, jlong, jlong, jlong, jint);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    numba_get_np_random_state
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1get_1np_1random_1state
  (JNIEnv *, jobject);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    numba_rnd_shuffle
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1rnd_1shuffle
  (JNIEnv *, jobject, jlong);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    numba_xxdot
 * Signature: (BBJJJJ)I
 */
JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1xxdot
  (JNIEnv *, jobject, jbyte, jbyte, jlong, jlong, jlong, jlong);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    numba_xxgemv
 * Signature: (BBJJJJJJJJ)I
 */
JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1xxgemv
  (JNIEnv *, jobject, jbyte, jbyte, jlong, jlong, jlong, jlong, jlong, jlong, jlong, jlong);

/*
 * Class:     io_github_maropu_lljvm_runtime_NumbaRuntimeNative
 * Method:    numba_xxgemm
 * Signature: (BBBJJJJJJJJJJJ)I
 */
JNIEXPORT jint JNICALL Java_io_github_maropu_lljvm_runtime_NumbaRuntimeNative_numba_1xxgemm
  (JNIEnv *, jobject, jbyte, jbyte, jbyte, jlong, jlong, jlong, jlong, jlong, jlong, jlong, jlong, jlong, jlong, jlong);

#ifdef __cplusplus
}
#endif
#endif
