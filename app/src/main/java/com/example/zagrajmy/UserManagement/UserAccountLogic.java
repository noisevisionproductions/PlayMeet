package com.example.zagrajmy.UserManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

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
    }

    public void greetNickname() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String nick = user.getDisplayName();
            if (nick != null) {
                AppCompatTextView displayNickname = findViewById(R.id.nickname);
                displayNickname.setText(nick);
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
