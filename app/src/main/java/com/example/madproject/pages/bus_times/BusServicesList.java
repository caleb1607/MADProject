package com.example.madproject.pages.bus_times;

import android.animation.Animator;
import android.content.Context;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madproject.R;
import com.example.madproject.datasets.BusServicesAtStop;
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

public class BusServicesList extends Fragment {

    View rootView;
    RecyclerView busServicePanelsRV;
    Button backButton;
    TextView busStopNameText;
    BusTimesBookmarksDB busTimesBookmarksDB;
    String busStopCode;
    String busStopName;
    List<BusServicePanel> fullPanelList = new ArrayList<>(); // list of panel data
    ItemAdapter adapter;
    static TextView clickedTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_busserviceslist, container, false);
        // views setup
        busServicePanelsRV = rootView.findViewById(R.id.BusServicesRV);
        busServicePanelsRV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        backButton = rootView.findViewById(R.id.ReturnButton2);
        backButton.setOnClickListener(view -> { goBack(); });
        // transition
        Transition transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_textview);
        setSharedElementEnterTransition(transition);
        // get input params
        Bundle bundle = getArguments();
        busStopCode = bundle.getString("value");
        // set title text
        Helper.GetBusStopInfo busStopInfo = new Helper.GetBusStopInfo(getContext(), busStopCode);
        busStopNameText = rootView.findViewById(R.id.BusStopNameText);
        busStopName = busStopInfo.getDescription();
        busStopNameText.setText(busStopName);
        // manage theme
        manageTheme();
        // class
        busTimesBookmarksDB = new BusTimesBookmarksDB(getContext());
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
                            new String[]{" ", " ", " "},
                            busTimesBookmarksDB.doesBusServiceExist(busService)
                    ));
                }
            }
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                for (int i = 0; i < fullPanelList.size(); i++) {
                    String[] arrivals = futures.get(i).get(); // Blocking call, waits for result
                    fullPanelList.get(i).setAT(arrivals);
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
        busServicePanelsRV.setAdapter(adapter);
        return rootView;
    }
    private void manageTheme() {
        TextView BUS =  rootView.findViewById(R.id.BUS);
        TextView ROUTE = rootView.findViewById(R.id.ROUTE);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            backButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            busStopNameText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.LmainBackground));
            backButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.black));
            busStopNameText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
    }
    // adapter for recycler view
    public static class ItemAdapter extends RecyclerView.Adapter<BusServicesList.ItemAdapter.ItemViewHolder> {
        private List<BusServicePanel> panelList;
        private BusServicesList.ItemAdapter.OnItemClickListener clickListener;
        private BusServicesList.ItemAdapter.OnItemClickListener bookmarkClickListener;
        private Context context;

        public ItemAdapter(
                List<BusServicePanel> panelList,
                OnItemClickListener clickListener,
                OnItemClickListener bookmarkClickListener,
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
                holder.bookmarkIcon.setVisibility(View.VISIBLE);
                holder.enabledBookmarkIcon.setVisibility(View.INVISIBLE);
                item.setBMAnimDone(false);
            } else {
                if (!item.bookmarkAnimDone()) {
                    holder.bookmarkIcon.setVisibility(View.INVISIBLE);
                    holder.enabledBookmarkIcon.setVisibility(View.VISIBLE);
                    int cx = holder.enabledBookmarkIcon.getWidth() / 2; // Center horizontally
                    int cy = 0; // Start from the top edge
                    float startRadius = 0f;
                    float endRadius = (float) Math.hypot(holder.enabledBookmarkIcon.getWidth(), holder.enabledBookmarkIcon.getHeight());
                    holder.enabledBookmarkIcon.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            holder.enabledBookmarkIcon.getViewTreeObserver().removeOnPreDrawListener(this);
                            Animator revealAnim = ViewAnimationUtils.createCircularReveal(holder.enabledBookmarkIcon, cx, cy, startRadius, endRadius);
                            revealAnim.setDuration(400);
                            revealAnim.start();
                            return true;
                        }
                    });
                    item.setBMAnimDone(true);
                }
            }
            manageThemeRV(holder);
        }

        private void manageThemeRV(BusServicesList.ItemAdapter.ItemViewHolder holder) {
            if (ThemeManager.isDarkTheme()) {
                holder.BSVPCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.backgroundPanel));
                holder.BUS.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.busNumber.setTextColor(ContextCompat.getColor(context, R.color.nyoomYellow));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.darkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.hintGray));
                holder.bookmarkIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.buttonPanel));
                //holder.enabledBookmarkIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nyoomLightYellow));
            } else { // light
                holder.BSVPCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.LbackgroundPanel));
                holder.BUS.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
                holder.busNumber.setTextColor(ContextCompat.getColor(context, R.color.LnyoomYellow));
                holder.RECTANGLE.setBackgroundColor(ContextCompat.getColor(context, R.color.LdarkGray));
                holder.ARRIVING_IN.setTextColor(ContextCompat.getColor(context, R.color.LhintGray));
                holder.bookmarkIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.LbuttonPanel));
                //holder.enabledBookmarkIcon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nyoomDarkYellow));
            }
        }
        // overrides size of recyclerview
        @Override
        public int getItemCount() {
            return panelList.size();
        }
        // contains the reference of views (UI) of a single item in recyclerview
        public class ItemViewHolder extends RecyclerView.ViewHolder {
            CardView BSVPCardView;
            TextView busNumber, BUS, AT1, AT2, AT3, MINS, NOW, unavailableText, ARRIVING_IN;
            Button bookmarkButton;
            ImageView bookmarkIcon, enabledBookmarkIcon;
            View RECTANGLE;
            public ItemViewHolder(View itemView) {
                super(itemView);
                BSVPCardView = itemView.findViewById(R.id.BSVPCardView);
                busNumber = itemView.findViewById(R.id.BusNumber);
                BUS = itemView.findViewById(R.id.BUS);
                AT1 = itemView.findViewById(R.id.AT1a);
                AT2 = itemView.findViewById(R.id.AT2a);
                AT3 = itemView.findViewById(R.id.AT3a);
                MINS = itemView.findViewById(R.id.MINS);
                NOW = itemView.findViewById(R.id.NOWa);
                unavailableText = itemView.findViewById(R.id.UnavailableTexta);
                ARRIVING_IN = itemView.findViewById(R.id.ARRIVING_IN);
                itemView.setOnClickListener(v -> {
                    if (clickListener != null) {
                        clickedTextView = itemView.findViewById(R.id.BusNumber);
                        clickListener.onItemClick(getAdapterPosition());
                    }
                });
                bookmarkIcon = itemView.findViewById(R.id.BookmarkIcon);
                enabledBookmarkIcon = itemView.findViewById(R.id.EnabledBookmarkIcon);
                bookmarkButton = itemView.findViewById(R.id.BookmarkButton);
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
        Fragment selectedFragment = new BusStopsList();
        ItemAdapter.ItemViewHolder holder = (ItemAdapter.ItemViewHolder) busServicePanelsRV.findViewHolderForAdapterPosition(position);
        holder.busNumber.setTransitionName("BusServiceText");
        holder.BUS.setTransitionName("BUS");
        Bundle bundle = new Bundle();
        bundle.putString("value", fullPanelList.get(position).getBusNumber());
        selectedFragment.setArguments(bundle);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .addSharedElement(holder.BUS, "BUS")
                .addSharedElement(holder.busNumber, "BusServiceText")
                .addToBackStack(null) // allows for backing
                .commit();
    }
    private void onBookmarkClick(int position) {
        String busStopName = this.busStopName;
        String busStopCode = this.busStopCode;
        String busService = fullPanelList.get(position).getBusNumber();
        if (!fullPanelList.get(position).getIsBookmarked()) {
            busTimesBookmarksDB.insert(busStopName,busStopCode, busService);

        } else {
            busTimesBookmarksDB.deleteBookmarkByService(busService);
        }
        fullPanelList.get(position).toggleIsBookmarked();
        adapter.notifyDataSetChanged();
    }
    private void goBack() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                R.anim.fade_in,  // Enter animation for the fragment being revealed
                R.anim.fade_out // Exit animation for the current fragmen
        )
                .addSharedElement(busStopNameText, "BusStopNameText")
        .commit();
        fragmentManager.popBackStack();
    }
}