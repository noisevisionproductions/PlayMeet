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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.dataManagement.PostsDiffCallbackForCopyOfPost;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

import java.util.ArrayList;
import java.util.List;

public class PostsCreatedByUserFragment extends Fragment {
    private View view;
    private final List<PostCreating> postsCreatedByUser = new ArrayList<>();
    private final List<PostCreatingCopy> savedPosts = new ArrayList<>();
    private ProgressBar progressBar;
    private AdapterCreatedByUserPosts adapterCreatedByUserPosts;
    private AdapterSavedByUserPosts adapterSavedByUserPosts;
    private RecyclerView postsCreatedByUserRecyclerView, postsSignedIntoByUserRecyclerView;
    private AppCompatTextView noPostsCreatedInfo, noPostsSignedIntoInfo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_created_by_user, container, false);

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
        progressBar = view.findViewById(R.id.progressBarLayout);
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
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();
            if (FirebaseAuthManager.isUserLoggedIn()) {
                // uzyskuje referencję do danych PostCreating stworzonego w Firebase
                DatabaseReference userPostsReference = FirebaseDatabase.getInstance().getReference("PostCreating");
                // nastepnie pobieram posty z bazy, które mają takie samo userId, co aktualnie zalogowany użytkownik
                userPostsReference.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PostCreating> newUserPosts = new ArrayList<>();
                        // wszystkie posty przypisane do użytkownika dodaję do listy newUserPosts, aby później je wyświetlić zaktualizowane za pomocą  DiffUtil
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            PostCreating postCreating = postSnapshot.getValue(PostCreating.class);
                            if (postCreating != null) {
                                newUserPosts.add(postCreating);
                            }
                        }
                        if (newUserPosts.isEmpty()) {
                            // jeżeli nie ma żadnych postów, to wyświetlam informację na temat pustej listy
                            postsCreatedByUserRecyclerView.setVisibility(View.GONE);
                            noPostsCreatedInfo.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            postsCreatedByUserRecyclerView.setVisibility(View.VISIBLE);
                            noPostsCreatedInfo.setVisibility(View.GONE);
                            updatePostsUsingDiffUtil(newUserPosts);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "Printing posts that User created " + error.getMessage());
                    }
                });
            }
        } else {
            postsCreatedByUserRecyclerView.setVisibility(View.GONE);
            noPostsCreatedInfo.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updatePostsUsingDiffUtil(@NonNull List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.postsCreatedByUser);
        new Thread(() -> {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                postsCreatedByUser.clear();
                postsCreatedByUser.addAll(newPosts);
                diffResult.dispatchUpdatesTo(adapterCreatedByUserPosts);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    private void showPostsSignedUpIntoByUser() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {

            String currentUserId = firebaseHelper.getCurrentUser().getUid();

            if (FirebaseAuthManager.isUserLoggedIn()) {
                DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(currentUserId);

                savedPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PostCreatingCopy> userSavedPosts = new ArrayList<>();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            PostCreatingCopy postCreatingCopy = postSnapshot.getValue(PostCreatingCopy.class);
                            if (postCreatingCopy != null && postCreatingCopy.getSavedByUser()) {
                                userSavedPosts.add(postCreatingCopy);
                            }
                        }
                        if (userSavedPosts.isEmpty()) {
                            noPostsSignedIntoInfo.setVisibility(View.VISIBLE);
                            postsSignedIntoByUserRecyclerView.setVisibility(View.GONE);
                        } else {
                            noPostsSignedIntoInfo.setVisibility(View.GONE);
                            postsSignedIntoByUserRecyclerView.setVisibility(View.VISIBLE);
                            updatePostsSignedUpIntoUsingDiffUtil(userSavedPosts);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "Printing posts that user signed up to " + error.getMessage());
                    }
                });
            } else {
                if (getContext() != null) {
                    ProjectUtils.showLoginSnackBar(getContext());
                }
            }
        }
    }

    private void updatePostsSignedUpIntoUsingDiffUtil(@NonNull List<PostCreatingCopy> newPosts) {
        final List<PostCreatingCopy> oldPosts = new ArrayList<>(this.savedPosts);

        new Thread(() -> {
//            progressBar.setVisibility(View.VISIBLE);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostsDiffCallbackForCopyOfPost(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                savedPosts.clear();
                savedPosts.addAll(newPosts);
                diffResult.dispatchUpdatesTo(adapterSavedByUserPosts);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }
}
