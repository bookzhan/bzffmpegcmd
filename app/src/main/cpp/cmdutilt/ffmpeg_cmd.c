#include <jni.h>
#include <string.h>
#include "ffmpeg.h"

#include <android/log.h>

const char *TAG = "bookzhan";

void log_call_back(void *ptr, int level, const char *fmt, va_list vl) {
    //自定义的日志
    if (level == 3) {
        __android_log_vprint(ANDROID_LOG_ERROR, TAG, fmt, vl);
    } else if (level == 2) {
        __android_log_vprint(ANDROID_LOG_DEBUG, TAG, fmt, vl);
    } else if (level == 1) {
        __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
    } else {
        if (level <= 16) {//ffmpeg 来的日志
            __android_log_vprint(ANDROID_LOG_ERROR, TAG, fmt, vl);
        } else {
            __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
        }
    }
}

//回调合并进度
void progressCallBack(int type, int what, float progress) {
//    LOGD("progressCallBack=%f", progress);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "type=%d--what=%d--progress=%f", type, what,
                        progress);
}

int executeFFmpegCommand(const char *command, void (*progressCallBack)(int, int, float)) {

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
    return run(index, argv, progressCallBack);
}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_FFmpegUtil_executeFFmpegCommand(JNIEnv
                                                       *env, jclass type, jstring command_) {
    int ret = 0;
    const char *command = (*env)->GetStringUTFChars(env, command_, 0);

    av_log_set_callback(log_call_back);
    register_lib();

    ret = executeFFmpegCommand(command, progressCallBack);
    (*env)->ReleaseStringUTFChars(env, command_, command);
    return ret;
}