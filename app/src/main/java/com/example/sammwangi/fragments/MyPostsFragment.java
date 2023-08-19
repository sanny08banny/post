package com.example.sammwangi.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.sammwangi.DAOs.PostItem;
import com.example.sammwangi.adapters.MyPostAdapter;
import com.example.sammwangi.MyPostItem;
import com.example.sammwangi.R;
import com.example.sammwangi.loaders.MyPostItemsLoader;
import com.example.sammwangi.utils.PostItemComparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPostsFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<PostItem>> {
    private static final int MY_POSTS_LOADER_ID = 39;
    private MyPostAdapter myPostAdapter;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout emptyRecyclerLayout;
    private ProgressBar progressBar;
    private String referenceNumber;


    private final ArrayList<MyPostItem> myPostItems = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<PostItem> postItems;
    private View retryButton;
    private boolean isLoading = false;

    public MyPostsFragment() {
        // Required empty public constructor
    }

    public MyPostsFragment(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
        LoaderManager.getInstance(this).initLoader(MY_POSTS_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_posts, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshMyPostsFragment);
        emptyRecyclerLayout = view.findViewById(R.id.empty_main_fragment);
        retryButton = view.findViewById(R.id.retry_button);
        progressBar = view.findViewById(R.id.my_posts_fragment_progressBar);

        listView = view.findViewById(R.id.my_posts_list);

        postItems = new ArrayList<>();

        myPostAdapter = new MyPostAdapter(requireContext(), postItems);
        listView.setAdapter(myPostAdapter);

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
        loadData();

        return view;
    }

    private void loadData() {
        if (!isLoading) {
            isLoading = true;
            showProgressBar();
            hideErrorLayout();

            LoaderManager.getInstance(this).restartLoader(MY_POSTS_LOADER_ID, null, this);
        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showErrorLayout() {
        emptyRecyclerLayout.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        emptyRecyclerLayout.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public Loader<List<PostItem>> onCreateLoader(int id, @Nullable Bundle args) {
        if (referenceNumber == null) {
            return new MyPostItemsLoader(requireContext(), getCurrentProfileReference());
        }else {
            return new MyPostItemsLoader(requireContext(), referenceNumber);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<PostItem>> loader, List<PostItem> data) {
        hideProgressBar();
        swipeRefreshLayout.setRefreshing(false);
        isLoading = false;

        if (data != null) {
            myPostAdapter.setPostItems(data);
            myPostAdapter.notifyDataSetChanged();
        } else {
            showErrorLayout();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<PostItem>> loader) {
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
        Collections.sort(postItems, PostItemComparators.DATE_POSTED_COMPARATOR);
        myPostAdapter.notifyDataSetChanged();
    }

    private void sortByAmount() {
        Collections.sort(postItems, PostItemComparators.POST_AMOUNT_COMPARATOR);
        myPostAdapter.notifyDataSetChanged();
    }

    private void sortByFullName() {
        Collections.sort(postItems, PostItemComparators.FULL_NAME_COMPARATOR);
        myPostAdapter.notifyDataSetChanged();
    }

    public String getCurrentProfileReference() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentProfileAccountRef", null);
    }
}