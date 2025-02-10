package com.example.madproject.pages.bookmarks;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.madproject.helper.BusTimesBookmarksDB;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;
import com.example.madproject.helper.APIReader;
import com.example.madproject.pages.bus_times.BusTimes;
import com.example.madproject.pages.settings.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Bookmarks extends Fragment {

    View rootView;
    List<BookmarkPanel> fullPanelList = new ArrayList<>(); // list of panel data
    BusTimesBookmarksDB BusTimesBookmarks;
    Bookmarks.ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        // manage theme
        manageTheme();
        // views setup
        RecyclerView bookmarksPanels = rootView.findViewById(R.id.BookmarksRV);
        bookmarksPanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        BusTimesBookmarks = new BusTimesBookmarksDB(getContext());
        // read from SQLite
         List<List<String>> sqlitedata = BusTimesBookmarks.getAllBookmarks();
//        Arrays.asList( // THIS SHIT IS TEMPORARY
////                Arrays.asList("1", "Woodgrove Pr Sch", "46971", "901"),
////                Arrays.asList("2", "Woodlands Int", "46009", "911A"),
////                Arrays.asList("3", "Blk 273B", "27459", "185")
//        );
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
                    new String[]{" ", " ", " "},
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
        adapter = new Bookmarks.ItemAdapter(fullPanelList, position -> onPanelClick(position), getContext());
        bookmarksPanels.setAdapter(adapter);
        return rootView;
    }

    private void manageTheme() {
        TextView YOUR_BOOKMARKS =  rootView.findViewById(R.id.YOUR_BOOKMARKS);
        ImageView BOOKMARK_ICON = rootView.findViewById(R.id.BOOKMARK_ICON);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            YOUR_BOOKMARKS.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            BOOKMARK_ICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
            YOUR_BOOKMARKS.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            BOOKMARK_ICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.black));

        }
    }

    // adapter for recycler view
    public static class ItemAdapter extends RecyclerView.Adapter<Bookmarks.ItemAdapter.ItemViewHolder> {
        private List<BookmarkPanel> panelList;
        private Bookmarks.ItemAdapter.OnItemClickListener listener;
        private Context context;

        public ItemAdapter(List<BookmarkPanel> panelList, OnItemClickListener listener, Context context) {
            // constructor
            this.panelList = panelList;
            this.listener = listener;
            this.context = context;
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
            manageThemeRV(holder);
        }

        private void manageThemeRV(Bookmarks.ItemAdapter.ItemViewHolder holder) {
            if (ThemeManager.isDarkTheme()) {
                holder.BUS.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.busNumber.setTextColor(ContextCompat.getColor(context, R.color.nyoomYellow));
                holder.busStopName.setTextColor(ContextCompat.getColor(context, R.color.nyoomLightYellow));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
            } else { // light
                holder.BUS.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
                holder.busNumber.setTextColor(ContextCompat.getColor(context, R.color.LnyoomYellow));
                holder.busStopName.setTextColor(ContextCompat.getColor(context, R.color.nyoomDarkYellow));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.LdarkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
            }
        }
        // overrides size of recyclerview
        @Override
        public int getItemCount() {
            return panelList.size();
        }
        // contains the reference of views (UI) of a single item in recyclerview
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView busNumber, busStopName, AT1, AT2, AT3, MINS, NOW, unavailableText, BUS, ARRIVING_IN;
            View RECTANGLE;
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
                BUS = itemView.findViewById(R.id.BUS);
                ARRIVING_IN = itemView.findViewById(R.id.ARRIVING_IN);
                RECTANGLE = itemView.findViewById(R.id.RECTANGLE);
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