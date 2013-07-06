package com.icechen1.face2gif;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Icechen1 on 05/07/13.
 */
public class OptionFragment extends DialogFragment {
    public OptionFragment() {
        // Empty constructor required for DialogFragment
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        return view;
    }
}
