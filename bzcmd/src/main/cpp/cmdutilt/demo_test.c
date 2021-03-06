//
/**
 * Created by zhandalin on 2019-02-25 09:50.
 * 说明:
 */
//
#include <jni.h>
#include "ffmpeg_cmd.h"
#include <android/log.h>
#include <libavutil/log.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <stdbool.h>

extern int hasRegistered;
typedef struct CallBackInfo {
    JNIEnv *env;
    jobject obj;
    jmethodID methodID;
} CallBackInfo;
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
        } else if (level <= 24) {
            __android_log_vprint(ANDROID_LOG_WARN, TAG, fmt, vl);
        } else {
//            __android_log_vprint(ANDROID_LOG_VERBOSE, TAG, fmt, vl);
        }
    }
}

void progressCallBack(int64_t handle, int what, float progress) {
    if (handle != 0) {
        struct CallBackInfo *onActionListener = (struct CallBackInfo *) (handle);
        JNIEnv *env = onActionListener->env;
        (*env)->CallVoidMethod(env, onActionListener->obj, onActionListener->methodID,
                               progress);
    }
}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_utils_FFmpegCMDUtil_executeFFmpegCommand(JNIEnv *env,
                                                                jclass type,
                                                                jstring command_,
                                                                jobject actionCallBack,
                                                                jlong totalTime) {
    int ret = 0;
    const char *command = (*env)->GetStringUTFChars(env, command_, 0);
    if (NULL != actionCallBack) {
        jclass actionClass = (*env)->GetObjectClass(env, actionCallBack);
        jmethodID progressMID = (*env)->GetMethodID(env, actionClass, "progress", "(F)V");
        jmethodID failMID = (*env)->GetMethodID(env, actionClass, "fail", "()V");
        jmethodID successMID = (*env)->GetMethodID(env, actionClass, "success", "()V");


        CallBackInfo onActionListener;
        onActionListener.env = env;
        onActionListener.obj = actionCallBack;
        onActionListener.methodID = progressMID;
        ret = executeFFmpegCommand4TotalTime((int64_t) (&onActionListener), command,
                                             progressCallBack, totalTime);
        if (ret < 0) {
            (*env)->CallVoidMethod(env, actionCallBack, failMID);
        } else {
            (*env)->CallVoidMethod(env, actionCallBack, successMID);
        }
        (*env)->DeleteLocalRef(env, actionClass);
    } else {
        ret = executeFFmpegCommand(0, command, NULL);
    }
    (*env)->ReleaseStringUTFChars(env, command_, command);
    return ret;
}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_utils_FFmpegCMDUtil_showLog(JNIEnv *env, jclass clazz, jboolean showLog) {
    if (showLog) {
        av_log_set_callback(log_call_back);
    } else {
        av_log_set_callback(NULL);
    }
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_utils_FFmpegCMDUtil_cancelExecuteFFmpegCommand(JNIEnv *env, jclass clazz) {
    return cancelExecuteFFmpegCommand();
}

JNIEXPORT jlong JNICALL
Java_com_luoye_bzmedia_utils_FFmpegCMDUtil_getMediaDuration(JNIEnv *env, jclass clazz,
                                                            jstring media_path) {
    if (NULL == media_path) {
        return -1;
    }
    if (!hasRegistered) {
        av_register_all();
        avcodec_register_all();
        avfilter_register_all();
        avformat_network_init();
        hasRegistered = true;
    }
    const char *mediaPath = (*env)->GetStringUTFChars(env, media_path, 0);
    AVFormatContext *in_fmt_ctx = NULL;
    int ret = 0;
    if ((ret = avformat_open_input(&in_fmt_ctx, mediaPath, NULL, NULL)) < 0) {
        av_log(NULL, AV_LOG_ERROR, "Cannot open input file mediaPath=%s", mediaPath);
        return ret;
    }
    if ((ret = avformat_find_stream_info(in_fmt_ctx, NULL)) < 0) {
        av_log(NULL, AV_LOG_ERROR, "Cannot find stream information\n");
        return ret;
    }
    int64_t videoDuration = 0;
    for (int i = 0; i < in_fmt_ctx->nb_streams; i++) {
        AVStream *stream;
        stream = in_fmt_ctx->streams[i];
        int64_t temp = stream->duration * 1000 * stream->time_base.num /
                       stream->time_base.den;
        if (temp > videoDuration)
            videoDuration = temp;
    }
    if (NULL != in_fmt_ctx)
        avformat_close_input(&in_fmt_ctx);

    (*env)->ReleaseStringUTFChars(env, media_path, mediaPath);
    return videoDuration;
}