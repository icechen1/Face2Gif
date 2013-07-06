package com.icechen1.face2gif.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.webkit.WebView;
import com.icechen1.face2gif.R;

/**
 * Created by Icechen1 on 06/07/13.
 * WebView that automatically loads the gif url
 */
public class GifWebView extends WebView {
    public GifWebView(Context context, String path) { super(context);
        //Center everything
/*        String  content="<html>" +
                "<head>" +
                "<style>\n" +
                "body\n" +
                "{\n" +
                "background-color:"+context.getResources().getString(R.color.background_window)+";\n" +
                "margin-left:auto;\n" +
                "margin-right:auto;\n"+
                "}\n" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<img src=\""+path+"\" alt=\"GIF Picture\"/>" +
                "</body>" +
                "</html>";
        Log.d("GifWebView", content);
        loadData(content, "text/html", null); }*/
        loadUrl(path);
        //loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} </style></head><body><img src='"+path+"'/></body></html>" ,"text/html",  "UTF-8");
    }
}
