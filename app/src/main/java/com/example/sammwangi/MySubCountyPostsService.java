package com.example.sammwangi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MySubCountyPostsService {
    @GET("/BySubCounty/{subCounty}")
    Call<List<PostItem>> getPostItemsBySubCounty(@Path("subCounty") String subCounty);
}
