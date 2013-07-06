package com.xinye.framework.demo.media;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import com.xinye.framework.demo.R;

import java.io.IOException;

/**
 * 录制视频最简单的使用示例，适用于2.2及以上版本
 */
public class VideoCapture04Activity extends Activity implements OnClickListener, SurfaceHolder.Callback {
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
        setContentView(R.layout.media_video_capture_04);

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

        CamcorderProfile profile = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // 对于android 3.0 以上的手机，可以根据需求以及手机自身的性能选择视频质量标准
            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            }
        } else {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        }

        profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);

        profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;

        profile.audioChannels = 1;
        profile.audioBitRate = 64000;
        profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
        profile.audioSampleRate = 44100;

        profile.videoCodec = MediaRecorder.VideoEncoder.H264;
        profile.videoBitRate = 400000;
        profile.videoFrameRate = 15;
        profile.videoFrameWidth = 640;
        profile.videoFrameHeight = 480;

        recorder.setProfile(profile);

        recorder.setOutputFile("/sdcard/videocapture_example.mp4");

    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());

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
