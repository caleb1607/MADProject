package com.example.madproject.ui.bus_times;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;

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
        for (BusStopsMap item : busStopsMapList) {
            if (item.getBusService().equals(busService)) {
                for (BusStopsMap.BusStopInfo busStopData : item.getDirection1List()) {
                    Log.d("getBusStopCode", busStopData.getBusStopCode());
                    Helper.GetBusStopInfo busStopInfo = new Helper.GetBusStopInfo(getContext(), busStopData.getBusStopCode());
                    fullPanelList.add(new BusStopPanel(
                            busStopInfo.getDescription(),
                            busStopData.getBusStopCode(),
                            busStopInfo.getRoadName(),
                            new String[]{"3", "7", "12"},
                            false
                    ));
                }
            }
        }
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
            holder.AT1.setText(item.getAT()[0]);
            holder.AT2.setText(item.getAT()[1]);
            holder.AT3.setText(item.getAT()[2]);
        }
        // overrides size of recyclerview
        @Override
        public int getItemCount() {
            return panelList.size();
        }
        // contains the reference of views (UI) of a single item in recyclerview
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView busStopName, busStopCode, streetName, AT1, AT2, AT3;
            public ItemViewHolder(View itemView) {
                super(itemView);
                busStopName = itemView.findViewById(R.id.BusStopName);
                busStopCode = itemView.findViewById(R.id.BusStopCode);
                streetName = itemView.findViewById(R.id.RoadName);
                AT1 = itemView.findViewById(R.id.AT1b);
                AT2 = itemView.findViewById(R.id.AT2b);
                AT3 = itemView.findViewById(R.id.AT3b);
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
        getActivity()
                .getSupportFragmentManager()
                .popBackStack("BusTimes", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}