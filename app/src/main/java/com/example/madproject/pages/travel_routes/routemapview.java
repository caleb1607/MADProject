package com.example.madproject.pages.travel_routes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.madproject.MapView;
import com.example.madproject.R;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class routemapview extends Fragment {
    View rootView;
    Button backButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_routemapview, container, false);
        backButton = rootView.findViewById(R.id.ReturnButton7);

        backButton.setOnClickListener(view -> {getParentFragmentManager().popBackStack();});
        loadMapFragment(new MapView());
        updateMap();

        return rootView;
        }

    private void loadMapFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.MapFragmentContainer3, fragment)
                .commit();
    }

    private void updateMap() {
//        FragmentManager fragmentManager = getChildFragmentManager(); // Or getChildFragmentManager() if inside another fragment
//        MapView mapView = (MapView) fragmentManager.findFragmentById(R.id.MapFragmentContainer);
//        // coords manipulation
//        Double fromLat = null;
//        Double fromLon = null;
//        Double toLat = null;
//        Double toLon = null;
//        if (fromData.getLat() != null) {
//            fromLat = Double.parseDouble(fromData.getLat());
//            fromLon = Double.parseDouble(fromData.getLon());
//        }
//        if (toData.getLat() != null) {
//            toLat = Double.parseDouble(toData.getLat());
//            toLon = Double.parseDouble(toData.getLon());
//        }
//        // place markers
////        float[] hsv = new float[3];
////        Color.colorToHSV(ContextCompat.getColor(getContext(), R.color.nyoomYellow), hsv);
////        float nyoomYellow = hsv[0];
////        Color.colorToHSV(ContextCompat.getColor(getContext(), R.color.nyoomBlue), hsv);
////        float nyoomBlue = hsv[0];
//        if (fromLat != null) {
//            mapView.addMarker(
//                    new LatLng(fromLat, fromLon),
//                    fromData.getName(),
//                    BitmapDescriptorFactory.HUE_YELLOW
//            );
//        }
//        if (toLat != null) {
//            mapView.addMarker(
//                    new LatLng(toLat, toLon),
//                    toData.getName(),
//                    BitmapDescriptorFactory.HUE_AZURE
//            );
//        }
//        // position camera
//        mapView.moveCamera(new LatLng(
//                        (fromLat + toLat) / 2,
//                        (fromLon + toLon) / 2
//                ), 11f
//        );
    }
}
