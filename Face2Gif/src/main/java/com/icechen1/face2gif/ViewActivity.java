package com.icechen1.face2gif;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class ViewActivity extends FragmentActivity {

    private ViewFragment viewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String path = extras.getString("path");
            int h = extras.getInt("h");
            int w = extras.getInt("w");
            viewFragment = new ViewFragment(path,h,w);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.view_container,viewFragment,"frag_view")
                    .commit();
        }else{
            //Show error dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error, no information provided.");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view, menu);
        return true;
    }
    
}
