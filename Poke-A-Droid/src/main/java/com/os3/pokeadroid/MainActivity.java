package com.os3.pokeadroid;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.app.Dialog;
import android.view.View.OnClickListener;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.*;
import java.lang.Process;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    final static String DEBUG_TAG = "MainActivity";
    final Context context = this;
    private Timer myTimer = new Timer();
    private int timerRuns = 0;
    private Camera camera;
    public TextView currenttext;
    private TextView shellout;
    private int loopCount = 0;
    private int codeCount = 0;
    private int hasDevOpt = 0;
    private int hasRoot = 0;
    private String scriptdir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.os3.pokeadroid/files/";
    private Button injectapkbutton;
    private Button checkrootbutton;
    private Button removelockbutton;
    private Button getgesturebutton;
    protected Button okButton;
    protected ProgressBar shellprogress;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(DEBUG_TAG, "OpenCV loaded successfully");

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

        installScripts();

        ////////////////////////////////////
        //Configure check dev options button
        ////////////////////////////////////
        Button checkdevbutton = (Button) findViewById(R.id.devoptions);
        checkdevbutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog shellpopup = new Dialog(context);
                shellpopup.setContentView(R.layout.shell_popup);
                shellpopup.setTitle("Output");
                shellout = (TextView) shellpopup.findViewById(R.id.shellText);
                shellout.setText(" ");
                okButton = (Button) shellpopup.findViewById(R.id.shellButtonOk);
                okButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        shellpopup.dismiss();
                    }
                });
                shellprogress = (ProgressBar) shellpopup.findViewById(R.id.shellProgress);
                shellpopup.show();
                new checkDevAsync().execute();
            }
        });

        ////////////////////////////////////
        //Configure inject APK button
        ////////////////////////////////////
        injectapkbutton = (Button) findViewById(R.id.inject);
        injectapkbutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog shellpopup = new Dialog(context);
                shellpopup.setContentView(R.layout.shell_popup);
                shellpopup.setTitle("Output");
                shellout = (TextView) shellpopup.findViewById(R.id.shellText);
                shellout.setText(" ");
                okButton = (Button) shellpopup.findViewById(R.id.shellButtonOk);
                okButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        shellpopup.dismiss();
                    }
                });
                okButton.setVisibility(View.INVISIBLE);
                shellprogress = (ProgressBar) shellpopup.findViewById(R.id.shellProgress);
                shellpopup.show();
                new injectApkAsync().execute();
            }
        });

        ////////////////////////////////////
        //Configure check root button
        ////////////////////////////////////
        checkrootbutton = (Button) findViewById(R.id.root);
        checkrootbutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog shellpopup = new Dialog(context);
                shellpopup.setContentView(R.layout.shell_popup);
                shellpopup.setTitle("Output");
                shellout = (TextView) shellpopup.findViewById(R.id.shellText);
                shellout.setText(" ");
                okButton = (Button) shellpopup.findViewById(R.id.shellButtonOk);
                okButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        shellpopup.dismiss();
                    }
                });
                okButton.setVisibility(View.INVISIBLE);
                shellprogress = (ProgressBar) shellpopup.findViewById(R.id.shellProgress);
                shellpopup.show();
                new checkRootAsync().execute();
            }
        });

        //////////////////////////////////////////
        //Configure remove password/gesture button
        //////////////////////////////////////////
        removelockbutton = (Button) findViewById(R.id.remove);
        removelockbutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog shellpopup = new Dialog(context);
                shellpopup.setContentView(R.layout.shell_popup);
                shellpopup.setTitle("Output");
                shellout = (TextView) shellpopup.findViewById(R.id.shellText);
                shellout.setText(" ");
                okButton = (Button) shellpopup.findViewById(R.id.shellButtonOk);
                okButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        shellpopup.dismiss();
                    }
                });
                shellprogress = (ProgressBar) shellpopup.findViewById(R.id.shellProgress);
                shellpopup.show();
                new removeLockAsync().execute();
            }
        });

        ////////////////////////////////////
        //Configure get gesture button
        ////////////////////////////////////
        getgesturebutton = (Button) findViewById(R.id.retrieve);
        getgesturebutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final Dialog shellpopup = new Dialog(context);
                shellpopup.setContentView(R.layout.shell_popup);
                shellpopup.setTitle("Output");
                shellout = (TextView) shellpopup.findViewById(R.id.shellText);
                okButton = (Button) shellpopup.findViewById(R.id.shellButtonOk);
                okButton.setVisibility(View.INVISIBLE);
                okButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        shellpopup.dismiss();
                    }
                });
                shellprogress = (ProgressBar) shellpopup.findViewById(R.id.shellProgress);
                shellpopup.show();
                new getGestureAsync().execute();
            }
        });

        ///////////////////////////////////////
        // Segment for bruteforce menu/progress
        ///////////////////////////////////////

        Button brutebutton = (Button) findViewById(R.id.bruteForce);

        brutebutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (camera == null){
                    confCamera();
                }

                final Dialog brutemenu = new Dialog(context);
                brutemenu.setContentView(R.layout.popup);
                brutemenu.setTitle("Bruteforce");

                Button dialogButton = (Button) brutemenu.findViewById(R.id.brutemenuButtonOK);
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
                                if (timerRuns == 1){
                                    myTimer.cancel();
                                    myTimer = new Timer();
                                }
                                camera.release();
                                camera = null;
                                brutemenu.dismiss();
                            }
                        });
                    }
                });

                Button cregButton = (Button) brutemenu.findViewById(R.id.creg);
                cregButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture("creg", false);
                    }
                });

                Button cpopupButton = (Button) brutemenu.findViewById(R.id.cpopup);
                cpopupButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture("cpopup", false);
                    }
                });

                Button cdimButton = (Button) brutemenu.findViewById(R.id.cdimmed);
                cdimButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePicture("cdim", false);
                    }
                });

                brutemenu.show();
            }



        });
    }

    private class checkDevAsync extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            Runtime rt = Runtime.getRuntime();
            try{
                Process proc = rt.exec("/system/xbin/bash " + scriptdir + "testdebug.sh");
                InputStream is = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("ENABLED") || line.equals("RECOVERY") || line.equals("DISABLED")){
                        System.out.println(line);
                        return line;
                    }
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "fail";
        }

        protected void onPostExecute(String result) {
            shellprogress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            shellout.append("Developer options: " + result);
            if (result.equals("ENABLED")) {
                hasDevOpt = 1;
            }
            else if (result.equals("RECOVERY")) {
                hasDevOpt = 2;
            }
            else if (result.equals("DISABLED")) {
                hasDevOpt = 0;
            }
            setButtonStatus(0);
        }
    }

    private class injectApkAsync extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            Runtime rt = Runtime.getRuntime();
            try{
                Process proc = rt.exec("/system/xbin/bash " + scriptdir + "installAnti.sh");
                InputStream is = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("0")){
                        System.out.println(line);
                        return line;
                    }
                    return line;
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "fail";
        }

        protected void onPostExecute(String result) {
            shellprogress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            if (result.equals("0")) {
                shellout.append("Success!");
            }
            else {
                shellout.append("Failed");
            }
        }
    }

    private class checkRootAsync extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            Runtime rt = Runtime.getRuntime();
            try{
                Process proc = rt.exec("/system/xbin/bash " + scriptdir + "testroot.sh");
                InputStream is = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("0") || line.equals("1") || line.equals("2")){
                        System.out.println(line);
                        return line;
                    }
                    return line;
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "fail";
        }

        protected void onPostExecute(String result) {
            shellprogress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            if (result.equals("0")) {
                shellout.append("No root access");
                hasRoot=0;
            }
            else if (result.equals("1")) {
                shellout.append("Native root access");
                hasRoot=1;
            }
            else if (result.equals("2")) {
                shellout.append("Root access through privilege escalation");
                hasRoot=2;
            }
            else if (result.equals("3")){
                shellout.append("Unknown state");
                hasRoot=0;
            }
            setButtonStatus(1);
        }
    }

    private class removeLockAsync extends AsyncTask<Void, Void, String> {
        String scriptname;
        protected String doInBackground(Void... params) {
            if (hasRoot == 1){
                scriptname = "removelock.sh";
            }
            else if (hasRoot == 2){
                scriptname = "removelockesc.sh";
            }
            Runtime rt = Runtime.getRuntime();
            try{
                Process proc = rt.exec("/system/xbin/bash " + scriptdir + scriptname);
                InputStream is = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("0")){
                        System.out.println(line);
                        return line;
                    }
                    return line;
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "fail";
        }

        protected void onPostExecute(String result) {
            shellprogress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            if (result.equals("0")) {
                shellout.append("All key files removed");
            }
            else {
                shellout.append("Failed");
            }
        }
    }

    private class getGestureAsync extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {
            Runtime rt = Runtime.getRuntime();
            try{
                Process proc = rt.exec("/system/xbin/bash " + scriptdir + "getgesture.sh");
                InputStream is = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                        System.out.println(line);
                        return line;
                }

            }
            catch (Exception e){
                e.printStackTrace();
            }
            return "fail";
        }

        protected void onPostExecute(String result) {
            shellprogress.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
            if (result.equals("fail")) {
                shellout.append("Failed");
            }
            else if (result.equals("1")){
                shellout.append("Gesture table not found");
            }
            else {
                shellout.append("Gesture: " + result);
            }
        }
    }

    private void setButtonStatus(int type){
        if (type == 0){
            if (hasDevOpt == 1){
                injectapkbutton.setEnabled(true);
                checkrootbutton.setEnabled(true);
            }
            else if (hasDevOpt == 2){
                checkrootbutton.setEnabled(true);
            }
            else if (hasDevOpt == 0){
                injectapkbutton.setEnabled(false);
                checkrootbutton.setEnabled(false);
                removelockbutton.setEnabled(false);
                getgesturebutton.setEnabled(false);
            }
        }
        else if (type == 1){
            if (hasRoot == 1 || hasRoot == 2){
                removelockbutton.setEnabled(true);
                getgesturebutton.setEnabled(true);
            }
            else if (hasRoot == 0){
                removelockbutton.setEnabled(false);
                getgesturebutton.setEnabled(false);
            }
        }
    }

    private Handler viewupdateHandler = new Handler() {

        public void handleMessage(Message msg) {
            String progText = String.format("Current codes: %d - %d", codeCount, codeCount + 5);
            currenttext.setText(progText);
        }
    };

    private void installScripts(){
        File myFilesDir = new File(scriptdir);
        myFilesDir.mkdirs();
        try {
            AssetManager am = getAssets();
            String[] list = am.list("");
            for (String s:list) {
                if (s.endsWith("sh") || s.endsWith("apk") || s.endsWith("txt.gz")) {
                    Log.d(DEBUG_TAG, "Copying asset file " + s);
                    InputStream inStream = am.open(s);
                    int size = inStream.available();
                    byte[] buffer = new byte[size];
                    inStream.read(buffer);
                    inStream.close();
                    FileOutputStream fos = new FileOutputStream(myFilesDir + File.separator + s);
                    fos.write(buffer);
                    fos.close();
                }
            }
        }
        catch (Exception e) {
            // Better to handle specific exceptions such as IOException etc
            // as this is just a catch-all
            e.printStackTrace();
        }
    }

    private void confCamera(){
        //////////////////
        //Configure camera
        //////////////////
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG).show();
        } else {
            int cameraId = findBackFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No back facing camera found.",
                        Toast.LENGTH_LONG).show();
            } else {
                try {
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
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void takePicture(String fileName, Boolean doCompare) {
        camera.takePicture(myShutterCallback, myPictureCallback_RAW,
                new PhotoHandler(getApplicationContext(), fileName, doCompare, codeCount));
    }

    public void photoLoop() {
        timerRuns=1;
        loopCount=0;
        codeCount=0;
        myTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                viewupdateHandler.sendEmptyMessage(0);
                loopCount++;
                if (camera != null) {
                    takePicture("testpic", true);
                }
                if (loopCount == 2){
                    loopCount = 0;
                    codeCount = codeCount + 5;
                }
            }
        }, 0, 15000);
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
        if (timerRuns == 1) {
            myTimer.cancel();
            myTimer = new Timer();
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (timerRuns == 1) {
            myTimer.cancel();
            myTimer = new Timer();
        }
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