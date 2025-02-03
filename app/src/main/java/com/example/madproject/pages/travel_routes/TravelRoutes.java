package com.example.madproject.pages.travel_routes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.madproject.R;

public class TravelRoutes extends Fragment {

    View rootView;
    LocationData fromData = new LocationData("", null, null);
    LocationData toData = new LocationData("", null, null);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_travelroutes, container, false);
        Button fromButton = rootView.findViewById(R.id.TRFromButton);
        fromButton.setOnClickListener(view -> {onSearch("from");});
        Button toButton = rootView.findViewById(R.id.TRToButton);
        toButton.setOnClickListener(view -> {onSearch("to");});
        Button findRouteButton = rootView.findViewById(R.id.FindRouteButton);
        findRouteButton.setOnClickListener(view -> onFindRoute());
        unloadData();
        return rootView;
    }
    private void unloadData() {
        // get data
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            String searchbarType = bundle.getString("searchbarType");
            String name = bundle.getString("name");
            String lat = bundle.getString("lat");
            String lon = bundle.getString("lon");
            Log.d("searchbarType",searchbarType);
            Log.d("name",name);
            if (searchbarType.equals("from")) {
                fromData.name = name;
                fromData.lat = lat;
                fromData.lon = lon;
            } else {
                toData.name = name;
                toData.lat = lat;
                toData.lon = lon;
            }
            updateView();
            checkForCompletion();
        });
    }
    private void updateView() {
        TextView fromText = rootView.findViewById(R.id.FromText);
        TextView toText = rootView.findViewById(R.id.ToText);
        int hintGray = getResources().getColor(R.color.hintGray);
        int nyoomYellow = getResources().getColor(R.color.nyoomYellow);
        int nyoomBlue = getResources().getColor(R.color.nyoomBlue);
        if (fromData.getName().equals("")) {
            // default
            fromText.setText("From...");
            fromText.setTextColor(hintGray);
        } else {
            // found
            fromText.setText(fromData.getName());
            fromText.setTextColor(nyoomYellow);
        }
        if (toData.getName().equals("")) {
            // default
            toText.setText("To Where?");
            toText.setTextColor(hintGray);
        } else {
            // found
            toText.setText(toData.getName());
            toText.setTextColor(nyoomBlue);
        }
    }
    private void checkForCompletion() {
        Button findRouteButton = rootView.findViewById(R.id.FindRouteButton);
        if (!fromData.getName().equals("") && !toData.getName().equals("")) { // both filled
            findRouteButton.setVisibility(View.VISIBLE);
        } else {
            findRouteButton.setVisibility(View.GONE);
        }
    }
    private void onSearch(String searchbarType) {
        Fragment selectedFragment = new TRSearch();

        Bundle bundle = new Bundle();
        bundle.putString("searchbarType", searchbarType);
        selectedFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slidefade_in_right,  // Enter animation
                        R.anim.slidefade_out_left,  // Exit animation
                        R.anim.slidefade_in_left,   // Pop enter animation (when fragment is re-added)
                        R.anim.slidefade_out_right  // Pop exit animation (when fragment is removed)
                )
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("TravelRoutes")
                .commit();
    }
    private void onFindRoute() {
        Fragment selectedFragment = new RouteView();

        Bundle bundle = new Bundle();
        bundle.putString("idk", "");
        selectedFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                /*.setCustomAnimations(
                        do fade animation later
                )*/
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("TravelRoutes")
                .commit();
    }
}