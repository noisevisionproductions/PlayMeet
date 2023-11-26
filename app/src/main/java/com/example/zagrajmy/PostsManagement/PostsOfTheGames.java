package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PageWithPosts.PostDesignAdapter;
import com.example.zagrajmy.PostsManagement.UserPosts.PostsSavedByUser;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsOfTheGames extends AppCompatActivity {
    private final List<PostCreating> posts = new ArrayList<>();
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        setContentView(R.layout.activity_posts_list);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);

        postCreate();

        PostDesignAdapter postDesignAdapter = new PostDesignAdapter(this, posts, null);
        recyclerView.setAdapter(postDesignAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       //postDesignAdapter.notifyDataSetChanged();

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

    public void postCreate() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        RealmResults<PostsSavedByUser> postsSavedByUser = realm.where(PostsSavedByUser.class).equalTo("isPostSavedByUser", true).equalTo("userId", user.getUid()).findAll();
        List<Integer> savedPostIds = new ArrayList<>();  // Zmieniamy typ listy na String
        for (PostsSavedByUser savedPost : postsSavedByUser) {
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

    public void filterSavedPostsByUser(){

    }
}
