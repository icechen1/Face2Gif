package com.icechen1.face2gif.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.icechen1.face2gif.AppPreferenceManager;
import com.icechen1.face2gif.MainActivity;
import com.icechen1.face2gif.R;
import com.icechen1.face2gif.Recorder;
import com.icechen1.face2gif.listener.CameraOrientationListener;
import com.icechen1.face2gif.ui.CameraPreview;

import java.text.DecimalFormat;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class PreviewFragment extends Fragment implements CameraOrientationListener.CameraOrientation{

    private static Camera mCamera;
    private SurfaceView mPreview;
    private Recorder rec;
    public boolean isRecording = false;
    private String TAG = "Face2Gif";
    private MediaActionSound mMediaActionSoundStart;
    private MediaActionSound mMediaActionSoundStop;
    RelativeLayout previewRelativeLayout;
    private TextView timer;
    private Integer minutes, seconds;
    private ImageView recordcircle;
    private Drawable recordSquareDrawable,recordCircleDrawable;
    private ImageButton toggleButton;
    private CameraOrientationListener orientationListener;
    private ImageButton optionsButton;
    private int prev_orientation;
    private EditText txtTop, txtBottom;
    private static boolean FRONT_CAM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        previewRelativeLayout = (RelativeLayout) view.findViewById(R.id.camera_preview);
        timer = (TextView) view.findViewById(R.id.timerView);
        recordcircle = (ImageView) view.findViewById(R.id.recordcircle);
        toggleButton = (ImageButton) view.findViewById(R.id.toggle);
        optionsButton = (ImageButton) view.findViewById(R.id.settings);
        recordcircle.setVisibility(View.INVISIBLE);
        //Load graphical assets
        recordSquareDrawable = getResources().getDrawable(R.drawable.shape_square);
        recordCircleDrawable = getResources().getDrawable(R.drawable.shape_circle_big);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMediaActionSoundStart = new MediaActionSound();
            mMediaActionSoundStop = new MediaActionSound();

            mMediaActionSoundStart.load(MediaActionSound.START_VIDEO_RECORDING);
            mMediaActionSoundStart.load(MediaActionSound.STOP_VIDEO_RECORDING);
        }

        orientationListener = new CameraOrientationListener(getActivity(),this);


        AppPreferenceManager pref = new AppPreferenceManager(getActivity());

        txtTop = (EditText) view.findViewById(R.id.editTextTop);
        txtBottom = (EditText) view.findViewById(R.id.editTextBottom);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/impact.ttf");

        txtTop.setTypeface(font);
        txtTop.setTextColor(Color.WHITE);
        txtTop.setShadowLayer(5, 0, 0, Color.BLACK);

        txtBottom.setTypeface(font);
        txtBottom.setTextColor(Color.WHITE);
        txtBottom.setShadowLayer(5,0,0,Color.BLACK);

        //Hide if needed
        if(!pref.getCaption()){
            txtTop.setVisibility(View.GONE);
            txtBottom.setVisibility(View.GONE);
        }
        //Do not show the hints again
        if(pref.getHintSeen()){
            txtTop.setHint("");
            txtBottom.setHint("");
        }else{
            pref.saveHintSeen();
        }
        ImageButton flipCamera = (ImageButton) view.findViewById(R.id.flipCamera);
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            // this device has 2 cameras

        } else {
            // <1 camera on this device
            flipCamera.setVisibility(View.GONE);
        }


        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
        orientationListener.disable();
        previewRelativeLayout.removeAllViewsInLayout();
    }

    public void cameraErrorDialog(String s){
        previewRelativeLayout.setBackgroundColor(Color.BLACK);
        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(s);
        builder.setPositiveButton("Open Gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                getActivity().finish();
            }
        });
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                getActivity().finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        orientationListener.enable();
        if(checkCameraHardware(getActivity())){

            // Create an instance of Camera
            mCamera = getCameraInstance(true);

            if(mCamera == null){
                //Get the back camera
                mCamera = getCameraInstance(false);
            }

            // Create our Preview view and set it as the content of our activity.
            if(mCamera == null){
                cameraErrorDialog("Cannot connect to the camera.");
            }else{
                mPreview = new CameraPreview(getActivity(), mCamera, CameraPreview.LayoutMode.FitToParent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mPreview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            previewRelativeLayout.addView(mPreview);
            }

        }else{
            //TODO error handling
            cameraErrorDialog("Cannot find camera.");
        }
    }
    
    public void flipCamera(View v){
        releaseCamera();              // release the old camera
        if(FRONT_CAM){
            //get the back cam
            mCamera = getCameraInstance(false);
        }else{
            mCamera = getCameraInstance(true);
        }
        previewRelativeLayout.removeAllViewsInLayout();
        mPreview = new CameraPreview(getActivity(), mCamera, CameraPreview.LayoutMode.FitToParent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPreview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
        previewRelativeLayout.addView(mPreview);
    }

    private void releaseCamera(){
        if(rec != null){
            rec.stop();
        }
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
        }

        mCamera = null;
    }

    public int getLayoutRotation(){
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees  = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        MainActivity.layout_angle = degrees;
        return degrees;
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(boolean frontCam){
        Camera cam = null;
        if(frontCam){
        try {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            Log.d("Face2Gif", "Camera count: " + cameraCount);
            for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
                Camera.getCameraInfo( camIdx, cameraInfo );
                if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    try {
                        cam = Camera.open( camIdx );
                        FRONT_CAM = true;
                        Log.d("Face2Gif", "Camera opened: " + camIdx);

                        //Get the display orientation
                        int displayOrientation = (cameraInfo.orientation + getLayoutRotation()) % 360;
                        displayOrientation = (360 - displayOrientation) % 360;

                        MainActivity.display_angle = displayOrientation;
                    } catch (RuntimeException e) {
                        //No front camera
                        Log.e("Face2Gif", "Camera failed to open: " + e.getLocalizedMessage() + "\n");
                        e.printStackTrace();

                        //Restart
                       // Intent intent = getActivity().getIntent();
                       // getActivity().finish();
                       // startActivity(intent);

                        //Back camera
                      //  int displayOrientation = (cameraInfo.orientation - getLayoutRotation() + 360) % 360;
                      //  MainActivity.display_angle = displayOrientation;
                    }
                }
            }
        }
        catch (Exception e){
            /* Camera is not available (in use or does not exist)
             * Show dialog
             */
            Log.e("Face2Gif", "Camera failed to open");
        }
        }else{
            try{
                cam = Camera.open();
                FRONT_CAM = false;
                Log.d("Face2Gif", "Back Camera opened");

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(1,cameraInfo);
                //Get the display orientation
                int displayOrientation = (cameraInfo.orientation - getLayoutRotation() + 360) % 360;
                MainActivity.display_angle = displayOrientation;
            }catch(Exception e){
                //todo error
                Log.e("Face2Gif", "Back Camera cannot be opened \n" + e.getMessage());
            }
        }
        return cam; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
        || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void startRecording(){
        //Load FPS
        AppPreferenceManager pref = new AppPreferenceManager(getActivity());
        //Start recording
        rec = new Recorder (mCamera,pref.getFPS() ,orientationListener.getOrientation());
        rec.start();
        //Update the button drawable
        toggleButton.setImageDrawable(recordSquareDrawable);
        //Play start record sound
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMediaActionSoundStart.play(MediaActionSound.START_VIDEO_RECORDING);
        }
        isRecording = true;

        //Start the timer
        startTimer();
        optionsButton.setEnabled(false);
    }

    public void stopRecording(){
        //Stop the recording
        isRecording = false;
        rec.stop();
        //Update the button drawable
        toggleButton.setImageDrawable(recordCircleDrawable);
        //Stop the timer from counting up
        stopTimer();
        optionsButton.setEnabled(true);
        //Play stop record sound if avail.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMediaActionSoundStop.play(MediaActionSound.STOP_VIDEO_RECORDING);
        }

        //Show the result fragment
        Fragment viewFragment = new RenderFragment(
                rec.retrieveData(),
                rec.getSize(),
                txtTop.getText().toString(),
                txtBottom.getText().toString());

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container,viewFragment,"frag_render")
                .addToBackStack(null)
                .commit();
    }

    public void cancelRecording() {
        //Stop the recording
        isRecording = false;
        rec.stop();
        //Update the button drawable
        toggleButton.setImageDrawable(recordCircleDrawable);
        //Stop the timer from counting up
        stopTimer();
        optionsButton.setEnabled(true);
        //Play stop record sound if avail.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMediaActionSoundStop.play(MediaActionSound.STOP_VIDEO_RECORDING);
        }
    }
    //Takes pictures
    public void toggle(View v){

        //Log.d(TAG, "Toggle button clicked");
        if(isRecording){
            stopRecording();
        }else{
            startRecording();
        }

    }
    public void startCountdown(){
        seconds = 3000;

    }

    public void startTimer(){
        resetTimer();
        countUp();

    }

    public void countUp(){
        if(isRecording){
            Handler handlerTimer = new Handler();
            handlerTimer.postDelayed(new Runnable() {
                public void run() {
                    if(seconds < 59){
                        seconds++;
                    }else{
                        minutes++;
                    }
                    DecimalFormat df = new DecimalFormat("#00.###");
                    df.setDecimalSeparatorAlwaysShown(false);

                    if(minutes<100){
                        timer.setText(df.format(minutes)+":"+df.format(seconds));
                    }
                    else{
                        timer.setText(minutes+":"+df.format(seconds));
                    }
                    if(recordcircle.getVisibility() == View.INVISIBLE){
                        recordcircle.setVisibility(View.VISIBLE);
                    } else{
                        recordcircle.setVisibility(View.INVISIBLE);
                    }
                    countUp();

                }
            }, 1000);
        }else{
            resetTimer();
        }
    }

    public void stopTimer(){
        resetTimer();
    }
    public void resetTimer(){
        timer.setText("00:00");
        minutes = 0;
        seconds = 0;
        recordcircle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onOrientationChanged(int orientation) {

/* TODO
        if (orientation != prev_orientation){

            float rot = prev_orientation - orientation;
            Log.d("Face2Gif", "Rotation needed: " + rot);

            Animation an = new RotateAnimation(0.0f, rot, 0, 0);
            an.setDuration(90);
            an.setRepeatCount(0);
            an.setFillAfter(true);               // keep rotation after animation
            txtTop.setAnimation(an);
        }


        prev_orientation = orientation; */
    }

    public void invalidateUIElements() {
        //So far only needed to show or hide the caption boxes
        AppPreferenceManager pref = new AppPreferenceManager(getActivity());

        if(!pref.getCaption()){
            txtTop.setVisibility(View.GONE);
            txtBottom.setVisibility(View.GONE);
        }else{
            txtTop.setVisibility(View.VISIBLE);
            txtBottom.setVisibility(View.VISIBLE);
        }
    }

}
