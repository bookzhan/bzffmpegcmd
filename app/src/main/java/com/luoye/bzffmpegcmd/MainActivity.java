package com.luoye.bzffmpegcmd;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.luoye.bzmedia.FFmpegCMDUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("bzffmpeg");
        System.loadLibrary("bzffmpegcmd");
    }

    private static final String TAG = "bz_MainActivity";
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_info = (TextView) findViewById(R.id.tv_info);
        requestPermission();
    }

    private boolean requestPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !PermissionUtil.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!PermissionUtil.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        String[] permissionStrings = new String[permissionList.size()];
        permissionList.toArray(permissionStrings);

        if (permissionList.size() > 0) {
            PermissionUtil.requestPermission(this, permissionStrings, PermissionUtil.CODE_REQ_PERMISSION);
            return false;
        } else {
            Log.d(TAG, "Have all permissions");
            return true;
        }
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                String cmd = "ffmpeg -y -i /sdcard/bzmedia/VID_029.mp4 /sdcard/bzmedia/out_" + System.nanoTime() + ".mp4";

//                String cmd = "ffmpeg -y -i /sdcard/bzmedia/VID_231_r270.mp4 -i /sdcard/bzmedia/av_test_1080x1920_16_r0.mp4 -filter_complex amix=inputs=2 -vn /storage/emulated/0/bzmedia/mix_" + System.nanoTime() + ".m4a";
//
//                String mergeCmd = "ffmpeg -y -i %s -i %s -c:v copy -filter_complex [0:a]aformat=fltp:44100:stereo,volume=%.2f,apad[0a];[1]aformat=fltp:44100:stereo,volume=%.2f[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 %s";
//                mergeCmd = String.format(mergeCmd, "/sdcard/Filter/temp_6.mp4", "/sdcard/Filter/input_bg.mp3", 0.5f, 1.0f, "/sdcard/Filter/output.mp4");
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

}
