package com.luoye.bzffmpegcmd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZPermissionUtil;
import com.luoye.bzmedia.utils.FFmpegCMDUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bz_MainActivity";
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_info = findViewById(R.id.tv_info);
        FFmpegCMDUtil.showLog(BuildConfig.DEBUG);
        BZPermissionUtil.requestCommonTestPermission(this);
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                String cmd = "ffmpeg -y -i /sdcard/bzmedia/testvideo.mp4 -b:v 806262 /sdcard/bzmedia/out_" + System.nanoTime() + ".mp4";
                //变速之后回调视频时长变了,进度就不准确了,需要传一个总时间去纠正进度
//                String cmd = "ffmpeg -y -i /storage/emulated/0/bzmedia/input_1.m4a -af atempo=10 /storage/emulated/0/bzmedia/audio_1617190120996.m4a";

//                String cmd = "ffmpeg -y -i /sdcard/bzmedia/testvideo2.mp4 -i /sdcard/bzmedia/testvideo3.mp4 -filter_complex amix=inputs=2 -vn /storage/emulated/0/bzmedia/mix_" + System.nanoTime() + ".m4a";
//
//                String cmd = "ffmpeg -y -i %s -i %s -c:v copy -filter_complex [0:a]aformat=fltp:44100:stereo,volume=%.2f,apad[0a];[1]aformat=fltp:44100:stereo,volume=%.2f[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 %s";
//                cmd = String.format(cmd, "/sdcard/Filter/temp_6.mp4", "/sdcard/Filter/input_bg.mp3", 0.5f, 1.0f, "/sdcard/Filter/output.mp4");
//                cmd = "ffmpeg -y -i /sdcard/Filter/temp_6.mp4 -i /sdcard/Filter/input_bg.aac -filter_complex [0:a]aformat=fltp:44100:stereo,volume=0.5,apad[0a];[1]aformat=fltp:44100:stereo,volume=1[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 /sdcard/Filter/output.mp4";
//                String cmd = "ffmpeg -y -ss 13.658 -t 13.418 -i /sdcard/bzmedia/VID_3093.mp4 -acodec copy -vcodec copy /sdcard/bzmedia/test.mp4";
//                String cmd = "ffmpeg -y -ss 0 -t 10.123 -i /sdcard/bzmedia/out_na.mp4 -vn -acodec copy /sdcard/bzmedia/out_test.m4a";
//                String cmd = "ffmpeg -y -ss 0 -t 10.030 -i \"/storage/emulated/0/bzmedia/test.mp3\" -vn -acodec copy /storage/emulated/0/bzmedia/alignment_music_1559539530386715.mp3";

                Log.d(TAG, "cmd=" + cmd);
                int ret = FFmpegCMDUtil.executeFFmpegCommand(cmd, new FFmpegCMDUtil.OnActionListener() {
                    @Override
                    public void progress(final float progress) {
                        Log.d(TAG, "executeFFmpegCommand progress=" + progress);
                        tv_info.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_info.setText("progress=" + progress);
                            }
                        });
                    }

                    @Override
                    public void fail() {
                        Log.e(TAG, "executeFFmpegCommand fail");
                    }

                    @Override
                    public void success() {
                        Log.d(TAG, "executeFFmpegCommand success");
                    }
                });
                Log.d(TAG, "ret=" + ret + "-----time cost=" + (System.currentTimeMillis() - startTime));
            }
        }).start();
    }

    public void cancel(View view) {
        FFmpegCMDUtil.cancelExecuteFFmpegCommand();
    }
}
