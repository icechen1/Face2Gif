package com.icechen1.face2gif;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.devspark.appmsg.AppMsg;
import com.icechen1.face2gif.ui.Converters;
import com.icechen1.face2gif.ui.GifMovieView;
import com.icechen1.face2gif.ui.GifWebView;
import com.icechen1.face2gif.ui.ImageViewEx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class ViewFragment extends Fragment{
    private final Camera.Size size;
    ArrayList<Bitmap> list;
    private static final int FPS = 10;
    private AsyncTask<ArrayList<Bitmap>,Integer,String> encodertask;
    private ProgressBar progressbar;
    private TextView textProgress;
    private String path;
    private LinearLayout pictureContainer;

    ViewFragment(ArrayList<Bitmap> a, Camera.Size size){
        list = a;
        this.size = size;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        pictureContainer = (LinearLayout)view.findViewById(R.id.pictureContainer);
        progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressbar.setMax(100);
        textProgress = (TextView) view.findViewById(R.id.textProgress);
        //Start encoding the gif file
        encodertask = new GifEncoderTask(FPS,size,this).execute(list);

        return view;
    }

    public void updateProgress(int i) {
        progressbar.setProgress(i);
        textProgress.setText(i + "%");
    }

    public void doneEncoding(String path){
        this.path = path;
        AppMsg.makeText(getActivity(), "Saved to " + path, AppMsg.STYLE_INFO).show();
        showPicture();
    }
    public void showPicture(){
        String webPath = "file://" + path;
        GifWebView gifView = new GifWebView(this.getActivity(),webPath);
        //Transparent background
        //gifView.setBackgroundColor(0x00000000);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size.width,size.height);
        //pictureContainer.setLayoutParams(params);
        //gifView.setLayoutParams(params);

//        InputStream stream = null;
//        try {
//            stream = new FileInputStream(new File(webPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

       // ImageViewEx view = new ImageViewEx(getActivity(),stream);
        progressbar.setVisibility(View.GONE);
        textProgress.setVisibility(View.GONE);
        pictureContainer.addView(gifView);

    }
    @Override
    public void onPause() {
        super.onPause();
        //TODO fix this
        encodertask.cancel(true);
    }
}
