package com.noisevisionproductions.playmeet.PostsManagement.UserPosts;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.noisevisionproductions.playmeet.Adapters.PostsAdapterSavedByUser;
import com.noisevisionproductions.playmeet.DataManagement.PostsDiffCallbackForCopyOfPost;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsSavedByUserFragment extends Fragment {
    private final List<PostCreatingCopy> savedPosts = new ArrayList<>();
    private ProgressBar progressBar;
    private PostsAdapterSavedByUser postsAdapterSavedByUser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_saved_by_user, container, false);
        progressBar = currentView.findViewById(R.id.progressBarLayout);

        showSavedPosts(currentView);

        getAddPostButton();
        return currentView;
    }

    public void showSavedPosts(View view) {
        AppCompatTextView noPostInfo = view.findViewById(R.id.noPostInfo);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView expandableListOfSavedPosts = view.findViewById(R.id.expandableListOfSavedPosts);

        postsAdapterSavedByUser = new PostsAdapterSavedByUser(getContext(), savedPosts);
        expandableListOfSavedPosts.setAdapter(postsAdapterSavedByUser);
        expandableListOfSavedPosts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<PostCreatingCopy> newList = new ArrayList<>();

        try (Realm realm = Realm.getDefaultInstance()) {
            if (currentUser != null) {
                RealmResults<PostCreatingCopy> userPostsFromRealm = realm.where(PostCreatingCopy.class)
                        .equalTo("userId", currentUser.getUid())
                        .findAll();
                if (userPostsFromRealm != null) {
                    for (PostCreatingCopy posts : userPostsFromRealm) {

                        newList.add(realm.copyFromRealm(posts));
                    }
                }

                if (newList.isEmpty()) {
                    noPostInfo.setVisibility(View.VISIBLE);
                    expandableListOfSavedPosts.setVisibility(View.GONE);
                } else {

                    expandableListOfSavedPosts.setVisibility(View.VISIBLE);
                    noPostInfo.setVisibility(View.GONE);

                    updatePostsUsingDiffUtil(newList);
                }
            }
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
