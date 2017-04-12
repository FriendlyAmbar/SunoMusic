package com.ambar.musicplayerapp.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ambar.musicplayerapp.R;

/**
 * Created by ambar on 12-04-2017.
 */

public class FragmentArtist extends android.support.v4.app.Fragment {

    public FragmentArtist() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmentartist,container,false);
    }
}
