package com.example.madproject.pages.bus_times;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
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
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.JSONReader;
import com.example.madproject.pages.travel_routes.TRSearch;

public class BusTimes extends Fragment {

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
        rootView = inflater.inflate(R.layout.fragment_busarrivaltimes, container, false);
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
        Log.d("query", query);
        if (!query.equals("")) {
            if (includeBusServices) {
                for (String item : busServicesList) {
                    if (item.toLowerCase().trim().contains(query.toLowerCase().trim())) {
                        searchResultList.add(new BTSearchResultItem(
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
                        R.anim.fade_in,  // Enter animation
                        R.anim.fade_out,  // Exit animation
                        R.anim.slide_in_left,   // Pop enter animation (when fragment is re-added)
                        R.anim.slide_out_right  // Pop exit animation (when fragment is removed)
                )
                .addSharedElement(rootView.findViewById(R.id.HHELLOOO), "sex")
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack("BusTimes") // allows for backing
                .commit();
    }
}
