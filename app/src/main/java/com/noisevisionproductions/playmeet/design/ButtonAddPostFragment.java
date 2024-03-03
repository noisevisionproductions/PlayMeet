package com.noisevisionproductions.playmeet.design;

import android.content.Intent;
import android.widget.Toast;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.postsManagement.PostCreatingLogic;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

public class ButtonAddPostFragment extends BaseFragmentForAddPostButton {
    private Toast toast;

    @Override
    protected int getLayoutId() {
        return R.layout.button_create_post;
    }

    @Override
    protected void onButtonClicked() {
        // mimo, że pojawia się informacja co robi te przycisk, to jest on dostępny tylko dla zarejestrowanych użytkowników, dlatego sprawdzam autoryzację
        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
            Intent intent = new Intent(getActivity(), PostCreatingLogic.class);
            startActivity(intent);
        } else {
            if (getView() != null) {
                if (getContext() != null){
                    ProjectUtils.showLoginSnackBar(getContext());
                }
            }
        }
    }

    // informacja, która informuje użytkownika, co robi przycisk po kliknięciu w niego. Info pojawia się, gdy użytkownik przyciśnie go, ale nie kliknie
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