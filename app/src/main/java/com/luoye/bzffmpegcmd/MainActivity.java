package com.luoye.bzffmpegcmd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bzcommon.utils.BZLogUtil;
import com.bzcommon.utils.BZPermissionUtil;
import com.luoye.bzmedia.utils.FFmpegCMDUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "bz_MainActivity";
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_info = findViewById(R.id.tv_info);
        FFmpegCMDUtil.showLog(true);
        BZPermissionUtil.requestCommonTestPermission(this);
    }

    public void start(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                String cmd = "ffmpeg -y -f lavfi -i anullsrc=channel_layout=stereo:sample_rate=44100 -t 131.233 /storage/emulated/0/bzmedia/audio_bzmedia_1.m4a";
//                String cmd = "ffmpeg -y -i /sdcard/bzmedia/testvideo.mp4 /sdcard/bzmedia/out_" + System.nanoTime() + ".mp4";
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
    private void runtimeCmdTest(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(getFilesDir().getAbsoluteFile() + "/ffmpeg");
                    boolean setExecutable = file.setExecutable(true);
                    BZLogUtil.d(TAG, "setExecutable=" + setExecutable);
                    Map<String, String> env = new HashMap<>(System.getenv());
                    env.put("LD_LIBRARY_PATH", "/data/user/0/com.luoye.bzffmpegcmd/files/");
//                    // 设置可执行程序及其参数
//                    String[] command = {"/storage/emulated/0/bzmedia/ffmpeg", "-y -f lavfi -i anullsrc=channel_layout=stereo:sample_rate=44100 -t 131.233 /storage/emulated/0/bzmedia/audio_test0123.m4a"};
//                    // 创建进程构建器并指定命令
//                    ProcessBuilder pb = new ProcessBuilder(command);
//                    // 启动进程
//                    Process process = pb.start();
//                    // 获取进程输出
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        BZLogUtil.d(TAG, line);
//                    }
//
//                    // 等待进程执行完毕
//                    int exitCode = process.waitFor();
                    String[] envp = new String[]{
                            "LD_LIBRARY_PATH=/data/user/0/com.luoye.bzffmpegcmd/files/"
                    };
                    String cmd = "./ffmpeg -y -f lavfi -i anullsrc=channel_layout=stereo:sample_rate=44100 -t 13.233 /storage/emulated/0/bzmedia/audio_20240506_0825_03860.m4a";
//                    String cmd = "./ffmpeg -y -i /sdcard/bzmedia/testvideo.mp4 /sdcard/bzmedia/out_" + System.nanoTime() + ".mp4";
                    Process process = Runtime.getRuntime().exec(cmd, envp, new File("/data/user/0/com.luoye.bzffmpegcmd/files/"));
                    InputStream inputStream = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 处理每行输出内容
                        BZLogUtil.d(TAG, line);
                    }

                    // 关闭输入流
                    inputStream.close();

                    // 等待进程执行完毕
                    int exitCode = process.waitFor();
                    BZLogUtil.d(TAG, "---end-- exitCode=" + exitCode);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }

            }
        }).start();
    }

    public void cancel(View view) {
        FFmpegCMDUtil.cancelExecuteFFmpegCommand();
    }
}
