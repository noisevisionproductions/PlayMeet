package com.example.zagrajmy.PostsManagement;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Adapters.PostDesignAdapterForAllPosts;
import com.example.zagrajmy.Design.ButtonAddPostFragment;
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
    private Realm realm;
    private PostDesignAdapterForAllPosts postDesignAdapterForAllPosts;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        View currentView = inflater.inflate(R.layout.activity_posts_list, container, false);

        showAllPosts(currentView);
        getAddPostButton();

        return currentView;
    }

    protected void showAllPosts(View view) {
        realm = Realm.getDefaultInstance();


        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_posts);

        postCreate();

        postDesignAdapterForAllPosts = new PostDesignAdapterForAllPosts(getContext(), posts);
        recyclerView.setAdapter(postDesignAdapterForAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        /*  *//*pozwala na przewijanie postow jeden po drugim*//*
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);*/

        filterAllPosts(view);

        getAddPostButton();
    }


    public void postCreate() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        RealmResults<PostCreating> postCreating = realm.where(PostCreating.class)
                .equalTo("isPostSavedByUser", true)
                .equalTo("userId", user.getUid())
                .findAll();
        List<Integer> savedPostIds = new ArrayList<>();  // Zmieniamy typ listy na String
        for (PostCreating savedPost : postCreating) {
            savedPostIds.add(savedPost.getPostId());  // Dodajemy identyfikatory postów do listy
        }

        RealmResults<PostCreating> allPosts = realm.where(PostCreating.class)
                .equalTo("isCreatedByUser", true)
                .notEqualTo("userId", user.getUid())
                .not().in("postId", savedPostIds.toArray(new Integer[0])).findAll();

        if (allPosts != null) {
            posts.addAll(realm.copyFromRealm(allPosts));
        }
        realm.close();
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
