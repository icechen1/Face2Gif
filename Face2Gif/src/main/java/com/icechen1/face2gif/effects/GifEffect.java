package com.icechen1.face2gif.effects;

import android.graphics.Bitmap;

/**
 * Created by Icechen1 on 07/07/13.
 */
public abstract class GifEffect {

    GifEffect(){

    }

    public abstract void setBitmap(Bitmap b);

    public abstract void draw();

    public abstract void set();

}
