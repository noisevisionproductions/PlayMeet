package com.example.zagrajmy.PostsManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.RealmDatabaseManagement;

public class PostCreatingLogic extends AppCompatActivity {
    RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
    PostCreating postCreating = new PostCreating();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creating);

        Button button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);

        createPost();
    }

    public void createPost() {
        Button createPost = findViewById(R.id.submitPost);
        createPost.setOnClickListener(view -> {
            setUniqueId();
            setSportType();
            realmDatabaseManagement.addPost(postCreating);

            Toast.makeText(PostCreatingLogic.this, "Post utworzony!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PostCreatingLogic.this, PostsOfTheGames.class);
            startActivity(intent);
        });
    }

    public void setUniqueId() {
        UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();

        int uniqueId;
        do {
            uniqueId = uniqueIdGenerator.generateUniqueId();
        } while (realmDatabaseManagement.checkIfIdExists(uniqueId));
        Log.d("test", String.valueOf(uniqueId));

        postCreating.setUniqueId(uniqueId);
    }

    public void setSportType() {
        AppCompatSpinner chooseSport = findViewById(R.id.arrays_sport_names);

        chooseSport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSport = (String) parent.getItemAtPosition(position);

                Log.d("test", selectedSport);
                postCreating.setSportType(selectedSport);
                realmDatabaseManagement.addPost(postCreating);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
