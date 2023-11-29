package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PageWithPosts.PostDesignAdapterForAllPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.SidePanelMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsOfTheGames extends AppCompatActivity {
    private final List<com.example.zagrajmy.PostCreating> posts = new ArrayList<>();
    private Realm realm;


    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private FirebaseUser user;

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    private void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationViewSidePanel);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_posts_list);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);


        postCreate();

        PostDesignAdapterForAllPosts postDesignAdapterForAllPosts = new PostDesignAdapterForAllPosts(this, posts);
        recyclerView.setAdapter(postDesignAdapterForAllPosts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupDrawerLayout();

        //postDesignAdapter.notifyDataSetChanged();
        SidePanelMenu sidePanelMenu = new SidePanelMenu(this);
        sidePanelMenu.manageDrawerButtonsNew();

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
        RealmResults<PostCreating> postCreating = realm.where(PostCreating.class).equalTo("isPostSavedByUser", true).equalTo("userId", user.getUid()).findAll();
        List<Integer> savedPostIds = new ArrayList<>();  // Zmieniamy typ listy na String
        for (PostCreating savedPost : postCreating) {
            savedPostIds.add(savedPost.getPostId());  // Dodajemy identyfikatory postów do listy
        }

        RealmResults<com.example.zagrajmy.PostCreating> allPosts = realm.where(com.example.zagrajmy.PostCreating.class)
                .equalTo("isCreatedByUser", true)
                .notEqualTo("userId", user.getUid())
                .not().in("postId", savedPostIds.toArray(new Integer[0])).findAll();

        if (allPosts != null) {
            posts.addAll(realm.copyFromRealm(allPosts));
        }
        realm.close();
    }

    public void filterSavedPostsByUser(){

    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layoutOfPostsList, myFragment).commit();
    }
}
