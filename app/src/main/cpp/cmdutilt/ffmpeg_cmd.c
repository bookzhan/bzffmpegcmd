#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "ffmpeg.h"
//const char *TAG = "bookzhan";
//void log_call_back(void *ptr, int level, const char *fmt, va_list vl) {
//    //自定义的日志
//    if (level == 3) {
//        __android_log_vprint(ANDROID_LOG_ERROR, TAG, fmt, vl);
//    } else if (level == 2) {
//        __android_log_vprint(ANDROID_LOG_DEBUG, TAG, fmt, vl);
//    } else if (level == 1) {
//        __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
//    } else {
//        if (level <= 16) {//ffmpeg 来的日志
//            __android_log_vprint(ANDROID_LOG_ERROR, TAG, fmt, vl);
//        } else {
//            __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
//        }
//    }
//}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_FFmpegUtil_executeFFmpegCommand(JNIEnv
                                                       *env, jclass type, jstring command_) {
    int ret = 0;
    const char *command = (*env)->GetStringUTFChars(env, command_, 0);

//    av_log_set_callback(log_call_back);
    char str[1024] = {0};

    strlcpy(str, command, strlen(command) + 1);
    char *argv[strlen(command)];

    const char *delims = " ";
    char *result = strtok(str, delims);

    int index = 0;
    while (result != NULL) {
        argv[index] = result;
        result = strtok(NULL, delims);
        index++;
    }
    //手动告诉它结束了,防止出现意外
    argv[index] = 0;
    ret = run(index, argv);
    (*env)->ReleaseStringUTFChars(env, command_, command);
    return ret;
}