package com.icechen1.face2gif.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.view.View;

import java.io.InputStream;

/**
 * Created by Icechen1 on 06/07/13.
 */
public class GifMovieView extends View {
    private final InputStream mStream;
    private Movie mMovie;
    private long mMoviestart;

    public GifMovieView(Context context, InputStream stream) {
        super(context);
        mStream = stream;
        mMovie = Movie.decodeStream(mStream);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        final long now = SystemClock.uptimeMillis();

        if (mMoviestart == 0) {
            mMoviestart = now;
        }

        final int relTime = (int)((now - mMoviestart) % mMovie.duration());
        mMovie.setTime(relTime);
        mMovie.draw(canvas, 10, 10);
        this.invalidate();
}}