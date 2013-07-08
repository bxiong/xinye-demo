package com.xinye.framework.demo.media.camera;


import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.xinye.framework.demo.R;
import com.xinye.framework.media.camera.CameraPreview;
import com.xinye.framework.media.camera.ManageCameraActivity;

import java.util.List;

/**
 * 简单的Camera Preview示例，定制Preview大小
 */
public class PicturePreview03Activity extends ManageCameraActivity
        implements CameraPreview.PreviewSizeChangedCallback {

    private Button mButtonFlash, mButtonFocus, mButtonSwitch,
            mButtonWhiteBalance, mButtonZoom;

    private CameraPreview mPreview;

    private int mnFlashMode, mnFocusMode, mnWhiteBalanceMode;
    private List<String> mlszFocusModes, mlszFlashModes, mlszWhiteBalanceModes;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Camera.Parameters cameraParameters = mCamera.getParameters();

            switch (v.getId()) {
                case R.id.changeCamera:
                    mCamera.stopPreview();
                    advanceCamera();
                    switchCameraUI();
                    // mCamera.startPreview();
                    // reset camera parameters
                    cameraParameters = mCamera.getParameters();
                    break;
                case R.id.changeFlash:
                    mnFlashMode = (mnFlashMode + 1) % mlszFlashModes.size();
                    cameraParameters.setFlashMode(mlszFlashModes.get(mnFlashMode));
                    break;
                case R.id.changeFocus:
                    mnFocusMode = (mnFocusMode + 1) % mlszFocusModes.size();
                    cameraParameters.setFocusMode(mlszFocusModes.get(mnFocusMode));
                    break;
                case R.id.changeWhite:
                    mnWhiteBalanceMode = (mnWhiteBalanceMode + 1)
                            % mlszWhiteBalanceModes.size();
                    cameraParameters.setWhiteBalance(mlszWhiteBalanceModes
                            .get(mnWhiteBalanceMode));
                    break;
                case R.id.changeZoom:
                    cameraParameters.setZoom((cameraParameters.getZoom() + 1) % (cameraParameters.getMaxZoom() + 1));
                    break;
                default:

            }

            // stop camera preview because changing some parameters caused a
            // RuntimeException if it is running
            mCamera.stopPreview();
            try {
                mCamera.setParameters(cameraParameters);
            } catch (RuntimeException rx) {
                String szError = getApplicationContext().getString(R.string.set_parameters_failed) +
                        rx.toString();
                Toast t = Toast.makeText(getApplicationContext(), szError, Toast.LENGTH_SHORT);
                t.show();
                // the camera parameter change failed. Reset current value
                cameraParameters = mCamera.getParameters();
            }
            mCamera.startPreview();
            setCameraLabels(cameraParameters);
        }
    };

    /**
     * switchCamera initializes the UI labels for the currently selected camera
     */
    private void switchCameraUI() {
        mPreview.switchCamera(mCamera);
        mButtonSwitch
                .setText(getText(R.string.camera) + " " + mDefaultCameraId);
        Camera.Parameters cameraParameters = mCamera.getParameters();
        mlszFlashModes = cameraParameters.getSupportedFlashModes();
        mlszFocusModes = cameraParameters.getSupportedFocusModes();
        mlszWhiteBalanceModes = cameraParameters.getSupportedWhiteBalance();
        mButtonFlash.setEnabled(mlszFlashModes != null
                && mlszFlashModes.size() > 0);
        mButtonZoom.setEnabled(cameraParameters.isZoomSupported()
                && cameraParameters.getMaxZoom() > 0);
        mnFlashMode = 0;
        mnFocusMode = 0;
        mnWhiteBalanceMode = 0;
        setCameraLabels(cameraParameters);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (mNumberOfCameras == 0) {
            // nothing can be done; tell the user then exit
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.no_cameras, Toast.LENGTH_LONG);
            toast.show();
            finish();
        }


        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        setContentView(R.layout.media_picture_capture_03);

        mButtonSwitch = (Button) findViewById(R.id.changeCamera);
        mButtonSwitch.setOnClickListener(mOnClickListener);
        mButtonFocus = (Button) findViewById(R.id.changeFocus);
        mButtonFocus.setOnClickListener(mOnClickListener);
        mButtonWhiteBalance = (Button) findViewById(R.id.changeWhite);
        mButtonWhiteBalance.setOnClickListener(mOnClickListener);
        mButtonZoom = (Button) findViewById(R.id.changeZoom);
        mButtonZoom.setOnClickListener(mOnClickListener);
        mButtonFlash = (Button) findViewById(R.id.changeFlash);
        mButtonFlash.setOnClickListener(mOnClickListener);

        mPreview = (CameraPreview) findViewById(R.id.cameraPreview);
        mPreview.setPreviewSizeChangedCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        switchCameraUI();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // clear current camera from UI
        mPreview.setCamera(null);
    }

    /**
     * set the button labels based on the current camera parameters
     *
     * @param cameraParameters : the curren camera parameters object
     */
    void setCameraLabels(Camera.Parameters cameraParameters) {
        if (mlszFlashModes != null) {
            mButtonFlash.setText(getText(R.string.flash) + " "
                    + cameraParameters.getFlashMode());
        } else {
            mButtonFlash.setText(getText(R.string.flash));
            mButtonZoom.setEnabled(false);
        }
        if (mlszFocusModes != null) {
            mButtonFocus.setText(getText(R.string.focus) + " "
                    + cameraParameters.getFocusMode());
        } else {
            mButtonFocus.setText(getText(R.string.focus));
            mButtonFocus.setEnabled(false);
        }
        if (mlszWhiteBalanceModes != null) {
            mButtonWhiteBalance.setText(getText(R.string.whiteBalance) + " "
                    + cameraParameters.getWhiteBalance());
        } else {
            mButtonWhiteBalance.setText(getText(R.string.whiteBalance));
            mButtonWhiteBalance.setEnabled(false);
        }
        mButtonZoom.setText(getText(R.string.zoom) + " "
                + cameraParameters.getZoom());

    }

    @Override
    public void previewSizeChanged() {
        Camera.Parameters cameraParameters = mCamera.getParameters();
        mButtonZoom.setEnabled(cameraParameters.isZoomSupported()
                && cameraParameters.getMaxZoom() > 0);
    }
}

// ----------------------------------------------------------------------


