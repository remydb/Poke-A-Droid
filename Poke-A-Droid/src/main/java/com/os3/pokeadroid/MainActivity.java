package com.os3.pokeadroid;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.SurfaceView;
import android.widget.Toast;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.widget.FrameLayout;
import com.os3.pokeadroid.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    final static String DEBUG_TAG = "MainActivity";
    private Camera camera;
    private int cameraId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // do we have a camera?
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
        } else {
            cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No back facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                CamPreview camPreview = new CamPreview(getApplicationContext(),camera);
                camPreview.setSurfaceTextureListener(camPreview);

                // Connect the preview object to a FrameLayout in your UI
                // You'll have to create a FrameLayout object in your UI to place this preview in
                FrameLayout preview = (FrameLayout) findViewById(R.id.cameraView);
                preview.addView(camPreview);

                // Attach a callback for preview
                CamCallback camCallback = new CamCallback();
                camera.setPreviewCallback(camCallback);

            }
        }
    }

   /* public void onClick(View view) {
        camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                new PhotoHandler(getApplicationContext()));
    }*/

    public void calibrate() {
        /* Calibrate the camera for different screens

         */
    }

    public void photoLoop(View view) {
        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                        new PhotoHandler(getApplicationContext()));
                CamCallback camCallback = new CamCallback();
                camera.setPreviewCallback(camCallback);
            }
        }, 0, 5000);

    }

    ShutterCallback myShutterCallback = new ShutterCallback(){

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }};

    PictureCallback myPictureCallback_RAW = new PictureCallback(){

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }};

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    protected void onPause() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

}