package com.example.sammwangi;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubCountyPosts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubCountyPosts extends Fragment {
    private RecyclerView postsRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    public AdminPostsAdapterInner subsPostAdapter;
    private LinearLayoutManager layoutManager;
    private List<PostItem> postItemList = new ArrayList<>();
    private RelativeLayout emptyRecyclerLayout;
    private ProgressBar progressBar;
    private String baseUrl;
    private RequestQueue requestQueue;

    private  Retrofit retrofit;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SubCountyPosts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubCountyPosts.
     */
    // TODO: Rename and change types and number of parameters
    public static SubCountyPosts newInstance(String param1, String param2) {
        SubCountyPosts fragment = new SubCountyPosts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_county_posts, container, false);

        requestQueue = Volley.newRequestQueue(requireContext());

        postsRecycler = view.findViewById(R.id.sub_county_posts_recycler);
        swipeRefreshLayout = view.findViewById(R.id.subs_swipeRefreshMainFragment);
        emptyRecyclerLayout = view.findViewById(R.id.empty_subs_fragment);
        progressBar = view.findViewById(R.id.subs_fragment_progressBar);

        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        postsRecycler.setLayoutManager(layoutManager);

        subsPostAdapter = new AdminPostsAdapterInner(postItemList);
        postsRecycler.setAdapter(subsPostAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllSubCountyPostsFromEndpointByVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(List<PostItem> postItems) {
                        // Sort the postItems list
                        Collections.sort(postItems, new Comparator<PostItem>() {
                            @Override
                            public int compare(PostItem o1, PostItem o2) {
                                return o2.getDateTime().compareTo(o1.getDateTime());
                            }
                        });

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (postItems.size() == 0) {
                                    emptyRecyclerLayout.setVisibility(View.VISIBLE);
                                    postsRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    subsPostAdapter.updatePosts(postItems);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle the error
                    }
                });
            }
        }).start();

        return view;
    }
    private void getAllSubCountyPostsFromEndpointByVolley(final VolleyCallback callback) {
        String baseUrl = getString(R.string.base_url_title);
        String subCounty = getCurrentProfileAccountSubCounty();
        String url = baseUrl + "/api/posts/BySubCounty/" + subCounty;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<PostItem> postItems = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                // Parse the JSON object and create a PostItem instance
                                // Assuming your PostItem class has a constructor that takes JSON data
                                String referenceNumber = jsonObject.getString("referenceNumber");
                                String transactionId = jsonObject.getString("transactionId");
                                String transactionType = jsonObject.getString("transactionType");
                                String postAmount = jsonObject.getString("postAmount");
                                String postType = jsonObject.getString("postType");
                                String datePosted = jsonObject.getString("datePosted");
                                String timePosted = jsonObject.getString("timePosted");
                                String fullName = jsonObject.getString("fullName");
                                String dateTime = jsonObject.getString("dateTime");

                                // Create a new PostItem object with the extracted fields
                                PostItem postItem = new PostItem(postAmount, postType, datePosted, fullName, transactionId, transactionType, timePosted, null, dateTime, referenceNumber);

                                // Add the postItem to the list
                                postItems.add(postItem);
                            }
                            // Notify the callback with the postItems list
                            callback.onSuccess(postItems);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Notify the callback with an error
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Notify the callback with an error
                        callback.onError(error);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }


    private List<PostItem> convertJsonArrayToPostItemList(JSONArray jsonArray) {
        List<PostItem> postItems = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Extract the necessary fields from the JSON object
                String referenceNumber = jsonObject.getString("referenceNumber");
                String transactionId = jsonObject.getString("transactionId");
                String transactionType = jsonObject.getString("transactionType");
                String postAmount = jsonObject.getString("postAmount");
                String postType = jsonObject.getString("postType");
                String datePosted = jsonObject.getString("datePosted");
                String timePosted = jsonObject.getString("timePosted");
                String fullName = jsonObject.getString("fullName");
                String dateTime = jsonObject.getString("dateTime");

                // Create a new PostItem object with the extracted fields
                PostItem postItem = new PostItem(postAmount,postType,datePosted,fullName,transactionId,transactionType,timePosted,null,dateTime,referenceNumber);

                // Add the postItem to the list
                postItems.add(postItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return postItems;
    }


    private List<PostItem> getAllSubCountyPostsFromEndpoint() {
        List<PostItem> postItems = new ArrayList<>();

        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/api/posts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MySubCountyPostsService postsService = retrofit.create(MySubCountyPostsService.class);

        Call<List<PostItem>> call1 = postsService.getPostItemsBySubCounty(getCurrentProfileAccountSubCounty());
        Log.d("SubCounty", "Chosen sub county: " + getCurrentProfileAccountSubCounty());


        try {
            retrofit2.Response<List<PostItem>> response = call1.execute();
            if (response.isSuccessful()) {
                postItems = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return postItems;
    }
    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllSubCountyPostsFromEndpointByVolley(new VolleyCallback() {
                    @Override
                    public void onSuccess(List<PostItem> postItems) {
                        // Sort the postItems list
                        Collections.sort(postItems, new Comparator<PostItem>() {
                            @Override
                            public int compare(PostItem o1, PostItem o2) {
                                return o2.getDateTime().compareTo(o1.getDateTime());
                            }
                        });

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (postItems.size() == 0) {
                                    emptyRecyclerLayout.setVisibility(View.VISIBLE);
                                    postsRecycler.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    subsPostAdapter.updatePosts(postItems);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle the error
                    }
                });
            }
        }).start();
    }
    public String getCurrentProfileAccountSubCounty(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountSubCounty",null);
    }

}