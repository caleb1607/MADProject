package com.example.madproject.ui.bus_times;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.madproject.R;

public class BusServicesList extends Fragment {
    String type;
    String busStopCode;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busserviceslist, container, false);
        // get input params
        Bundle bundle = getArguments();
        type = bundle.getString("type");
        busStopCode = bundle.getString("value");
        return rootView;
    }
}