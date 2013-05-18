package com.os3.pokeadroid;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    final static String DEBUG_TAG = "MainActivity";
    final Context context = this;
    private Button button;
    private Camera camera;
    private int cameraId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.bruteForce);

        // add button listener
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup);
                dialog.setTitle("Bruteforce");

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        photoLoop();
                    }
                });

                Button cregButton = (Button) dialog.findViewById(R.id.creg);
                // if button is clicked, close the custom dialog
                cregButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calib_reg();
                    }
                });

                Button cpopupButton = (Button) dialog.findViewById(R.id.cpopup);
                // if button is clicked, close the custom dialog
                cpopupButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calib_popup();
                    }
                });

                Button cdimButton = (Button) dialog.findViewById(R.id.cdimmed);
                // if button is clicked, close the custom dialog
                cdimButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calib_dim();
                    }
                });

                dialog.show();
            }
        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
        } else {
            cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No back facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                camera = Camera.open(cameraId);
                Camera.Parameters parameters = camera.getParameters();
                parameters.set("orientation", "portrait");
                parameters.setRotation(90);
                camera.setParameters(parameters);
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

    public void calib_reg() {
        String filePartName = "creg";
        camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                new PhotoHandler(getApplicationContext(), filePartName));
    }

    public void calib_popup() {
        String filePartName = "cpop";
        camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                new PhotoHandler(getApplicationContext(), filePartName));
    }

    public void calib_dim() {
        String filePartName = "cdim";
        camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                new PhotoHandler(getApplicationContext(), filePartName));
    }

    public void photoLoop() {
        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //if (PicID == 0){
                String filePartName = "testpic";
                camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                        new PhotoHandler(getApplicationContext(), filePartName));
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