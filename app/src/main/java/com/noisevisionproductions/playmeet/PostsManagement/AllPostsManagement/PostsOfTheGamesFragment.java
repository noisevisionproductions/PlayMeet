package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.DataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering.PostsFilter;
import com.noisevisionproductions.playmeet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PostsOfTheGamesFragment extends Fragment {
    private FirebaseAuthManager authenticationManager;
    private FirebaseHelper firebaseHelper;
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
    private String lastKey = null;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
//
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_posts_list, container, false);

        setupView(view);
        showAllPosts();
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(this::refreshData, 100));
        getAddPostButton();

        filterAllPosts(view);
        return view;
    }

    public void setupView(View view) {
        postsAdapterAllPosts = new PostsAdapterAllPosts(posts, getChildFragmentManager());
        authenticationManager = new FirebaseAuthManager();
        firebaseHelper = new FirebaseHelper();

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
        //int endIndex = startIndex + postsPerPage;
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating");
        Query query;
        if (lastKey == null) {
            query = postsReference.orderByKey().limitToFirst(postsPerPage);
        } else {
            query = postsReference.orderByKey().startAt(lastKey).limitToFirst(postsPerPage + 1);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<PostCreating> nextPagerPosts = new ArrayList<>();
                    boolean isFirstItem = true;

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        // Skip the first item if this is a subsequent load
                        if (isFirstItem && lastKey != null) {
                            isFirstItem = false;
                            continue;
                        }

                        PostCreating posts = postSnapshot.getValue(PostCreating.class);
                        nextPagerPosts.add(posts);
                        lastKey = postSnapshot.getKey();
                    }

                    handleLoadedData(nextPagerPosts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


    protected void showAllPosts() {
        recyclerView.setAdapter(postsAdapterAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        if (authenticationManager.isUserLoggedIn()) {
            postCreateForLoggedInUser();
        } else {
            postCreateForUnregisteredUser();
        }

        //pozwala na przewijanie postow jeden po drugim

        if (postsAdapterAllPosts != null) {
            loadPartOfThePosts();
        }

        getAddPostButton();
    }

    public void postCreateForUnregisteredUser() {
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating");
        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<PostCreating> newPostCreatingList = new ArrayList<>();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        PostCreating posts = postSnapshot.getValue(PostCreating.class);
                        newPostCreatingList.add(posts);
                    }

                    int size = Math.min(initialPostsToLoad, newPostCreatingList.size());

                    if (size > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        noPostFound.setVisibility(View.GONE);
                        updatePostsUsingDiffUtil(newPostCreatingList.subList(0, size));
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        noPostFound.setVisibility(View.VISIBLE);
                    }
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noPostFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                recyclerView.setVisibility(View.GONE);
                noPostFound.setVisibility(View.VISIBLE);
            }
        });
    }

    public void postCreateForLoggedInUser() {
        if (firebaseHelper.getCurrentUser() != null) {
            DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(firebaseHelper.getCurrentUser().getUid());
            DatabaseReference allPostsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating");
            savedPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot savedPostsSnapshot) {
                    List<String> savedPostIds = new ArrayList<>();

                    if (savedPostsSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : savedPostsSnapshot.getChildren()) {
                            savedPostIds.add(postSnapshot.getKey());
                        }
                    }

                    allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot allPostsSnapshot) {
                            List<PostCreating> newPostCreatingList = new ArrayList<>();

                            for (DataSnapshot postSnapshot : allPostsSnapshot.getChildren()) {
                                PostCreating postCreating = postSnapshot.getValue(PostCreating.class);

                                if (postCreating != null && !postCreating.isCreatedByUser() && !postCreating.getUserId().equals(firebaseHelper.getCurrentUser().getUid()) && !savedPostIds.contains(postCreating.getPostId())) {
                                    newPostCreatingList.add(postCreating);
                                }
                            }

                            int size = Math.min(initialPostsToLoad, newPostCreatingList.size());
                            newPostCreatingList = newPostCreatingList.subList(0, size);

                            if (newPostCreatingList.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                noPostFound.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                noPostFound.setVisibility(View.GONE);
                                updatePostsUsingDiffUtil(newPostCreatingList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            recyclerView.setVisibility(View.GONE);
                            noPostFound.setVisibility(View.VISIBLE);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    recyclerView.setVisibility(View.GONE);
                    noPostFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            recyclerView.setVisibility(View.GONE);
            noPostFound.setVisibility(View.VISIBLE);
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

}
