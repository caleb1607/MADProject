package com.example.madproject.pages.bus_times;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.MapView;
import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.datasets.BusStopsMap;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.BusTimesBookmarksDB;
import com.example.madproject.helper.Helper;
import com.example.madproject.helper.JSONReader;
import com.example.madproject.pages.settings.ThemeManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BTMap extends Fragment {
    MapView mapView;
    View rootView;
    Button backButton;
    TextView busStopNameTextt;
    BusTimesBookmarksDB busTimesBookmarksDB;
    List<BusServicePanel> fullPanelList = new ArrayList<>(); // list of panel data
    RecyclerView BusServicesBT;
    List<BusStopsComplete> busStopsCompleteList;

    BusServicesList.ItemAdapter adapter;

    static TextView clickedTextView;
    boolean markerFlag = false; // this flag is so that marker click overrides map click

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_btmap, container, false);
        // read from datasets
        busStopsCompleteList = JSONReader.bus_stops_complete(getContext());


        BusServicesBT = rootView.findViewById(R.id.BusServicesBT);
        BusServicesBT.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // transition
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_textview);
        setSharedElementEnterTransition(transition);

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
        mapView.moveCamera(singaporeLocation, 16f);  // Zoom level 15
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

        String busStopCode = null;
        String busStopName = null;
        String busServices = "Services: ";

        // Find the clicked bus stop
        for (BusStopsComplete busStop : busStopsCompleteList) {
            LatLng position = new LatLng(busStop.getLatitude(), busStop.getLongitude());

            if (position.equals(marker.getPosition())) {
                busStopCode = busStop.getBusStopCode(); // Bus stop code
                busStopName = busStop.getDescription(); // Bus stop name


                // set title text
                Helper.GetBusStopInfo busStopInfo = new Helper.GetBusStopInfo(getContext(), busStopCode);
                busStopNameTextt = rootView.findViewById(R.id.BusStopNameTextt);
                busStopName = busStopInfo.getDescription();
                busStopNameTextt.setText(busStopName);

                busTimesBookmarksDB = new BusTimesBookmarksDB(getContext());
                // Fetch bus services at this stop
                // Assuming JSONReader.bus_services_at_stop() returns a List of BusServicesAtStop
                List<BusServicesAtStop> busServicesAtStopList = JSONReader.bus_services_at_stop(getContext());

                ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool for efficiency
                List<Future<String[]>> futures = new ArrayList<>();
                for (BusServicesAtStop item : busServicesAtStopList) {
                    if (item.getBusStopCode().equals(busStopCode)) {
                        for (String busService : item.getBusServices()) {
                            String finalBusStopCode = busStopCode;
                            Future<String[]> future = executor.submit(() ->
                                    APIReader.fetchBusArrivals(finalBusStopCode, busService)
                            );
                            futures.add(future);
                            fullPanelList.add(new BusServicePanel(
                                    busService,
                                    new String[]{" ", " ", " "},
                                    busTimesBookmarksDB.doesBusServiceExist(busService)
                            ));
                        }
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        for (int i = 0; i < fullPanelList.size(); i++) {
                            String[] arrivals = futures.get(i).get(); // Blocking call, waits for result
                            fullPanelList.get(i).setAT(arrivals);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


                // Find the corresponding BusServicesAtStop object based on the bus stop code
                for (BusServicesAtStop serviceAtStop : busServicesAtStopList) {
                    if (serviceAtStop.getBusStopCode().equals(busStopCode)) {
                        List<String> services = serviceAtStop.getBusServices();
                        busServices += String.join(", ", services); // Add services to the string
                        break; // Once we find the matching bus stop code, no need to continue the loop
                    }
                }
                break; // Exit the outer loop once we find the clicked bus stop
            }
        }

        if (busStopCode != null && busStopName != null) {
            // Show bus stop code, name & services using a Toast
            String toastMessage = "Bus Stop Code: " + busStopCode + "\n" +
                    "Bus Stop Name: " + busStopName + "\n" +
                    busServices;
            Toast.makeText(getContext(), toastMessage, Toast.LENGTH_LONG).show();
        }

        // Move camera to clicked marker
        mapView.moveCamera(marker.getPosition(), 15f);
        openBTPopup(true);

    }

    private void manageTheme() {
        TextView BUS = rootView.findViewById(R.id.BUS);
        TextView ROUTE = rootView.findViewById(R.id.ROUTE);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            backButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            busStopNameTextt.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
            backButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
            busStopNameTextt.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
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
