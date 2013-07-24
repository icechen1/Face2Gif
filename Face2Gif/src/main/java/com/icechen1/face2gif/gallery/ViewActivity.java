package com.icechen1.face2gif.gallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import com.icechen1.face2gif.R;
import com.icechen1.face2gif.fragments.OptionFragment;
import com.icechen1.face2gif.fragments.ViewFragment;

import java.io.File;

public class ViewActivity extends FragmentActivity {

    static final int FILE_DELETED = 1;
    private ViewFragment viewFragment;
    private String path;
    private int originActivity;
    private int h,w;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            path = extras.getString("path");
            h = extras.getInt("h");
            w = extras.getInt("w");
            viewFragment = new ViewFragment(path,h,w);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.view_container,viewFragment,"frag_view")
                    .commit();
            originActivity = extras.getInt("origin"); //1 for MainActivity, 0 for gallery
        }else{
            if (savedInstanceState != null) {
                // Restore values from last time
                path = savedInstanceState.getString("path");
                h = savedInstanceState.getInt("h");
                w = savedInstanceState.getInt("w");
                viewFragment = new ViewFragment(path,h,w);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.view_container,viewFragment,"frag_view")
                        .commit();
                originActivity = savedInstanceState.getInt("origin"); //1 for MainActivity, 0 for gallery
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
        setupActionBar();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        savedInstanceState.putString("path", path);
        savedInstanceState.putInt("h", h);
        savedInstanceState.putInt("h", w);
        savedInstanceState.putInt("origin", originActivity);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view, menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
             initializeShareAction(menu.findItem(R.id.share));
        }
        return true;
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void initializeShareAction(MenuItem shareItem) {
        ShareActionProvider shareProvider = (ShareActionProvider) shareItem.getActionProvider();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + new File(path).toString()));
        //Log.d("share","file://" + new File(path).toString());
        shareIntent.setType("image/gif");

        shareProvider.setShareIntent(shareIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(originActivity == 0){
                    NavUtils.navigateUpFromSameTask(this);
                }else{
                    //Go back to mainactivity which should be still open
                    finish();
                }
                break;
            case R.id.action_settings:
                openSettings();
                break;
            case R.id.sharebtn:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/gif");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + new File(path).toString()));
                startActivity(Intent.createChooser(share, "Share Image"));
                break;
            case R.id.action_delete:
                //Show delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getText(R.string.delete_confirmation));
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File file = new File(path);
                        boolean deleted = file.delete();
                        Log.d("Face2Gif", "Delete status: " + deleted);
                        //Go back
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("TAG", path);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();

                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(false);
                alert.show();
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    //Open the settings DialogFragment
    public void openSettings(){
        FragmentManager fm = getSupportFragmentManager();
        OptionFragment OptionFragment = new OptionFragment();
        OptionFragment.show(fm, "fragment_options");
    }


}
