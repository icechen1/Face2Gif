package com.icechen1.face2gif.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.icechen1.face2gif.R;
import com.icechen1.face2gif.ui.GifWebView;

/**
 * Created by Icechen1 on 04/07/13.
 */
public class ViewFragment extends Fragment{
    private final int h,w;
    private String path;
    private LinearLayout pictureContainer;

    public ViewFragment(){
        //TODO restore state in ViewActivity
        //TODO Change path
        h = 0; w=0;
    }
    public ViewFragment(String path, int h, int w){
        this.path = path;
        this.h=h;
        this.w=w;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        pictureContainer = (LinearLayout)view.findViewById(R.id.pictureContainer);
        showPicture();
        return view;
    }

    public void showPicture(){
        String webPath = "file://" + path;
        GifWebView gifView = new GifWebView(this.getActivity(),webPath);
        //Transparent background
        //gifView.setBackgroundColor(0x00000000);

        ViewGroup.LayoutParams params;
        if(h<w){
            //Portrait
            params = new LinearLayout.LayoutParams(h,w);

        }else{
            params = new LinearLayout.LayoutParams(w,h);
        }
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size.width,size.height);
        //pictureContainer.setLayoutParams(params);
       // gifView.setLayoutParams(params);

//        InputStream stream = null;
//        try {
//            stream = new FileInputStream(new File(webPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // ImageViewEx view = new ImageViewEx(getActivity(),stream);
        pictureContainer.addView(gifView);

    }
}
