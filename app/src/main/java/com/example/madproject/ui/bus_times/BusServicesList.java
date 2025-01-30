package com.example.madproject.ui.bus_times;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.helper.JSONReader;

import java.util.ArrayList;
import java.util.List;

public class BusServicesList extends Fragment {

    // widgets
    String type;
    String busStopCode;
    // variables
    List<BusServicePanel> busServiceList = new ArrayList<>();
    ItemAdapter adapter = new ItemAdapter(busServiceList, position -> {
       //
    });
    List<BusServicesAtStop> busServicesAtStopList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busserviceslist, container, false);
        // views setup
        RecyclerView busServicePanels = rootView.findViewById(R.id.BusServicePanels);
        busServicePanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        busServicePanels.setAdapter(adapter);
        // get input params
        Bundle bundle = getArguments();
        busStopCode = bundle.getString("value");
        // Read from datasets
        busServicesAtStopList = JSONReader.bus_services_at_stop(getContext());
        for (BusServicesAtStop item : busServicesAtStopList) {
            if (item.getBusStopCode().equals(busStopCode)) {
                for (String busService : item.getBusServices()) {
                    busServiceList.add(new BusServicePanel(
                            busService,
                            "3",
                            "7",
                            "12",
                            false
                    ));
                }
            }
        }
        return rootView;
    }
    public static class ItemAdapter extends RecyclerView.Adapter<BusServicesList.ItemAdapter.ItemViewHolder> {
        private List<BusServicePanel> itemList;
        private BusServicesList.ItemAdapter.OnItemClickListener listener;

        public ItemAdapter(List<BusServicePanel> itemList, BusServicesList.ItemAdapter.OnItemClickListener listener) {

            this.itemList = itemList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public BusServicesList.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_service_panel, parent, false);
            return new BusServicesList.ItemAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BusServicesList.ItemAdapter.ItemViewHolder holder, int position) {
            BusServicePanel item = itemList.get(position);
            holder.busNumber.setText(item.getBusNumber());
            holder.AT1.setText(item.getAT1());
            holder.AT2.setText(item.getAT2());
            holder.AT3.setText(item.getAT3());
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView busNumber, AT1, AT2, AT3;

            public ItemViewHolder(View itemView) {
                super(itemView);
                busNumber = itemView.findViewById(R.id.BusNumber);
                AT1 = itemView.findViewById(R.id.AT1);
                AT2 = itemView.findViewById(R.id.AT2);
                AT3 = itemView.findViewById(R.id.AT3);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}