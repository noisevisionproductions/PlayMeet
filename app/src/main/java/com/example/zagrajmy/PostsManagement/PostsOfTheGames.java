package com.example.zagrajmy.PostsManagement;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostsManagement.PageWithPosts.MyAdapter;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsOfTheGames extends AppCompatActivity {
    List<PostCreating> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_posts);

        postCreate();

        MyAdapter myAdapter = new MyAdapter(posts);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

    public void postCreate() {
        Realm realm = Realm.getDefaultInstance();

        RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();
        if (allPosts != null){
            posts.addAll(realm.copyFromRealm(allPosts));
        }
        realm.close();

    }

}
