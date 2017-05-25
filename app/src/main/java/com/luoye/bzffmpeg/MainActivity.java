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
        int ret = FFmpegUtil.executeFFmpegCommand("ffmpeg -y -i /sdcard/Filter/input.mp4 -vcodec copy -acodec copy /sdcard/Filter/output.mp4");
        Log.d("kkk", "ret=" + ret);
    }

}
