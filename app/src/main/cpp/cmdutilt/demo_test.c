//
/**
 * Created by zhandalin on 2019-02-25 09:50.
 * 说明:
 */
//

#include <android/log.h>
#include <jni.h>
#include "ffmpeg.h"
#include "ffmpeg_cmd.h"




const char *TAG = "bz_";

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
        } else if (level == 48) {
            __android_log_vprint(ANDROID_LOG_DEBUG, TAG, fmt, vl);
        } else {
//            __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
        }
    }
}

//回调合并进度
void progressCallBack(int64_t type, int what, float progress) {
//    LOGD("progressCallBack=%f", progress);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "type=%lld--what=%d--progress=%f", type, what,
                        progress);
}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_FFmpegUtil_executeFFmpegCommand(JNIEnv *env, jclass type, jstring command_,
                                                       jboolean needProgressCallBack) {
    int ret = 0;
    const char *command = (*env)->GetStringUTFChars(env, command_, 0);

    avcodec_register_all();
#if CONFIG_AVDEVICE
    avdevice_register_all();
#endif
    avfilter_register_all();
    av_register_all();
    avformat_network_init();

    av_log_set_callback(log_call_back);
    if (needProgressCallBack) {
        ret = executeFFmpegCommand(-99, command, progressCallBack);
    } else {
        ret = executeFFmpegCommand(-99, command, NULL);
    }

    (*env)->ReleaseStringUTFChars(env, command_, command);
    return ret;
}
