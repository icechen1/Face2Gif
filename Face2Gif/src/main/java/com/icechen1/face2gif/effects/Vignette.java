package com.icechen1.face2gif.effects;

import android.graphics.*;

/**
 * Created by Icechen1 on 07/07/13.
 * Applies a vignette effect to each frame
 */
public class Vignette extends GifEffect {

    private Canvas canvas;
    private final float radius;
    private final Paint paint;
    private final RectF rectf;
    private final Rect rect;
    private Bitmap image;

    public Vignette(Bitmap i){
        rect = new Rect(0, 0, i.getWidth(),i.getHeight());
        radius = (float) (i.getWidth()/1.15);
        RadialGradient gradient = new RadialGradient(i.getWidth()/2, i.getHeight()/2, radius, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setAlpha(150);
        paint.setShader(gradient);

        rectf = new RectF(rect);

    }
    @Override
    public void setBitmap(Bitmap b) {
        image = b;
    }

    @Override
    public void draw() {
        canvas = new Canvas();

        paint.setXfermode(null);

        canvas.drawARGB(1, 0, 0, 0);

        canvas.setBitmap(image);
        canvas.drawRect(rectf, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(image, rect, rect, paint);
    }

    @Override
    public void set() {

    }
}
