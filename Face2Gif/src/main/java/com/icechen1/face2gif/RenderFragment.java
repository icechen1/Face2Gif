package com.icechen1.face2gif;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.devspark.appmsg.AppMsg;
import com.icechen1.face2gif.ui.GifWebView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class RenderFragment extends Fragment{
    private final Camera.Size size;
    ArrayList<Bitmap> list;
    private static final int FPS = 10;
    private AsyncTask<ArrayList<Bitmap>,Integer,String> encodertask;
    private ProgressBar progressbar;
    private TextView textProgress;
    private String path;
    private LinearLayout pictureContainer;
    private Random rand;
    private String[] messages;
    private TextView motivationText;

    RenderFragment(ArrayList<Bitmap> a, Camera.Size size){
        list = a;
        this.size = size;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_render, container, false);
        pictureContainer = (LinearLayout)view.findViewById(R.id.pictureContainer);
        progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressbar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        progressbar.setMax(100);
        textProgress = (TextView) view.findViewById(R.id.textProgress);
        motivationText = (TextView) view.findViewById(R.id.motivationText);
        //Start encoding the gif file
        encodertask = new GifEncoderTask(FPS,size,this).execute(list);
        //Totally legitimate
        messages = getResources().getStringArray(R.array.loading_messages);
        rand = new Random();



        return view;
    }

    public void showPath(String path){
        AppMsg.makeText(getActivity(), "Saving to " + path, AppMsg.STYLE_INFO).show();
    }

    public void updateProgress(int i) {
        progressbar.setProgress(i);
        textProgress.setText(i + "%");
        int randomMessage = rand.nextInt(messages.length);
        motivationText.setText(messages[randomMessage]);
    }

    public void doneEncoding(String path){
        this.path = path;
        launchPictureActivity();
    }
    public void launchPictureActivity(){
        Intent i = new Intent(getActivity().getApplicationContext(), ViewActivity.class);
        i.putExtra("path",path);
        i.putExtra("h",size.height);
        i.putExtra("w",size.width);
        startActivity(i);
        loadPreviewFragment();

    }
    @Override
    public void onPause() {
        super.onPause();
        //TODO fix this
        cancel();
    }

    public void cancel() {
        encodertask.cancel(false);
        loadPreviewFragment();
    }
    public void loadPreviewFragment(){
        //restore the preview fragment if the user comes back
        PreviewFragment previewFragment = (PreviewFragment) getActivity().getSupportFragmentManager().findFragmentByTag("frag_rec");
        if(previewFragment == null ) previewFragment = new PreviewFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, previewFragment, "frag_rec")
                .commit();

    }
}
