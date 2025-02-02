package com.example.madproject.ui.bus_times;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.madproject.R;
import com.example.madproject.datasets.BusStopsMap;
import com.example.madproject.helper.Helper;
import com.example.madproject.helper.JSONReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class BusStopsList extends Fragment {

    String busService; // bus stop code of bus stop
    List<BusStopPanel> fullPanelList = new ArrayList<>(); // list of panel data
    ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busstopslist, container, false);
        // views setup
        RecyclerView busStopPanels = rootView.findViewById(R.id.BusStopsRW);
        busStopPanels.setLayoutManager(new GridLayoutManager(getContext(), 1));
        Button backButton = rootView.findViewById(R.id.ReturnButton1);
        backButton.setOnClickListener(view -> { goBack(); });
        // get input params
        Bundle bundle = getArguments();
        busService = bundle.getString("value");
        // set title text
        TextView busServiceText = rootView.findViewById(R.id.BusServiceText);
        busServiceText.setText(busService);
        // Read from datasets
        List<BusStopsMap> busStopsMapList = JSONReader.busstops_map(getContext());
        // async
        ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool for efficiency
        List<Future<String[]>> futures = new ArrayList<>();
        for (BusStopsMap item : busStopsMapList) {
            if (item.getBusService().equals(busService)) {
                for (BusStopsMap.BusStopInfo busStopData : item.getDirection1List()) {
                    Future<String[]> future = executor.submit(() ->
                            Helper.fetchBusArrivals(busStopData.getBusStopCode(), busService)
                    );
                    futures.add(future);
                    Helper.GetBusStopInfo busStopInfo = new Helper.GetBusStopInfo(getContext(), busStopData.getBusStopCode());
                    fullPanelList.add(new BusStopPanel(
                            busStopInfo.getDescription(),
                            busStopData.getBusStopCode(),
                            busStopInfo.getRoadName(),
                            new String[]{"-", "-", "-"},
                            false
                    ));
                }
            }
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                for (int i = 0; i < fullPanelList.size(); i++) {
                    String[] arrivals = futures.get(i).get(); // Blocking call, waits for result
                    Log.d("futures.get(i).get()", Arrays.toString(futures.get(i).get()));
                    if (arrivals != null) {
                        fullPanelList.get(i).setAT(arrivals);
                    } else {
                        fullPanelList.get(i).setAT(null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // Shutdown executor to prevent memory leaks
        executor.shutdown();
        // adapter setup
        adapter = new ItemAdapter(fullPanelList, position -> onPanelClick(position));
        busStopPanels.setAdapter(adapter);
        return rootView;
    }

    // adapter for recycler view
    public static class ItemAdapter extends RecyclerView.Adapter<BusStopsList.ItemAdapter.ItemViewHolder> {
        private List<BusStopPanel> panelList;
        private BusStopsList.ItemAdapter.OnItemClickListener listener;

        public ItemAdapter(List<BusStopPanel> panelList, BusStopsList.ItemAdapter.OnItemClickListener listener) {
            // constructor
            this.panelList = panelList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        // changes layout view to our version
        @Override
        public BusStopsList.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_panel, parent, false);
            return new BusStopsList.ItemAdapter.ItemViewHolder(view);
        }
        // changes what each item of recyclerview's value should be
        @Override
        public void onBindViewHolder(BusStopsList.ItemAdapter.ItemViewHolder holder, int position) {
            BusStopPanel item = panelList.get(position);
            holder.busStopName.setText(item.getBusStopName());
            holder.busStopCode.setText(item.getBusStopCode());
            holder.streetName.setText(item.getStreetName());
            if (item.getAT() != null) {
                holder.AT1.setText(item.getAT()[0]);
                holder.AT2.setText(item.getAT()[1]);
                holder.AT3.setText(item.getAT()[2]);
            } else {
                holder.unavailableText.setVisibility(View.VISIBLE);
                holder.AT1.setVisibility(View.INVISIBLE);
                holder.AT2.setVisibility(View.INVISIBLE);
                holder.AT3.setVisibility(View.INVISIBLE);
                holder.MINS.setVisibility(View.INVISIBLE);
            }
        }
        // overrides size of recyclerview
        @Override
        public int getItemCount() {
            return panelList.size();
        }
        // contains the reference of views (UI) of a single item in recyclerview
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView busStopName, busStopCode, streetName, AT1, AT2, AT3, MINS, unavailableText;
            public ItemViewHolder(View itemView) {
                super(itemView);
                busStopName = itemView.findViewById(R.id.BusStopName);
                busStopCode = itemView.findViewById(R.id.BusStopCode);
                streetName = itemView.findViewById(R.id.RoadName);
                AT1 = itemView.findViewById(R.id.AT1b);
                AT2 = itemView.findViewById(R.id.AT2b);
                AT3 = itemView.findViewById(R.id.AT3b);
                MINS = itemView.findViewById(R.id.MINS);
                unavailableText = itemView.findViewById(R.id.UnavailableText);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private void onPanelClick(int position) { // move to BusStopsList
        Fragment selectedFragment = new BusServicesList();
        Bundle bundle = new Bundle();
        bundle.putString("value", fullPanelList.get(position).getBusStopCode());
        selectedFragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack(null) // allows for backing
                .commit();
    }
    private void goBack() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_left,  // Enter animation for the fragment being revealed
                        R.anim.slide_out_right // Exit animation for the current fragment
                )
                .commit();
        fragmentManager.popBackStack("BusTimes", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
