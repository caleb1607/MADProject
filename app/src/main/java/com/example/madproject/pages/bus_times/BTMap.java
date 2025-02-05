package com.example.madproject.pages.bus_times;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.madproject.MapView;
import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.helper.JSONReader;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class BTMap extends Fragment {
    MapView mapView;
    View rootView;
    List<BusStopsComplete> busStopsCompleteList;
    boolean markerFlag = false; // this flag is so that marker click overrides map click

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_btmap, container, false);
       // read from datasets
        busStopsCompleteList = JSONReader.bus_stops_complete(getContext());
        // setup widgets
        FrameLayout mapFragmentContainer = rootView.findViewById(R.id.MapFragmentContainer2);
        mapFragmentContainer.setOnClickListener(v -> {
            openBTPopup(false);
            Log.d("clicko","");
        });
        Button returnButton = rootView.findViewById(R.id.ReturnButton4);
        returnButton.setOnClickListener(v -> onReturn());
        // Load the MapView fragment
        loadMapFragment(new MapView());
        // Use a Handler or delay to give the fragment time to load
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.MapFragmentContainer2);
            if (fragment instanceof MapView) {
                mapView = (MapView) fragment;
                setupMap();
            }
        }, 500);  // 500ms delay for it to load
        return rootView;
    }
    private void setupMap() {
        //for (BusStopsComplete BSData : busStopsCompleteList) {
            Log.d("adding marker", "adding marker");
            mapView.addMarker(
                    new LatLng(1.3098/*BSData.getLatitude()*/, 103.7775/*BSData.getLongitude()*/),
                    "SP"/*BSData.getDescription()*/,
                    BitmapDescriptorFactory.HUE_AZURE
            );
        //}
        mapView.setMarkerOnClickListener(this::onMarkerClick);
        mapView.setMapOnClickListener(this::onMapClick);
    }
    private void onMarkerClick(Marker marker) {
        openBTPopup(true);
        mapView.moveCamera(marker.getPosition(), 15f);
    }
    private void onMapClick(LatLng position) {
        openBTPopup(false);
    }
    private void openBTPopup(boolean doOpen) {
        FrameLayout slidingView = rootView.findViewById(R.id.BTMapBusTimesPanel);
        if (doOpen && slidingView.getVisibility() == View.INVISIBLE) {
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slidefade_in_bottom);
            slidingView.setVisibility(View.VISIBLE);
            slidingView.startAnimation(anim);
        } else if (!doOpen && slidingView.getVisibility() == View.VISIBLE) { // close
            Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.slidefade_out_bottom);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    slidingView.setVisibility(View.INVISIBLE); // hide after anim ends
                }
                @Override public void onAnimationRepeat(Animation animation) {}
            });
            slidingView.startAnimation(anim);
        }
    }
    private void loadMapFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.MapFragmentContainer2, fragment)
                .commit();
    }
    private void onReturn() {
        Log.d("returning", "returning");
        getParentFragmentManager().popBackStack("BusTimes", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}