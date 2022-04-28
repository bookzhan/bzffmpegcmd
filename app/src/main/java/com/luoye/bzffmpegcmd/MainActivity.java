package com.luoye.bzffmpegcmd;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.luoye.bzmedia.utils.FFmpegCMDUtil;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bz_MainActivity";
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_info = findViewById(R.id.tv_info);
        requestPermission();
        FFmpegCMDUtil.showLog(BuildConfig.DEBUG);
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
                String cmd = "ffmpeg -y -i /sdcard/bzmedia/testvideo.mp4 " + FilePathUtil.getAVideoPath(getBaseContext());
//                String cmd = "ffmpeg -y -i /storage/emulated/0/bzmedia/input_1.m4a -af atempo=10 " + FilePathUtil.getAAudioPath(getBaseContext());
//                String cmd = "ffmpeg -y -i /sdcard/bzmedia/testvideo.mp4 -i /sdcard/bzmedia/testvideo3.mp4 -filter_complex amix=inputs=2 -vn " + FilePathUtil.getAAudioPath(getBaseContext());
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
