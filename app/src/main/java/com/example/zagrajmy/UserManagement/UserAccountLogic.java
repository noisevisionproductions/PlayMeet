package com.example.zagrajmy.UserManagement;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.Utilities.NavigationUtils;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.UserManagement.UserFieldsManagement.EditableUserFieldsAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class UserAccountLogic extends SidePanelBaseActivity {

    private List<EditableField> editableFields;

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
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewUserInfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        EditableUserFieldsAdapter adapter = new EditableUserFieldsAdapter(getApplicationContext(), editableFields);
        recyclerView.setAdapter(adapter);

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
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        try (Realm realm = Realm.getDefaultInstance()) {
            if (user != null) {
                UserModel userModel = realm.where(UserModel.class)
                        .equalTo("userId", user.getId())
                        .findFirst();
                if (userModel != null) {
                    String nick = userModel.getNickName();
                    AppCompatTextView displayNickname = findViewById(R.id.nickname);
                    displayNickname.setText(nick);
                }
            }

        }
    }

    public void setUserAvatar() {
        CircleImageView userAvatar = findViewById(R.id.userAvatar);
        AppCompatButton uploadAvatar = findViewById(R.id.uploadAvatar);
        uploadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layoutOfUserData, myFragment).commit();
    }

}
