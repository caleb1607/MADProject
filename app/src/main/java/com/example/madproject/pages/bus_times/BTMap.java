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
import com.example.madproject.datasets.BusStopsMap;
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
        if (busStopsCompleteList == null || busStopsCompleteList.isEmpty()) {
            Log.e("BTMap", "Bus stops data is empty or not loaded!");
            return;
        }

        LatLng singaporeLocation = new LatLng(1.3500, 103.7044);
        mapView.moveCamera(singaporeLocation, 15f);  // Zoom level 15
        updateVisibleMarkers(mapView.getCameraPosition());
        mapView.addMarker(singaporeLocation, "My Bookmark", BitmapDescriptorFactory.HUE_RED);



        mapView.setOnCameraMoveListener(() -> {
            LatLng newCenter = mapView.getCameraPosition();
            updateVisibleMarkers(newCenter);
        });


        for (BusStopsComplete busStop : busStopsCompleteList) {
            LatLng position = new LatLng(busStop.getLatitude(), busStop.getLongitude());

            double distance = calculateDistance(1.3500, 103.7044, busStop.getLatitude(), busStop.getLongitude());


        }



        mapView.setMarkerOnClickListener(this::onMarkerClick);
        mapView.setMapOnClickListener(this::onMapClick);
    }



    private void updateVisibleMarkers(LatLng center) {
        mapView.clearBookmarks(); // Remove old markers

        LatLng singaporeLocation = new LatLng(1.3500, 103.7044);
        mapView.addMarker(singaporeLocation, "My Bookmark", BitmapDescriptorFactory.HUE_RED);

        for (BusStopsComplete busStop : busStopsCompleteList) {
            LatLng position = new LatLng(busStop.getLatitude(), busStop.getLongitude());


            double distance = calculateDistance(
                    center.latitude, center.longitude,
                    busStop.getLatitude(), busStop.getLongitude()
            );


            if (distance <= 0.3) { //0.3=300m
                mapView.addMarker(
                        position,
                        busStop.getDescription(),
                        BitmapDescriptorFactory.HUE_AZURE // BLUE MARKER
                );
            }
        }
    }

    private void onMarkerClick(Marker marker) {
        openBTPopup(true);
        mapView.moveCamera(marker.getPosition(), 15f);

    }
    private void onMapClick(LatLng position) {
        openBTPopup(false);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in kmmmmmmm
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
        getParentFragmentManager().popBackStack("BusTimes", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
