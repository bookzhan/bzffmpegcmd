package com.luoye.bzffmpeg;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.luoye.bzmedia.FFmpegUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("bzffmpeg");
        System.loadLibrary("bzffmpegcmd");
    }

    private static final String TAG = "bz_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        requestPermission();
    }

    private boolean requestPermission() {
        ArrayList<String> permissionList = new ArrayList<>();
        //内存卡权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && !PermissionUtil.isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!PermissionUtil.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!PermissionUtil.isPermissionGranted(this, Manifest.permission.CAMERA)) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (!PermissionUtil.isPermissionGranted(this, Manifest.permission.RECORD_AUDIO)) {
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        String[] permissionStrings = new String[permissionList.size()];
        permissionList.toArray(permissionStrings);

        if (permissionList.size() > 0) {
            PermissionUtil.requestPermission(this, permissionStrings, PermissionUtil.CODE_REQ_PERMISSION);
            return false;
        } else {
            Log.d(TAG, "所要的权限全都有了");
            return true;
        }
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                        String cmd = "ffmpeg -y -i /sdcard/bzmedia/temp_132.mp4 -i /sdcard/bzmedia/2岁男童差一秒被甩下电梯 地铁站务员飞奔救人-高清视频在线观看-资讯-爱奇艺.mp4 -map 0:v -vcodec copy -map 1:a -acodec copy /storage/emulated/0/bzmedia/video_replace_1543826101491872.mp4";
//                String cmd = "ffmpeg -y -i /sdcard/bzmedia/VID_231_r270.mp4 -i /sdcard/bzmedia/av_test_1080x1920_16_r0.mp4 -filter_complex amix=inputs=2 -vn /storage/emulated/0/bzmedia/mix_" + System.nanoTime() + ".m4a";

//        String mergeCmd = "ffmpeg -y -i %s -i %s -c:v copy -filter_complex [0:a]aformat=fltp:44100:stereo,volume=%.2f,apad[0a];[1]aformat=fltp:44100:stereo,volume=%.2f[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 %s";
//        mergeCmd = String.format(mergeCmd, "/sdcard/Filter/temp_6.mp4", "/sdcard/Filter/input_bg.mp3", 0.5f, 1.0f, "/sdcard/Filter/output.mp4");


//        cmd = "ffmpeg -y -i /sdcard/Filter/temp_6.mp4 -i /sdcard/Filter/input_bg.aac -filter_complex [0:a]aformat=fltp:44100:stereo,volume=0.5,apad[0a];[1]aformat=fltp:44100:stereo,volume=1[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 /sdcard/Filter/output.mp4";
//                String cmd = "ffmpeg -y -i /sdcard/bzmedia/VID_231_r270.mp4 /sdcard/bzmedia/out_" + System.nanoTime() + ".mp4";
//                String cmd = "ffmpeg -y -ss 13.658 -t 13.418 -i /sdcard/bzmedia/VID_3093.mp4 -acodec copy -vcodec copy /sdcard/bzmedia/test.mp4";
//                String cmd = "ffmpeg -y -ss 0 -t 10.123 -i /sdcard/bzmedia/out_na.mp4 -vn -acodec copy /sdcard/bzmedia/out_test.m4a";
                String cmd = "ffmpeg -y -i /sdcard/bzmedia/temp_16.mp4 /sdcard/bzmedia/out_test.mp4";
                long startTime = System.currentTimeMillis();
                int ret = FFmpegUtil.executeFFmpegCommand(cmd, true);
                Log.d(TAG, "ret=" + ret + "-----耗时=" + (System.currentTimeMillis() - startTime));
            }
        }).start();
    }

}
