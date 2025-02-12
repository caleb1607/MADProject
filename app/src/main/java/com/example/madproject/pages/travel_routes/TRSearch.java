package com.example.madproject.pages.travel_routes;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchApi;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchClient;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchResponse;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.Helper;
import com.example.madproject.pages.bus_times.BusTimes;
import com.example.madproject.pages.settings.ThemeManager;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class TRSearch extends Fragment {

    View rootView;
    // widgets
    EditText TRSearchBar;
    String searchbarType;
    Button returnButton;
    RecyclerView searchResultsRV;
    TextView startTyping;
    ImageView startTypingImage;
    // variables
    static String query = "";
    List<LocationData> searchResultList = new ArrayList<>();
    TRSearch.ItemAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_trsearch, container, false);
        // views setup
        startTyping = rootView.findViewById(R.id.startTyping);
        startTypingImage = rootView.findViewById(R.id.startTypingImage);
        TRSearchBar = rootView.findViewById(R.id.TRSearchBar);
        TRSearchBar.addTextChangedListener(SearchBarTextWatcher());
        searchResultsRV = rootView.findViewById(R.id.TravelRoutesRV);
        searchResultsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        returnButton = rootView.findViewById(R.id.ReturnButton);
        returnButton.setOnClickListener(view -> transaction(-1));
        // manage theme
        manageTheme();// adapter
        adapter = new TRSearch.ItemAdapter(searchResultList, this::transaction, getContext());
        searchResultsRV.setAdapter(adapter);
        // receive bundle
        if (getArguments() != null) {
            searchbarType = getArguments().getString("searchbarType");
            query = getArguments().getString("savedQuery");
            if (query != null) {
                onSearch();
                TRSearchBar.setText(query);
                adapter.notifyDataSetChanged();
            }
        }
        TRSearchBar.requestFocus();
        return rootView;
    }
    private void manageTheme() {
        Button returnButton = rootView.findViewById(R.id.ReturnButton);
        if (ThemeManager.isDarkTheme()) {
            rootView.setBackgroundColor(getResources().getColor(R.color.mainBackground));
            returnButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            TRSearchBar.setTextColor(getResources().getColor(R.color.white));
            TRSearchBar.setHintTextColor(getResources().getColor(R.color.hintGray));
            TRSearchBar.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.backgroundPanel));
            startTyping.setTextColor(getResources().getColor(R.color.hintGray));
            startTypingImage.setImageTintList(getResources().getColorStateList(R.color.hintGray));
        } else { // light
            rootView.setBackgroundColor(getResources().getColor(R.color.nyoomGreen));
            returnButton.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.white));
            TRSearchBar.setTextColor(getResources().getColor(R.color.black));
            TRSearchBar.setHintTextColor(getResources().getColor(R.color.LhintGray));
            TRSearchBar.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.LbackgroundPanel));
            startTyping.setTextColor(getResources().getColor(R.color.white));
            startTypingImage.setImageTintList(getResources().getColorStateList(R.color.white));
        }
    }
    public void onSearch() {
        searchResultList.clear();
        if (query.length() > 0) {
            OnemapSearchApi onemapSearchApi = OnemapSearchClient.getApiService();
            onemapSearchApi.getSearchResults(APIReader.getAPIKey(), "Y", "Y", 1, query)
                .enqueue(new Callback<OnemapSearchResponse>() {
                    @Override
                    public void onResponse(Call<OnemapSearchResponse> call, Response<OnemapSearchResponse> response) {
                        if (response.isSuccessful()) {
                            OnemapSearchResponse onemapResponse = response.body();
                            assert onemapResponse != null;
                            for (OnemapSearchResponse.Result result : onemapResponse.Results) {
                                searchResultList.add(new LocationData(
                                    Helper.FormatSEARCHVAL(result.SEARCHVAL),
                                        result.BLK_NO,
                                        result.LATITUDE,
                                        result.LONGITUDE,
                                        ""
                                ));
                            }
                            Helper.FormatSearchResultList(searchResultList, query);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<OnemapSearchResponse> call, Throwable t) {
                        Log.e("Failed to load data: ", t.getMessage());
                    }
                });
        }
    }
    private TextWatcher SearchBarTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                query = charSequence.toString();
                onSearch();
                if (charSequence.length() > 0) {
                    // hide "start typing" hint
                    startTyping.setVisibility(View.GONE);
                    startTypingImage.setVisibility(View.GONE);
                } else {
                    // show "start typing" hint
                    startTyping.setVisibility(View.VISIBLE);
                    startTypingImage.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    public static class ItemAdapter extends RecyclerView.Adapter<TRSearch.ItemAdapter.ItemViewHolder> {
        private List<LocationData> itemList;
        private TRSearch.ItemAdapter.OnItemClickListener listener;
        private Context context;

        public ItemAdapter(List<LocationData> itemList, OnItemClickListener listener, Context context) {
            this.itemList = itemList;
            this.listener = listener;
            this.context = context;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public TRSearch.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travelroutes_searchresult_item, parent, false);
            return new TRSearch.ItemAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TRSearch.ItemAdapter.ItemViewHolder holder, int position) {
            LocationData item = itemList.get(position);
            String originalText = item.getName();
            SpannableString spannable = new SpannableString(originalText);
            if (!query.isEmpty()) {
                String lowerText = originalText.toLowerCase();
                String lowerQuery = query.toLowerCase();
                int start = lowerText.indexOf(lowerQuery);
                while (start >= 0) {
                    int end = start + query.length();
                    spannable.setSpan(new BackgroundColorSpan(Color.argb(51, 25, 127, 175)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = lowerText.indexOf(lowerQuery, end); // Find next occurrence
                }
            }
            holder.searchResultText.setText(spannable);
            manageThemeRV(holder);
        }

        private void manageThemeRV(TRSearch.ItemAdapter.ItemViewHolder holder) {
            if (ThemeManager.isDarkTheme()) {
                holder.searchResultText.setTextColor(ContextCompat.getColorStateList(context, R.color.nyoomYellow));
                holder.TRSRCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.backgroundPanel));
            } else { // light
                holder.searchResultText.setTextColor(ContextCompat.getColorStateList(context, R.color.LnyoomYellow));
                holder.TRSRCardView.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.LbackgroundPanel));
            }
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView searchResultText;
            CardView TRSRCardView;
            public ItemViewHolder(View itemView) {
                super(itemView);
                searchResultText = itemView.findViewById(R.id.SearchResultText);
                TRSRCardView = itemView.findViewById(R.id.TRSRCardView);
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                });
            }
        }
    }

    private void transaction(int position) {
        Bundle result = new Bundle();
        if (position != -1) {
            result.putString("searchbarType", searchbarType);
            result.putString("savedQuery", query);
            result.putString("name", searchResultList.get(position).getName());
            result.putString("lat", searchResultList.get(position).getLat());
            result.putString("lon", searchResultList.get(position).getLon());
        } else {
            result.putString("searchbarType", searchbarType);
            result.putString("savedQuery", query);
        }
        getParentFragmentManager().setFragmentResult("requestKey", result);
        getParentFragmentManager().popBackStack(); // Go back to FragmentA
    }
}