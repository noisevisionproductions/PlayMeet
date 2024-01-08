package com.example.zagrajmy.Design;

import android.content.Intent;
import android.widget.Toast;

import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.PostsManagement.PostCreatingLogic;
import com.example.zagrajmy.R;

public class ButtonAddPostFragment extends BaseFragmentForAddPostButton {
    private Toast toast;

    @Override
    protected int getLayoutId() {
        return R.layout.button_create_post;
    }

    @Override
    protected void onButtonClicked() {
        RealmAuthenticationManager authenticationManager = new RealmAuthenticationManager();

        if (authenticationManager.isUserLoggedIn()) {
            Intent intent = new Intent(getActivity(), PostCreatingLogic.class);
            startActivity(intent);
        } else {
            Toast.makeText(requireContext().getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników!", Toast.LENGTH_SHORT).show();
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
