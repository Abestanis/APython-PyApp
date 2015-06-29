#include "main.h"

JNIEXPORT void JNICALL Java_com_apython_python_apython_1pyapp_PythonExecuteActivity_setEnv(JNIEnv *env, jobject obj, jstring jKey, jstring jValue) {
    const char *key = (*env)->GetStringUTFChars(env, jKey, 0);
    const char *value = (*env)->GetStringUTFChars(env, jValue, 0);
    setenv(key, value, 1);
    (*env)->ReleaseStringUTFChars(env, jKey, key);
    (*env)->ReleaseStringUTFChars(env, jValue, value);
}