package com.icechen1.face2gif;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import com.icechen1.face2gif.fragments.OptionFragment;
import com.icechen1.face2gif.fragments.PreviewFragment;
import com.icechen1.face2gif.fragments.RenderFragment;
import com.icechen1.face2gif.gallery.GalleryActivity;
import com.icechen1.face2gif.ui.AppRater;

/**
 * TODO
 * -Scale,size
 * -Effects
 * -Rotation
 * -Intent
 * -Gallery
 * -Move strings to xml
 * -Grid style options OK
 * -Delete pic
 * -change hue and stuff every frame for a psychdelic effect
 * -back camera
 * -add caption after taking the video
 * -maybe declare camera as static
 */
public class MainActivity extends FragmentActivity implements OptionFragment.OptionListener{


    public static int display_angle = 0;
    public static int layout_angle = 0;
    private PreviewFragment previewFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //No actionbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Prevent auto-rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		setContentView(R.layout.activity_main);

        previewFragment = new PreviewFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, previewFragment, "frag_rec")
                .commit();
        AppRater.app_launched(this);
       // AppRater.showRateDialog(this, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    //Forwards the click to the Fragment
	public void toggle(View v){
        try{
        previewFragment.toggle(v);
        }catch(Exception e){
            //Fragment not active
            Log.e("Face2Gif", e.getMessage());
        }
	}

    public void flipCamera(View v){
        previewFragment.flipCamera(v);
        try{

        }catch(Exception e){
            //Fragment not active
            Log.e("Face2Gif", e.getMessage());
        }
    }

    //Open the settings DialogFragment
    public void openSettings(View v){
        FragmentManager fm = getSupportFragmentManager();
        OptionFragment OptionFragment = new OptionFragment(this);
        OptionFragment.show(fm, "fragment_options");
    }

    public void cancelRender(View v){
        try{
            ((RenderFragment)getSupportFragmentManager().findFragmentByTag("frag_render")).cancel();
        }catch(Exception e){

        }
    }

    public void gallery(View v){
        Intent i = new Intent(this, GalleryActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed(){
        //If user came back from options: Update the UI
        try{
            ((PreviewFragment) getSupportFragmentManager().findFragmentByTag("frag_rec")).invalidateUIElements();
        }catch(Exception e){

        }
        //If user clicked back from the render screen: cancel the task
        try{
            ((RenderFragment) getSupportFragmentManager().findFragmentByTag("frag_render")).backCancel();
        }catch(Exception e){

        }

        //Stop recording
        try{
            PreviewFragment frag = ((PreviewFragment) getSupportFragmentManager().findFragmentByTag("frag_rec"));
            if (frag.isRecording){
                frag.cancelRecording();
                return;
            }

        }catch(Exception e){

        }
       super.onBackPressed();
    }


    @Override
    public void onOptionFragmentClosed() {
        //Settings changed: Update the UI
        try{
            ((PreviewFragment) getSupportFragmentManager().findFragmentByTag("frag_rec")).invalidateUIElements();
        }catch(Exception e){

        }
    }
}
