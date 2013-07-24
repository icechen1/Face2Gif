package com.icechen1.face2gif.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.devspark.appmsg.AppMsg;
import com.icechen1.face2gif.GifEncoderTask;
import com.icechen1.face2gif.R;
import com.icechen1.face2gif.gallery.ViewActivity;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class RenderFragment extends Fragment{
    private final Camera.Size size;
    private final String top, bottom;
    ArrayList<Bitmap> list;
    private AsyncTask<ArrayList<Bitmap>,Integer,String> encodertask;
    private ProgressBar progressbar;
    private TextView textProgress;
    private String path;
    private LinearLayout pictureContainer;
    private Random rand;
    private String[] messages;
    private TextView motivationText;
    private ImageView imagePreview;

    public RenderFragment(ArrayList<Bitmap> a, Camera.Size size, String top, String bottom){
        list = a;
        this.size = size;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_render, container, false);
        pictureContainer = (LinearLayout)view.findViewById(R.id.pictureContainer);
        imagePreview = (ImageView) view.findViewById(R.id.imagePreview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressbar.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        progressbar.setMax(100);
        textProgress = (TextView) view.findViewById(R.id.textProgress);
        motivationText = (TextView) view.findViewById(R.id.motivationText);
        //Start encoding the gif file
        encodertask = new GifEncoderTask(size,this,top,bottom).execute(list);
        //frame = 0;
        imagePreview.setImageBitmap(list.get(0));
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

        //Show a preview
       // imagePreview.setImageBitmap(list.get(frame));
       // frame++;
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
        i.putExtra("origin",1);
        startActivity(i);
      //  loadPreviewFragment();

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
        // restore the preview fragment if the user comes back

        getFragmentManager().popBackStackImmediate();

    }
    public void backCancel(){
        encodertask.cancel(true);
    }
}
