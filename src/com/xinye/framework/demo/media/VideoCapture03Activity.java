package com.xinye.framework.demo.media;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
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
        setContentView(R.layout.meida_video_capture_01);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(true);
        cameraView.setOnClickListener(this);
    }

    private void initRecorder() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        recorder.setAudioChannels(1);//TODO 音频声道,1为单身到，2为双声道
        recorder.setAudioEncodingBitRate(64000);//TODO 音频码率
        recorder.setAudioSamplingRate(44100);//TODO 音频采样率

        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// TODO 音频编码
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // TODO 视频编码
        recorder.setVideoEncodingBitRate(400000);//TODO 视频码率
        recorder.setVideoSize(640, 480); // TODO 视频大小
        recorder.setVideoFrameRate(10);// TODO 帧率


        recorder.setOutputFile("/sdcard/videocapture_example.mp4");
        recorder.setMaxDuration(6000000); // TODO 视频的最大时间长度
        recorder.setMaxFileSize(500000000);// TODO 视频的最大文件大小
    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        recorder.setOrientationHint(90);//TODO 视频播放时的纠偏角度

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
