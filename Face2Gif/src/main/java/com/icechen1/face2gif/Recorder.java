package com.icechen1.face2gif;

import android.graphics.*;
import android.hardware.Camera;

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
    private final int orientation;
    private boolean YUY2_isSupported = false;
    private boolean isRecording = false;
    private Camera mCamera;
    private String TAG = "Face2Gif";
    private ArrayList<Bitmap> picData;
    private int delay;
    private YuvImage yuvImage;
    private Bitmap image;

    public Recorder (Camera c, int fps, int orientation){
        mCamera = c;
        picData = new ArrayList<Bitmap>();
        delay = 1000/fps;
        width = getSize().width;
        height = getSize().height;
        rect = new Rect(0, 0, width, height);
        out = new ByteArrayOutputStream();
        this.orientation = orientation;

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
        for (int f:c.getParameters().getSupportedPreviewFormats()){
            if (f == ImageFormat.YUY2) {
                YUY2_isSupported = true;
            }
        }
    }

    public Camera.Size getSize(){
        //Log.d(TAG, "Preview Size - w: " + mCamera.getParameters().getPreviewSize().width + ", h: " + mCamera.getParameters().getPreviewSize().height);

        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(){
       // mCamera.autoFocus(null);
        mCamera.setPreviewCallbackWithBuffer(this);
    }

    public void start(){
        if(!isRecording){
            takePicture();
            isRecording = true;
        }
    }

    public void stop(){
        isRecording = false;
        mCamera.setPreviewCallbackWithBuffer(null);
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
            int rotation = (
                    MainActivity.display_angle
                            + orientation
                            + MainActivity.layout_angle
            ) % 360;
            //Check image data format
            if(YUY2_isSupported){
                // Compress the incoming frame (must be in the YUV2 Format!)
                // From stackoverflow.com/questions/9192982/displaying-yuv-image-in-android
                // This should probably be moved on its own thread, but I have not seen significant performance problems when
                // run on the UI thread

                yuvImage = new YuvImage(bytes, ImageFormat.YUY2, width, height, null);
                yuvImage.compressToJpeg(rect, 100, out); //TODO quality
                //Stream to a byte array
                byte[] imageBytes = out.toByteArray();
                out.reset();
                //Rotate it to the right orientation + Resize it to be half the original size
                if (rotation != 0) {
                    image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,options);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-rotation);
                    //Log.d("Face2Gif", width + " " + height + " " + image.getWidth() + image.getHeight());
                    image = Bitmap.createBitmap(image, 0, 0, width/2,
                            height/2, matrix, true);
                    //Flip height / width if portrait
                    image = Bitmap.createScaledBitmap(image, image.getWidth(),image.getHeight(), true);

                } else
                    image = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length,options), width/2,
                            height / 2, true);
            }else{
                //YUV_NV21 format
                int[] imageInts = new int[width*height];
                YUV_NV21_TO_RGB(imageInts,bytes,width,height);
                image = Bitmap.createBitmap(imageInts,width,height,Bitmap.Config.RGB_565);
                //Rotate it to the right orientation + Resize it to be half the original size
                if (rotation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(-rotation);
                    //Rotate if needed
                    image = Bitmap.createBitmap(image, 0, 0, width,
                            height, matrix, true);


                    image = Bitmap.createScaledBitmap(image, image.getWidth()/2,
                            image.getHeight() / 2, true);

                } else
                image = Bitmap.createScaledBitmap(image, width/2,
                        height / 2, true);
            }
            picData.add(image);
        }
        i++;

     }

    public static void YUV_NV21_TO_RGB(int[] argb, byte[] yuv, int width, int height) {
        final int frameSize = width * height;

        final int ii = 0;
        final int ij = 0;
        final int di = +1;
        final int dj = +1;

        int a = 0;
        for (int i = 0, ci = ii; i < height; ++i, ci += di) {
            for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
                int y = (0xff & ((int) yuv[ci * width + cj]));
                int v = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 0]));
                int u = (0xff & ((int) yuv[frameSize + (ci >> 1) * width + (cj & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
                int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                argb[a++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
    }
}
