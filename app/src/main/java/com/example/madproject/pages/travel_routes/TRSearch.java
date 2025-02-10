package com.example.madproject.pages.travel_routes;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.madproject.R;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchApi;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchClient;
import com.example.madproject.helper.API.OnemapSearch.OnemapSearchResponse;
import com.example.madproject.helper.APIReader;
import com.example.madproject.helper.Helper;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class TRSearch extends Fragment {

    // widgets
    EditText TRSearchBar;
    String searchbarType;
    TextView startTyping;
    // variables
    static String query = "";
    List<LocationData> searchResultList = new ArrayList<>();
    TRSearch.ItemAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trsearch, container, false);
        // views setup
        startTyping = rootView.findViewById(R.id.startTyping);
        TRSearchBar = rootView.findViewById(R.id.TRSearchBar);
        TRSearchBar.addTextChangedListener(SearchBarTextWatcher());
        RecyclerView searchResults = rootView.findViewById(R.id.TravelRoutesRV);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        Button returnButton = rootView.findViewById(R.id.ReturnButton);
        returnButton.setOnClickListener(view -> transaction(-1));
        // adapter
        adapter = new TRSearch.ItemAdapter(searchResultList, this::transaction);
        searchResults.setAdapter(adapter);
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
    public void onSearch() {
        searchResultList.clear();
        if (query.length() >= 2) {
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
                                    Helper.TitleCase(result.ADDRESS),
                                    result.LATITUDE,
                                    result.LONGITUDE,
                                        ""
                                ));
                            }
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
                // Hide the placeholder text when the user starts typing
                if (charSequence.length() > 0) {
                    startTyping.setVisibility(View.GONE);  // Hide the TextView
                } else {
                    startTyping.setVisibility(View.VISIBLE);  // Show the TextView if the field is empty
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    public static class ItemAdapter extends RecyclerView.Adapter<TRSearch.ItemAdapter.ItemViewHolder> {
        private List<LocationData> itemList;
        private TRSearch.ItemAdapter.OnItemClickListener listener;

        public ItemAdapter(List<LocationData> itemList, TRSearch.ItemAdapter.OnItemClickListener listener) {

            this.itemList = itemList;
            this.listener = listener;
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
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView searchResultText;
            public ItemViewHolder(View itemView) {
                super(itemView);
                searchResultText = itemView.findViewById(R.id.SearchResultText);
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