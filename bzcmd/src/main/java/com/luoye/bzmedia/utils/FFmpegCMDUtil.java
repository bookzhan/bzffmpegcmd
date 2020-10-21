package com.luoye.bzmedia.utils;

/**
 * Created by zhandalin on 2017-05-25 16:20.
 * 说明:
 */
public class FFmpegCMDUtil {
    static {
        System.loadLibrary("bzffmpeg");
        System.loadLibrary("bzffmpegcmd");
    }

    public static native int showLog(boolean showLog);

    /**
     * Adding a thread lock at the native to allow only single-threaded execution
     */
    public static native int executeFFmpegCommand(String command, OnActionListener onActionListener);

    /**
     * This method and the executeFFmpegCommand method must be called in separate threads.
     */
    public static native int cancelExecuteFFmpegCommand();

    public interface OnActionListener {
        void progress(float progress);

        void fail();

        void success();
    }
}
