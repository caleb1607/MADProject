package com.example.madproject;

import androidx.fragment.app.Fragment;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MapView extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<MarkerOptions> markerQueue = new ArrayList<>();
    private List<CameraUpdate> cameraQueue = new ArrayList<>();
    private List<Consumer<Marker>> markerOnClickListenerQueue = new ArrayList<>();
    private List<Consumer<LatLng>> mapOnClickListenerQueue = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (cameraQueue.isEmpty()) {
            // set position to world spawn
            moveCamera(new LatLng(1.3832, 103.819105), 11f);
        }
        for (MarkerOptions markerOptions : markerQueue) {
            mMap.addMarker(markerOptions);
        }
        markerQueue.clear();
        for (CameraUpdate cameraUpdate : cameraQueue) {
            mMap.moveCamera(cameraUpdate);
        }
        cameraQueue.clear();
        for (Consumer<Marker> markerConsumer : markerOnClickListenerQueue) {
            setMarkerOnClickListener(markerConsumer);
        }
        markerOnClickListenerQueue.clear();
        for (Consumer<LatLng> mapConsumer : mapOnClickListenerQueue) {
            setMapOnClickListener(mapConsumer);
        }
        mapOnClickListenerQueue.clear();
    }

    public void addMarker(LatLng coords, String title, float colour) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(coords)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(colour));
        if (mMap == null) {
            markerQueue.add(markerOptions);
        } else {
            mMap.addMarker(markerOptions);
        }
    }
    public void moveCamera(LatLng coords, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, zoom);
        if (mMap == null) {
            cameraQueue.add(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }
    public void setMarkerOnClickListener(Consumer<Marker> consumer) {
        if (mMap == null) {
            markerOnClickListenerQueue.add(consumer);
        } else {
            mMap.setOnMarkerClickListener(marker -> {
                consumer.accept(marker);
                return true;
            });
        }
    }
    public void setMapOnClickListener(Consumer<LatLng> consumer) {
        if (mMap == null) {
            mapOnClickListenerQueue.add(consumer);
        } else {
            mMap.setOnMapClickListener(position -> {
                consumer.accept(position);
            });
        }
    }
}
