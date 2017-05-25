#include <jni.h>
#include <string.h>
#include "ffmpeg.h"

JNIEXPORT jint JNICALL
Java_com_luoye_bzmedia_FFmpegUtil_executeFFmpegCommand(JNIEnv
                                                       *env, jclass type, jstring command_) {
    int ret = 0;
    const char *command = (*env)->GetStringUTFChars(env, command_, 0);

    char str[1024] = {0};
    strcpy(str, command);
    char *argv[strlen(command)];

    const char *delims = " ";
    char *result = strtok(str, delims);

    argv[0] = " ";
    int index = 1;//TODO 看看有没有问题
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