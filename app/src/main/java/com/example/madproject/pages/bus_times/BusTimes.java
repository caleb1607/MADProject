package com.example.madproject.pages.bus_times;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
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
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.JSONReader;

public class BusTimes extends Fragment {

    // widgets
    Button viewMapButton;
    Button busServicesButton;
    Button busStopsButton;
    EditText searchBar;
    String query = "";
    // variables
    List<BSSearchResultItem> searchResultList = new ArrayList<>();
    ItemAdapter adapter = new ItemAdapter(searchResultList, position -> {
        transaction(searchResultList.get(position).getType(), searchResultList.get(position).getValue());
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
        View rootView = inflater.inflate(R.layout.fragment_busarrivaltimes, container, false);
        // views setup
        viewMapButton = rootView.findViewById(R.id.ViewMapButton);
        viewMapButton.setOnClickListener(onViewMap);
        busServicesButton = rootView.findViewById(R.id.BusServicesButton);
        busServicesButton.setOnClickListener(toggleBusServicesFilter);
        busStopsButton = rootView.findViewById(R.id.BusStopsButton);
        busStopsButton.setOnClickListener(toggleBusStopsFilter);
        searchBar = rootView.findViewById(R.id.SearchBar);
        searchBar.addTextChangedListener(SearchBarTextWatcher());
        RecyclerView searchResults = rootView.findViewById(R.id.BusTimesRV);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResults.setAdapter(adapter);
        // Read from datasets
        busServicesList = JSONReader.bus_services(getContext());
        busStopsList = JSONReader.bus_stops_complete(getContext());
        return rootView;
    }

    public void onSearch() {
        searchResultList.clear();
        if (query != "") {
            if (includeBusServices) {
                for (String item : busServicesList) {
                    if (item.toLowerCase().trim().contains(query.toLowerCase().trim())) {
                        searchResultList.add(new BSSearchResultItem(
                                "busService",
                                item,
                                "Bus " + item,
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
                        searchResultList.add(new BSSearchResultItem(
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
        Log.d("the key", APIReader.getAPIKey());
    };
    private View.OnClickListener toggleBusServicesFilter = view -> {
        includeBusServices = true;
        busServicesButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
        busServicesButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonPanel)));
        if (includeBusStops) { // true
            includeBusStops = false;
            busStopsButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.hintGray)));
            busStopsButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonPanelPressed)));
        } else { // false
            includeBusStops = true;
            busStopsButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
            busStopsButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonPanel)));
        }
        onSearch();
    };
    private View.OnClickListener toggleBusStopsFilter = view -> {
        includeBusStops = true;
        busStopsButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
        busStopsButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonPanel)));
        if (includeBusServices) { // true
            includeBusServices = false;
            busServicesButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.hintGray)));
            busServicesButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonPanelPressed)));
        } else { // false
            includeBusServices = true;
            busServicesButton.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.white)));
            busServicesButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.buttonPanel)));
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
        private List<BSSearchResultItem> itemList;
        private OnItemClickListener listener;

        public ItemAdapter(List<BSSearchResultItem> itemList, OnItemClickListener listener) {

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
            BSSearchResultItem item = itemList.get(position);
            holder.header.setText(item.getHeader());
            holder.subheader1.setText(item.getSubheader1());
            holder.subheader2.setText(item.getSubheader2());
            holder.icon.setImageResource(
                    item.getType() == "busService" ? R.drawable.bus : R.drawable.bus_stop_icon
            );
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView header, subheader1, subheader2;
            ImageView icon;
            public ItemViewHolder(View itemView) {
                super(itemView);
                header = itemView.findViewById(R.id.BUS);
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

    private void transaction(String type, String value) {
        Fragment selectedFragment = (type == "busService") ? new BusStopsList() : new BusServicesList();
        Bundle bundle = new Bundle();
        bundle.putString("value", value);
        selectedFragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,  // Enter animation
                        R.anim.slide_out_left,  // Exit animation
                        R.anim.slide_in_left,   // Pop enter animation (when fragment is re-added)
                        R.anim.slide_out_right  // Pop exit animation (when fragment is removed)
                )
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("BusTimes") // allows for backing
                .commit();
    }
}
