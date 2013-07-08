package com.icechen1.face2gif;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.icechen1.face2gif.effects.Caption;
import com.icechen1.face2gif.effects.Vignette;
import com.icechen1.face2gif.encoder.AnimatedGifEncoder;
import com.icechen1.face2gif.fragments.RenderFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class GifEncoderTask extends AsyncTask<ArrayList<Bitmap>, Integer, String> {

    private final RenderFragment frag;
    private final boolean repeat;
    private final int quality;
    private final String top,bottom;
    private int fps;
    private String TAG = "Face2Gif";
    private int height;
    private int width;
    private Activity cxt;
    private String outputFileName;
    private boolean vignette = true;

      //Vignette values
        int[] colors = new int[] { 0, 0, 0x7f000000 };
        float[] pos = new float[] { 0.0f, 0.7f, 1.0f };
    private boolean caption = true;


    public GifEncoderTask( Camera.Size s, RenderFragment parentFragment, String top, String bottom){
        height = s.height;
        width = s.width;
        cxt = parentFragment.getActivity();
        frag = parentFragment;

        //Getting preferences
        AppPreferenceManager pref = new AppPreferenceManager(cxt);
        vignette = pref.getVignette();
        caption = pref.getCaption();
        fps = pref.getFPS();
        repeat = pref.getRepeat();
        quality = pref.getQuality();

        this.top = top;
        this.bottom = bottom;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Face2Gif");
        directory.mkdirs();

        outputFileName = directory.getAbsolutePath() + "/Face2Gif_" + new Date().getTime() + ".gif";
        frag.showPath(outputFileName);
    }

    protected String doInBackground(ArrayList<Bitmap>... data) {

        if (!isExternalStorageWritable()){
            Log.d(TAG, "Cannot Write to external storage!");
            return null;
        }


        AnimatedGifEncoder e = new AnimatedGifEncoder();
        e.start(outputFileName);
        e.setDelay(1000 / fps);
        if(repeat) e.setRepeat(0); else e.setRepeat(1);
        e.setQuality(quality);
        Typeface font = Typeface.createFromAsset(frag.getActivity().getAssets(), "fonts/impact.ttf");

        //Use the first frame to set up the effects
        Vignette v = null;
        if(vignette){
            v = new Vignette(data[0].get(0));
        }
        Caption c = null;
        if(caption){
            c = new Caption(cxt,data[0].get(0));
            c.setTopText(top);
            c.setBottomText(bottom);
        }

        int i = 0;
        //Loop through all the frames
        for (Bitmap image: data[0]) {
            // Escape early if cancel() is called
            if (isCancelled()){
                //Deletes this file
                try{
                    new File(outputFileName).delete();
                }catch(Exception exp){
                    //I/O Error?
                }

                break;
            }
            /*
             * ADDING EFFCTS
             */
            if(vignette){
                v.setBitmap(image);
                v.draw();
            }

            if(caption){
                c.setBitmap(image);
                c.draw();
            }
            /*
             * ADDING FRAME TO THE GIF
             */
            e.addFrame(image);

            publishProgress((int) ((double)i/data[0].size() * 100));
            i++;
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
