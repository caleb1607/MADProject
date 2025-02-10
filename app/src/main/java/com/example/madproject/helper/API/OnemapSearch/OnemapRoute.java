package com.example.madproject.helper.API.OnemapSearch;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OnemapRoute {
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

    public static class OnemapSearchClient {
        private static final String BASE_URL = "https://www.onemap.gov.sg/";
        private static Retrofit retrofit = null;

        public static com.example.madproject.helper.API.OnemapSearch.OnemapSearchApi getApiService() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit.create(com.example.madproject.helper.API.OnemapSearch.OnemapSearchApi.class);
        }
    }

    public static class OnemapSearchResponse {
        @SerializedName("results")
        public List<com.example.madproject.helper.API.OnemapSearch.OnemapSearchResponse.Result> Results;

        public OnemapSearchResponse(List<com.example.madproject.helper.API.OnemapSearch.OnemapSearchResponse.Result> Results) {
            this.Results = Results;
        }
        public static class Result {
            public String SEARCHVAL;
            public String LATITUDE;
            public String LONGITUDE;
            public String ADDRESS;

            public Result(String ADDRESS, String LATITUDE, String LONGITUDE) {
                this.ADDRESS = ADDRESS;
                this.LATITUDE = LATITUDE;
                this.LONGITUDE = LONGITUDE;
            }
        }
    }

}
