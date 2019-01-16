package com.fervenzagames.apparbitraje.PhoneFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fervenzagames.apparbitraje.R;

public class PhoneStatsFragment extends Fragment {

    public PhoneStatsFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.phone_stats_fragment, container, false);
    }
}
