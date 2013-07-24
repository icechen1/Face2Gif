package com.icechen1.face2gif.listener;

import android.content.Context;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

/**
 * Created by Icechen1 on 06/07/13.
 * Took a hint from http://www.androidzeitgeist.com/2013/01/fixing-rotation-camera-picture.html
 */
public class CameraOrientationListener extends OrientationEventListener {
    private int currentOrientation;
    private CameraOrientation mCallback;

    public CameraOrientationListener(Context context,CameraOrientation cam) {
        super(context, SensorManager.SENSOR_DELAY_UI);
        mCallback=cam;
    }
    public interface CameraOrientation{
        public void onOrientationChanged(int orientation);
    }
    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation != ORIENTATION_UNKNOWN) {
            int normal = normalize(orientation);
            currentOrientation = normal;
            mCallback.onOrientationChanged(normal);
        }
    }

    private int normalize(int degrees) {
        if (degrees > 315 || degrees <= 45) {
            return 0;
        }

        if (degrees > 45 && degrees <= 135) {
            return 90;
        }

        if (degrees > 135 && degrees <= 225) {
            return 180;
        }

        if (degrees > 225 && degrees <= 315) {
            return 270;
        }

        throw new RuntimeException("Value must be 0<=x<360");
    }

    public int getOrientation() {
        return currentOrientation;
    }
}
