/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_xhrd_mobile_hybridframework_util_EncryptUtil */

#ifndef _Included_com_xhrd_mobile_hybridframework_util_EncryptUtil
#define _Included_com_xhrd_mobile_hybridframework_util_EncryptUtil
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_xhrd_mobile_hybridframework_util_EncryptUtil
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_xhrd_mobile_hybridframework_util_EncryptUtil_init
  (JNIEnv *, jclass);

/*
 * Class:     com_xhrd_mobile_hybridframework_util_EncryptUtil
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_xhrd_mobile_hybridframework_util_EncryptUtil_close
  (JNIEnv *, jclass);

/*
 * Class:     com_xhrd_mobile_hybridframework_util_EncryptUtil
 * Method:    encrypt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_xhrd_mobile_hybridframework_util_EncryptUtil_encrypt
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     com_xhrd_mobile_hybridframework_util_EncryptUtil
 * Method:    decrypt
 * Signature: ([B)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_xhrd_mobile_hybridframework_util_EncryptUtil_decrypt
  (JNIEnv *, jclass, jbyteArray);

/*
 * Class:     com_xhrd_mobile_hybridframework_util_EncryptUtil
 * Method:    getInt
 * Signature: ([B)I
 */
JNIEXPORT jint JNICALL Java_com_xhrd_mobile_hybridframework_util_EncryptUtil_getInt
  (JNIEnv *, jclass, jbyteArray);

#ifdef __cplusplus
}
#endif
#endif
