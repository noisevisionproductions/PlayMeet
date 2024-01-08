package com.example.zagrajmy.PostsManagement.AllPostsManagement;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.zagrajmy.DataManagement.PostDiffCallback;
import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostCreatingCopy;
import com.example.zagrajmy.PostsManagement.PostsFiltering.PostsFilter;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class PostsOfTheGamesFragment extends Fragment {
    private RealmAuthenticationManager authenticationManager;
    private PostsAdapterAllPosts postsAdapterAllPosts;
    private final List<PostCreating> posts = new ArrayList<>();
    private ProgressBar progressBar, loadingMorePostsIndicator;
    private AppCompatTextView loadingMorePostsText, noPostFound;
    private AppCompatButton filterButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private int initialPostsToLoad;
    private int currentPage = 1;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_posts_list, container, false);

        setupView(view);
        showAllPosts(view);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(this::refreshData, 100));
        getAddPostButton();

        filterAllPosts(view);
        return view;
    }

    public void setupView(View view) {
        postsAdapterAllPosts = new PostsAdapterAllPosts(posts, getChildFragmentManager());
        authenticationManager = new RealmAuthenticationManager();

        progressBar = view.findViewById(R.id.progressBarLayout);
        loadingMorePostsIndicator = view.findViewById(R.id.loadMorePostsIndicator);
        loadingMorePostsText = view.findViewById(R.id.loadingPostsText);
        noPostFound = view.findViewById(R.id.noPostFound);
        filterButton = view.findViewById(R.id.postsFilter);

        recyclerView = view.findViewById(R.id.recycler_view_posts);

        initialPostsToLoad = 10;

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    public void loadPartOfThePosts() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && !isLoading) {
                        loadMoreData();
                    }
                }
            }
        });
    }

    private void loadMoreData() {
        if (!isLoading) {
            isLoading = true;

            loadingMorePostsIndicator.setVisibility(View.VISIBLE);
            loadingMorePostsText.setVisibility(View.VISIBLE);

            int delayLoading = 1000;

            if (currentPage <= 0) {
                loadingMorePostsIndicator.setVisibility(View.GONE);
                loadingMorePostsText.setVisibility(View.GONE);
                isLoading = false;
                return;
            }

            new Handler().postDelayed(() -> {
                threadPool.execute(() -> {
                    List<PostCreating> moreData = loadNextPager();
                    if (isAdded()) { // sprawdza, czy fragment jest aktywny, bez tego crashuje aktywnosc, bo ladowanie na poziomie ui nie jest dokocznone
                        requireActivity().runOnUiThread(() -> {
                            handleLoadedData(moreData);
                            loadingMorePostsIndicator.setVisibility(View.GONE);
                            loadingMorePostsText.setVisibility(View.GONE);

                            if (moreData.isEmpty()) {
                                currentPage = 0;
                            }
                        });
                    }
                });
                isLoading = false;
            }, delayLoading);
        }
    }

    private void handleLoadedData(List<PostCreating> moreData) {
        if (moreData != null) {
            int oldSize = posts.size();
            posts.addAll(moreData);
            postsAdapterAllPosts.notifyItemRangeInserted(oldSize, moreData.size());
        }
        isLoading = false;
    }

    private List<PostCreating> loadNextPager() {
        int postsPerPage = 10;
        int startIndex = currentPage * postsPerPage;
        int endIndex = startIndex + postsPerPage;


        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<PostCreating> allPosts = realm.where(PostCreating.class)
                    .findAll();

            if (startIndex < allPosts.size()) {
                // Limit the range to the available size of allPosts
                endIndex = Math.min(endIndex, allPosts.size());

                List<PostCreating> nextPagerPosts = new ArrayList<>(realm.copyFromRealm(allPosts.subList(startIndex, endIndex)));

                currentPage++;

                return nextPagerPosts;
            }
        }
        return Collections.emptyList();
    }

    public void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        int delayLoading = 1000;

        new Handler().postDelayed(() -> {
            int oldSize = posts.size();
            currentPage = 1;

            posts.clear();

            if (authenticationManager.isUserLoggedIn()) {
                postCreateForLoggedInUser();
            } else {
                postCreateForUnregisteredUser();
            }

            // Calculate the new size of the dataset
            int newSize = posts.size();

            // Use notifyItemRangeRemoved and notifyItemRangeInserted
            postsAdapterAllPosts.notifyItemRangeRemoved(0, oldSize);
            postsAdapterAllPosts.notifyItemRangeInserted(0, newSize);

            filterButton.setSelected(false);
            // Set refreshing to false after data has been refreshed
            swipeRefreshLayout.setRefreshing(false);
        }, delayLoading);
    }


    protected void showAllPosts(View view) {
        recyclerView.setAdapter(postsAdapterAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        if (authenticationManager.isUserLoggedIn()) {
            postCreateForLoggedInUser();
        } else {
            postCreateForUnregisteredUser();
        }

        //*pozwala na przewijanie postow jeden po drugim*//*
        /*PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);*/

        if (postsAdapterAllPosts != null) {
            loadPartOfThePosts();
        }

        getAddPostButton();
    }

    public void postCreateForUnregisteredUser() {
        try (Realm realm = Realm.getDefaultInstance()) {

            RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();

            List<PostCreating> newPostCreatingList = new ArrayList<>(allPosts.subList(0, Math.min(initialPostsToLoad, allPosts.size())));

            if (newPostCreatingList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noPostFound.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noPostFound.setVisibility(View.GONE);
                updatePostsUsingDiffUtil(newPostCreatingList);
            }
        }
    }

    public void postCreateForLoggedInUser() {
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        try (Realm realm = Realm.getDefaultInstance()) {
            RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();
            RealmResults<PostCreatingCopy> copiedPosts = realm.where(PostCreatingCopy.class).findAll();

            List<PostCreating> newPostCreatingList = new ArrayList<>();

            List<Integer> savedPostIds = new ArrayList<>();
            for (PostCreatingCopy post : copiedPosts) {
                if (post.getSavedByUser() && user != null && post.getUserId().equals(user.getId())) {
                    savedPostIds.add(post.getPostId());
                }
            }

            for (PostCreating post : allPosts) {
                if (post.isCreatedByUser() && user != null && !post.getUserId().equals(user.getId()) && !savedPostIds.contains(post.getPostId())) {
                    newPostCreatingList.add(post);
                }
            }

            // Limit the initial load to the specified number of posts
            newPostCreatingList = newPostCreatingList.subList(0, Math.min(initialPostsToLoad, newPostCreatingList.size()));

            if (newPostCreatingList.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                noPostFound.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                noPostFound.setVisibility(View.GONE);
                updatePostsUsingDiffUtil(newPostCreatingList);
            }
        }
    }

    // obliczanie roznicy miedzy postami w celu szybszego ladowania, pozwala na dzialanie na watku w tle
    public void updatePostsUsingDiffUtil(List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.posts);
        progressBar.setVisibility(View.VISIBLE);

        threadPool.execute(() -> {
            try {
                final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));

                new Handler(Looper.getMainLooper()).post(() -> {
                    posts.clear();
                    posts.addAll(newPosts);
                    progressBar.setVisibility(View.GONE);
                    diffResult.dispatchUpdatesTo(postsAdapterAllPosts);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getChildFragmentManager().beginTransaction().add(R.id.layoutForAddPostButton, myFragment).commit();
    }

    public void filterAllPosts(View view) {
        AppCompatButton deleteFilters = view.findViewById(R.id.deleteFilters);
        filterButton.setOnClickListener(v -> {
            PostsFilter postsFilter = new PostsFilter(postsAdapterAllPosts, posts, filterButton, deleteFilters, noPostFound);
            postsFilter.filterPostsWindow((Activity) getContext());
        });

    }

    public interface OnDataReceived {
        void onDataReceived(String data);
    }

    private OnDataReceived onDataReceived;

    public void setOnDataReceived(OnDataReceived onDataReceived) {
        this.onDataReceived = onDataReceived;
    }

    public void updateRecyclerView(String data) {

    }

}
