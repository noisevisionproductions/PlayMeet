package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.paging.LoadState;
import androidx.paging.PagingConfig;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

public class PostsOfTheGamesFragment extends Fragment {
    private FirestorePostsDisplay firestorePostsDisplay;
    private AdapterAllPosts adapterAllPosts;
    private ProgressBar progressBar;
    private AppCompatButton filterButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private AppCompatTextView noPostFound;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_posts_list, container, false);

        setupView(view);

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(this::refreshData, 100));

        setRecyclerView();
        handleBackPressed();
        filterAllPosts(view);

        return view;
    }

    private void setupView(@NonNull View view) {
        boolean isUserLoggedIn = FirebaseAuthManager.isUserLoggedIn();
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            firestorePostsDisplay = new FirestorePostsDisplay(isUserLoggedIn, firebaseHelper.getCurrentUser().getUid());
        }
        progressBar = view.findViewById(R.id.progressBarLayout);
        noPostFound = view.findViewById(R.id.noPostFound);
        filterButton = view.findViewById(R.id.postsFilter);

        recyclerView = view.findViewById(R.id.recycler_view_posts);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setRecyclerView() {
        Query query = firestorePostsDisplay.getQuery();

        int pageSize = 5;
        PagingConfig config = new PagingConfig(pageSize, 1, true, 10, 100);

        FirestorePagingOptions<PostModel> options = new FirestorePagingOptions.Builder<PostModel>()
                .setLifecycleOwner((LifecycleOwner) requireContext())
                .setQuery(query, config, PostModel.class)
                .build();

        adapterAllPosts = new AdapterAllPosts(options, getChildFragmentManager(), getContext());
        recyclerView.setAdapter(adapterAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);

        adapterAllPosts.addLoadStateListener(loadStates -> {
            if (loadStates.getRefresh() instanceof LoadState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
            return null;
        });
    }

    public void refreshData() {
        int refreshLength = 600;
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(() -> {
            setRecyclerView();

            filterButton.setSelected(false);
            noPostFound.setVisibility(View.GONE);

            swipeRefreshLayout.setRefreshing(false);
        }, refreshLength);
    }

    private void filterAllPosts(@NonNull View view) {
     /*   AppCompatButton deleteFilters = view.findViewById(R.id.deleteFilters);
        filterButton.setOnClickListener(v -> {
            PostsFilter postsFilter = new PostsFilter(adapterAllPosts, posts, filterButton, deleteFilters, noPostFound);
            if (getContext() != null) {
                postsFilter.filterPostsWindow((Activity) getContext());
            }
        });*/
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
}
