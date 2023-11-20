package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PageWithPosts.PostDesignAdapter;
import com.example.zagrajmy.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsOfTheGames extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_posts_list);

        postCreate();

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

    public void postCreate() {
        List<PostCreating> posts = new ArrayList<>();

        Realm realm = Realm.getDefaultInstance();

        RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();
        if (allPosts != null) {
            posts.addAll(realm.copyFromRealm(allPosts));
        }
        realm.close();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);
        PostDesignAdapter postDesignAdapter = new PostDesignAdapter(this, posts);
        recyclerView.setAdapter(postDesignAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
