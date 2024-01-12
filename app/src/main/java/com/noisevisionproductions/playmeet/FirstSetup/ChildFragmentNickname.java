package com.noisevisionproductions.playmeet.FirstSetup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChildFragmentNickname extends Fragment {
    private AppCompatAutoCompleteTextView getNicknameInput;
    private TextInputLayout getNicknameInputLayout;
    private AppCompatButton setUserInfoButton, checkIfNickAvailable, cancelButton;
    private AppCompatTextView stepNumber;
    private String nickname;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_nickname, container, false);

        setUpUIElements(view);
        hideKeyboard(view);
        handleSetNicknameButton(view);
        checkValidationsForNicknameField();
        NavigationUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    public void setUpUIElements(View view) {
        getNicknameInput = view.findViewById(R.id.getNicknameInput);
        setUserInfoButton = view.findViewById(R.id.setUserInfoButtonFirstTime);
        checkIfNickAvailable = view.findViewById(R.id.checkIfNickAvailable);
        getNicknameInputLayout = view.findViewById(R.id.getNicknameInputLayout);
        stepNumber = view.findViewById(R.id.stepNumber);
        cancelButton = view.findViewById(R.id.cancelButton);
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

    public void handleSetNicknameButton(View view) {
        deleteSpaces();
        setUserInfoButton.setOnClickListener(v -> setNickname(view));
    }

    public void setNickname(View view) {
        if (validateNickname()) {
            onNicknameEntered(nickname, view);
        }
    }

    public void checkValidationsForNicknameField() {
        checkIfNickAvailable.setOnClickListener(v -> {
            if (validateNickname()) {
                try (Realm realm = Realm.getDefaultInstance()) {
                    RealmResults<UserModel> users = realm.where(UserModel.class)
                            .findAll();
                    for (UserModel userModel : users) {
                        String existingNickname = userModel.getNickName();
                        if (existingNickname != null && existingNickname.equals(nickname)) {
                            setAutoCompleteTextViewError("Nazwa użytkownika jest zajęta");
                            getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor));
                            return;
                        }
                    }
                }
                getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.successColor));
            }
        });
    }

    private boolean validateNickname() {
        int minLength = 3;
        int maxLength = 30;
        nickname = getNicknameInput.getText().toString();


        if (nickname.isEmpty()) {
            setAutoCompleteTextViewError("Pole nie może być puste");
            getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor));
            return false;
        } else if (nickname.length() < minLength || nickname.length() > maxLength) {
            setAutoCompleteTextViewError("Nazwa użytkownika powinna mieć od " + minLength + " do " + maxLength + " znaków");
            getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor));
            return false;
        } else {
            setAutoCompleteTextViewError(null);
            return true;
        }
    }

    public void deleteSpaces() {
        getNicknameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String filteredText = s.toString().replace(" ", "");
                    getNicknameInput.setText(filteredText);
                    getNicknameInput.setSelection(filteredText.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setAutoCompleteTextViewError(String error) {
        getNicknameInput.setError(error);
        getNicknameInput.requestFocus();
    }

    public void onNicknameEntered(String nickname, View view) {
        this.nickname = nickname;

        FrameLayout nicknameLayoutFragment = view.findViewById(R.id.nicknameLayoutFragment);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // chowa wszystkie obiekty z aktualnego layoutu, aby nie pojawiały się na kolejnym fragmencie
                hideLayout();

                ChildFragmentCity childFragmentCity = new ChildFragmentCity();
                Bundle args = new Bundle();
                args.putString("nickname", nickname);
                childFragmentCity.setArguments(args);

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container, childFragmentCity, "tag_child_fragment_city");

                Fragment previousFragment = getParentFragmentManager().findFragmentByTag("tag_child_fragment_nickname");
                if (previousFragment != null) {
                    fragmentTransaction.remove(previousFragment);
                }
                nicknameLayoutFragment.setVisibility(View.INVISIBLE);

                fragmentTransaction.commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public void hideLayout() {
        getNicknameInput.setVisibility(View.GONE);
        setUserInfoButton.setVisibility(View.GONE);
        checkIfNickAvailable.setVisibility(View.GONE);
        getNicknameInputLayout.setVisibility(View.GONE);
        stepNumber.setVisibility(View.GONE);
    }
}