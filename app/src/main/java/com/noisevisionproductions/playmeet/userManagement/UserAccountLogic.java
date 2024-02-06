package com.noisevisionproductions.playmeet.userManagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.noisevisionproductions.playmeet.adapters.ToastManager;
import com.noisevisionproductions.playmeet.design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.userManagement.userFieldsManagement.EditableUserFieldsAdapter;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

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

        deleteAvatarButton.setOnClickListener(v -> {
            deleteUserAvatar();
            ToastManager.showToast(getApplicationContext(), "Avatar usunięty");
        });
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
        if (firebaseHelper.getCurrentUser() != null) {
            String userId = firebaseHelper.getCurrentUser().getUid();
            firebaseHelper.getUserAvatar(getApplicationContext(), userId, avatarImageView);
        }
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Usunięcie obrazu avatara z Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars").child(userId);
            storageReference.delete().addOnSuccessListener(aVoid -> {
                // Avatar usunięty ze Storage
            }).addOnFailureListener(e -> Log.e("Avatar", "Error while deleting avatar from Firebase Storage " + e.getMessage()));

            // Usunięcie linku do avatara z Firebase Realtime Database
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(userId);
            userReference.child("avatar").removeValue().addOnSuccessListener(aVoid -> {
                // Link do avatara usunięty z bazy danych
            }).addOnFailureListener(e -> Log.e("Avatar", "Error while deleting avatar link from Firebase Database " + e.getMessage()));
        }
    }


    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, myFragment).commit();
    }
}
