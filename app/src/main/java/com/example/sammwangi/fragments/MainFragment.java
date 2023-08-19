package com.example.sammwangi.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.sammwangi.adapters.PostAdapter;
import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.R;
import com.example.sammwangi.loaders.PostItemPagedLoader;
import com.example.sammwangi.utils.PostItemComparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PostItem>>{
    private static final int STORI_LOADER_ID = 89;
    private RecyclerView postsRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    public PostAdapter postAdapter;
    private LinearLayoutManager layoutManager;
    private List<PostItem> postItemList = new ArrayList<>();
    private RelativeLayout errorLayout;
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
    private View retryButton;
    private boolean isLoading = false;
    private int currentPage = 0;
    private boolean isLoadingMore = false;
    private View sortButton;

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
        errorLayout = view.findViewById(R.id.empty_main_fragment);
        progressBar = view.findViewById(R.id.main_fragment_progressBar);
        retryButton = view.findViewById(R.id.retry_button);

        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        postsRecycler.setLayoutManager(layoutManager);

        postAdapter = new PostAdapter(postItemList);
        postsRecycler.setAdapter(postAdapter);

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

            LoaderManager.getInstance(this).restartLoader(STORI_LOADER_ID, null, this);
        }
    }
    private void loadMoreData() {
        if (!isLoadingMore) {
            isLoadingMore = true;
            currentPage++;
            LoaderManager.getInstance(this).restartLoader(STORI_LOADER_ID, null, this);
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

    @NonNull
    @Override
    public Loader<List<PostItem>> onCreateLoader(int id, @Nullable Bundle args) {
        return new PostItemPagedLoader(requireContext(),currentPage,10,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<PostItem>> loader, List<PostItem> data) {
        hideProgressBar();
        swipeRefreshLayout.setRefreshing(false);
        isLoading = false;

        if (data != null) {
            if (currentPage == 0) {
                postItemList.clear();
            }
            postItemList.addAll(data);
            postAdapter.notifyDataSetChanged();
        } else {
            if (currentPage == 0) {
                postItemList.clear();
                postAdapter.notifyDataSetChanged();
                showErrorLayout();
            }
        }
        isLoadingMore = false;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<PostItem>> loader) {
        postAdapter.notifyDataSetChanged();
        hideProgressBar();
        hideErrorLayout();
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
        postAdapter.notifyDataSetChanged();
    }

    private void sortByAmount() {
        Collections.sort(postItemList, PostItemComparators.POST_AMOUNT_COMPARATOR);
        postAdapter.notifyDataSetChanged();
    }

    private void sortByFullName() {
        Collections.sort(postItemList, PostItemComparators.FULL_NAME_COMPARATOR);
        postAdapter.notifyDataSetChanged();
    }

}