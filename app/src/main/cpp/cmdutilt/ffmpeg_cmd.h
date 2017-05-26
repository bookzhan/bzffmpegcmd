//
// Created by luoye on 17/5/25.
//

#ifndef FFMPEGCMD_FFMPEG_CMD_H
#define FFMPEGCMD_FFMPEG_CMD_H

//小于0失败,>0成功
int executeFFmpegCommand(const char *,void (*progressCallBack)(int, int, float));

#endif //FFMPEGCMD_FFMPEG_CMD_H
