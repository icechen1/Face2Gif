package com.icechen1.face2gif;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.MediaActionSound;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.DecimalFormat;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class PreviewFragment extends Fragment {

    private Camera mCamera;
    private SurfaceView mPreview;
    private Recorder rec;
    private boolean isRecording = false;
    private String TAG = "Face2Gif";
    private MediaActionSound mMediaActionSoundStart;
    private MediaActionSound mMediaActionSoundStop;
    RelativeLayout previewRelativeLayout;
    private TextView timer;
    private Integer minutes, seconds;
    private ImageView recordcircle;
    private Drawable recordSquareDrawable,recordCircleDrawable;
    private ImageButton toggleButton;
    private static final int FPS = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        previewRelativeLayout = (RelativeLayout) view.findViewById(R.id.camera_preview);
        timer = (TextView) view.findViewById(R.id.timerView);
        recordcircle = (ImageView) view.findViewById(R.id.recordcircle);
        toggleButton = (ImageButton) view.findViewById(R.id.toggle);
        //Hide the red circle
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
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseCamera();              // release the camera immediately on pause event

        previewRelativeLayout.removeAllViewsInLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(checkCameraHardware(getActivity())){
            // Create an instance of Camera
            mCamera = getCameraInstance();
            // Create our Preview view and set it as the content of our activity.
            if(mCamera == null){
                previewRelativeLayout.setBackgroundColor(Color.BLACK);
                //Show dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Cannot connect to the camera.");
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
                alert.show();

            }else{
                mPreview = new CameraPreview(getActivity(), mCamera, CameraPreview.LayoutMode.FitToParent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mPreview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                }
            previewRelativeLayout.addView(mPreview);
            }

        }else{
            //TODO error handling

        }
    }

    private void releaseCamera(){
        if(rec != null){
            rec.stop();
        }
        if (mCamera != null){
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance(){
        Camera cam = null;
        try {
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            Log.d("Face2Gif", "Camera count: " + cameraCount);
            for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
                Camera.getCameraInfo( camIdx, cameraInfo );
                if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                    try {
                        cam = Camera.open( camIdx );
                        Log.d("Face2Gif", "Camera opened: " + camIdx);
                    } catch (RuntimeException e) {
                        //No front camera
                        Log.e("Face2Gif", "Camera failed to open: " + e.getLocalizedMessage() + "\n");
                        e.printStackTrace();
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
        return cam; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    //Takes pictures
    public void toggle(View v){

        Log.d(TAG, "Toggle button clicked");
        if(isRecording){
            //Stop the recording
            isRecording = false;
            rec.stop();
            //Update the button drawable
            toggleButton.setImageDrawable(recordCircleDrawable);
            //Stop the timer from counting up
            stopTimer();
            //Play stop record sound if avail.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mMediaActionSoundStop.play(MediaActionSound.STOP_VIDEO_RECORDING);
            }

            //Show the result fragment
            Fragment viewFragment = new RenderFragment(rec.retrieveData(), rec.getSize());
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,viewFragment,"frag_render")
                    .addToBackStack(null)
                    .commit();
        }else{
            //Start recording
            rec = new Recorder (mCamera, FPS);
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
}
