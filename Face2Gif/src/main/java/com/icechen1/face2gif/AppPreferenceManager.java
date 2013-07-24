package com.icechen1.face2gif;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.devspark.appmsg.AppMsg;

/**
 * Created by Icechen1 on 07/07/13.
 */
public class AppPreferenceManager {
    Activity cxt;

    SharedPreferences settings;
    SharedPreferences.Editor edit;

    public AppPreferenceManager(Activity cxt){
        this.cxt = cxt;
        settings = PreferenceManager.getDefaultSharedPreferences(cxt.getApplicationContext());
        edit = settings.edit();
    }

    /**
     * Creates a new instance of SettingsProvider
     * @param cxt Context
     * @return a SettingsProvider object
     */
    public static AppPreferenceManager newInstance(Activity cxt){
        return new AppPreferenceManager(cxt);
    }

    /**
     * Fetches the saved fps
     * @return integer: fps
     */
    public int getFPS(){
        int fps=10;
            try{
                fps = settings.getInt("fps", 10);
                if(fps>31 || fps <1) throw new Exception("Off limit FPS detected");
            }catch(Exception e){
                edit.putInt("fps", 10).commit();
                AppMsg.makeText(cxt, "Error loading FPS setting, resetting... ", AppMsg.STYLE_ALERT).show();

            }

        return fps;
    }

    /**
     * Saves the fps preference
     * @param value fps
     */
    public void saveFPS(int value){
        edit.putInt("fps", value).commit();
    }

    /**
     * Fetches the quality
     * @return integer: quality
     */
        public int getQuality(){
        int qual = settings.getInt("quality", 100);
        return qual;
    }

    /**
     * Saves the quality preference
     * @param value fps
     */
    public void saveQuality(int value){
        edit.putInt("quality", value).commit();
    }

    /**
     * Fetches repeat
     * @return boolean: repeat
     */
    public boolean getRepeat(){
        return settings.getBoolean("repeat", true);
    }

    /**
     * Saves the repeat preference
     * @param repeat repeat
     */
    public void saveRepeat(boolean repeat){
        edit.putBoolean("repeat", repeat).commit();
    }

    /**
     * Fetches vignette preference
     * @return boolean: vignette
     */
    public boolean getVignette(){
        return settings.getBoolean("vignette", false);
    }

    /**
     * Saves the vignette preference
     * @param v vignette
     */
    public void saveVignette(boolean v){
        edit.putBoolean("vignette", v).commit();
    }

    /**
     * Fetches caption preference
     * @return boolean: caption
     */
    public boolean getCaption(){
        return settings.getBoolean("caption", false);
    }

    /**
     * Saves the caption preference
     * @param c caption
     */
    public void saveCaption(boolean c){
        edit.putBoolean("caption", c).commit();
    }

    /**
     * Fetches caption preference
     * @return boolean: caption
     */
    public boolean getHintSeen(){
        return settings.getBoolean("hint_seen", false);
    }

    /**
     * Saves the caption preference
     */
    public void saveHintSeen(){
        edit.putBoolean("hint_seen", true).commit();
    }


    /**
     * Fetches caption preference
     * @return boolean: caption
     */
    public boolean getCameraFront(){
        return settings.getBoolean("camera_front_side", true);
    }

    /**
     * Saves the camera side preference
     */
    public void saveCameraFront(boolean front){
        edit.putBoolean("camera_front_side", front).commit();
    }
}
