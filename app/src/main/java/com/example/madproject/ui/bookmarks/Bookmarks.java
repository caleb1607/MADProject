package com.example.madproject.ui.bookmarks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
import com.example.madproject.helper.Helper;
import com.example.madproject.helper.JSONReader;
import com.example.madproject.ui.bus_times.BusServicePanel;
import com.example.madproject.ui.bus_times.BusServicesList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Bookmarks extends Fragment {

    List<BookmarkPanel> fullPanelList = new ArrayList<>(); // list of panel data
    Bookmarks.ItemAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        // views setup
        RecyclerView bookmarksPanels = rootView.findViewById(R.id.BookmarksRW);
        bookmarksPanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // read from SQLite
        List<List<String>> sqlitedata = Arrays.asList( // THIS SHIT IS TEMPORARY
                Arrays.asList("1", "Orchard Road", "12345", "Bus 190"),
                Arrays.asList("2", "Marina Bay", "67890", "Bus 97"),
                Arrays.asList("3", "Jurong East", "54321", "Bus 333")
        );
        // async
        // CONTINUE HHERE
        ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool for efficiency
        List<Future<String[]>> futures = new ArrayList<>();
        for (BusServicesAtStop item : busServicesAtStopList) {
            if (item.getBusStopCode().equals(busStopCode)) {
                for (String busService : item.getBusServices()) {
                    Future<String[]> future = executor.submit(() ->
                            Helper.fetchBusArrivals(busStopCode, busService)
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
        adapter = new BusServicesList.ItemAdapter(fullPanelList, position -> onPanelClick(position));
        busServicePanels.setAdapter(adapter);
        return rootView;
    }
}