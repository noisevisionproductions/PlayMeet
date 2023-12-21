package com.example.zagrajmy.PostsManagement.UserPosts;

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

import com.example.zagrajmy.DataManagement.PostDiffCallback;
import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.Adapters.PostDesignAdapterForUserActivity;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsFavoriteByUserFragment extends Fragment {
    private final List<PostCreating> savedPosts = new ArrayList<>();
    private ProgressBar progressBar;
    private PostDesignAdapterForUserActivity postDesignAdapterForUserActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.posts_added_as_favorite_fragment, container, false);
        progressBar = currentView.findViewById(R.id.progressBarLayout);

        showSavedPosts(currentView);

        getAddPostButton();
        return currentView;
    }

    public void showSavedPosts(View view) {
        AppCompatTextView noPostInfo = view.findViewById(R.id.noPostInfo);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        RecyclerView expandableListOfSavedPosts = view.findViewById(R.id.expandableListOfSavedPosts);
        postDesignAdapterForUserActivity = new PostDesignAdapterForUserActivity(getContext(), savedPosts);
        expandableListOfSavedPosts.setAdapter(postDesignAdapterForUserActivity);
        expandableListOfSavedPosts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        List<PostCreating> newList = new ArrayList<>();

        try (Realm realm = Realm.getDefaultInstance()) {
            if (user != null) {
                RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class)
                        .equalTo("userId", user.getUid())
                        .equalTo("isPostSavedByUser", true)
                        .findAll();
                if (userPostsFromRealm != null) {
                    newList.addAll(userPostsFromRealm);
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

    public void updatePostsUsingDiffUtil(List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.savedPosts);

        new Thread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                savedPosts.clear();
                savedPosts.addAll(newPosts);
                diffResult.dispatchUpdatesTo(postDesignAdapterForUserActivity);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getParentFragmentManager().beginTransaction().add(R.id.layoutOfAddedPost, myFragment).commit();
    }
}
