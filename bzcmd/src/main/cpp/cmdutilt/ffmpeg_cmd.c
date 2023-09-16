#include <string.h>
#include <stdbool.h>
#include "ffmpeg.h"

//保证同时只能一个线程执行
static pthread_mutex_t cmdLock;
static int cmdLockHasInit = 0;
static const int CMD_BUFFER_SIZE = 2048;

bool hasRegistered = false;


char *append_command(const char *src_cmd, const char *add_cmd) {
    char *buffer = malloc(CMD_BUFFER_SIZE);
    memset(buffer, 0, CMD_BUFFER_SIZE);
    const char *last_space = strrchr(src_cmd, ' ');
    strncpy(buffer, src_cmd, last_space - src_cmd + 1);  // 复制最后一个空格之前的部分
    strcpy(buffer + (last_space - src_cmd + 1), add_cmd);  // 添加选项和之后的部分
    strcat(buffer, last_space + 1);  // 添加空格之后的内容
    return buffer;
}

char *pre_hande_cmd(const char *src_cmd) {
    char *final_cmd = malloc(CMD_BUFFER_SIZE);
    memset(final_cmd, 0, CMD_BUFFER_SIZE);
    strcpy(final_cmd, src_cmd);
    //如果保存的是mp4文件,并且没有设置编码的情况下才默认处理
    if (strstr(final_cmd, "-c:v") == NULL && strstr(final_cmd, "-vn") == NULL
        && (strstr(final_cmd, ".mp4") != NULL || strstr(final_cmd, ".MP4") != NULL)) {
        char *buffer = append_command(final_cmd, "-c:v libx264 ");
        memset(final_cmd, 0, CMD_BUFFER_SIZE);
        strcpy(final_cmd, buffer);
        free(buffer);
    }
    if (strstr(final_cmd, "libx264") != NULL && strstr(final_cmd, "-preset") == NULL) {
        char *buffer = append_command(final_cmd, "-preset ultrafast ");
        memset(final_cmd, 0, CMD_BUFFER_SIZE);
        strcpy(final_cmd, buffer);
        free(buffer);
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

