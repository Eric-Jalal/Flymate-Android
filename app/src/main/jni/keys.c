//
// Created by Amirhossein Hosseini on 2/3/19.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_net_teslaa_flymate_ui_OpenWeather_getNativeKey1(JNIEnv *env, jobject instance) {
 return (*env)->  NewStringUTF(env, "3c1fc1665bcfc46d841ebde49c835fd2");
}