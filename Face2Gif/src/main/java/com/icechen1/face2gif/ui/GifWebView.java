package com.icechen1.face2gif.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.webkit.WebView;
import com.icechen1.face2gif.R;

import java.io.File;

/**
 * Created by Icechen1 on 06/07/13.
 * WebView that automatically loads the gif url
 */
public class GifWebView extends WebView {
    public GifWebView(Context context, String path) { super(context);
        //Center everything
        //loadUrl(path);
        File myPath = new File(path);
        Log.d("GifWebView",myPath.getParent() +" "+ myPath.getName());
        loadDataWithBaseURL(myPath.getParent()+"/","<html><center><img src=\""+myPath.getName()+"\"></html>","text/html","utf-8","");
        //loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} </style></head><body><img src='"+path+"'/></body></html>" ,"text/html",  "UTF-8");
        setBackgroundColor(0);
    }
}
