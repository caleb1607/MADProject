package com.example.madproject.pages.travel_routes;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

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

        ArrayList<LatLng> receivedCoordinates = getArguments().getParcelableArrayList("coordinates");

        if (receivedCoordinates != null) {
            for (LatLng coord : receivedCoordinates) {
                Log.d("Coordinates", "Lat: " + coord.latitude + ", Lon: " + coord.longitude);
            }
        }

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
//        MapView mapView = (MapView) fragmentManager.findFragmentById(R.id.MapFragmentContainer3);
//        Log.d("TAG", "updateMap: " + mapView);
//        ArrayList<LatLng> receivedCoordinates = getArguments().getParcelableArrayList("coordinates_list");
//        ArrayList<String> receivedNames = getArguments().getStringArrayList("nameList");
//        LatLng startCoords = null;
//        LatLng endCoords = null;
//
//        if (receivedCoordinates != null) {
//            startCoords = receivedCoordinates.get(0);
//            endCoords = receivedCoordinates.get(receivedCoordinates.size()-1);
//            for (int i = 0; i < receivedCoordinates.size(); i++) {
//                LatLng coord = receivedCoordinates.get(i);
//                String name = receivedNames.get(i);
//                System.out.println("Index " + i + ": Lat = " + coord.latitude + ", Lon = " + coord.longitude);
//                Log.d("Coordinates", "Lat: " + coord.latitude + ", Lon: " + coord.longitude);
//                mapView.addMarker(
//                        new LatLng(coord.latitude, coord.longitude),
//                        name,
//                        BitmapDescriptorFactory.HUE_YELLOW
//                );
//            }
//            // position camera
//            mapView.moveCamera(new LatLng(
//                            (startCoords.latitude + startCoords.longitude) / 2,
//                            (endCoords.latitude + endCoords.longitude) / 2
//                    ), 11f
//            );
//        }
    }
}
