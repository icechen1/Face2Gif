package com.icechen1.face2gif;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;

/**
 * TODO
 * -Scale,size
 * -Effects
 * -Rotation
 * -Intent
 * -Gallery
 */
public class MainActivity extends FragmentActivity {


    private PreviewFragment previewFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

        previewFragment = new PreviewFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,previewFragment,"frag_rec")
                .commit();
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

    //Open the settings DialogFragment
    public void openSettings(View v){
        FragmentManager fm = getSupportFragmentManager();
        OptionFragment OptionFragment = new OptionFragment();
        OptionFragment.show(fm, "fragment_options");
    }


}
