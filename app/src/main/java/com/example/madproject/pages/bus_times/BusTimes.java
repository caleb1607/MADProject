package com.example.madproject.pages.bus_times;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.madproject.R;
import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.helper.JSONReader;

public class BusTimes extends Fragment {

    RecyclerView searchResultsRV;
    // widgets
    View rootView;
    Button viewMapButton;
    Button busServicesButton;
    Button busStopsButton;
    EditText searchBar;
    String query = "";
    // variables
    List<BTSearchResultItem> searchResultList = new ArrayList<>();
    ItemAdapter adapter = new ItemAdapter(searchResultList, position -> {
        transaction(position);
    });
    List<String> busServicesList;
    List<BusStopsComplete> busStopsList;
    Boolean includeBusServices = true;
    Boolean includeBusStops = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bustimes, container, false);
        // views setup
        viewMapButton = rootView.findViewById(R.id.ViewMapButton);
        viewMapButton.setOnClickListener(onViewMap);
        busServicesButton = rootView.findViewById(R.id.BusServicesButton);
        busServicesButton.setOnClickListener(toggleBusServicesFilter);
        busStopsButton = rootView.findViewById(R.id.BusStopsButton);
        busStopsButton.setOnClickListener(toggleBusStopsFilter);
        searchBar = rootView.findViewById(R.id.SearchBar);
        searchBar.addTextChangedListener(SearchBarTextWatcher());
        searchResultsRV = rootView.findViewById(R.id.BusTimesRV);
        searchResultsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultsRV.setAdapter(adapter);
        // Read from datasets
        busServicesList = JSONReader.bus_services(getContext());
        busStopsList = JSONReader.bus_stops_complete(getContext());
        return rootView;
    }

    public void onSearch() {
        searchResultList.clear();
        if (!query.equals("")) {
            if (includeBusServices) {
                for (String item : busServicesList) {
                    if (item.toLowerCase().trim().contains(query.toLowerCase().trim())) {
                        searchResultList.add(new BTSearchResultItem(
                                "busService",
                                item,
                                item,
                                "Bus Service",
                                ""
                        ));
                    }
                }
            }
            if (includeBusStops) {
                for (BusStopsComplete item : busStopsList) {
                    if (
                            item.getBusStopCode().toLowerCase().trim().contains(query.toLowerCase().trim())
                                    || item.getDescription().toLowerCase().trim().contains(query.toLowerCase().trim())
                    ) {
                        searchResultList.add(new BTSearchResultItem(
                                "busStop",
                                item.getBusStopCode(),
                                item.getDescription(),
                                item.getBusStopCode(),
                                item.getRoadName()
                        ));
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private View.OnClickListener onViewMap = view -> {
        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slidefade_in_right,
                        R.anim.slidefade_out_left,
                        R.anim.slidefade_in_left,
                        R.anim.slidefade_out_right
                )
                .addToBackStack("BusTimes")
                .replace(R.id.fragment_container, new BTMap())
                .commit();
    };
    private View.OnClickListener toggleBusServicesFilter = view -> {
        includeBusServices = true;
        busServicesButton.setAlpha(1f);
        busServicesButton.setTypeface(null, Typeface.BOLD);
        if (includeBusStops) { // true
            includeBusStops = false;
            busStopsButton.setAlpha(0.4f);
            busStopsButton.setTypeface(null, Typeface.NORMAL);
        } else { // false
            includeBusStops = true;
            busStopsButton.setAlpha(0.8f);
            busServicesButton.setAlpha(0.8f);
            busServicesButton.setTypeface(null, Typeface.NORMAL);
        }
        onSearch();
    };
    private View.OnClickListener toggleBusStopsFilter = view -> {
        includeBusStops = true;
        busStopsButton.setAlpha(1f);
        busStopsButton.setTypeface(null, Typeface.BOLD);
        if (includeBusServices) { // true
            includeBusServices = false;
            busServicesButton.setAlpha(0.4f);
            busServicesButton.setTypeface(null, Typeface.NORMAL);
        } else { // false
            includeBusServices = true;
            busServicesButton.setAlpha(0.8f);
            busStopsButton.setAlpha(0.8f);
            busStopsButton.setTypeface(null, Typeface.NORMAL);
        }
        onSearch();
    };
    private TextWatcher SearchBarTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                query = charSequence.toString();
                onSearch();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private List<BTSearchResultItem> itemList;
        private OnItemClickListener listener;

        public ItemAdapter(List<BTSearchResultItem> itemList, OnItemClickListener listener) {

            this.itemList = itemList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bustimes_searchresult_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            BTSearchResultItem item = itemList.get(position);
            if (item.getType().equals("busService"))
                    holder.BUS.setVisibility(View.VISIBLE);
            else // busStop
                    holder.BUS.setVisibility(View.GONE);
            holder.header.setText(item.getHeader());
            holder.subheader1.setText(item.getSubheader1());
            holder.subheader2.setText(item.getSubheader2());
            holder.icon.setImageResource(
                    item.getType().equals("busService") ? R.drawable.bus : R.drawable.bus_stop_icon
            );
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView BUS, header, subheader1, subheader2;
            ImageView icon;
            public ItemViewHolder(View itemView) {
                super(itemView);
                BUS = itemView.findViewById(R.id.BUS3);
                header = itemView.findViewById(R.id.header);
                subheader1 = itemView.findViewById(R.id.subheader1);
                subheader2 = itemView.findViewById(R.id.subheader2);
                icon = itemView.findViewById(R.id.TypeIcon);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private void transaction(int position) {
        String type = searchResultList.get(position).getType();
        String value = searchResultList.get(position).getValue();
        Fragment selectedFragment = (type.equals("busService")) ? new BusStopsList() : new BusServicesList();
        BusTimes.ItemAdapter.ItemViewHolder holder = (BusTimes.ItemAdapter.ItemViewHolder) searchResultsRV.findViewHolderForAdapterPosition(position);
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        selectedFragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slidefade_in_right,  // Enter animation
                        R.anim.slidefade_out_left,  // Exit animation
                        R.anim.slidefade_in_left,   // Pop enter animation (when fragment is re-added)
                        R.anim.slidefade_out_right  // Pop exit animation (when fragment is removed)
                )
                .replace(R.id.fragment_container, selectedFragment);
        if (type.equals("busService")) {
            holder.BUS.setTransitionName("BUS");
            holder.header.setTransitionName("BusServiceText");
            transaction
                    .addSharedElement(holder.BUS, "BUS")
                    .addSharedElement(holder.header, "BusServiceText");
        } else { // busStop
            holder.header.setTransitionName("BusStopNameText");
            transaction
                    .addSharedElement(holder.header, "BusStopNameText");
        }
        transaction
                .addToBackStack("BusTimes") // allows for backing
                .commit();
    }
}
