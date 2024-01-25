package com.noisevisionproductions.playmeet.UserManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.IntentSanitizer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement.EditableUserFieldsAdapter;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountLogic extends SidePanelBaseActivity {

    private FirebaseHelper firebaseHelper;
    private CircleImageView avatarImageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBarLayout;
    private AppCompatButton deleteAvatarButton;

    public UserAccountLogic() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        setupDrawerLayout();
        setupNavigationView();
        setupRecyclerView();

        greetNickname();
        getAddPostButton();

        deleteAvatarButton.setOnClickListener(v -> deleteUserAvatar());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewUserInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseHelper = new FirebaseHelper();

        progressBarLayout = findViewById(R.id.progressBarLayout);
        deleteAvatarButton = findViewById(R.id.deleteAvatarButton);
        avatarImageView = findViewById(R.id.userAvatar);

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        ProjectUtils.backToMainMenuButton(button, this);

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(this));

        getUserDataFromRealm();
    }

    public void greetNickname() {
        AppCompatTextView displayNickname = findViewById(R.id.nickname);
        firebaseHelper.getData(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        String userNickname = userModel.getNickname();
                        displayNickname.setText(userNickname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Printing user nickname " + error.getMessage());
            }
        }, "UserModel");
        String userId = firebaseHelper.getCurrentUser().getUid();
        firebaseHelper.getUserAvatar(getApplicationContext(), userId, avatarImageView);
    }

    private void getUserDataFromRealm() {
        // zanim pola EditableField się załadują wraz z nickiem z bazy danych, wyświetlam ikonkę ładowania
        progressBarLayout.setVisibility(View.VISIBLE);
        firebaseHelper.getData(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        // pobieram dane użytkownika z UserModel z bazy firebase, a następnie pokazuje je już wybrane w wyświetlanych w polach
                        // z tego względu, że używam RecyclerView do wyświetlania pól EditableField, muszę dodawać je do listy
                        // a następnie ustawić ją w adapterze
                        List<EditableField> userData = new ArrayList<>();

                        userData.add(new EditableField(getString(R.string.provideName), userModel.getName(), false, true, false, EditableField.FieldType.FIELD_TYPE_EDITTEXT));
                        userData.add(new EditableField(getString(R.string.provideAboutYou), userModel.getAboutMe(), false, true, false, EditableField.FieldType.FIELD_TYPE_EDITTEXT));
                        userData.add(new EditableField(getString(R.string.provideCity), userModel.getLocation(), true, true, true, EditableField.FieldType.FIELD_TYPE_CITY_SPINNER));
                        userData.add(new EditableField(getString(R.string.provideAge), userModel.getAge(), true, true, true, EditableField.FieldType.FIELD_TYPE_AGE_SPINNER));
                        EditableUserFieldsAdapter adapter = new EditableUserFieldsAdapter(getApplicationContext(), userData);
                        recyclerView.setAdapter(adapter);
                    }
                    progressBarLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        }, "UserModel");
    }

    public void deleteUserAvatar() {
        String userId = firebaseHelper.getCurrentUser().getUid();

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(userId);
        userReference.child("avatar").removeValue();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars").child(userId);
        storageReference.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Avatar usunięty", Toast.LENGTH_SHORT).show();
                    avatarImageView.setImageDrawable(null);
                })
                .addOnFailureListener(e -> Log.e("Avatar", "Error while deleting from Firebase Storage " + e.getMessage()));
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, myFragment).commit();
    }
}
