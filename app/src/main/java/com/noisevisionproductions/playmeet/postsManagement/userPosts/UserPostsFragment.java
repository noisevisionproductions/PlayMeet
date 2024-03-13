package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostsDisplay;

import java.util.ArrayList;
import java.util.List;

public class UserPostsFragment extends Fragment {
    private View view;
    private final List<PostModel> postsCreatedByUser = new ArrayList<>();
    private final List<PostModel> savedPosts = new ArrayList<>();
    private ProgressBar progress_bar_yourPosts, progress_bar_registeredPosts;
    private AdapterCreatedByUserPosts adapterCreatedByUserPosts;
    private AdapterSavedByUserPosts adapterSavedByUserPosts;
    private FirestorePostsDisplay firestorePostsDisplay;
    private RecyclerView postsCreatedByUserRecyclerView, postsSignedIntoByUserRecyclerView;
    private AppCompatTextView noPostsCreatedInfo, noPostsSignedIntoInfo;
    private String currentUserId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_posts, container, false);

        setupView();

        setPostsCreatedByUserRecyclerView();
        setPostsSignedIntoByUserRecyclerView();

        showPostsCreatedByUser();
        showPostsSignedUpIntoByUser();

        return view;
    }

    private void setupView() {
        noPostsCreatedInfo = view.findViewById(R.id.noPostsCreatedInfo);
        noPostsSignedIntoInfo = view.findViewById(R.id.noPostsSignedIntoInfo);
        progress_bar_yourPosts = view.findViewById(R.id.progress_bar_yourPosts);
        progress_bar_registeredPosts = view.findViewById(R.id.progress_bar_registeredPosts);
        firestorePostsDisplay = new FirestorePostsDisplay();

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            currentUserId = firebaseHelper.getCurrentUser().getUid();
        }
    }

    private void setPostsCreatedByUserRecyclerView() {
        postsCreatedByUserRecyclerView = view.findViewById(R.id.expandableListOfUserPosts);
        adapterCreatedByUserPosts = new AdapterCreatedByUserPosts(getContext(), getChildFragmentManager(), postsCreatedByUser, noPostsCreatedInfo);
        postsCreatedByUserRecyclerView.setAdapter(adapterCreatedByUserPosts);
        postsCreatedByUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setPostsSignedIntoByUserRecyclerView() {
        postsSignedIntoByUserRecyclerView = view.findViewById(R.id.expandableListOfSavedPosts);
        adapterSavedByUserPosts = new AdapterSavedByUserPosts(getContext(), getParentFragmentManager(), savedPosts, noPostsSignedIntoInfo);
        postsSignedIntoByUserRecyclerView.setAdapter(adapterSavedByUserPosts);
        postsSignedIntoByUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void showPostsCreatedByUser() {
        if (currentUserId != null && FirebaseAuthManager.isUserLoggedIn()) {
            firestorePostsDisplay.getUserPosts(currentUserId, (posts, e) -> {

                if (e != null) {
                    Log.e("Fetching user posts", "Fetching user posts: " + e.getMessage());
                    postsCreatedByUserRecyclerView.setVisibility(View.GONE);
                    noPostsCreatedInfo.setVisibility(View.VISIBLE);
                    return;
                }
                if (posts != null && !posts.isEmpty()) {
                    postsCreatedByUserRecyclerView.setVisibility(View.VISIBLE);
                    noPostsCreatedInfo.setVisibility(View.GONE);
                    updatePostsCreatedByUserDiffUtil(posts);
                } else {
                    postsCreatedByUserRecyclerView.setVisibility(View.GONE);
                    noPostsCreatedInfo.setVisibility(View.VISIBLE);
                }
                progress_bar_yourPosts.setVisibility(View.GONE);
            });
        } else {
            postsCreatedByUserRecyclerView.setVisibility(View.GONE);
            noPostsCreatedInfo.setVisibility(View.VISIBLE);
            progress_bar_yourPosts.setVisibility(View.GONE);
        }
    }

    private void showPostsSignedUpIntoByUser() {
        firestorePostsDisplay.getRegisteredPosts(currentUserId, (posts, e) -> {
            if (e != null) {
                Log.e("Fetching registered posts", "Fetching registered posts: " + e.getMessage());
                noPostsSignedIntoInfo.setVisibility(View.VISIBLE);
                postsSignedIntoByUserRecyclerView.setVisibility(View.GONE);
                return;
            }
            if (posts != null && !posts.isEmpty()) {
                noPostsSignedIntoInfo.setVisibility(View.GONE);
                postsSignedIntoByUserRecyclerView.setVisibility(View.VISIBLE);
                updatePostsSignedUpIntoUsingDiffUtil(posts);
            } else {
                noPostsSignedIntoInfo.setVisibility(View.VISIBLE);
                postsSignedIntoByUserRecyclerView.setVisibility(View.GONE);
            }
            progress_bar_registeredPosts.setVisibility(View.GONE);
        });
    }

    private void updatePostsCreatedByUserDiffUtil(@NonNull List<PostModel> newPosts) {
        final List<PostModel> oldPosts = new ArrayList<>(this.postsCreatedByUser);
        new Thread(() -> {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                postsCreatedByUser.clear();
                postsCreatedByUser.addAll(newPosts);
                diffResult.dispatchUpdatesTo(adapterCreatedByUserPosts);
                postsCreatedByUserRecyclerView.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void updatePostsSignedUpIntoUsingDiffUtil(@NonNull List<PostModel> newPosts) {
        final List<PostModel> oldPosts = new ArrayList<>(this.savedPosts);
        new Thread(() -> {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                savedPosts.clear();
                savedPosts.addAll(newPosts);
                diffResult.dispatchUpdatesTo(adapterSavedByUserPosts);
                postsSignedIntoByUserRecyclerView.setVisibility(View.VISIBLE);
            });
        }).start();
    }
}
