package com.supportanimator.sample;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class PreferencesActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {

    public static final String PREF_TRANSITION_MODE="transition";
    public static final String PREF_STANDARD_TRANSITION="standard_transition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        if(getPreferenceScreen()!=null) {
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_TRANSITION_MODE);
            updateSummary(getPreferenceScreen().getSharedPreferences(), PREF_STANDARD_TRANSITION);
        }
    }

    @Override
    protected void onPause() {
        if(getPreferenceScreen()!=null)
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(getPreferenceScreen()!=null)
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        super.onResume();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(sharedPreferences, key);
    }

    private void updateSummary(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesActivity.PREF_TRANSITION_MODE))
            findPreference(key).setSummary(sharedPreferences.getString(key, ""));
        else if (key.equals(PreferencesActivity.PREF_STANDARD_TRANSITION)) {
            int transition = Integer.parseInt(sharedPreferences.getString(key, ""));
            switch(transition) {
                case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE:
                    findPreference(key).setSummary("TRANSIT_FRAGMENT_CLOSE");
                    break;
                case FragmentTransaction.TRANSIT_FRAGMENT_OPEN:
                    findPreference(key).setSummary("TRANSIT_FRAGMENT_OPEN");
                    break;
                case FragmentTransaction.TRANSIT_FRAGMENT_FADE:
                    findPreference(key).setSummary("TRANSIT_FRAGMENT_FADE");
                    break;
            }
        }
    }

}

