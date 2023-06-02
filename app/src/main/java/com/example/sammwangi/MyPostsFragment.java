package com.example.sammwangi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPostsFragment extends Fragment {
    private MyPostAdapter myPostAdapter;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout emptyRecyclerLayout;
    private ProgressBar progressBar;
    private String baseUrl;


    private final ArrayList<MyPostItem> myPostItems = new ArrayList<>();
//    private Retrofit retrofit = new Retrofit.Builder()
//            .baseUrl("http://192.168.179.69:8090/api/posts/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyPostsFragment newInstance(String param1, String param2) {
        MyPostsFragment fragment = new MyPostsFragment();
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
        View view= inflater.inflate(R.layout.fragment_my_posts, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshMyPostsFragment);
        emptyRecyclerLayout = view.findViewById(R.id.empty_main_fragment);

        listView = view.findViewById(R.id.my_posts_list);

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        baseUrl = getString(R.string.base_url_title);

// Define the URL for the request, replacing {referenceNumber} with the actual reference number
        String url = baseUrl + "/api/posts/allPostsByRef/" + getCurrentProfileReference();
        Log.d("MyPostFragment","String URL: " + url);


// Define a StringRequest to make a GET request to the URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Parse the response JSON string into a list of PostItems
                        List<PostItem> postItems = new Gson().fromJson(response, new TypeToken<List<PostItem>>(){}.getType());
                        // Do something with the list of PostItems
                        // For example, display them in a ListView or RecyclerView
                        Collections.sort(postItems, new Comparator<PostItem>() {
                            @Override
                            public int compare(PostItem o1, PostItem o2) {
                                return o2.getDateTime().compareTo(o1.getDateTime());
                            }
                        });
                        Log.d("MyPostFragment","Number of items in postItems: " + postItems.size());
                        myPostAdapter = new MyPostAdapter(requireContext(),postItems);
                        listView.setAdapter(myPostAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
            }
        });

// Add the request to the RequestQueue
        queue.add(stringRequest);
        return view;
    }
public String getCurrentProfileReference() {
    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    return sharedPreferences.getString("currentProfileAccountRef", null);
}
}