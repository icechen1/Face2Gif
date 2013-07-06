package com.icechen1.face2gif;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.hardware.Camera;
import com.icechen1.face2gif.encoder.AnimatedGifEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class GifEncoderTask extends AsyncTask<ArrayList<Bitmap>, Integer, String> {

    private final ViewFragment frag;
    private int fps;
    private String TAG = "Face2Gif";
    private int height;
    private int width;
    private Context cxt;
    GifEncoderTask(int _fps, Camera.Size s,ViewFragment parentFragment){
        fps = _fps;
        height = s.height;
        width = s.width;
        cxt = parentFragment.getActivity();
        frag = parentFragment;

    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

    }

    protected String doInBackground(ArrayList<Bitmap>... data) {

        if (!isExternalStorageWritable()){
            Log.d(TAG, "Cannot Write to external storage!");
            return null;
        }

        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Face2Gif");
        directory.mkdirs();

        AnimatedGifEncoder e = new AnimatedGifEncoder();
        String outputFileName = directory.getAbsolutePath() + "/Face2Gif_" + new Date().getTime() + ".gif";

        e.start(outputFileName);
        e.setDelay(1000/fps);

        /* YuvImage yuvImage;
        ByteArrayOutputStream out = new ByteArrayOutputStream();;
        byte[] imageBytes;
        Bitmap image;
        Rect rect = new Rect(0, 0, width, height); */

        int i = 0;
        for (Bitmap image: data[0]) {
            /*
                From stackoverflow.com/questions/9192982/displaying-yuv-image-in-android


            yuvImage = new YuvImage(b, ImageFormat.YUY2, width, height, null);
            yuvImage.compressToJpeg(rect, 100, out);
            imageBytes = out.toByteArray();
            out.reset();
            image = BitmapcreateScaledBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length),width/2,height/2,true); picData.add(bytes); */

            //Another method, too slow
            //image = Bitmap.createBitmap(convertYUV420_NV21toRGB8888(b, width, height), width, height, Bitmap.Config.ARGB_8888);
            e.addFrame(image);

            publishProgress((int) ((double)i/data[0].size() * 100));
            i++;
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        e.finish();
        //Log.d(TAG, "Progress: " + outputFileName);
        return outputFileName;
    }

    protected void onProgressUpdate(Integer... progress) {
        //Print the progress
        Log.d(TAG, "Progress: " + progress[0].intValue());

        frag.updateProgress(progress[0].intValue());

    }

    protected void onPostExecute(String path) {
        frag.updateProgress(100);
        frag.doneEncoding(path);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
