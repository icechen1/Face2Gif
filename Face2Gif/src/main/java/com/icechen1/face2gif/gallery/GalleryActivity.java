package com.icechen1.face2gif.gallery;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.icechen1.face2gif.R;
import com.icechen1.face2gif.fragments.OptionFragment;
import com.jake.quiltview.QuiltView;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends FragmentActivity {

    private QuiltView quiltView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        // Show the Up button in the action bar.
        setupActionBar();

        if(!isExternalStorageWritable()){
            showNoStorageDialog();
            return;
        }
        quiltView = (QuiltView) findViewById( R. id. quilt);

        ArrayList<ImageView> views = new ArrayList<ImageView>();

        ArrayList<File> gifFiles = getGifFileList();
        if (gifFiles.size() == 0){
            //No valid pictures found
            TextView noPics = new TextView(this);
            noPics.setText(getResources().getText(R.string.no_pics_gallery));
           // views.add(noPics);
           // quiltView.addPatchViews(views);
            quiltView.addView(noPics);
            return;
        }
        //Add all the relevant gif files to a list then add it to the quilt view
        for(File f: gifFiles){
            final String path = f.getAbsolutePath();
            //GifWebView v = new GifWebView(this, path);
            final Bitmap bmp = BitmapFactory.decodeFile(path);
            ImageView img = new ImageView(this);
            img.setTag(path);
            img.setImageBitmap(bmp);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ViewActivity.class);
                    i.putExtra("path",path);
                    i.putExtra("h",bmp.getHeight());
                    i.putExtra("w",bmp.getWidth());
                    i.putExtra("origin",0);
                    startActivityForResult(i, ViewActivity.FILE_DELETED);
                }
            });
            views.add(img);
        }

        quiltView.addPatchImages(views);
    }
    private ArrayList<File> getGifFileList(){
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Face2Gif");
        directory.mkdirs();

        File[] files = directory.listFiles();
        ArrayList<File> gifFiles = new ArrayList<File>();

        for(File f: files){
            if(f.getAbsolutePath().matches(".*\\.gif")) gifFiles.add(f);
        }
        return gifFiles;
    }

    private void showNoStorageDialog() {
        //Show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("No external storage found!");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                openSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    //Open the settings DialogFragment
    public void openSettings(){
        FragmentManager fm = getSupportFragmentManager();
        OptionFragment OptionFragment = new OptionFragment();
        OptionFragment.show(fm, "fragment_options");
    }

    //Handles file deletions, etc
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (ViewActivity.FILE_DELETED) : {
                if (resultCode == Activity.RESULT_OK) {
                    String tag = data.getStringExtra("TAG");
                    Log.d("Face2Gif","Removing tag: " + tag);
                    quiltView.removeQuilt(quiltView.findViewWithTag(tag));
                    quiltView.invalidate();
                }
                break;
            }
        }
    }
}
