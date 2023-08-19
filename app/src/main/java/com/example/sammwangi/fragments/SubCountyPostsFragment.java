package com.example.sammwangi.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.example.sammwangi.adapters.AdminPostsAdapterInner;
import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.R;
import com.example.sammwangi.loaders.PostItemPagedLoader;
import com.example.sammwangi.utils.PostItemComparators;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SubCountyPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SubCountyPostsFragment extends Fragment {
    private static final int SUB_COUNTY_LOADER_ID = 49;
    private RecyclerView postsRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    public AdminPostsAdapterInner subsPostAdapter;
    private LinearLayoutManager layoutManager;
    private List<PostItem> postItemList = new ArrayList<>();
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
    private int currentPage = 0;
    private MaterialButton retryButton;
    private ImageButton sortButton;
    private boolean isLoading = false;
    private boolean isLoadingMore = false;
    private View errorLayout;

    public SubCountyPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubCountyPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SubCountyPostsFragment newInstance(String param1, String param2) {
        SubCountyPostsFragment fragment = new SubCountyPostsFragment();
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

        postsRecycler = view.findViewById(R.id.sub_county_posts_recycler);
        swipeRefreshLayout = view.findViewById(R.id.subs_swipeRefreshMainFragment);
        progressBar = view.findViewById(R.id.subs_fragment_progressBar);
        errorLayout = view.findViewById(R.id.empty_subs_fragment);
        retryButton = view.findViewById(R.id.retry_button);

        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        postsRecycler.setLayoutManager(layoutManager);

        subsPostAdapter = new AdminPostsAdapterInner(postItemList, requireContext());
        postsRecycler.setAdapter(subsPostAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
        postsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    assert layoutManager != null;
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        // Load more data when the user reaches the end of the list
                        loadMoreData();
                    }
                }
            }
        });
        sortButton = view.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortMenu(v);
            }
        });

        loadData();

        return view;
    }

    private void loadData() {
        if (!isLoading) {
            isLoading = true;
            showProgressBar();
            hideErrorLayout();

            getAllSubCountyPostsFromEndpoint();
        }
    }
    private void loadMoreData() {
        if (!isLoadingMore) {
            isLoadingMore = true;
            currentPage++;
            getAllSubCountyPostsFromEndpoint();
        }
    }
    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        errorLayout.setVisibility(View.GONE);
    }
    private void getAllSubCountyPostsFromEndpoint() {
        PostItemPagedLoader postItemPagedLoader = new PostItemPagedLoader(requireContext(),
                currentPage,10,getCurrentProfileAccountSubCounty());
        postItemPagedLoader.forceLoad();
        postItemPagedLoader.registerListener(SUB_COUNTY_LOADER_ID, new Loader.OnLoadCompleteListener<List<PostItem>>() {
            @Override
            public void onLoadComplete(@NonNull Loader<List<PostItem>> loader, @Nullable List<PostItem> data) {
                hideProgressBar();
                swipeRefreshLayout.setRefreshing(false);
                isLoading = false;

                if (data != null) {
                    if (currentPage == 0) {
                        postItemList.clear();
                    }
                    postItemList.addAll(data);
                    subsPostAdapter.notifyDataSetChanged();
                } else {
                    if (currentPage == 0) {
                        postItemList.clear();
                        subsPostAdapter.notifyDataSetChanged();
                        showErrorLayout();
                    }
                }
                isLoadingMore = false;
            }
        });
    }

    public String getCurrentProfileAccountSubCounty(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs",MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountSubCounty",null);
    }
    private void showSortMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.menu_sort);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sort_by_date:
                        sortByDateTimePosted();
                        return true;
                    case R.id.sort_by_amount:
                        sortByAmount();
                        return true;
                    case R.id.sort_by_full_name:
                        sortByFullName();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
    private void sortByDateTimePosted() {
        Collections.sort(postItemList, PostItemComparators.DATE_POSTED_COMPARATOR);
        subsPostAdapter.notifyDataSetChanged();
    }

    private void sortByAmount() {
        Collections.sort(postItemList, PostItemComparators.POST_AMOUNT_COMPARATOR);
        subsPostAdapter.notifyDataSetChanged();
    }

    private void sortByFullName() {
        Collections.sort(postItemList, PostItemComparators.FULL_NAME_COMPARATOR);
        subsPostAdapter.notifyDataSetChanged();
    }

}