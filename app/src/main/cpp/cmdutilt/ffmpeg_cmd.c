#include <string.h>
#include "ffmpeg.h"

int executeFFmpegCommand(int64_t handle, const char *command,
                         void (*progressCallBack)(int64_t, int, float)) {

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
    int ret = run(handle, index, argv, progressCallBack);
    for (int i = 0; i < index; ++i) {
        free(argv[i]);
    }
    return ret;
}

