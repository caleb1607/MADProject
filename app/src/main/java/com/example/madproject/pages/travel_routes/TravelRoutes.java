package com.example.madproject.pages.travel_routes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madproject.MapView;
import com.example.madproject.R;
import com.example.madproject.pages.settings.ThemeManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

public class TravelRoutes extends Fragment {

    Button fromButton;
    Button toButton;
    Button findRouteButton;
    TextView sitText;
    View rootView;
    LocationData fromData = new LocationData("", "",null, null, "");
    LocationData toData = new LocationData("", "",null, null, "");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_travelroutes, container, false);
        // onclick listeners
        fromButton = rootView.findViewById(R.id.TRFromButton);
        fromButton.setOnClickListener(view -> {onSearch("from");});
        toButton = rootView.findViewById(R.id.TRToButton);
        toButton.setOnClickListener(view -> {onSearch("to");});
        findRouteButton = rootView.findViewById(R.id.FindRouteButton);
        findRouteButton.setOnClickListener(view -> onFindRoute());
        // manage theme
        manageTheme();
        // map fragment
        loadMapFragment(new MapView());
        // update data & views
        unloadData();
        return rootView;
    }
    private void manageTheme() {
        FrameLayout fromFL = rootView.findViewById(R.id.FromFL);
        FrameLayout toFL = rootView.findViewById(R.id.ToFL);
        TextView fromText = rootView.findViewById(R.id.FromText);
        TextView toText = rootView.findViewById(R.id.ToText);
        ImageView MARKER_ICON =  rootView.findViewById(R.id.MARKER_ICON);
        TextView FIND_A_ROUTE = rootView.findViewById(R.id.FIND_A_ROUTE);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            fromFL.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.backgroundPanel));
            toFL.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.backgroundPanel));
            fromText.setTextColor(getResources().getColor(R.color.hintGray));
            toText.setTextColor(getResources().getColor(R.color.hintGray));
            MARKER_ICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            FIND_A_ROUTE.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.nyoomGreen));
            fromFL.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbackgroundPanel));
            toFL.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbackgroundPanel));
            fromText.setTextColor(getResources().getColor(R.color.LhintGray));
            toText.setTextColor(getResources().getColor(R.color.LhintGray));
            MARKER_ICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            FIND_A_ROUTE.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }
    private void unloadData() {
        // get data
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            String searchbarType = bundle.getString("searchbarType");
            String name = bundle.getString("name");
            String lat = bundle.getString("lat");
            String lon = bundle.getString("lon");
            String savedQuery = bundle.getString("savedQuery");
            if (searchbarType.equals("from")) {
                if (name != null) {
                    fromData.name = name;
                    fromData.lat = lat;
                    fromData.lon = lon;
                }
                fromData.savedQuery = savedQuery;
            } else {
                if (name != null) {
                    toData.name = name;
                    toData.lat = lat;
                    toData.lon = lon;
                }
                toData.savedQuery = savedQuery;
            }
            updateView();
            updateMap();
            checkForCompletion();
        });
    }
    private void updateView() {
        TextView fromText = rootView.findViewById(R.id.FromText);
        TextView toText = rootView.findViewById(R.id.ToText);
        int hintGray = getResources().getColor((ThemeManager.isDarkTheme()) ? R.color.hintGray : R.color.LhintGray);
        int nyoomYellow = getResources().getColor((ThemeManager.isDarkTheme()) ? R.color.nyoomYellow : R.color.LnyoomYellow);
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
    private void updateMap() {
        FragmentManager fragmentManager = getChildFragmentManager(); // Or getChildFragmentManager() if inside another fragment
        MapView mapView = (MapView) fragmentManager.findFragmentById(R.id.MapFragmentContainer);
        // coords manipulation
        Double fromLat = null;
        Double fromLon = null;
        Double toLat = null;
        Double toLon = null;
        if (fromData.getLat() != null) {
            fromLat = Double.parseDouble(fromData.getLat());
            fromLon = Double.parseDouble(fromData.getLon());
        }
        if (toData.getLat() != null) {
            toLat = Double.parseDouble(toData.getLat());
            toLon = Double.parseDouble(toData.getLon());
        }
        // place markers
//        float[] hsv = new float[3];
//        Color.colorToHSV(ContextCompat.getColor(getContext(), R.color.nyoomYellow), hsv);
//        float nyoomYellow = hsv[0];
//        Color.colorToHSV(ContextCompat.getColor(getContext(), R.color.nyoomBlue), hsv);
//        float nyoomBlue = hsv[0];
        if (fromLat != null) {
            mapView.addMarker(
                    new LatLng(fromLat, fromLon),
                    fromData.getName(),
                    BitmapDescriptorFactory.HUE_YELLOW
            );
        }
        if (toLat != null) {
            mapView.addMarker(
                    new LatLng(toLat, toLon),
                    toData.getName(),
                    BitmapDescriptorFactory.HUE_AZURE
            );
        }
        // position camera
        if (fromData.getLat() != null && toData.getLat() != null) { // both selected // mid point
            mapView.moveCamera(new LatLng(
                    (fromLat + toLat)/2,
                    (fromLon + toLon)/2
                    ), 11f
                    );
        } else if (fromData.getLat() != null && toData.getLat() == null) { // only FROM coords
            mapView.moveCamera(new LatLng(
                    fromLat,
                    fromLon
                    ), 16f
                    );
        } else if (fromData.getLat() == null && toData.getLat() != null) { // only TO coords
            mapView.moveCamera(new LatLng(
                    toLat,
                    toLon
                    ), 16f
                    );
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
        String savedQuery = searchbarType.equals("from") ? fromData.getSavedQuery() : toData.getSavedQuery();
        bundle.putString("savedQuery", savedQuery);
        bundle.putString("searchbarType", searchbarType);
        selectedFragment.setArguments(bundle);

        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slidefade_in_right,
                        R.anim.slidefade_out_left,
                       R.anim.slidefade_in_left,
                        R.anim.slidefade_out_right
                )
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("TravelRoutes")
                .commit();
    }
    private void onFindRoute() {
        Fragment selectedFragment = new RouteView();

        Bundle bundle = new Bundle();
        bundle.putString("lat1", fromData.getLat());
        bundle.putString("lon1", fromData.getLon());
        bundle.putString("name1", fromData.getName());
        bundle.putString("lat2", toData.getLat());
        bundle.putString("lon2", toData.getLon());
        bundle.putString("name2", toData.getName());
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
    private void loadMapFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.MapFragmentContainer, fragment)
                .commit();
    }
}