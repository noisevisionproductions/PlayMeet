package com.noisevisionproductions.playmeet.utilities.admin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.adapters.UserListAdapter;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnUserModelListCompleted;
import com.noisevisionproductions.playmeet.userManagement.UserModel;

import java.util.List;

public class AdminPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_page);

        showAllUsers();
    }

    private void showAllUsers() {
        RecyclerView recyclerViewAllUsers = findViewById(R.id.recyclerViewAllUsers);
        AppCompatImageView allUsersIcon = findViewById(R.id.allUsersIcon);
        allUsersIcon.setOnClickListener(v -> {
            FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();

            if (recyclerViewAllUsers.getVisibility() == View.VISIBLE) {
                recyclerViewAllUsers.setVisibility(View.GONE);
            } else {
                recyclerViewAllUsers.setVisibility(View.VISIBLE);

                firebaseUserRepository.getAllUsers(new OnUserModelListCompleted() {
                    @Override
                    public void onSuccess(List<UserModel> usersList) {
                        UserListAdapter userListAdapter = new UserListAdapter(usersList, getSupportFragmentManager(), getApplicationContext());
                        recyclerViewAllUsers.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                        recyclerViewAllUsers.setAdapter(userListAdapter);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });
            }
        });
    }
}
