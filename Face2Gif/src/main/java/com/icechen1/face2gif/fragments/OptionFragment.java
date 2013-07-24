package com.icechen1.face2gif.fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.ads.*;
import com.icechen1.face2gif.AppPreferenceManager;
import com.icechen1.face2gif.R;

/**
 * Created by Icechen1 on 05/07/13.
 */
public class OptionFragment extends DialogFragment {
    private OptionListener listener = null;
    private Integer currentFps;
    private SeekBar qualitySeekBar;
    private TextView qualityText;
    private CheckBox repeatCheckBox;
    private CheckBox vignetteCheckBox;
    private CheckBox captionCheckBox;
    private AdView adView;

    public OptionFragment() {
        // Empty constructor required for DialogFragment
    }

    public OptionFragment(OptionListener lis){
        super();
        listener = lis;
    }

    public interface OptionListener{
        public void onOptionFragmentClosed();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NO_FRAME;
        int theme = android.R.style.Theme_Holo_Light_Dialog;

        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        final ImageButton addFps = (ImageButton) view.findViewById(R.id.addFps);
        final AppPreferenceManager pref = new AppPreferenceManager(getActivity());
        currentFps = pref.getFPS();
        //Log.d("Face2Gif", "" + currentFps);
        final ImageButton minusFps = (ImageButton) view.findViewById(R.id.minusFps);
        final TextView fpsView = (TextView) view.findViewById(R.id.fps_TextView);
        fpsView.setText(currentFps.toString());
        if(currentFps == 30){
            addFps.setEnabled(false);
        }

        if(currentFps == 1){
            minusFps.setEnabled(false);
        }

        addFps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Increment FPS
                if(currentFps<30){
                    currentFps++;
                    minusFps.setEnabled(true);
                }else{
                    addFps.setEnabled(false);
                }
                fpsView.setText(currentFps.toString());
                pref.saveFPS(currentFps);
            }
        });

        minusFps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Decrease FPS
                if(currentFps>1){
                    currentFps--;
                    addFps.setEnabled(true);
                }else{
                    minusFps.setEnabled(false);
                }
                fpsView.setText(currentFps.toString());
                pref.saveFPS(currentFps);
            }
        });


        qualitySeekBar = (SeekBar) view.findViewById(R.id.qualitySeekBar);
        qualitySeekBar.setProgress(pref.getQuality());
        qualityText = (TextView) view.findViewById(R.id.qualityTextView);
        Integer qual = pref.getQuality();
        qualityText.setText(qual.toString());
        qualitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //change quality
                Integer inte = i;
                pref.saveQuality(i);
                qualityText.setText(inte.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        repeatCheckBox = (CheckBox) view.findViewById(R.id.repeatCheckBox);
        repeatCheckBox.setChecked(pref.getRepeat());
        repeatCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                pref.saveRepeat(b);
            }
        });
        vignetteCheckBox = (CheckBox) view.findViewById(R.id.vignetteCheckBox);
        vignetteCheckBox.setChecked(pref.getVignette());
        vignetteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                pref.saveVignette(b);
            }
        });        
        
        captionCheckBox = (CheckBox) view.findViewById(R.id.captionCheckBox);
        captionCheckBox.setChecked(pref.getCaption());
        captionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                pref.saveCaption(b);
            }
        });
        // Create the adView
        adView = new AdView(getActivity(), AdSize.BANNER, "a151d97ffdd5c65");
        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout"
        final LinearLayout layout = (LinearLayout)view.findViewById(R.id.optionAdViewContainer);
        layout.setVisibility(View.GONE);
        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest().addTestDevice("016B75D70501901B"));
        adView.setAdListener(new AdListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailedToReceiveAd(Ad ad, AdRequest.ErrorCode errorCode) {

            }

            @Override
            public void onPresentScreen(Ad ad) {

            }

            @Override
            public void onDismissScreen(Ad ad) {

            }

            @Override
            public void onLeaveApplication(Ad ad) {

            }
        });

        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
        if(listener != null){
            listener.onOptionFragmentClosed();;
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
