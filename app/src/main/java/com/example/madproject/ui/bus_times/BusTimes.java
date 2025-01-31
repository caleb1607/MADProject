package com.example.madproject.ui.bus_times;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.example.madproject.R;
import com.example.madproject.datasets.BusStopsComplete;
import com.example.madproject.helper.JSONReader;

public class BusTimes extends Fragment {

    // widgets
    Button viewMapButton;
    EditText searchBar;
    // variables
    List<SearchResultItem> searchResultList = new ArrayList<>();
    ItemAdapter adapter = new ItemAdapter(searchResultList, position -> {
        transaction(searchResultList.get(position).getType(), searchResultList.get(position).getValue());
    });
    List<String> busServicesList;
    List<BusStopsComplete> busStopsList;
    Boolean includeBusServices = Boolean.TRUE;
    Boolean includeBusStops = Boolean.TRUE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busarrivaltimes, container, false);
        // views setup
        viewMapButton = rootView.findViewById(R.id.ViewMapButton);
        viewMapButton.setOnClickListener(onViewMap);
        searchBar = rootView.findViewById(R.id.SearchBar);
        searchBar.addTextChangedListener(SearchBarTextWatcher());
        RecyclerView searchResults = rootView.findViewById(R.id.SearchResults);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResults.setAdapter(adapter);
        // Read from datasets
        busServicesList = JSONReader.bus_services(getContext());
        busStopsList = JSONReader.bus_stops_complete(getContext());
        return rootView;
    }

    public void onSearch(String query) {
        searchResultList.clear();
        if (includeBusServices) {
            for (String item : busServicesList) {
                if (item.toLowerCase().trim().contains(query.toLowerCase().trim())) {
                    searchResultList.add(new SearchResultItem(
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
                    searchResultList.add(new SearchResultItem(
                            "busStop",
                            item.getBusStopCode(),
                            item.getDescription(),
                            item.getBusStopCode(),
                            item.getRoadName()
                    ));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
    private View.OnClickListener onViewMap = view -> {
        // on view map
    };
    private TextWatcher SearchBarTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                onSearch(query);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
        private List<SearchResultItem> itemList;
        private OnItemClickListener listener;

        public ItemAdapter(List<SearchResultItem> itemList, OnItemClickListener listener) {

            this.itemList = itemList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            SearchResultItem item = itemList.get(position);
            holder.header.setText(item.getHeader());
            holder.subheader1.setText(item.getSubheader1());
            holder.subheader2.setText(item.getSubheader2());
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView header, subheader1, subheader2;

            public ItemViewHolder(View itemView) {
                super(itemView);
                header = itemView.findViewById(R.id.BUS);
                subheader1 = itemView.findViewById(R.id.subheader1);
                subheader2 = itemView.findViewById(R.id.subheader2);
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
                .replace(R.id.fragment_container, selectedFragment)
                .addToBackStack(null) // allows for backing
                .commit();
    }
}
