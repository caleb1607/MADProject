package com.example.madproject.pages.bookmarks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.madproject.helper.BusTimesBookmarksDB;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;
import com.example.madproject.helper.APIReader;
import com.example.madproject.pages.settings.ThemeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Bookmarks extends Fragment {

    private void manageTheme() {
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
        }
    }

    View rootView;
    static List<BookmarkPanel> fullPanelList = new ArrayList<>(); // list of panel data
    BusTimesBookmarksDB busTimesBookmarks;
    private boolean isDataLoaded = false;
    static Bookmarks.ItemAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        manageTheme();

        RecyclerView bookmarksPanels = rootView.findViewById(R.id.BookmarksRV);
        bookmarksPanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        busTimesBookmarks = new BusTimesBookmarksDB(getContext());

        // Clear any previous data to avoid duplication
        fullPanelList.clear();

        // Ensure bookmarks are only loaded once
        loadBookmarks();

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<String[]>> futures = new ArrayList<>();

        for (BookmarkPanel panel : fullPanelList) {
            Future<String[]> future = executor.submit(() ->
                    APIReader.fetchBusArrivals(panel.getBusStopCode(), panel.getBusNumber())
            );
            futures.add(future);
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                for (int i = 0; i < fullPanelList.size(); i++) {
                    String[] arrivals = futures.get(i).get();
                    fullPanelList.get(i).setAT(arrivals);
                    adapter.notifyItemChanged(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        executor.shutdown();

        // Setup adapter
        adapter = new Bookmarks.ItemAdapter(fullPanelList, position -> onPanelClick(position));
        bookmarksPanels.setAdapter(adapter);

        return rootView;
    }



    // adapter for recycler view
    public static class ItemAdapter extends RecyclerView.Adapter<Bookmarks.ItemAdapter.ItemViewHolder> {
        private List<BookmarkPanel> panelList;
        private Bookmarks.ItemAdapter.OnItemClickListener listener;

        public ItemAdapter(List<BookmarkPanel> panelList, Bookmarks.ItemAdapter.OnItemClickListener listener) {
            // constructor
            this.panelList = panelList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }


        // changes layout view to our version
        @Override
        public Bookmarks.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_panel, parent, false);
            return new Bookmarks.ItemAdapter.ItemViewHolder(view);
        }
        // changes what each item of recyclerview's value should be
        @Override
        public void onBindViewHolder(Bookmarks.ItemAdapter.ItemViewHolder holder, int position) {
            BookmarkPanel item = panelList.get(position);
            holder.busNumber.setText(item.getBusNumber());
            holder.busStopName.setText(item.getBusStopName());
            if (item.getAT() != null) {
                holder.unavailableText.setVisibility(View.INVISIBLE);
                holder.AT1.setVisibility(View.VISIBLE);
                holder.AT2.setVisibility(View.VISIBLE);
                holder.AT3.setVisibility(View.VISIBLE);
                holder.MINS.setVisibility(View.VISIBLE);
                holder.NOW.setVisibility(View.INVISIBLE);
                if (item.getAT()[0].equals("0")) {
                    holder.AT1.setVisibility(View.INVISIBLE);
                    holder.MINS.setVisibility(View.INVISIBLE);
                    holder.NOW.setVisibility(View.VISIBLE);
                }
                holder.AT1.setText(item.getAT()[0]);
                holder.AT2.setText(item.getAT()[1]);
                holder.AT3.setText(item.getAT()[2]);
            } else {
                holder.unavailableText.setVisibility(View.VISIBLE);
                holder.AT1.setVisibility(View.INVISIBLE);
                holder.AT2.setVisibility(View.INVISIBLE);
                holder.AT3.setVisibility(View.INVISIBLE);
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
            TextView busNumber, busStopName, AT1, AT2, AT3, MINS, NOW, unavailableText;
            public ItemViewHolder(View itemView) {
                super(itemView);
                busNumber = itemView.findViewById(R.id.BusNumber);
                busStopName = itemView.findViewById(R.id.BusStopName);
                AT1 = itemView.findViewById(R.id.AT1c);
                AT2 = itemView.findViewById(R.id.AT2c);
                AT3 = itemView.findViewById(R.id.AT3c);
                MINS = itemView.findViewById(R.id.MINS);
                NOW = itemView.findViewById(R.id.NOWc);
                unavailableText = itemView.findViewById(R.id.UnavailableTextc);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private void onPanelClick(int position) {
        BookmarkPanel bookmark = fullPanelList.get(position);

        Log.d("SQLite", "Deleting: " + bookmark.getBusStopName() + " (" + bookmark.getBusStopCode() + ")");

        // Remove from SQLite
        busTimesBookmarks.deleteBookmark(bookmark.getBusStopName(), bookmark.getBusStopCode(), bookmark.getBusNumber());

        // Remove from the list and update UI
        fullPanelList.remove(position);
        adapter.notifyItemRemoved(position);
    }


    private void loadBookmarks() {
        // Only load the bookmarks if they haven't been loaded yet
        if (!isDataLoaded) {
            List<List<String>> sqlitedata = busTimesBookmarks.getAllBookmarks();

            // Clear previous data if any (if you want to refresh the list)
            fullPanelList.clear();

            for (List<String> row : sqlitedata) {
                // Log the data for debugging
                Log.d("SQLite", "BusNumber: " + row.get(3) +
                        ", BusStopName: " + row.get(1) +
                        ", BusStopCode: " + row.get(2));

                // Add data to the fullPanelList
                fullPanelList.add(new BookmarkPanel(
                        row.get(3),
                        row.get(1),
                        row.get(2),
                        new String[]{" ", " ", " "},
                        true
                ));
            }

            // Set the flag to indicate that data has been loaded
            isDataLoaded = true;
        }
    }




}