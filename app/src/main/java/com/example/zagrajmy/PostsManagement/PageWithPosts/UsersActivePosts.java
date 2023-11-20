package com.example.zagrajmy.PostsManagement.PageWithPosts;

import android.os.Bundle;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UsersActivePosts extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_posts);

        adapterCall();

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

    public void adapterCall(){
        List<String> expandableListTitle = new ArrayList<String>();
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        expandableListTitle.add("TWOJE POSTY");
        expandableListTitle.add("ZAPISANE POSTY");

        expandableListDetail.put("TWOJE POSTY", Collections.singletonList("tsettsetsetse"));
        expandableListDetail.put("ZAPISANE POSTY", Collections.singletonList("lololooll"));

        ExpandableListView expandableListView = findViewById(R.id.expandableList);
       /* AdapterForUsersPosts adapter = new AdapterForUsersPosts(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(adapter);*/
    }
}