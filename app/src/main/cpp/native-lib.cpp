//
// Created by papa on 25/11/2020.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_edu_uoc_pac3_data_oauth_OAuthConstants_stringFromJNI(
        JNIEnv *env,
        jobject /*this*/) {
    std::string user = "w52mk080xjl1m0cg8vrnc25l4480wj";
    return env-> NewStringUTF(user.c_str());
}
