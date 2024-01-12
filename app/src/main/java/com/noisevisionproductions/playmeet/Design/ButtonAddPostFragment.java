package com.noisevisionproductions.playmeet.Design;

import android.content.Intent;
import android.widget.Toast;

import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostsManagement.PostCreatingLogic;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;
import com.noisevisionproductions.playmeet.R;

public class ButtonAddPostFragment extends BaseFragmentForAddPostButton {
    private Toast toast;

    @Override
    protected int getLayoutId() {
        return R.layout.button_create_post;
    }

    @Override
    protected void onButtonClicked() {
        FirebaseAuthManager authenticationManager = new FirebaseAuthManager();

        if (authenticationManager.isUserLoggedIn()) {
            Intent intent = new Intent(getActivity(), PostCreatingLogic.class);
            startActivity(intent);
        } else {
            if (getView() != null) {
                NavigationUtils.showOnlyForLoggedUserMessage(getView());
            }
        }
    }

    @Override
    protected void showInfo() {
        toast = Toast.makeText(getActivity(), "Stwórz nowy post", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    protected void hideInfo() {
        toast.cancel();
    }
}
