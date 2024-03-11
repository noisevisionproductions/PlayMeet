package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.postsManagement.postsFiltering.PostsFilter;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.ArrayList;
import java.util.List;

public class PostsOfTheGamesFragment extends Fragment {
    private FirebaseHelper firebaseHelper;
    private AdapterAllPosts adapterAllPosts;
    private final List<PostModel> posts = new ArrayList<>();
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

        setupPostsList();
        //initializePostsDisplay();
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(this::refreshData, 100));

        //filterAllPosts(view);
        handleBackPressed();

        return view;
    }

    private void setupView(@NonNull View view) {
        firebaseHelper = new FirebaseHelper();

        progressBar = view.findViewById(R.id.progressBarLayout);
        loadingMorePostsIndicator = view.findViewById(R.id.loadMorePostsIndicator);
        loadingMorePostsText = view.findViewById(R.id.loadingPostsText);
        noPostFound = view.findViewById(R.id.noPostFound);
        filterButton = view.findViewById(R.id.postsFilter);

        recyclerView = view.findViewById(R.id.recycler_view_posts);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

    private void setupPostsList() {
        FirestorePostsDisplay firestorePostsDisplay = new FirestorePostsDisplay();
        progressBar.setVisibility(View.VISIBLE);

        boolean isUserLoggedIn = FirebaseAuthManager.isUserLoggedIn();
        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = isUserLoggedIn ? firebaseHelper.getCurrentUser().getUid() : null;
            firestorePostsDisplay.filterAllPosts(isUserLoggedIn, currentUserId, (posts, e) -> {
                progressBar.setVisibility(View.GONE);

                if (e != null) {
                    Log.e("Fetching all posts", "Error: " + e.getMessage());
                    recyclerView.setVisibility(View.GONE);
                    noPostFound.setVisibility(View.VISIBLE);
                    return;
                }

                if (posts != null && !posts.isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    noPostFound.setVisibility(View.GONE);

                    FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                            .setQuery(firestorePostsDisplay.getQuery(), PostModel.class) // Użyj pełnej kolekcji jako zapytania, ponieważ dane są już przefiltrowane
                            .setLifecycleOwner((LifecycleOwner) getContext()) // Aby automatycznie zarządzać nasłuchiwaniem
                            .build();

                    adapterAllPosts = new AdapterAllPosts(options, getChildFragmentManager(), getContext());
                    recyclerView.setAdapter(adapterAllPosts);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                    recyclerView.setHasFixedSize(true);
                    adapterAllPosts.startListening();
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noPostFound.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void refreshData() {
        swipeRefreshLayout.setRefreshing(true);

        new Handler().postDelayed(() -> {
            posts.clear();

            setupPostsList();

            filterButton.setSelected(false);
            noPostFound.setVisibility(View.GONE);
            // Set refreshing to false after data has been refreshed
            swipeRefreshLayout.setRefreshing(false);
        }, 800);
    }


    private void loadMorePosts() {
        currentPage++;

        loadingMorePostsIndicator.setVisibility(View.VISIBLE);
        loadingMorePostsText.setVisibility(View.VISIBLE);

        setupPostsList();
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
        if (adapterAllPosts != null) {
            adapterAllPosts.startListening();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapterAllPosts != null) {
            adapterAllPosts.stopListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapterAllPosts != null) {
            adapterAllPosts.stopListening();
        }
    }
}
