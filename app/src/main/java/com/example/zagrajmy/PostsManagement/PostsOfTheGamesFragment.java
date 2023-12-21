package com.example.zagrajmy.PostsManagement;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Adapters.PostDesignAdapterForAllPosts;
import com.example.zagrajmy.DataManagement.PostDiffCallback;
import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PostsFiltering.PostsFilter;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsOfTheGamesFragment extends Fragment {
    private final List<PostCreating> posts = new ArrayList<>();
    private PostDesignAdapterForAllPosts postDesignAdapterForAllPosts;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.activity_posts_list, container, false);
        progressBar = currentView.findViewById(R.id.progressBarLayout);

        showAllPosts(currentView);
        getAddPostButton();

        return currentView;
    }

    protected void showAllPosts(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_posts);

        postDesignAdapterForAllPosts = new PostDesignAdapterForAllPosts(getContext(), posts);
        recyclerView.setAdapter(postDesignAdapterForAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        if (AuthenticationManager.isUserLoggedIn()) {
            postCreateForLoggedInUser();
        } else {
            postCreateForUnregisteredUser();
        }

        //*pozwala na przewijanie postow jeden po drugim*//*
        /*PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);*/

        filterAllPosts(view);

        getAddPostButton();
    }

    public void postCreateForUnregisteredUser() {
        try (Realm realm = Realm.getDefaultInstance()) {

            RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();

            List<PostCreating> newPostCreatingList = new ArrayList<>(allPosts);

            updatePostsUsingDiffUtil(newPostCreatingList);
        }
    }


    public void postCreateForLoggedInUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try (Realm realm = Realm.getDefaultInstance()) {

            RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();
            List<PostCreating> newPostCreatingList = new ArrayList<>();

            // filtrowanie ze wszystkich postow czy sa stworzone przez zalogowanego uzytkownika,
            // jezeli tak, to nie bedzie on wyswietlany w menu glownym
            List<Integer> savedPostIds = new ArrayList<>();
            for (PostCreating post : allPosts) {
                if (post.isPostSavedByUser()) {
                    assert user != null;
                    if (post.getUserId().equals(user.getUid())) {
                        savedPostIds.add(post.getPostId());
                    }
                }
            }

            // filtrowanie wszystkich postów
            for (PostCreating post : allPosts) {
                if (post.isCreatedByUser()) {
                    assert user != null;
                    if (!post.getUserId().equals(user.getUid()) && !savedPostIds.contains(post.getPostId())) {
                        newPostCreatingList.add(post);
                    }
                }
            }
            updatePostsUsingDiffUtil(newPostCreatingList);
        }
    }

    // obliczanie roznicy miedzy postami w celu szybszego ladowania, pozwala na dzialanie na watku w tle
    public void updatePostsUsingDiffUtil(List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.posts);

        new Thread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                posts.clear();
                posts.addAll(newPosts);
                progressBar.setVisibility(View.GONE);
                diffResult.dispatchUpdatesTo(postDesignAdapterForAllPosts);
            });
        }).start();
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getChildFragmentManager().beginTransaction().add(R.id.layoutOfPostsList, myFragment).commit();
    }

    public void filterAllPosts(View view) {
        AppCompatButton filterButton = view.findViewById(R.id.postsFilter);
        AppCompatButton deleteFilters = view.findViewById(R.id.deleteFilters);
        PostsFilter postsFilter = new PostsFilter(postDesignAdapterForAllPosts, posts, filterButton, deleteFilters);
        postsFilter.filterPostsWindow((Activity) getContext());
    }
}
