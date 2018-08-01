/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class maropu_lljvm_LLJVMNative */

#ifndef _Included_maropu_lljvm_LLJVMNative
#define _Included_maropu_lljvm_LLJVMNative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     maropu_lljvm_LLJVMNative
 * Method:    magicNumber
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_maropu_lljvm_LLJVMNative_magicNumber
  (JNIEnv *, jobject);

/*
 * Class:     maropu_lljvm_LLJVMNative
 * Method:    addressOf
 * Signature: ([B)J
 */
JNIEXPORT jlong JNICALL Java_maropu_lljvm_LLJVMNative_addressOf
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     maropu_lljvm_LLJVMNative
 * Method:    veryfyBitcode
 * Signature: ([B)V
 */
JNIEXPORT void JNICALL Java_maropu_lljvm_LLJVMNative_veryfyBitcode
  (JNIEnv *, jobject, jbyteArray);

/*
 * Class:     maropu_lljvm_LLJVMNative
 * Method:    asJVMAssemblyCode
 * Signature: ([BI)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_maropu_lljvm_LLJVMNative_asJVMAssemblyCode
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     maropu_lljvm_LLJVMNative
 * Method:    asLLVMAssemblyCode
 * Signature: ([B)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_maropu_lljvm_LLJVMNative_asLLVMAssemblyCode
  (JNIEnv *, jobject, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
