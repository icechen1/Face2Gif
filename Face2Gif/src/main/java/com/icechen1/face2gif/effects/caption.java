package com.icechen1.face2gif.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by Icechen1 on 07/07/13.
 */
public class Caption extends GifEffect{

    private final Canvas canvas;
    private final Paint strokePaint;
    private final Paint textPaint;
    private final int width,height;
    private String topText;
    private String botText;

    public Caption(Context cxt,Bitmap b){
        canvas = new Canvas();
        Typeface font = Typeface.createFromAsset(cxt.getAssets(), "fonts/impact.ttf");

        strokePaint = new Paint();
        strokePaint.setARGB(255, 0, 0, 0);
        strokePaint.setTextAlign(Paint.Align.CENTER);
        strokePaint.setTextSize(42);
        strokePaint.setTypeface(font);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(2);
        strokePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(42);
        textPaint.setTypeface(font);
        textPaint.setAntiAlias(true);

        width=b.getWidth();
        height=b.getHeight();

    }

    @Override
    public void setBitmap(Bitmap b) {
        canvas.setBitmap(b);
    }

    public void setTopText(String s){
        topText = s;
    }

    public void setBottomText(String s){
        botText = s;
    }

    @Override
    public void draw() {

        if(topText != null){
            canvas.drawText(topText, width/2, height/6, strokePaint);
            canvas.drawText(topText, width/2, height/6, textPaint);
        }

        if(botText != null){
            canvas.drawText(botText, width/2, height/6 *5, strokePaint);
            canvas.drawText(botText, width/2, height/6 *5, textPaint);
        }
    }

    @Override
    public void set() {

    }

}
