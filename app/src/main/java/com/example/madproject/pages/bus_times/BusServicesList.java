package com.example.madproject.pages.bus_times;

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
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.Helper;
import com.example.madproject.helper.JSONReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        RecyclerView busServicePanels = rootView.findViewById(R.id.BusServicesRV);
        busServicePanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        Button backButton = rootView.findViewById(R.id.ReturnButton2);
        backButton.setOnClickListener(view -> { goBack(); });
        //backButton.
        // get input params
        Bundle bundle = getArguments();
        busStopCode = bundle.getString("value");
        // set title text
        Helper.GetBusStopInfo busStopInfo = new Helper.GetBusStopInfo(getContext(), busStopCode);
        TextView busStopNameText = rootView.findViewById(R.id.BusStopNameText);
        busStopNameText.setText(busStopInfo.getDescription());
        Log.d("Bundle received:", busStopCode);
        // Read from datasets
        List<BusServicesAtStop> busServicesAtStopList = JSONReader.bus_services_at_stop(getContext());
        // async
        ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool for efficiency
        List<Future<String[]>> futures = new ArrayList<>();
        for (BusServicesAtStop item : busServicesAtStopList) {
            if (item.getBusStopCode().equals(busStopCode)) {
                for (String busService : item.getBusServices()) {
                    Future<String[]> future = executor.submit(() ->
                            APIReader.fetchBusArrivals(busStopCode, busService)
                    );
                    futures.add(future);
                    fullPanelList.add(new BusServicePanel(
                            busService,
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
            if (item.getAT() != null) {
                if (item.getAT()[0].equals("0")) {
                    holder.NOW.setVisibility(View.VISIBLE);
                    holder.AT1.setVisibility(View.INVISIBLE);
                    holder.MINS.setVisibility(View.INVISIBLE);
                }
                holder.AT1.setText(item.getAT()[0]);
                holder.AT2.setText(item.getAT()[1]);
                holder.AT3.setText(item.getAT()[2]);
            } else {
                holder.unavailableText.setVisibility(View.VISIBLE);
                holder.AT1.setVisibility(View.INVISIBLE);
                holder.AT2.setVisibility(View.INVISIBLE);
                holder.AT3.setVisibility(View.INVISIBLE);
                holder.NOW.setVisibility(View.INVISIBLE);
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
            TextView busNumber, AT1, AT2, AT3, MINS, NOW, unavailableText;
            public ItemViewHolder(View itemView) {
                super(itemView);
                busNumber = itemView.findViewById(R.id.BusNumber);
                AT1 = itemView.findViewById(R.id.AT1a);
                AT2 = itemView.findViewById(R.id.AT2a);
                AT3 = itemView.findViewById(R.id.AT3a);
                MINS = itemView.findViewById(R.id.MINS);
                NOW = itemView.findViewById(R.id.NOWa);
                unavailableText = itemView.findViewById(R.id.UnavailableTexta);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private void onPanelClick(int position) { // move to BusStopsList
        Fragment selectedFragment = new BusStopsList();
        Bundle bundle = new Bundle();
        bundle.putString("value", fullPanelList.get(position).getBusNumber());
        selectedFragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.MapFragmentContainer, selectedFragment)
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