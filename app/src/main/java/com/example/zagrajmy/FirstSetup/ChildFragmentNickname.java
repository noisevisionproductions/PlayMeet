package com.example.zagrajmy.FirstSetup;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.UserManagement.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class ChildFragmentNickname extends Fragment {
    private AppCompatAutoCompleteTextView getNicknameInput;
    private TextInputLayout getNicknameInputLayout;
    private AppCompatButton setUserInfoButton, checkIfNickAvailable;
    private String nickname;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_nickname, container, false);

        getNicknameInput = view.findViewById(R.id.getNicknameInput);
        setUserInfoButton = view.findViewById(R.id.setUserInfoButtonFirstTime);
        checkIfNickAvailable = view.findViewById(R.id.checkIfNickAvailable);
        getNicknameInputLayout = view.findViewById(R.id.getNicknameInputLayout);

        hideKeyboard(view);
        setNickname(view);
        checkValidationsForNicknameField();
        return view;
    }

    public void hideKeyboard(View view) {
        View.OnTouchListener onTouchListener = (v, event) -> {
            NavigationUtils.hideKeyboardForFragments(requireActivity(), view);
            v.performClick();
            getNicknameInput.clearFocus();
            return false;
        };
        view.setOnTouchListener(onTouchListener);
    }

    public void setNickname(View view) {
        setUserInfoButton.setOnClickListener(v -> {
            if (validateNickname(v)) {
                App app = RealmAppConfig.getApp();
                User currentUser = app.currentUser();

                if (currentUser != null) {
                /*    try (Realm realm = Realm.getDefaultInstance()) {
                        realm.executeTransactionAsync(realm1 -> {
                            UserModel userModel = realm1.where(UserModel.class)
                                    .equalTo("userId", currentUser.getId())
                                    .findFirst();
                            if (userModel != null && userModel.getNickName() == null) {*/
                    //userModel.setNickName(nickname);

                    onNicknameEntered(nickname, view);

                          /*  }
                        });
                    }*/
                }
            }
        });
    }

    public void checkValidationsForNicknameField() {
        checkIfNickAvailable.setOnClickListener(v -> {
            if (validateNickname(v)) {
                try (Realm realm = Realm.getDefaultInstance()) {
                    RealmResults<UserModel> users = realm.where(UserModel.class)
                            .findAll();
                    for (UserModel userModel : users) {
                        String existingNickname = userModel.getNickName();
                        if (existingNickname != null && existingNickname.equals(nickname)) {
                            setAutoCompleteTextViewError("Nazwa użytkownika jest zajęta");
                            getNicknameInput.setTextColor(Color.RED);
                            return;
                        }
                    }
                }
                getNicknameInput.setTextColor(Color.GREEN);
            }
        });
    }

    private boolean validateNickname(View v) {
        int minLength = 3;
        int maxLength = 30;
        nickname = Objects.requireNonNull(getNicknameInput.getText()).toString();

        if (nickname.isEmpty()) {
            setAutoCompleteTextViewError("Pole nie może być puste");
            getNicknameInput.setTextColor(Color.RED);
            return false;
        } else if (nickname.length() < minLength || nickname.length() > maxLength) {
            setAutoCompleteTextViewError("Nazwa użytkownika powinna mieć od " + minLength + " do " + maxLength + " znaków");
            getNicknameInput.setTextColor(Color.RED);
            return false;
        } else {
            setAutoCompleteTextViewError(null);
            return true;
        }
    }

    public void setAutoCompleteTextViewError(String error) {
        getNicknameInput.setError(error);
        getNicknameInput.requestFocus();
    }

    public void onNicknameEntered(String nickname, View view) {
        this.nickname = nickname;

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getNicknameInput.setVisibility(View.GONE);
                setUserInfoButton.setVisibility(View.GONE);
                checkIfNickAvailable.setVisibility(View.GONE);
                getNicknameInputLayout.setVisibility(View.GONE);

                ChildFragmentCity childFragmentCity = new ChildFragmentCity();

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

                fragmentTransaction.replace(R.id.nicknameLayoutFragment, childFragmentCity);
                fragmentTransaction.commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }
}
