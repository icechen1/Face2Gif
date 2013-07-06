package com.icechen1.face2gif;

import android.graphics.*;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Handler;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Icechen1 on 03/07/13.
 */
public class Recorder implements Camera.PreviewCallback{
    private final int width, height;
    private final Rect rect;
    private final ByteArrayOutputStream out;
    private final BitmapFactory.Options options;
    private boolean isRecording = false;
    private Camera mCamera;
    private String TAG = "Face2Gif";
    private ArrayList<Bitmap> picData;
    private int delay;
    private YuvImage yuvImage;
    private byte[] imageBytes;
    private Bitmap image;

    public Recorder (Camera c, int fps){
        mCamera = c;
        picData = new ArrayList<Bitmap>();
        delay = 1000/fps;
        width = getSize().width;
        height = getSize().height;
        rect = new Rect(0, 0, width, height);
        out = new ByteArrayOutputStream();

        //Some settings to optimize the bitmap conversion process
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        options.inDither = false; // Disable Dithering mode
        options.inPurgeable = true; // Tell to gc that whether it needs free
        // memory, the Bitmap can be cleared
        options.inInputShareable = true; // Which kind of reference will be
        // used to recover the Bitmap
        // data after being clear, when
        // it will be used in the future
        options.inTempStorage = new byte[32 * 1024];
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public Camera.Size getSize(){
        //Log.d(TAG, "Preview Size - w: " + mCamera.getParameters().getPreviewSize().width + ", h: " + mCamera.getParameters().getPreviewSize().height);

        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(){
       // mCamera.autoFocus(null);
        mCamera.setPreviewCallback(this);
    }

    public void start(){
        if(!isRecording){
            takePicture();
            isRecording = true;
        }
    }

    public void stop(){
        isRecording = false;
    }

    public ArrayList<Bitmap> retrieveData(){
        return picData;
    }


    //callback for picture
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        //Loop this with a delay...
        int i = 0;
        //Take a frame every time the delay is reached
        if (i % delay == 0 && isRecording){
           // picData.add(bytes);

            // Compress the incoming frame (must be in the YUV2 Format!)
            // From stackoverflow.com/questions/9192982/displaying-yuv-image-in-android
            // This should probably be moved on its own thread, but I have not seen significant performance problems when
            // run on the UI thread

            yuvImage = new YuvImage(bytes, ImageFormat.YUY2, width, height, null);
            yuvImage.compressToJpeg(rect, 100, out); //TODO quality
            //Stream to a byt array
            imageBytes = out.toByteArray();
            out.reset();

            //Rotate it to the right orientation + Resize it to be half the original size
            // others devices
            int orientation = MainActivity.camera_angle;

            if (orientation != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(-orientation);
                image = Bitmap.createBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,options), 0, 0, width/2,
                        height / 2, matrix, true);
            } else
                image = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,options), width/2,
                        height / 2, true);

            picData.add(image);
        }
        i++;

     }
}
