#include <string.h>
#include <stdbool.h>
#include "ffmpeg.h"

//保证同时只能一个线程执行
static pthread_mutex_t cmdLock;
static int cmdLockHasInit = 0;
bool hasRegistered = false;

char *pre_hande_cmd(const char *src_cmd) {
    char *final_cmd = malloc(2048);
    memset(final_cmd, 0, 2048);
    const char *option = "-c:v";  // 子串
    // 检测是否包含子串
    if (strstr(src_cmd, option) == NULL) {
        const char *last_space = strrchr(src_cmd, ' ');
        strncpy(final_cmd, src_cmd, last_space - src_cmd + 1);  // 复制最后一个空格之前的部分
        strcpy(final_cmd + (last_space - src_cmd + 1), "-c:v libx264 -preset ultrafast ");  // 添加选项和之后的部分
        strcat(final_cmd, last_space + 1);  // 添加空格之后的内容
    } else {
        strcpy(final_cmd, src_cmd);
    }
    return final_cmd;
}

int executeFFmpegCommand4TotalTime(int64_t handle, const char *in_command,
                                   void (*progressCallBack)(int64_t, int, float),
                                   int64_t totalTime) {
    if (NULL == in_command) {
        av_log(NULL, AV_LOG_ERROR, "NULL==in_command");
        return -1;
    }
    if (!hasRegistered) {
        avformat_network_init();
        hasRegistered = true;
    }
    char *command = pre_hande_cmd(in_command);
    av_log(NULL, AV_LOG_INFO, "after pre_hande_cmd=%s", command);
    if (!cmdLockHasInit) {
        pthread_mutex_init(&cmdLock, NULL);//初始化
        cmdLockHasInit = 1;
    }
    pthread_mutex_lock(&cmdLock);

    char *pCommand = (char *) command;
    int stingLen = (int) (strlen(command) + 1);
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
//    for (int i = 0; i < index; ++i) {
//        av_log(NULL, AV_LOG_DEBUG, "cmd=%s", argv[i]);
//    }
    //手动告诉它结束了,防止出现意外
    argv[index] = 0;
    int ret = exe_ffmpeg_cmd(index, argv, handle, progressCallBack, totalTime);
    for (int i = 0; i < index; ++i) {
        free(argv[i]);
    }
    free(command);
    pthread_mutex_unlock(&cmdLock);
    return ret;
}

int executeFFmpegCommand(int64_t handle, const char *command,
                         void (*progressCallBack)(int64_t, int, float)) {
    return executeFFmpegCommand4TotalTime(handle, command, progressCallBack, -1);
}

int cancelExecuteFFmpegCommand() {
    return cancel_exe_ffmpeg_cmd();
}

