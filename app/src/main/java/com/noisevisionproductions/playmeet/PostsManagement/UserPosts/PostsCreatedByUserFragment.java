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

import com.noisevisionproductions.playmeet.Adapters.PostsAdapterCreatedByUser;
import com.noisevisionproductions.playmeet.DataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Realm.RealmAppConfig;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class PostsCreatedByUserFragment extends Fragment {
    private final List<PostCreating> postsCreatedByUser = new ArrayList<>();
    private ProgressBar progressBar;
    private PostsAdapterCreatedByUser postsAdapterCreatedByUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_created_by_user, container, false);

        progressBar = currentView.findViewById(R.id.progressBarLayout);
        showUserPosts(currentView);

        getAddPostButton();
        return currentView;
    }

    public void showUserPosts(View view) {
        AppCompatTextView noPosts = view.findViewById(R.id.noPostInfo);
        RecyclerView expandableListOfYourPosts = view.findViewById(R.id.expandableListOfUserPosts);
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        postsAdapterCreatedByUser = new PostsAdapterCreatedByUser(getContext(), postsCreatedByUser);
        expandableListOfYourPosts.setAdapter(postsAdapterCreatedByUser);
        expandableListOfYourPosts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FirebaseAuthManager authenticationManager = new FirebaseAuthManager();

        if (authenticationManager.isUserLoggedIn() && user != null) {
            List<PostCreating> newUserPosts = new ArrayList<>();

            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class)
                        .equalTo("userId", user.getId())
                        .findAll();

                if (userPostsFromRealm != null) {
                    for (PostCreating posts : userPostsFromRealm) {
                        newUserPosts.add(realm.copyFromRealm(posts));
                    }
                }
            }
            if (newUserPosts.isEmpty()) {
                expandableListOfYourPosts.setVisibility(View.GONE);
                noPosts.setVisibility(View.VISIBLE);
            } else {
                expandableListOfYourPosts.setVisibility(View.VISIBLE);
                noPosts.setVisibility(View.GONE);
                updatePostsUsingDiffUtil(newUserPosts);
            }
        }
    }

    public void updatePostsUsingDiffUtil(List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.postsCreatedByUser);

        new Thread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                postsCreatedByUser.clear();
                postsCreatedByUser.addAll(newPosts);
                diffResult.dispatchUpdatesTo(postsAdapterCreatedByUser);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getParentFragmentManager().beginTransaction().add(R.id.layoutOfCreatedPosts, myFragment).commit();
    }
}
