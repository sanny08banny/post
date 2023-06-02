package com.example.sammwangi;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    private RecyclerView postsRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    public PostAdapter postAdapter;
    private LinearLayoutManager layoutManager;
    private List<PostItem> postItemList = new ArrayList<>();
    private RelativeLayout emptyRecyclerLayout;
    private ProgressBar progressBar;
    private String baseUrl;

    private  Retrofit retrofit;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        View view =  inflater.inflate(R.layout.fragment_main, container, false);
        postsRecycler = view.findViewById(R.id.posts_recycler);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshMainFragment);
        emptyRecyclerLayout = view.findViewById(R.id.empty_main_fragment);
        progressBar = view.findViewById(R.id.main_fragment_progressBar);

        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        postsRecycler.setLayoutManager(layoutManager);

        postAdapter = new PostAdapter(postItemList);
        postsRecycler.setAdapter(postAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
              refreshData();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PostItem> posts = getAllPostsFromEndpoint();

                postItemList = posts;
                Collections.sort(postItemList, new Comparator<PostItem>() {
                    @Override
                    public int compare(PostItem o1, PostItem o2) {
                        return o2.getDateTime().compareTo(o1.getDateTime());
                    }
                });
//                postItemList.clear();
//                postItemList.addAll(posts);
                Log.d("ProfileAdapter", "Number of items in the postItemList: " + postItemList.size());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (postItemList.size() == 0){
                            emptyRecyclerLayout.setVisibility(View.VISIBLE);
                            postsRecycler.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            postAdapter.updatePosts(postItemList);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });

            }
        }).start();

        return view;
    }
    private List<PostItem> getAllPostsFromEndpoint() {
        List<PostItem> postItems = new ArrayList<>();

        baseUrl = getString(R.string.base_url_title);

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl + "/api/posts/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PostsService postsService = retrofit.create(PostsService.class);

        Call<List<PostItem>> call = postsService.getAllPosts();

        try {
            retrofit2.Response<List<PostItem>> response = call.execute();
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
                List<PostItem> refreshedPosts = getAllPostsFromEndpoint();

                postItemList.clear();
                postItemList.addAll(refreshedPosts);

                Collections.sort(postItemList, new Comparator<PostItem>() {
                    @Override
                    public int compare(PostItem o1, PostItem o2) {
                        return o2.getDateTime().compareTo(o1.getDateTime());
                    }
                });

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (refreshedPosts.size() == 0){
                            emptyRecyclerLayout.setVisibility(View.VISIBLE);
                            postsRecycler.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            postAdapter.updatePosts(postItemList);
                            swipeRefreshLayout.setRefreshing(false);
                        }
//                        subsPostAdapter.updatePosts(postItemList);
//                        swipeRefreshLayout.setRefreshing(false); // Stop the refreshing animation
                    }
                });
            }
        }).start();
    }

}