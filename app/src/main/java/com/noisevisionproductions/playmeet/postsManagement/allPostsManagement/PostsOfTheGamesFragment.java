package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.postsManagement.postsFiltering.PostsFilter;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.ArrayList;
import java.util.List;

public class PostsOfTheGamesFragment extends Fragment {
    private DatabaseReference allPostsReference;
    private FirebaseHelper firebaseHelper;
    private AdapterAllPosts adapterAllPosts;
    private final List<PostCreating> posts = new ArrayList<>();
    private final List<String> savedPostIds = new ArrayList<>();
    private ProgressBar progressBar, loadingMorePostsIndicator;
    private AppCompatButton filterButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private boolean isLoading = false;
    private int currentPage = 1;
    private static final int POSTS_PER_PAGE = 10;
    private AppCompatTextView loadingMorePostsText, noPostFound;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_posts_list, container, false);

        setupView(view);

        // refreshData(); // odświeżam aktywność na starcie, bo filtry bez tego nie działają jak należy TODO
        showAllPosts();
       /* swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(this::refreshData, 100));

        filterAllPosts(view);
*/
        handleBackPressed();

        return view;
    }

    private void setupView(@NonNull View view) {
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating");
        FirebaseRecyclerOptions<PostCreating> options = new FirebaseRecyclerOptions.Builder<PostCreating>()
                .setQuery(postsReference, PostCreating.class)
                .build();
        adapterAllPosts = new AdapterAllPosts(options, getChildFragmentManager(), getContext());

        firebaseHelper = new FirebaseHelper();
        allPostsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating");

        progressBar = view.findViewById(R.id.progressBarLayout);
        loadingMorePostsIndicator = view.findViewById(R.id.loadMorePostsIndicator);
        loadingMorePostsText = view.findViewById(R.id.loadingPostsText);
        noPostFound = view.findViewById(R.id.noPostFound);
        filterButton = view.findViewById(R.id.postsFilter);

        recyclerView = view.findViewById(R.id.recycler_view_posts);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

     /*   recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!isLoading) {
                    if (linearLayoutManager != null) {
                        int totalItemCount = linearLayoutManager.getItemCount();
                        int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                        if (lastVisibleItemPosition + 1 == totalItemCount) {
                            loadMorePosts();
                            isLoading = true;
                        }
                    }
                }
            }
        });*/

    }

    private void showAllPosts() {
        recyclerView.setAdapter(adapterAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

    /*    if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            postCreateForLoggedInUser();
        } else {
            postCreateForUnregisteredUser();
        }*/
    }

    private void loadMorePosts() {
        currentPage++;

        loadingMorePostsIndicator.setVisibility(View.VISIBLE);
        loadingMorePostsText.setVisibility(View.VISIBLE);

        if (FirebaseAuthManager.isUserLoggedIn()) {
            postCreateForLoggedInUser();
        } else {
            postCreateForUnregisteredUser();
        }
    }

    public void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        new Handler().postDelayed(() -> {
            posts.clear();

            if (FirebaseAuthManager.isUserLoggedIn()) {
                postCreateForLoggedInUser();
            } else {
                postCreateForUnregisteredUser();
            }

            filterButton.setSelected(false);
            noPostFound.setVisibility(View.GONE);
            // Set refreshing to false after data has been refreshed
            swipeRefreshLayout.setRefreshing(false);
        }, 800);
    }


    private void postCreateForLoggedInUser() {
        if (firebaseHelper.getCurrentUser() != null) {
            DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(firebaseHelper.getCurrentUser().getUid());
            savedPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot savedPostsSnapshot) {
                    if (savedPostsSnapshot.exists()) {
                        for (DataSnapshot postSnapshot : savedPostsSnapshot.getChildren()) {
                            savedPostIds.add(postSnapshot.getKey());
                        }
                    }

                    allPostsReference.orderByKey().limitToFirst(currentPage * POSTS_PER_PAGE).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot allPostsSnapshot) {
                            List<PostCreating> newPostCreatingList = new ArrayList<>();

                            for (DataSnapshot postSnapshot : allPostsSnapshot.getChildren()) {
                                PostCreating postCreating = postSnapshot.getValue(PostCreating.class);

                                if (postCreating != null && !postCreating.getUserId().equals(firebaseHelper.getCurrentUser().getUid()) && !savedPostIds.contains(postCreating.getPostId()) && !postCreating.getActivityFull()) {
                                    newPostCreatingList.add(postCreating);
                                }
                            }

                            if (newPostCreatingList.isEmpty()) {
                                recyclerView.setVisibility(View.GONE);
                                noPostFound.setVisibility(View.VISIBLE);
                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                noPostFound.setVisibility(View.GONE);
                                updatePostsUsingDiffUtil(newPostCreatingList);

                            }
                            loadingMorePostsIndicator.setVisibility(View.GONE);
                            loadingMorePostsText.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase RealmTime Database error", "Downloading posts for logged user " + error.getMessage());
                            loadingMorePostsIndicator.setVisibility(View.GONE);
                            loadingMorePostsText.setVisibility(View.GONE);
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

    private void postCreateForUnregisteredUser() {
        allPostsReference.orderByKey().limitToFirst(currentPage * POSTS_PER_PAGE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<PostCreating> newPostCreatingList = new ArrayList<>();

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        PostCreating posts = postSnapshot.getValue(PostCreating.class);
                        newPostCreatingList.add(posts);
                    }

                    updatePostsUsingDiffUtil(newPostCreatingList);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noPostFound.setVisibility(View.VISIBLE);
                }
                loadingMorePostsIndicator.setVisibility(View.GONE);
                loadingMorePostsText.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Printing all posts for guest user " + error.getMessage());
                loadingMorePostsIndicator.setVisibility(View.GONE);
                loadingMorePostsText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                noPostFound.setVisibility(View.VISIBLE);
            }
        });
    }

    // obliczanie roznicy miedzy postami w celu szybszego ladowania, pozwala na dzialanie na watku w tle
    private void updatePostsUsingDiffUtil(@NonNull List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.posts);
        progressBar.setVisibility(View.VISIBLE);

        try {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));

            new Handler(Looper.getMainLooper()).post(() -> {
                posts.clear();
                posts.addAll(newPosts);
                progressBar.setVisibility(View.GONE);
                diffResult.dispatchUpdatesTo(adapterAllPosts);
            });
        } catch (Exception e) {
            Log.e("UpdatePosts", "Error updating posts using DiffUtil", e);
        }
    }

    private void filterAllPosts(@NonNull View view) {
        AppCompatButton deleteFilters = view.findViewById(R.id.deleteFilters);
        filterButton.setOnClickListener(v -> {
            PostsFilter postsFilter = new PostsFilter(adapterAllPosts, posts, filterButton, deleteFilters, noPostFound);
            if (getContext() != null) {
                postsFilter.filterPostsWindow((Activity) getContext());
            }
        });
    }

    public interface OnDataReceived {
        void onDataReceived(String data);
    }

    private void handleBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void showExitDialog() {
        if (FirebaseAuthManager.isUserLoggedIn()) {
            new AlertDialog.Builder(getContext()).setTitle("Wyjście").setMessage("Wylogować, czy zamknąć aplikację?").setPositiveButton("Wyloguj się", (dialog, which) -> {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    firebaseAuth.signOut();
                    ToastManager.showToast(requireContext(), "Pomyślnie wylogowano");
                    Intent intent = new Intent(requireContext(), LoginAndRegisterActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("Wyjście", (dialog, which) -> requireActivity().finishAffinity()).setNeutralButton("Anuluj", null).show();
        } else {
            Intent intent = new Intent(requireContext(), LoginAndRegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapterAllPosts.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapterAllPosts != null) {
            adapterAllPosts.stopListening();
        }
    }
}
