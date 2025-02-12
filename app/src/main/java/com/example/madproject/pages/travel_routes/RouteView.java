package com.example.madproject.pages.travel_routes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.madproject.R;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteApi;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteClient;
import com.example.madproject.helper.API.OnemapRoute.OnemapRouteResponse;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.Helper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteView extends Fragment {
    Button backButton;
    TextView start;
    TextView end;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_routeview, container, false);
        backButton = rootView.findViewById(R.id.ReturnButton3);
        backButton.setOnClickListener(view -> {getParentFragmentManager().popBackStack();});
        start = rootView.findViewById(R.id.routeStart);
        end = rootView.findViewById(R.id.routeEnd);
        assert getArguments() != null;
        String lat1 = getArguments().getString("lat1");
        String lon1 = getArguments().getString("lon1");
        String name1 = getArguments().getString("name1");
        String lat2 = getArguments().getString("lat2");
        String lon2 = getArguments().getString("lon2");
        String name2 = getArguments().getString("name2");
        String startCoords = "";
        String endCoords = "";
        String date = "";
        String time = "";
        start.setText(name1);
        end.setText(name2);
        Log.d("RouteView.java", name1 + lat1 + ", " + lon1);
        Log.d("RouteView.java", name2 + lat2 + ", " + lon2);
        OnemapRouteApi onemapRouteApi = OnemapRouteClient.getApiService();
        onemapRouteApi.getRouteResults(APIReader.getAPIKey(), startCoords, endCoords, "pt", date, time, "TRANSIT", "2")
            .enqueue(new Callback<OnemapRouteResponse>() {
                @Override
                public void onResponse(Call<OnemapRouteResponse> call, Response<OnemapRouteResponse> response) {
                    if (response.isSuccessful()) {
                        new RouteViewAdapter(response;
                    }
                }

                @Override
                public void onFailure(Call<OnemapRouteResponse> call, Throwable t) {
                    Log.e("Failed to load data: ", t.getMessage());
                }
            });


        return rootView;
    }

    private void onBack() {

    }

    static class RouteViewAdapter extends RecyclerView.Adapter<RouteViewAdapter.ViewHolder> {
        private List<OnemapRouteResponse.Itinerary> itineraries;

        public RouteViewAdapter(List<OnemapRouteResponse.Itinerary> itineraries) {
            this.itineraries = itineraries;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OnemapRouteResponse.Itinerary itinerary = itineraries.get(position);
            holder.routeTextView.setText("Duration: " + itinerary.getDuration() + " seconds");
        }

        @Override
        public int getItemCount() {
            return itineraries.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView routeTextView;

            ViewHolder(View itemView) {
                super(itemView);
                routeTextView = itemView.findViewById(android.R.id.text1);
            }
        }
}