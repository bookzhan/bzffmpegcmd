package com.luoye.bzffmpeg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.luoye.bzmedia.FFmpegUtil;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("ffmpegCmd");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
    }

    public void start(View view) {
        String cmd = "ffmpeg -y -i /sdcard/Filter/input.mp4 -vcodec copy -acodec copy /sdcard/Filter/output.mp4";

//        String mergeCmd = "ffmpeg -y -i %s -i %s -c:v copy -filter_complex [0:a]aformat=fltp:44100:stereo,volume=%.2f,apad[0a];[1]aformat=fltp:44100:stereo,volume=%.2f[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 %s";
//        mergeCmd = String.format(mergeCmd, "/sdcard/Filter/temp_6.mp4", "/sdcard/Filter/input_bg.mp3", 0.5f, 1.0f, "/sdcard/Filter/output.mp4");


//        cmd = "ffmpeg -y -i /sdcard/Filter/temp_6.mp4 -i /sdcard/Filter/input_bg.aac -filter_complex [0:a]aformat=fltp:44100:stereo,volume=0.5,apad[0a];[1]aformat=fltp:44100:stereo,volume=1[1a];[0a][1a]amerge[a] -map 0:v -map [a] -ac 2 /sdcard/Filter/output.mp4";
        long startTime = System.currentTimeMillis();
        int ret = FFmpegUtil.executeFFmpegCommand(cmd);
        Log.d("bookzhan", "ret=" + ret + "-----耗时=" + (System.currentTimeMillis() - startTime));
    }

}
