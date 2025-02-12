package com.example.madproject.pages.travel_routes;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.madproject.R;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteApi;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteClient;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteResponse;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchResponse;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.Helper;
import com.example.madproject.pages.bus_times.BusServicesList;
import com.example.madproject.pages.bus_times.BusStopPanel;
import com.example.madproject.pages.bus_times.BusStopsList;
import com.example.madproject.pages.settings.ThemeManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteView extends Fragment {
    Button backButton;
    TextView start;
    TextView end;
    LegAdapter adapter;
    TextView timeTakenV;
    TextView startTimeV;
    TextView endTimeV;
    TextView fareV;
    RecyclerView routeRV;
    List<LegPanel> fullLegList = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routeview, container, false);
        backButton = rootView.findViewById(R.id.ReturnButton3);
        timeTakenV = rootView.findViewById(R.id.timeTaken);
        startTimeV = rootView.findViewById(R.id.startTime);
        endTimeV = rootView.findViewById(R.id.endTime);
        fareV = rootView.findViewById(R.id.fare);
        backButton.setOnClickListener(view -> {getParentFragmentManager().popBackStack();});
        routeRV = rootView.findViewById(R.id.routeViewRV);
        routeRV.setLayoutManager(new LinearLayoutManager(getContext()));
        start = rootView.findViewById(R.id.routeStart);
        end = rootView.findViewById(R.id.routeEnd);
        assert getArguments() != null;
        adapter = new LegAdapter(fullLegList, getContext());
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
        onemapRouteApi.getRouteResults(APIReader.getAPIKey(), startCoords, endCoords, "pt", formattedDate, "15%253A05%253A40", "TRANSIT", "1")
            .enqueue(new Callback<OnemapRouteResponse>() {
                @Override
                public void onResponse(Call<OnemapRouteResponse> call, Response<OnemapRouteResponse> response) {

                    if (response.isSuccessful()) {
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
                            String mode = leg.getMode();
                            String fromLocation = from.getName();
                            String toLocation = to.getName();
                            int durationPerLeg = leg.getDuration();
                            fullLegList.add(new LegPanel(
                                    fromLocation,
                                    mode,
                                    toLocation,
                                    durationPerLeg
                            ));
                        }
                        adapter.notifyDataSetChanged();
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
            holder.fromTextView.setText(item.getFromLocation());
            holder.modeTextView.setText(item.getMode());
            holder.toTextView.setText(item.getToLocation());
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
            TextView fromTextView, modeTextView, toTextView, timeTextView;
            TextView TAKE, TO, MINS;
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
            }
        }
    }
}