//
/**
 * Created by zhandalin on 2019-02-25 09:50.
 * 说明:
 */
//
#include <jni.h>
#include "ffmpeg_cmd.h"

typedef struct CallBackInfo {
    JNIEnv *env;
    jobject obj;
    jmethodID methodID;
} CallBackInfo;

void progressCallBack(int64_t handle, int what, float progress) {
    if (handle != 0) {
        struct CallBackInfo *onActionListener = (struct CallBackInfo *) (handle);
        JNIEnv *env=onActionListener->env;
        (*env)->CallVoidMethod(env,onActionListener->obj, onActionListener->methodID,
                                              progress);
    }
}

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_FFmpegCMDUtil_executeFFmpegCommand(JNIEnv *env,
                                                          jclass type,
                                                          jstring command_,
                                                          jobject actionCallBack) {
    int ret = 0;
    const char *command = (*env)->GetStringUTFChars(env,command_, 0);
    if (NULL != actionCallBack) {
        jclass actionClass = (*env)->GetObjectClass(env,actionCallBack);
        jmethodID progressMID = (*env)->GetMethodID(env,actionClass, "progress", "(F)V");
        jmethodID failMID = (*env)->GetMethodID(env,actionClass, "fail", "()V");
        jmethodID successMID = (*env)->GetMethodID(env,actionClass, "success", "()V");


        CallBackInfo onActionListener;
        onActionListener.env = env;
        onActionListener.obj = actionCallBack;
        onActionListener.methodID = progressMID;
        ret = executeFFmpegCommand((int64_t) (&onActionListener), command,
                                   progressCallBack);
        if (ret < 0) {
            (*env)->CallVoidMethod(env,actionCallBack, failMID);
        } else {
            (*env)->CallVoidMethod(env,actionCallBack, successMID);
        }
        (*env)->DeleteLocalRef(env,actionClass);
    } else {
        ret = executeFFmpegCommand(0, command, NULL);
    }
    (*env)->ReleaseStringUTFChars(env,command_, command);
    return ret;
}

