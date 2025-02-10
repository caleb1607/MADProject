package com.example.madproject.pages.bus_times;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madproject.R;
import com.example.madproject.datasets.BusStopsMap;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.BusTimesBookmarksDB;
import com.example.madproject.helper.Helper;
import com.example.madproject.helper.JSONReader;
import com.example.madproject.pages.settings.ThemeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context; // dont use


public class BusStopsList extends Fragment {

    View rootView;
    RecyclerView busStopPanelsRV;
    Button backButton;
    TextView busServiceText;
    BusTimesBookmarksDB busTimesBookmarksDB;
    String busService; // bus stop code of bus stop
    List<BusStopPanel> fullPanelList = new ArrayList<>(); // list of panel data
    ItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_busstopslist, container, false);
        // views setup
        busStopPanelsRV = rootView.findViewById(R.id.BusStopsRV);
        busStopPanelsRV.setLayoutManager(new GridLayoutManager(getContext(), 1));
        backButton = rootView.findViewById(R.id.ReturnButton3);
        backButton.setOnClickListener(view -> { goBack(); });
        // transition
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_textview);
        setSharedElementEnterTransition(transition);
        // get input params
        Bundle bundle = getArguments();
        busService = bundle.getString("value");
        // set title text
        busServiceText = rootView.findViewById(R.id.BusServiceText);
        busServiceText.setText(busService);
        // manage theme
        manageTheme();
        // class
        busTimesBookmarksDB = new BusTimesBookmarksDB(getContext());
        // Read from datasets
        List<BusStopsMap> busStopsMapList = JSONReader.busstops_map(getContext());

        // async
        ExecutorService executor = Executors.newFixedThreadPool(10); // Use a thread pool for efficiency
        List<Future<String[]>> futures = new ArrayList<>();
        for (BusStopsMap item : busStopsMapList) {
            if (item.getBusService().equals(busService)) {
                for (BusStopsMap.BusStopInfo busStopData : item.getDirection1List()) {
                    Future<String[]> future = executor.submit(() ->
                            APIReader.fetchBusArrivals(busStopData.getBusStopCode(), busService)
                    );
                    futures.add(future);
                    Helper.GetBusStopInfo busStopInfo = new Helper.GetBusStopInfo(getContext(), busStopData.getBusStopCode());
                    fullPanelList.add(new BusStopPanel(
                            busStopInfo.getDescription(),
                            busStopData.getBusStopCode(),
                            busStopInfo.getRoadName(),
                            new String[]{" ", " ", " "},
                            false
                    ));
                }
            }
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
        adapter = new ItemAdapter(fullPanelList,
                position -> onPanelClick(position),
                position -> onBookmarkClick(position),
                getContext());
        busStopPanelsRV.setAdapter(adapter);
        return rootView;
    }
    private void manageTheme() {
        TextView BUS =  rootView.findViewById(R.id.BUS);
        TextView ROUTE = rootView.findViewById(R.id.ROUTE);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            backButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            BUS.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            busServiceText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            ROUTE.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
            backButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
            BUS.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            busServiceText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            ROUTE.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }

    // adapter for recycler view
    public static class ItemAdapter extends RecyclerView.Adapter<BusStopsList.ItemAdapter.ItemViewHolder> {
        private List<BusStopPanel> panelList;
        private BusServicesList.ItemAdapter.OnItemClickListener clickListener;
        private BusServicesList.ItemAdapter.OnItemClickListener bookmarkClickListener;
        private Context context;

        public ItemAdapter(
                List<BusStopPanel> panelList,
                BusServicesList.ItemAdapter.OnItemClickListener clickListener,
                BusServicesList.ItemAdapter.OnItemClickListener bookmarkClickListener,
                Context context) {
            // constructor
            this.panelList = panelList;
            this.clickListener = clickListener;
            this.bookmarkClickListener = bookmarkClickListener;
            this.context = context;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        // changes layout view to our version
        @Override
        public BusStopsList.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_stop_panel, parent, false);
            return new BusStopsList.ItemAdapter.ItemViewHolder(view);
        }
        // changes what each item of recyclerview's value should be
        @Override
        public void onBindViewHolder(BusStopsList.ItemAdapter.ItemViewHolder holder, int position) {
            BusStopPanel item = panelList.get(position);
            holder.busStopName.setText(item.getBusStopName());
            holder.busStopCode.setText(item.getBusStopCode());
            holder.streetName.setText(item.getStreetName());
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
//                int cx = holder.enabledBookmarkIcon.getWidth() / 2; // Center horizontally
//                int cy = 0; // Start from the top edge
//                float startRadius = 0f;
//                float endRadius = (float) Math.hypot(holder.enabledBookmarkIcon.getWidth(), holder.enabledBookmarkIcon.getHeight());
//                Animator revealAnim = ViewAnimationUtils.createCircularReveal(holder.enabledBookmarkIcon, cx, cy, startRadius, endRadius);
//                holder.enabledBookmarkIcon.setVisibility(View.VISIBLE);
//                revealAnim.setDuration(500);
//                revealAnim.start();
                holder.bookmarkIcon.setImageTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.nyoomLightYellow)
                ));
            }
            manageThemeRV(holder);
        }

        private void manageThemeRV(BusStopsList.ItemAdapter.ItemViewHolder holder) {
            if (ThemeManager.isDarkTheme()) {
                holder.BSPCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.backgroundPanel));
                holder.busStopName.setTextColor(ContextCompat.getColor(context, R.color.nyoomYellow));
                holder.streetName.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.busStopCode.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
            } else { // light
                holder.BSPCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.LbackgroundPanel));
                holder.busStopName.setTextColor(ContextCompat.getColor(context, R.color.LnyoomYellow));
                holder.streetName.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
                holder.busStopCode.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
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
            CardView BSPCardView;
            TextView busStopName, busStopCode, streetName, AT1, AT2, AT3, MINS, NOW, unavailableText, ARRIVING_IN;
            Button bookmarkButton;
            ImageView bookmarkIcon, enabledBookmarkIcon;
            View RECTANGLE;
            public ItemViewHolder(View itemView) {
                super(itemView);
                BSPCardView = itemView.findViewById(R.id.BSPCardView);
                busStopName = itemView.findViewById(R.id.BusStopName);
                busStopCode = itemView.findViewById(R.id.BusStopCode);
                streetName = itemView.findViewById(R.id.RoadName);
                AT1 = itemView.findViewById(R.id.AT1b);
                AT2 = itemView.findViewById(R.id.AT2b);
                AT3 = itemView.findViewById(R.id.AT3b);
                MINS = itemView.findViewById(R.id.MINS);
                NOW = itemView.findViewById(R.id.NOWb);
                unavailableText = itemView.findViewById(R.id.UnavailableTextb);
                ARRIVING_IN = itemView.findViewById(R.id.ARRIVING_IN);
                itemView.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickListener.onItemClick(getAdapterPosition());
                    }
                });
                bookmarkIcon = itemView.findViewById(R.id.BookmarkIcon2);
                enabledBookmarkIcon = itemView.findViewById(R.id.EnabledBookmarkIcon2);
                bookmarkButton = itemView.findViewById(R.id.BookmarkButton2);
                bookmarkButton.setOnClickListener(v -> {
                    if (bookmarkClickListener != null) {
                        bookmarkClickListener.onItemClick(getAdapterPosition());
                    }
                });
                RECTANGLE = itemView.findViewById(R.id.RECTANGLE);
            }
        }
    }

    private void onPanelClick(int position) { // move to BusStopsList
        Fragment selectedFragment = new BusServicesList();
        BusStopsList.ItemAdapter.ItemViewHolder holder = (BusStopsList.ItemAdapter.ItemViewHolder) busStopPanelsRV.findViewHolderForAdapterPosition(position);
        holder.busStopName.setTransitionName("BusStopNameText");
        Bundle bundle = new Bundle();
        bundle.putString("value", fullPanelList.get(position).getBusStopCode());
        selectedFragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addSharedElement(holder.busStopName, "BusStopNameText")
                .addToBackStack(null) // allows for backing
                .commit();
    }
    private void onBookmarkClick(int position) {
        String busStopName = fullPanelList.get(position).getBusStopName();
        String busStopCode = fullPanelList.get(position).getBusStopCode();
        String busService = this.busService;
        if (!fullPanelList.get(position).getIsBookmarked()) {
            busTimesBookmarksDB.insert(busStopName, busStopCode, busService);

        } else {
           busTimesBookmarksDB.deleteBookmarksbyBusStop(busStopName, busStopCode);

        }
        fullPanelList.get(position).toggleIsBookmarked();
        adapter.notifyDataSetChanged();
    }
    private void goBack() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slidefade_in_left,  // Enter animation for the fragment being revealed
                        R.anim.slidefade_out_right // Exit animation for the current fragment
                )
                .commit();
        fragmentManager.popBackStack("BusTimes", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}


