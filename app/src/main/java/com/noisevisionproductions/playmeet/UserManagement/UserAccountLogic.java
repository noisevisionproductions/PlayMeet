package com.noisevisionproductions.playmeet.UserManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement.EditableUserFieldsAdapter;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountLogic extends SidePanelBaseActivity {

    private FirebaseHelper firebaseHelper;
    private CircleImageView avatarImageView;
    private RecyclerView recyclerView;
    private ProgressBar progressBarLayout;
    private AppCompatButton uploadAvatarButton;

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

        AvatarManagement avatarManagement = new AvatarManagement(this, uploadAvatarButton);
        avatarManagement.setupListener();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewUserInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseHelper = new FirebaseHelper();

        progressBarLayout = findViewById(R.id.progressBarLayout);
        uploadAvatarButton = findViewById(R.id.uploadAvatar);
        avatarImageView = findViewById(R.id.userAvatar);

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> NavigationUtils.hideSoftKeyboard(this));

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

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, myFragment).commit();
    }
}
