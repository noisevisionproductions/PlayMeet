package com.example.zagrajmy.UserManagement;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class UserAccountLogic extends SidePanelBaseActivity {

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
        Button button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
        hideKeyboardAfterSendingMsg();
        View mainView = findViewById(android.R.id.content);
    }

    public void hideKeyboardAfterSendingMsg() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
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
        getSupportFragmentManager().beginTransaction().add(R.id.postCreatingLayout, myFragment).commit();
    }
}
