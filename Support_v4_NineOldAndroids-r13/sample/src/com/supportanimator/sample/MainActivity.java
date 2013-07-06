package com.supportanimator.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.supportanimator.sample.drawer.DrawerNavigationActivity;

public class MainActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button transitionButton = (Button)findViewById(R.id.transitions_button);
        transitionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TransitionActivity.class));
            }
        });
        Button pagerButton = (Button)findViewById(R.id.pager_button);
        pagerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PagerActivity.class));
            }
        });
        Button drawerButton = (Button)findViewById(R.id.drawer_button);
        drawerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DrawerNavigationActivity.class));
            }
        });
    }
}
