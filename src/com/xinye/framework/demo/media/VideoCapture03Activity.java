package com.xinye.framework.demo.media;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.xinye.framework.demo.R;

import java.io.IOException;

/**
 * 录制视频最简单的定制示例，适用于所有版本
 */
public class VideoCapture03Activity extends Activity
        implements OnClickListener, SurfaceHolder.Callback {

    MediaRecorder recorder;
    SurfaceHolder holder;

    boolean recording = false;
    public static final String TAG = "VIDEOCAPTURE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new MediaRecorder();
        initRecorder();
        //对于有录制视频的界面，屏幕的Orientation会自动指定为landscape
        setContentView(R.layout.media_video_capture_03);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);

        //重置view大小
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int viewHeight = display.getHeight();
        int viewWidth = (viewHeight * 640) / 480;
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(viewWidth, viewHeight);
        param.alignWithParent = true;
        param.addRule(RelativeLayout.CENTER_IN_PARENT);
        cameraView.setLayoutParams(param);

        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
    }

    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setAudioChannels(1);//音频声道,1为单身到，2为双声道
        recorder.setAudioEncodingBitRate(64000);//音频码率
        recorder.setAudioSamplingRate(44100);//音频采样率

        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//音频编码
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); //视频编码
        recorder.setVideoEncodingBitRate(400000);//视频码率
        recorder.setVideoSize(640, 480); //视频大小
        recorder.setVideoFrameRate(15);//视频帧率，很多手机并不按照指定的帧率，只是将此值作为最大帧率因此如果将此值设置过小，在某些机型上会有问题


        recorder.setOutputFile("/sdcard/videocapture_example.mp4");
        recorder.setMaxDuration(6000000); //视频的最大时间长度
        recorder.setMaxFileSize(500000000);//视频的最大文件大小
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        recorder.setOrientationHint(90);//视频播放时的纠偏90度，但是部分手机会自动纠偏

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {
        if (recording) {
            recorder.stop();
            recording = false;
            Log.v(TAG, "Recording Stopped");
            // Let's initRecorder so we can record again
            initRecorder();

        } else {
            recording = true;
            prepareRecorder();
            recorder.start();
            Log.v(TAG, "Recording Started");
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(TAG, "surfaceCreated");
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(TAG, "surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.release();
        finish();
    }
}
