package com.example.madproject.pages.bookmarks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;
import com.example.madproject.helper.APIReader;

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
                Arrays.asList("1", "Woodgrove Pr Sch", "46971", "901"),
                Arrays.asList("2", "Woodlands Int", "46009", "911A"),
                Arrays.asList("3", "Blk 273B", "27459", "185")
        );
        // async
        // CONTINUE HHERE
        ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool for efficiency
        List<Future<String[]>> futures = new ArrayList<>();
        for (List<String> row : sqlitedata) {
            Future<String[]> future = executor.submit(() ->
                    APIReader.fetchBusArrivals(row.get(2), row.get(3))
            );
            futures.add(future);
            fullPanelList.add(new BookmarkPanel(
                    row.get(3),
                    row.get(1),
                    row.get(2),
                    new String[]{"-", "-", "-"},
                    true
            ));
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                for (int i = 0; i < fullPanelList.size(); i++) {
                    String[] arrivals = futures.get(i).get(); // Blocking call, waits for result
                    fullPanelList.get(i).setAT(arrivals);
                    adapter.notifyItemChanged(i); // Update only the changed item
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Shutdown executor to prevent memory leaks
        executor.shutdown();
        // adapter setup
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
                Log.d("item.getAT()2",Arrays.toString(item.getAT()));
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

    }
}