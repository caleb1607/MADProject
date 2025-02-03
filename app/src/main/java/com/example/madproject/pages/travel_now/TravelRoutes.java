package com.example.madproject.pages.travel_now;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TravelRoutes extends Fragment {

    // widgets
    EditText startingPointSearchBar;
    EditText destinationSearchBar;
    String query = "";
    // variables
    List<TRSearchResultItem> searchResultList = new ArrayList<>();
    TravelRoutes.ItemAdapter adapter = new TravelRoutes.ItemAdapter(searchResultList, position -> {
        transaction();
    });
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_travelroutes, container, false);
        // views setup
        startingPointSearchBar = rootView.findViewById(R.id.StartingPointSearchBar);
        startingPointSearchBar.addTextChangedListener(SearchBarTextWatcher());
        //destinationSearchBar = rootView.findViewById(R.id.SearchBar);
        //destinationSearchBar.addTextChangedListener(SearchBarTextWatcher());
        RecyclerView searchResults = rootView.findViewById(R.id.TravelRoutesRV);
        searchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResults.setAdapter(adapter);
        return rootView;
    }
    public void onSearch() {
        Log.d("onSearch", "");
        searchResultList.clear();
        if (query.length() >= 2) {
            OnemapSearchApi onemapSearchApi = OnemapSearchClient.getApiService();
            onemapSearchApi.getSearchResults(APIReader.getAPIKey(), "Y", "N", 1, query)
                .enqueue(new Callback<OnemapSearchResponse>() {
                    @Override
                    public void onResponse(Call<OnemapSearchResponse> call, Response<OnemapSearchResponse> response) {
                        if (response.isSuccessful()) {
                            OnemapSearchResponse onemapResponse = response.body();
                            for (OnemapSearchResponse.Result result : onemapResponse.Results) {
                                searchResultList.add(new TRSearchResultItem(
                                    Helper.TitleCase(result.SEARCHVAL),
                                    result.LATITUDE,
                                    result.LONGITUDE
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
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        };
    }

    public static class ItemAdapter extends RecyclerView.Adapter<TravelRoutes.ItemAdapter.ItemViewHolder> {
        private List<TRSearchResultItem> itemList;
        private TravelRoutes.ItemAdapter.OnItemClickListener listener;

        public ItemAdapter(List<TRSearchResultItem> itemList, TravelRoutes.ItemAdapter.OnItemClickListener listener) {

            this.itemList = itemList;
            this.listener = listener;
        }
        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        @Override
        public TravelRoutes.ItemAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.travelroutes_searchresult_item, parent, false);
            return new TravelRoutes.ItemAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TravelRoutes.ItemAdapter.ItemViewHolder holder, int position) {
            TRSearchResultItem item = itemList.get(position);
            holder.searchResultText.setText(item.getName());
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

    private void transaction() {
        // add later
    }
}