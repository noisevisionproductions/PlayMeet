package com.noisevisionproductions.playmeet.PostsManagement.UserPosts;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.DataManagement.PostsDiffCallbackForCopyOfPost;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.R;

import java.util.ArrayList;
import java.util.List;

public class PostsSavedByUserFragment extends Fragment {
    private final List<PostCreatingCopy> savedPosts = new ArrayList<>();
    private ProgressBar progressBar;
    private PostsAdapterSavedByUser postsAdapterSavedByUser;
    private RecyclerView expandableListOfSavedPosts;
    private AppCompatTextView noPostInfo;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_by_user, container, false);

        setupView(view);
        showSavedPosts();
        getAddPostButton();

        return view;
    }

    public void setupView(View view) {
        noPostInfo = view.findViewById(R.id.noPostInfo);
        progressBar = view.findViewById(R.id.progressBarLayout);

        expandableListOfSavedPosts = view.findViewById(R.id.expandableListOfSavedPosts);
        postsAdapterSavedByUser = new PostsAdapterSavedByUser(getContext(), savedPosts, noPostInfo);
        expandableListOfSavedPosts.setAdapter(postsAdapterSavedByUser);
        expandableListOfSavedPosts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void showSavedPosts() {
        FirebaseAuthManager authenticationManager = new FirebaseAuthManager();
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();

        if (authenticationManager.isUserLoggedIn()) {
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
                        noPostInfo.setVisibility(View.VISIBLE);
                        expandableListOfSavedPosts.setVisibility(View.GONE);
                    } else {
                        noPostInfo.setVisibility(View.GONE);
                        expandableListOfSavedPosts.setVisibility(View.VISIBLE);
                        updatePostsUsingDiffUtil(userSavedPosts);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("PostsSavedByUserFragment", "Błąd podczas odczytu danych z Firebase", error.toException());
                }
            });
        }
    }

    public void updatePostsUsingDiffUtil(List<PostCreatingCopy> newPosts) {
        final List<PostCreatingCopy> oldPosts = new ArrayList<>(this.savedPosts);

        new Thread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostsDiffCallbackForCopyOfPost(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                savedPosts.clear();
                savedPosts.addAll(newPosts);
                diffResult.dispatchUpdatesTo(postsAdapterSavedByUser);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getParentFragmentManager().beginTransaction().add(R.id.layoutOfAddedPost, myFragment).commit();
    }
}
