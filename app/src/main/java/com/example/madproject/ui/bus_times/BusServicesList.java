package com.example.madproject.ui.bus_times;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.helper.JSONReader;

import java.util.ArrayList;
import java.util.List;

public class BusServicesList extends Fragment {

    String busStopCode; // bus stop code of bus stop
    List<BusServicePanel> fullPanelList = new ArrayList<>(); // list of panel data
    ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busserviceslist, container, false);
        // views setup
        RecyclerView busServicePanels = rootView.findViewById(R.id.BusServicesRW);
        busServicePanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // get input params
        Bundle bundle = getArguments();
        busStopCode = bundle.getString("value");
        // Read from datasets
        List<BusServicesAtStop> busServicesAtStopList = JSONReader.bus_services_at_stop(getContext());
        for (BusServicesAtStop item : busServicesAtStopList) {
            if (item.getBusStopCode().equals(busStopCode)) {
                for (String busService : item.getBusServices()) {
                    fullPanelList.add(new BusServicePanel(
                            busService,
                            new String[]{"3", "7", "12"},
                            false
                    ));
                }
            }
        }
        // adapter setup
        adapter = new ItemAdapter(fullPanelList, position -> onPanelClick());
        busServicePanels.setAdapter(adapter);
        return rootView;
    }

    // adapter for recycler view
    public static class ItemAdapter extends RecyclerView.Adapter<BusServicesList.ItemAdapter.ItemViewHolder> {
        private List<BusServicePanel> panelList;
        private BusServicesList.ItemAdapter.OnItemClickListener listener;

        public ItemAdapter(List<BusServicePanel> panelList, BusServicesList.ItemAdapter.OnItemClickListener listener) {
            // constructor
            this.panelList = panelList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        // changes layout view to our version
        @Override
        public BusServicesList.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_service_panel, parent, false);
            return new BusServicesList.ItemAdapter.ItemViewHolder(view);
        }
        // changes what each item of recyclerview's value should be
        @Override
        public void onBindViewHolder(BusServicesList.ItemAdapter.ItemViewHolder holder, int position) {
            BusServicePanel item = panelList.get(position);
            holder.busNumber.setText(item.getBusNumber());
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
            TextView busNumber, AT1, AT2, AT3;
            public ItemViewHolder(View itemView) {
                super(itemView);
                busNumber = itemView.findViewById(R.id.BUS);
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

    private void onPanelClick() { // move to BusStopsList
        Fragment selectedFragment = new BusStopsList();
        Bundle bundle = new Bundle();
        bundle.putString("value", busStopCode);
        selectedFragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack(null) // allows for backing
                .commit();
    }
}