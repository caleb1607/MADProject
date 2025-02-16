package com.example.madproject.pages.travel_routes;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.madproject.R;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteApi;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteClient;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteResponse;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.Helper;
import com.example.madproject.pages.settings.ThemeManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteView extends Fragment {

    View rootView;
    Button backButton;
    TextView showOnMapStatic;
    Button showOnMapButton;
    TextView start;
    TextView end;
    LegAdapter adapter;
    TextView timeTakenStatic;
    TextView timeTakenV;
    TextView minutes;
    TextView startTimeStatic;
    TextView startTimeV;
    TextView endTimeStatic;
    TextView endTimeV;
    TextView fareStatic;
    TextView fareV;
    RecyclerView routeRV;
    TextView routeNotFound;
    TextView loading;
    List<LegPanel> fullLegList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_routeview, container, false);

        backButton = rootView.findViewById(R.id.ReturnButton30);
        showOnMapStatic = rootView.findViewById(R.id.showOnMapStatic);
        showOnMapButton = rootView.findViewById(R.id.showOnMapButton);
        timeTakenStatic = rootView.findViewById(R.id.timeTakenStatic);
        timeTakenV = rootView.findViewById(R.id.timeTaken);
        minutes = rootView.findViewById(R.id.min);
        startTimeStatic = rootView.findViewById(R.id.startTimeStatic);
        startTimeV = rootView.findViewById(R.id.startTime);
        endTimeStatic = rootView.findViewById(R.id.endTimeStatic);
        endTimeV = rootView.findViewById(R.id.endTime);
        fareStatic = rootView.findViewById(R.id.fareStatic);
        fareV = rootView.findViewById(R.id.fare);
        routeNotFound = rootView.findViewById(R.id.noRouteFound);
        loading = rootView.findViewById(R.id.loading);
        routeRV = rootView.findViewById(R.id.routeViewRV);
        start = rootView.findViewById(R.id.routeStart);
        end = rootView.findViewById(R.id.routeEnd);

        timeTakenStatic.setVisibility(View.GONE);
        timeTakenV.setVisibility(View.GONE);
        minutes.setVisibility(View.GONE);
        startTimeStatic.setVisibility(View.GONE);
        startTimeV.setVisibility(View.GONE);
        endTimeStatic.setVisibility(View.GONE);
        endTimeV.setVisibility(View.GONE);
        fareStatic.setVisibility(View.GONE);
        fareV.setVisibility(View.GONE);
        routeRV.setVisibility(View.GONE);

        backButton.setOnClickListener(view -> {getParentFragmentManager().popBackStack();});
        showOnMapStatic.setOnClickListener(view -> onMapView());
        showOnMapButton.setOnClickListener(view -> onMapView());
        routeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        assert getArguments() != null;
        adapter = new LegAdapter(fullLegList, getContext());
        routeRV.setAdapter(adapter);
        // manage theme
        manageTheme();

        // input
        String TAG = "RouteView.javaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String lat1 = getArguments().getString("lat1");
        String lon1 = getArguments().getString("lon1");
        String name1 = getArguments().getString("name1");
        String lat2 = getArguments().getString("lat2");
        String lon2 = getArguments().getString("lon2");
        String name2 = getArguments().getString("name2");
        String startCoords = lat1 + "%2C" + lon1;
        String endCoords = lat2 + "%2C" + lon2;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String formattedDate = now.format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = now.format(timeFormatter);
        String encodedTime = "";
        try {
            encodedTime = URLEncoder.encode(formattedTime, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        start.setText(name1);
        end.setText(name2);
        OnemapRouteApi onemapRouteApi = OnemapRouteClient.getApiService();
        onemapRouteApi.getRouteResults(APIReader.getAPIKey(), startCoords, endCoords, "pt", formattedDate, encodedTime, "TRANSIT", "1")
            .enqueue(new Callback<OnemapRouteResponse>() {
                @Override
                public void onResponse(Call<OnemapRouteResponse> call, Response<OnemapRouteResponse> response) {
                    loading.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        timeTakenStatic.setVisibility(View.VISIBLE);
                        timeTakenV.setVisibility(View.VISIBLE);
                        minutes.setVisibility(View.VISIBLE);
                        startTimeStatic.setVisibility(View.VISIBLE);
                        startTimeV.setVisibility(View.VISIBLE);
                        endTimeStatic.setVisibility(View.VISIBLE);
                        endTimeV.setVisibility(View.VISIBLE);
                        fareStatic.setVisibility(View.VISIBLE);
                        fareV.setVisibility(View.VISIBLE);
                        routeRV.setVisibility(View.VISIBLE);
                        OnemapRouteResponse OnemapResponse = response.body();
                        OnemapRouteResponse.Plan plan = OnemapResponse.getPlan();
                        List<OnemapRouteResponse.Itinerary> itinerary = plan.getItineraries();
                        float duration = itinerary.get(0).getDuration();
                        float durationInMin = Math.round(duration/60);
                        String fare = itinerary.get(0).getFare();
                        long startTime = itinerary.get(0).getStartTime();
                        Date date = new Date(startTime);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String startTimeString = sdf.format(date);
                        long endTime = itinerary.get(0).getEndTime();
                        date = new Date(endTime);
                        String endTimeString = sdf.format(date);
                        timeTakenV.setText(String.valueOf(durationInMin));
                        fareV.setText(fare);
                        startTimeV.setText(startTimeString);
                        endTimeV.setText(endTimeString);


                        for (OnemapRouteResponse.Leg leg: itinerary.get(0).getLegs()) {
                            OnemapRouteResponse.from from = leg.getFrom();
                            OnemapRouteResponse.to to = leg.getTo();
                            String route = leg.getRoute();
                            String mode = leg.getMode();
                            String fromLocation = from.getName();
                            int startIndex = from.getStopIndex();
                            String toLocation = to.getName();
                            int endIndex = to.getStopIndex();
                            float durationPerLeg = leg.getDuration();
                            float durationPerLegInMin = Math.round(durationPerLeg/60);
                            double startLat = from.getLat();
                            double startLon = from.getLon();
                            double endLat = to.getLat();
                            double endLon = to.getLon();
                            fullLegList.add(new LegPanel(
                                    fromLocation,
                                    mode,
                                    toLocation,
                                    (int) durationPerLegInMin,
                                    route,
                                    startIndex,
                                    endIndex
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        routeNotFound.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<OnemapRouteResponse> call, Throwable t) {
                    Log.e("Failed to load data: ", t.getMessage());
                }
            });


        return rootView;
    }

    public static class LegAdapter extends RecyclerView.Adapter<RouteView.LegAdapter.ItemViewHolder> {
        private List<LegPanel> panelList;
        private Context context;

        public LegAdapter(
                List<LegPanel> panelList,
                Context context) {
            // constructor
            this.panelList = panelList;
            this.context = context;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        // changes layout view to our version
        @Override
        public RouteView.LegAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leg_panel, parent, false);
            return new RouteView.LegAdapter.ItemViewHolder(view);
        }
        // changes what each item of recyclerview's value should be
        @Override
        public void onBindViewHolder(RouteView.LegAdapter.ItemViewHolder holder, int position) {
            LegPanel item = panelList.get(position);
            holder.fromTextView.setText(Helper.FormatSEARCHVAL(item.getFromLocation()));
            String mode = Helper.FormatSEARCHVAL(item.getMode());
            if (mode.equals("Subway")) {
                holder.modeTextView.setText("MRT");
            } else {
                holder.modeTextView.setText(Helper.FormatSEARCHVAL(item.getMode()));
            }
            if (mode.equals("Walk")) {
                holder.TAKE.setVisibility(View.GONE);
                holder.comma.setVisibility(View.GONE);
                holder.stopsTextView.setVisibility(View.GONE);
                holder.STOPS.setVisibility(View.GONE);
            } else {
                int stopCount = Math.abs(item.getEndIndex() - item.getStartIndex());
                holder.stopsTextView.setText(String.valueOf(stopCount));
            }
            String route = item.getRoute();
            if (route.equals("")) {
                holder.openBracket.setVisibility(View.GONE);
                holder.methodView.setVisibility(View.GONE);
                holder.closeBracket.setVisibility(View.GONE);
            } else {
                holder.methodView.setText(item.getRoute());
            }
            holder.toTextView.setText(Helper.FormatSEARCHVAL(item.getToLocation()));
            holder.timeTextView.setText(String.valueOf(item.getDuration()));
            manageThemeRV(holder); // light mode
        }

        private void manageThemeRV(RouteView.LegAdapter.ItemViewHolder holder) {
            // lighht mode code will go here eventually
            if (ThemeManager.isDarkTheme()) {
                
            } else { // light

            }
        }
        @Override
        public int getItemCount() {
            return panelList.size();
        }
        // contains the reference of views (UI) of a single item in recyclerview
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            LinearLayout panelBG;
            TextView fromTextView, modeTextView, toTextView, timeTextView, methodView, stopsTextView;
            TextView TAKE, TO, MINS, openBracket, closeBracket, comma, STOPS;
            public ItemViewHolder(View itemView) {
                super(itemView);
                //panelBG = itemView.findViewById(R.id.) no name yet
                fromTextView = itemView.findViewById(R.id.FromTextView);
                modeTextView = itemView.findViewById(R.id.ModeTextView);
                toTextView = itemView.findViewById(R.id.ToTextView);
                timeTextView = itemView.findViewById(R.id.TimeTextView);
                TAKE = itemView.findViewById(R.id.TAKE);
                TO = itemView.findViewById(R.id.TO);
                MINS = itemView.findViewById(R.id.MINS);
                methodView = itemView.findViewById(R.id.MethodView);
                openBracket = itemView.findViewById(R.id.openBracket);
                closeBracket = itemView.findViewById(R.id.closeBracket);
                comma = itemView.findViewById(R.id.comma);
                stopsTextView = itemView.findViewById(R.id.stopsTextView);
                STOPS = itemView.findViewById(R.id.STOPS);
            }
        }
    }
    private void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.nyoomGreen));
        }
    }

    private void onMapView() {
        Fragment selectedFragment = new routemapview();

        ArrayList<String> coordinates = new ArrayList<>();
        coordinates.add("1.320981,103.844150"); // Example coordinates
        coordinates.add("1.326762,103.855900");

        // Create a bundle to pass data
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("coordinates_list", coordinates);
        selectedFragment.setArguments(bundle);

        // Create an instance of FragmentMapView and set arguments
        getParentFragmentManager()
                .beginTransaction()
                /*.setCustomAnimations(
                        do fade animation later
                )*/
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("RouteView")
                .commit();
    }
}