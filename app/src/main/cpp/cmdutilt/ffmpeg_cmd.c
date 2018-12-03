#include <string.h>
#include "ffmpeg.h"

//#include <android/log.h>
//#include <jni.h>
//
//const char *TAG = "bz_";
//
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
//        } else if (level == 48) {
//            __android_log_vprint(ANDROID_LOG_DEBUG, TAG, fmt, vl);
//        } else {
////            __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
//        }
//    }
//}
//
////回调合并进度
//void progressCallBack(int type, int what, float progress) {
////    LOGD("progressCallBack=%f", progress);
//    __android_log_print(ANDROID_LOG_DEBUG, TAG, "type=%d--what=%d--progress=%f", type, what,
//                        progress);
//}

int executeFFmpegCommand(int callbackType, const char *command,
                         void (*progressCallBack)(int, int, float)) {

    char *pCommand = (char *) command;
    int stingLen = strlen(command);
    char *argv[stingLen];

    char *buffer = NULL;
    int index = 0;
    int isStartYH = 0;
    for (int i = 0; i < stingLen; ++i) {
        char str = *pCommand;
        pCommand++;
        if (NULL == buffer) {
            buffer = malloc(512);
            memset(buffer, 0, 512);
            argv[index++] = buffer;
        }
        //保证引号成对出现
        if (str == '"') {
            if (isStartYH) {
                isStartYH = 0;
            } else {
                isStartYH = 1;
            }
            continue;
        }
        if (str != ' ' || isStartYH) {
            *buffer = str;
            buffer++;
        } else {
            buffer = NULL;
        }
    }
    for (int i = 0; i < index; ++i) {
        av_log(NULL, AV_LOG_DEBUG, "cmd=%s", argv[i]);
    }
    //手动告诉它结束了,防止出现意外
    argv[index] = 0;
    int ret = run(callbackType, index, argv, progressCallBack);
    for (int i = 0; i < index; ++i) {
        free(argv[i]);
    }
    return ret;
}

//
//JNIEXPORT jint JNICALL
//Java_com_luoye_bzmedia_FFmpegUtil_executeFFmpegCommand(JNIEnv *env, jclass type, jstring command_,
//                                                       jboolean needProgressCallBack) {
//    int ret = 0;
//    const char *command = (*env)->GetStringUTFChars(env, command_, 0);
//
//    av_log_set_callback(log_call_back);
//    register_lib();
//
//    if (needProgressCallBack) {
//        ret = executeFFmpegCommand(0, command, progressCallBack);
//    } else {
//        ret = executeFFmpegCommand(0, command, NULL);
//    }
//
//    (*env)->ReleaseStringUTFChars(env, command_, command);
//    return ret;
//}