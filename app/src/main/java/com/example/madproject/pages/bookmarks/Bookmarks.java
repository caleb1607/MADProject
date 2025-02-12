package com.example.madproject.pages.bookmarks;



import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.madproject.helper.BusTimesBookmarksDB;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
    TextView bookmarkStopName;
    FrameLayout ConfirmationPopup;
    FrameLayout sadBookmark;
    static List<BookmarkPanel> fullPanelList = new ArrayList<>(); // list of panel data
    BusTimesBookmarksDB busTimesBookmarks;
    private boolean isDataLoaded = false;
    Bookmarks.ItemAdapter adapter;
    boolean confirmed;
    int bookmarkPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        RecyclerView bookmarksPanels = rootView.findViewById(R.id.BookmarksRV);
        ConfirmationPopup = rootView.findViewById(R.id.ConfirmationPopup);
        ConfirmationPopup.setVisibility(View.GONE);
        Button ConfirmationButton = rootView.findViewById(R.id.ConfirmationButton);
        ConfirmationButton.setOnClickListener(view -> onDeletionConfirmed());
        ImageView CancellationButton = rootView.findViewById(R.id.CancellationButton);
        CancellationButton.setOnClickListener(view -> onDeletionCancelled());
        sadBookmark = rootView.findViewById(R.id.SadBookmark);
        bookmarksPanels.setLayoutManager(new GridLayoutManager(getContext(), 2));
        busTimesBookmarks = new BusTimesBookmarksDB(getContext());
        bookmarkStopName = rootView.findViewById(R.id.BookmarkStopName);
        // manage theme
        manageTheme();
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

        // adapter setup
        adapter = new Bookmarks.ItemAdapter(fullPanelList, position -> onBookmarkClick(position), getContext());
        bookmarksPanels.setAdapter(adapter);

        return rootView;
    }
    private void manageTheme() {
        LinearLayout PopupPanel = rootView.findViewById(R.id.PopupPanel);
        TextView DELETE_BOOKMARK = rootView.findViewById(R.id.DELETE_BOOKMARK);
        TextView YOUR_BOOKMARKS =  rootView.findViewById(R.id.YOUR_BOOKMARKS);
        ImageView BOOKMARK_ICON = rootView.findViewById(R.id.BOOKMARK_ICON);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            PopupPanel.setBackgroundTintList(getResources().getColorStateList(R.color.buttonPanel));
            DELETE_BOOKMARK.setTextColor(getResources().getColor(R.color.hintGray));
            bookmarkStopName.setTextColor(getResources().getColor(R.color.white));
            YOUR_BOOKMARKS.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            BOOKMARK_ICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LnyoomYellow));
            PopupPanel.setBackgroundTintList(getResources().getColorStateList(R.color.LbuttonPanel));
            DELETE_BOOKMARK.setTextColor(getResources().getColor(R.color.LhintGray));
            bookmarkStopName.setTextColor(getResources().getColor(R.color.black));
            YOUR_BOOKMARKS.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            BOOKMARK_ICON.setImageTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
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
            if (!item.getIsBookmarked()) {
                holder.bookmarkIcon.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.darkGray)
                ));
            } else {
                holder.bookmarkIcon.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.nyoomYellow)
                ));
            }
            manageThemeRV(holder);
        }

        private void manageThemeRV(Bookmarks.ItemAdapter.ItemViewHolder holder) {
            if (ThemeManager.isDarkTheme()) {
                holder.BMPCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.backgroundPanel));
                holder.BUS.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.busNumber.setTextColor(ContextCompat.getColor(context, R.color.nyoomYellow));
                holder.busStopName.setTextColor(ContextCompat.getColor(context, R.color.nyoomLightYellow));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.bookmarkIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nyoomYellow));
            } else { // light
                holder.BMPCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.LbackgroundPanel));
                holder.BUS.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
                holder.busNumber.setTextColor(ContextCompat.getColor(context, R.color.black));
                holder.busStopName.setTextColor(ContextCompat.getColor(context, R.color.nyoomDarkYellow));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.LdarkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
                holder.bookmarkIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nyoomYellow));
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
            CardView BMPCardView;
            Button BOOKMARKBTN;
            ImageView bookmarkIcon;
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
                BMPCardView = itemView.findViewById(R.id.BMPCardView);
                RECTANGLE = itemView.findViewById(R.id.RECTANGLE);
                BOOKMARKBTN =itemView.findViewById(R.id.BookmarkButton);
                bookmarkIcon = itemView.findViewById(R.id.BookmarkIcon2);


                BOOKMARKBTN.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private void onBookmarkClick(int position) {
        bookmarkPosition = position;
        if (!confirmed) {
            bookmarkStopName.setText(fullPanelList.get(position).getBusStopName());
            ConfirmationPopup.setVisibility(View.VISIBLE);
        } else {
            BookmarkPanel bookmark = fullPanelList.get(position);

            Log.d("SQLite", "Deleting: " + bookmark.getBusStopName() + " (" + bookmark.getBusStopCode() + ")");

            // Remove from SQLite
            busTimesBookmarks.deleteBookmark(bookmark.getBusStopName(), bookmark.getBusStopCode(), bookmark.getBusNumber());

            // Remove from the list and update UI
            fullPanelList.remove(position);
            adapter.notifyItemRemoved(position);

            // set visibility
            sadBookmark.setVisibility(busTimesBookmarks.getAllBookmarks().size() == 0 ? View.VISIBLE : View.INVISIBLE);

            confirmed = false;
        }
    }

    private void onDeletionConfirmed() {
        confirmed = true;
        onBookmarkClick(bookmarkPosition);
        ConfirmationPopup.setVisibility(View.GONE);
    }

    private void onDeletionCancelled() {
        confirmed = false;
        ConfirmationPopup.setVisibility(View.GONE);
    }

    private void loadBookmarks() {
        // Only load the bookmarks if they haven't been loaded yet
        if (!isDataLoaded) {
            List<List<String>> sqlitedata = busTimesBookmarks.getAllBookmarks();
            // set visibility
            sadBookmark.setVisibility(sqlitedata.size() == 0 ? View.VISIBLE : View.INVISIBLE);
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