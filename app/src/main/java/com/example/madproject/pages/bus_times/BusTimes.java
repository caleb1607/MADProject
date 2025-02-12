package com.example.madproject.pages.bus_times;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
import com.example.madproject.pages.settings.ThemeManager;

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
    ItemAdapter adapter;
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
        adapter = new ItemAdapter(searchResultList, position -> {
            transaction(position);
        }, getContext());
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
        // manage theme
        manageTheme();
        // Read from datasets
        busServicesList = JSONReader.bus_services(getContext());
        busStopsList = JSONReader.bus_stops_complete(getContext());
        return rootView;
    }

    private void manageTheme() {
        ImageView MAPICON = rootView.findViewById(R.id.MAPICON);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            MAPICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            viewMapButton.setTextColor(getResources().getColor(R.color.white));
            viewMapButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.buttonPanel));
            busServicesButton.setTextColor(getResources().getColor(R.color.white));
            busServicesButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.buttonPanel));
            busStopsButton.setTextColor(getResources().getColor(R.color.white));
            busStopsButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.buttonPanel));
            searchBar.setTextColor(getResources().getColor(R.color.white));
            searchBar.setHintTextColor(getResources().getColor(R.color.hintGray));
            searchBar.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.backgroundPanel));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.nyoomBlue));
            MAPICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
            viewMapButton.setTextColor(getResources().getColor(R.color.black));
            viewMapButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbuttonPanel));
            busServicesButton.setTextColor(getResources().getColor(R.color.black));
            busServicesButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbuttonPanel));
            busStopsButton.setTextColor(getResources().getColor(R.color.black));
            busStopsButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbuttonPanel));
            searchBar.setTextColor(getResources().getColor(R.color.black));
            searchBar.setHintTextColor(getResources().getColor(R.color.LhintGray));
            searchBar.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbackgroundPanel));
        }
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
        private Context context;

        public ItemAdapter(List<BTSearchResultItem> itemList, OnItemClickListener listener, Context context) {
            this.itemList = itemList;
            this.listener = listener;
            this.context = context;
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
            manageThemeRV(holder);
        }

        private void manageThemeRV(ItemViewHolder holder) {
            if (ThemeManager.isDarkTheme()) {
                holder.BTSRCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.backgroundPanel));
                holder.icon.setImageTintList(ContextCompat.getColorStateList(context, R.color.hintGray));
                holder.bookmarkButton.setImageTintList(ContextCompat.getColorStateList(context, R.color.buttonPanel));
                holder.BUS.setTextColor(ContextCompat.getColorStateList(context, R.color.nyoomYellow));
                holder.header.setTextColor(ContextCompat.getColorStateList(context, R.color.nyoomYellow));
            } else { // light
                holder.BTSRCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.LbackgroundPanel));
                holder.icon.setImageTintList(ContextCompat.getColorStateList(context, R.color.LhintGray));
                holder.bookmarkButton.setImageTintList(ContextCompat.getColorStateList(context, R.color.LbuttonPanel));
                holder.BUS.setTextColor(ContextCompat.getColorStateList(context, R.color.nyoomDarkYellow));
                holder.header.setTextColor(ContextCompat.getColorStateList(context, R.color.nyoomDarkYellow));

            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView BUS, header, subheader1, subheader2;
            CardView BTSRCardView;
            ImageView icon, bookmarkButton;
            public ItemViewHolder(View itemView) {
                super(itemView);
                BUS = itemView.findViewById(R.id.BUS3);
                header = itemView.findViewById(R.id.header);
                subheader1 = itemView.findViewById(R.id.subheader1);
                subheader2 = itemView.findViewById(R.id.subheader2);
                BTSRCardView = itemView.findViewById(R.id.BTSRCardView);
                icon = itemView.findViewById(R.id.TypeIcon);
                bookmarkButton = itemView.findViewById(R.id.BookmarkButton2);
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
