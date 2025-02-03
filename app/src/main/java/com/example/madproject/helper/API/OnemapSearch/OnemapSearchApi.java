package com.example.madproject.helper.API.OnemapSearch;

import com.example.madproject.helper.API.OnemapSearch.OnemapSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface OnemapSearchApi {
    @GET("api/common/elastic/search")
    Call<OnemapSearchResponse> getSearchResults(
            @Header("Authorization") String authorization,
            @Query("returnGeom") String returnGeom,
            @Query("getAddrDetails") String getAddrDetails,
            @Query("pageNum") int pageNum,
            @Query("searchVal") String searchVal
    );
}


