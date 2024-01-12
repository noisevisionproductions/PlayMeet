package com.noisevisionproductions.playmeet.UserManagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Realm.RealmAppConfig;
import com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement.EditableUserFieldsAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class UserAccountLogic extends SidePanelBaseActivity {

    private List<EditableField> editableFields;
    private CircleImageView avatarImageView;
    private AppCompatButton uploadAvatarButton;

    public UserAccountLogic() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        setupDrawerLayout();
        setupNavigationView();

        greetNickname();
        getAddPostButton();
        initEditableFields();
        setupRecyclerView();
        setUserAvatar();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewUserInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditableUserFieldsAdapter adapter = new EditableUserFieldsAdapter(getApplicationContext(), editableFields);
        recyclerView.setAdapter(adapter);

        uploadAvatarButton = findViewById(R.id.uploadAvatar);
        avatarImageView = findViewById(R.id.userAvatar);
        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
    }

    private void initEditableFields() {
        editableFields = getUserDataFromRealm();
    }

    private List<EditableField> getUserDataFromRealm() {
        List<EditableField> userData = new ArrayList<>();
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        try (Realm realm = Realm.getDefaultInstance()) {
            if (user != null) {
                UserModel userModel = realm.where(UserModel.class)
                        .equalTo("userId", user.getId())
                        .findFirst();
                if (userModel != null) {
                    userData.add(new EditableField(getString(R.string.provideName), userModel.getName(), false, true, false, EditableField.FieldType.FIELD_TYPE_EDITTEXT));
                    userData.add(new EditableField(getString(R.string.provideCity), userModel.getLocation(), true, true, true, EditableField.FieldType.FIELD_TYPE_CITY_SPINNER));
                    userData.add(new EditableField(getString(R.string.provideAge), userModel.getBirthDay(), true, true, true, EditableField.FieldType.FIELD_TYPE_AGE_SPINNER));
                    userData.add(new EditableField(getString(R.string.provideAboutYou), userModel.getAboutMe(), false, true, false, EditableField.FieldType.FIELD_TYPE_EDITTEXT));
                }
            }
        }
        return userData;
    }

    public void greetNickname() {
        AppCompatTextView displayNickname = findViewById(R.id.nickname);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserModel").child("userId");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        String userNickname = userModel.getUserId();
                        displayNickname.setText(userNickname);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setUserAvatar() {
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode() == Activity.RESULT_OK) {
                Intent data = o.getData();
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    avatarImageView.setImageURI(selectedImageUri);
                }
            }
        });
        uploadAvatarButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layoutOfUserData, myFragment).commit();
    }

}
