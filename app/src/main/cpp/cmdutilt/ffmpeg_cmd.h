//
// Created by luoye on 17/5/25.
//

#ifndef FFMPEGCMD_FFMPEG_CMD_H
#define FFMPEGCMD_FFMPEG_CMD_H

#include <pty.h>

//小于0失败,>0成功
int executeFFmpegCommand(int64_t handle, const char *, void (*progressCallBack)(int64_t, int, float));

#endif //FFMPEGCMD_FFMPEG_CMD_H
