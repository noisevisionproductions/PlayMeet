package com.example.zagrajmy.PostsManagement.PageWithPosts;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UsersActivePosts extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_posts);

        buttonForUserPosts();

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

    public void buttonForUserPosts() {
        AppCompatButton yourPosts = findViewById(R.id.yourPosts);
        RecyclerView expandableListOfYourPosts = findViewById(R.id.expandableListOfYourPosts);

        Realm realm = Realm.getDefaultInstance();
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        assert userFirebase != null;
        String test = userFirebase.getUid();
        User user = new User();
        List<PostCreating> newUserPosts = new ArrayList<>();

       // RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class).findAll();
        RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class).equalTo("userId", user.getUserId()).findAll();

        if (userPostsFromRealm != null) {
            newUserPosts.addAll(realm.copyFromRealm(userPostsFromRealm));
        }
        realm.close();
        yourPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableListOfYourPosts.getVisibility() == View.GONE) {
                    expandableListOfYourPosts.setVisibility(View.VISIBLE);
                    PostDesignAdapter postDesignAdapter = new PostDesignAdapter(getApplicationContext(), newUserPosts);
                    expandableListOfYourPosts.setAdapter(postDesignAdapter);
                    expandableListOfYourPosts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                } else {
                    expandableListOfYourPosts.setVisibility(View.GONE);
                }
            }
        });
    }

    public void adapterCall() {
        List<String> expandableListTitle = new ArrayList<String>();
        HashMap<String, List<PostCreating>> expandableListDetail = new HashMap<String, List<PostCreating>>();
        expandableListTitle.add("TWOJE POSTY");
        expandableListTitle.add("ZAPISANE POSTY");

        Realm realm = Realm.getDefaultInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        List<PostCreating> newUserPosts = new ArrayList<>();
        RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class).equalTo("userId", String.valueOf(currentUser)).findAll();
        if (userPostsFromRealm != null) {
            newUserPosts.addAll(realm.copyFromRealm(userPostsFromRealm));
        }
        realm.close();
    }
}