package com.os3.pokeadroid;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
//import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
//import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.widget.FrameLayout;
//import com.os3.pokeadroid.R;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import org.opencv.core.Core;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    final static String DEBUG_TAG = "MainActivity";
    final Context context = this;
    private Timer myTimer = new Timer();
    private Camera camera;
    public TextView currenttext;
    //Handler handler=new Handler();
    //private Dialog bruteprog;
    private int loopCount = 0;
    private int codeCount = 0;
    //private Button button;
    //private int cameraId = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(DEBUG_TAG, "OpenCV loaded successfully");

                    /* Now enable camera view to start receiving frames
                    mOpenCvCameraView.setOnTouchListener(Puzzle15Activity.this);
                    mOpenCvCameraView.enableView();*/
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ///////////////////////////////////////
        // Segment for bruteforce menu/progress
        ///////////////////////////////////////

        Button brutebutton = (Button) findViewById(R.id.bruteForce);

        brutebutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // custom brutemenu
                final Dialog brutemenu = new Dialog(context);
                brutemenu.setContentView(R.layout.popup);
                brutemenu.setTitle("Bruteforce");

                Button dialogButton = (Button) brutemenu.findViewById(R.id.brutemenuButtonOK);
                // if button is clicked, close the custom brutemenu
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        brutemenu.setContentView(R.layout.bruteprog);
                        brutemenu.setTitle("Progress");
                        currenttext = (TextView) brutemenu.findViewById(R.id.progText);
                        photoLoop();
                        Button progButton = (Button) brutemenu.findViewById(R.id.bruteprogButtonCancel);
                        progButton.setOnClickListener(new OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                myTimer.cancel();
                                brutemenu.dismiss();
                            }
                        });
                    }
                });

                Button cregButton = (Button) brutemenu.findViewById(R.id.creg);
                // if button is clicked, close the custom brutemenu
                cregButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture("creg", false);
                    }
                });

                Button cpopupButton = (Button) brutemenu.findViewById(R.id.cpopup);
                // if button is clicked, close the custom brutemenu
                cpopupButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture("cpopup", false);
                    }
                });

                Button cdimButton = (Button) brutemenu.findViewById(R.id.cdimmed);
                // if button is clicked, close the custom brutemenu
                cdimButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture("cdim", false);
                    }
                });

                brutemenu.show();
            }



        });

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
        } else {
            int cameraId = findBackFacingCamera();
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

    private Handler viewupdateHandler = new Handler() {

        public void handleMessage(Message msg) {
            String progText = String.format("Current codes: %d - %d", codeCount, codeCount + 5);
            currenttext.setText(progText);
        }
    };

    public void takePicture(String fileName, Boolean doCompare) {
        camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                new PhotoHandler(getApplicationContext(), fileName, doCompare));
    }

    public void photoLoop() {
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                viewupdateHandler.sendEmptyMessage(0);
                loopCount++;
                takePicture("testpic", true);
                if (loopCount == 6){
                    loopCount = 0;
                    codeCount = codeCount + 5;
                }
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
        myTimer.cancel();
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        myTimer.cancel();
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }


}