/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class io_github_maropu_lljvm_LLJVMNative */

#ifndef _Included_io_github_maropu_lljvm_LLJVMNative
#define _Included_io_github_maropu_lljvm_LLJVMNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     io_github_maropu_lljvm_LLJVMNative
 * Method:    magicNumber
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_github_maropu_lljvm_LLJVMNative_magicNumber
  (JNIEnv *, jobject);

/*
 * Class:     io_github_maropu_lljvm_LLJVMNative
 * Method:    addressOf
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_io_github_maropu_lljvm_LLJVMNative_addressOf
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     io_github_maropu_lljvm_LLJVMNative
 * Method:    veryfyBitcode
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_io_github_maropu_lljvm_LLJVMNative_veryfyBitcode
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     io_github_maropu_lljvm_LLJVMNative
 * Method:    asJVMAssemblyCode
 * Signature: ([BIII)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_github_maropu_lljvm_LLJVMNative_asJVMAssemblyCode
  (JNIEnv *, jobject, jbyteArray, jint, jint, jint);

/*
 * Class:     io_github_maropu_lljvm_LLJVMNative
 * Method:    asLLVMAssemblyCode
 * Signature: ([BII)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_io_github_maropu_lljvm_LLJVMNative_asLLVMAssemblyCode
  (JNIEnv *, jobject, jbyteArray, jint, jint);

#ifdef __cplusplus
}
#endif
#endif
