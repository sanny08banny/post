package com.example.sammwangi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.sammwangi.DAOs.ActivityItem;
import com.example.sammwangi.R;
import com.example.sammwangi.adapters.ActivityListAdapter;
import com.example.sammwangi.loaders.ActivityRetrieverLoader;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivitiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivitiesFragment extends Fragment {
    private ListView listView;
    private ActivityListAdapter activityListAdapter;
    private ArrayList<ActivityItem> activityItems;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageButton refreshButton;
    private View emptyRecyclerLayout;
    private MaterialButton retryButton;
    private ProgressBar progressBar;
    private boolean isLoading = false;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ActivitiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivitiesFragment newInstance(String param1, String param2) {
        ActivitiesFragment fragment = new ActivitiesFragment();
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
        View view =  inflater.inflate(R.layout.fragment_activities, container, false);
        listView = view.findViewById(R.id.activities_list);
        refreshButton = view.findViewById(R.id.refreshButon);
        emptyRecyclerLayout = view.findViewById(R.id.empty_main_fragment);
        retryButton = view.findViewById(R.id.retry_button);
        progressBar = view.findViewById(R.id.my_posts_fragment_progressBar);

        activityItems = new ArrayList<>();
        activityListAdapter = new ActivityListAdapter(requireContext(),activityItems);
        listView.setAdapter(activityListAdapter);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadActivities();
            }
        });

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadActivities();
            }
        });

        loadActivities();
        return view;
    }

    private void loadActivities() {
        if (!isLoading) {
            isLoading = true;
            showProgressBar();
            hideErrorLayout();

            ActivityRetrieverLoader activityRetrieverLoader = new ActivityRetrieverLoader(requireContext());
            activityRetrieverLoader.forceLoad();
            activityRetrieverLoader.registerListener(0, new Loader.OnLoadCompleteListener<List<ActivityItem>>() {
                @Override
                public void onLoadComplete(Loader<List<ActivityItem>> loader, List<ActivityItem> data) {
                    hideProgressBar();
                    isLoading = false;

                    if (data != null) {
                        activityListAdapter.setItems(data);
                    } else {
                        showErrorLayout();
                    }
                }
            });
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
}