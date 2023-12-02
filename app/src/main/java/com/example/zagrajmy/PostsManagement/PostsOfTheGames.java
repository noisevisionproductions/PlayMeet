package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PageWithPosts.PostDesignAdapterForAllPosts;
import com.example.zagrajmy.PostsManagement.PostsFiltering.PostsFilter;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsOfTheGames extends SidePanelBaseActivity {
    private final List<PostCreating> posts = new ArrayList<>();
    private Realm realm;
    private RealmResults<PostCreating> allPosts;
    private PostDesignAdapterForAllPosts postDesignAdapterForAllPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_posts_list);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);

        postCreate();

        postDesignAdapterForAllPosts = new PostDesignAdapterForAllPosts(this, posts);
        recyclerView.setAdapter(postDesignAdapterForAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        /*  *//*pozwala na przewijanie postow jeden po drugim*//*
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);*/

        setupDrawerLayout();
        setupNavigationView();
        filterAllPosts();

        getAddPostButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void postCreate() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        RealmResults<PostCreating> postCreating = realm.where(PostCreating.class)
                .equalTo("isPostSavedByUser", true)
                .equalTo("userId", user.getUid()).findAll();
        List<Integer> savedPostIds = new ArrayList<>();  // Zmieniamy typ listy na String
        for (PostCreating savedPost : postCreating) {
            savedPostIds.add(savedPost.getPostId());  // Dodajemy identyfikatory postów do listy
        }

        allPosts = realm.where(PostCreating.class)
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
        getSupportFragmentManager().beginTransaction().add(R.id.layoutOfPostsList, myFragment).commit();
    }

    public void filterAllPosts() {
        AppCompatButton filterButton = findViewById(R.id.postsFilter);
        AppCompatButton deleteFilters = findViewById(R.id.deleteFilters);
        PostsFilter postsFilter = new PostsFilter(postDesignAdapterForAllPosts, posts, filterButton, deleteFilters);
        postsFilter.filterPostsWindow(this);
    }


}
