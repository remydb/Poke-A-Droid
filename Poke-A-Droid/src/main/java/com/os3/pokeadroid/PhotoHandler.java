package com.os3.pokeadroid;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;

public class PhotoHandler implements PictureCallback {

    private final Context context;
    private final String fileName;
    private final Boolean doCompare;

    public PhotoHandler(Context context, String fileName, Boolean doCompare) {
        this.context = context;
        this.fileName = fileName;
        this.doCompare = doCompare;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        camera.startPreview();
        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(MainActivity.DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date());*/
        String photoFile = fileName + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            if (!doCompare) {
            Toast.makeText(context, "New Image saved:" + photoFile,
                    Toast.LENGTH_LONG).show();
            }
        } catch (Exception error) {
            Log.d(MainActivity.DEBUG_TAG, "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
        if (doCompare){
            PhotoCompare(filename, "creg");
            PhotoCompare(filename, "cpopup");
            PhotoCompare(filename, "cdim");
        }
        /*
        Above is testing code, below actual code for end product:
        if (doCompare){
            if (PhotoCompare(filename, "creg") == 1) {
                return;
            }
            if (PhotoCompare(filename, "cpopup") == 1) {
                return;
            }
            if (PhotoCompare(filename, "cdim") == 1) {
                return;
            }
            else {
                String resultfile = pictureFileDir.getPath() + File.separator + code.txt;
                BufferedWriter out = new BufferedWriter(new FileWriter(resultfile));
                out.write("The code is within %d and %d", codeCount -5, codecount);
                out.close();
            }
        }

         */
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "PokeADroid");
    }

    private void PhotoCompare(String filename2, String part) {

        File pictureFileDir = getDir();
        String filename1 = pictureFileDir.getPath() + File.separator + part + ".jpg";
        //String filename2 = pictureFileDir.getPath() + File.separator + part + ".jpg";
        //Load images to compare
        Mat img1 = Highgui.imread(filename1, Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        Mat img2 = Highgui.imread(filename2, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();

        //Definition of ORB keypoint detector and descriptor extractors
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

        //Detect keypoints
        detector.detect(img1, keypoints1);
        detector.detect(img2, keypoints2);
        //Extract descriptors
        extractor.compute(img1, keypoints1, descriptors1);
        extractor.compute(img2, keypoints2, descriptors2);

        //Definition of descriptor matcher
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        //Match points of two images
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptors1,descriptors2 ,matches);
        //DMatch adm[] = matches.toArray();
        List<DMatch> matchesList = matches.toList();
        double minDistance = 0;
        double maxDistance = 1000;

        int rowCount = matchesList.size();
        for (int i = 0; i < rowCount; i++) {
            double dist = matchesList.get(i).distance;
            if (dist < maxDistance) maxDistance = dist;
            if (dist > minDistance) minDistance = dist;
        }

        List<DMatch> nonMatchesList = new ArrayList<DMatch>();
        double upperBound = 6 * maxDistance;
        for (int i = 0; i < rowCount; i++) {
            if (matchesList.get(i).distance < upperBound) {
                nonMatchesList.add(matchesList.get(i));
            }
        }
        Toast.makeText(context, "Non-Matching KeyPoints: " + nonMatchesList.size() + " of " + matchesList.size(),Toast.LENGTH_LONG).show();
        //System.out.println("Matches: " + matches.size() + " of " + descriptors1.size());
    }
}