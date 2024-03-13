package com.noisevisionproductions.playmeet.firstSetup;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.userManagement.NicknameValidation;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

public class ChildFragmentNickname extends Fragment implements NicknameValidation.NicknameValidationCallback {
    private AppCompatAutoCompleteTextView getNicknameInput;
    private TextInputLayout getNicknameInputLayout;
    private AppCompatButton setUserInfoButton, cancelButton;
    private AppCompatTextView stepNumber;
    private String nickname;
    private View view;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_fragment_nickname, container, false);

        setUpUIElements(view);
        hideKeyboard(view);
        handleSetNicknameButton();

        ProjectUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    private void setUpUIElements(@NonNull View view) {
        getNicknameInput = view.findViewById(R.id.getNicknameInput);
        setUserInfoButton = view.findViewById(R.id.setUserInfoButtonFirstTime);
        getNicknameInputLayout = view.findViewById(R.id.getNicknameInputLayout);
        stepNumber = view.findViewById(R.id.stepNumber);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    private void hideKeyboard(@NonNull View view) {
        View.OnTouchListener onTouchListener = (v, event) -> {

            // kiedy użytkownik jest w trybie wprowadzania tekstu, to po kliknięciu w layout,
            // chowa klawiaturę oraz anuluje skupienie z pola tekstowego
            // NavigationUtils.hideKeyboardForFragments(requireActivity(), view);
            v.performClick();
            getNicknameInput.clearFocus();
            return false;
        };
        view.setOnTouchListener(onTouchListener);
    }

    private void handleSetNicknameButton() {
        // podczas wprowadzania nicku, spacje zostają automatycznie usuwane
        deleteSpaces();

        setUserInfoButton.setOnClickListener(v -> {
            nickname = getNicknameInput.getText().toString().trim();

            if (NicknameValidation.validateNickname(nickname, this)) {
                NicknameValidation.isNicknameAvailable(nickname, this);
            }
        });

        // po kliknięciu w przycisk, który ustawia nick, to najpierw sprawdza walidację tego Nicku, czy spełnia warunki

    }

    public void deleteSpaces() {
        getNicknameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(@NonNull CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    // usuwam spacje poprzez zamianę " " na ""
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

    private void setAutoCompleteTextViewError(String error) {
        getNicknameInput.setError(error);
        getNicknameInput.requestFocus();
    }

    private void onNicknameEntered(String nickname, @NonNull View view) {
        this.nickname = nickname;
        // pobieram id z FrameLayout z aktualnego layoutu

        FrameLayout nicknameLayoutFragment = view.findViewById(R.id.nicknameLayoutFragment);
        // dodaje animacje przejścia między fragmentami
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // chowa wszystkie obiekty z aktualnego layoutu, aby nie pojawiały się na kolejnym fragmencie
                hideLayout();

                // ustawiam kolejny fragment, który ma się pojawić i zastępuje aktualny fragment nowym
                ChildFragmentCity childFragmentCity = new ChildFragmentCity();
                // tworzę Bundle, który przechowuje podany nickname w celu przeniesienia go do kolejnego fragmentu, ponieważ dopiero na ostatnim fragmencie zostaną wszystkie zebrane dane zapisywane w bazie danych
                Bundle args = new Bundle();
                args.putString("nickname", nickname);
                childFragmentCity.setArguments(args);

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                // animacja, gdzie stary fragment chowa się za ekran w lewo, a nowy fragment pojawia się spoza okna z prawej strony
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container, childFragmentCity, "tag_child_fragment_city");

                Fragment previousFragment = getParentFragmentManager().findFragmentByTag("tag_child_fragment_nickname");
                if (previousFragment != null) {
                    // usuwam stary fragment, aby nowy był kompatybilny i nie było żadnych problemów
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

    private void hideLayout() {
        getNicknameInput.setVisibility(View.GONE);
        setUserInfoButton.setVisibility(View.GONE);
        getNicknameInputLayout.setVisibility(View.GONE);
        stepNumber.setVisibility(View.GONE);
    }

    @Override
    public void onNicknameValidationError(String error) {
        setAutoCompleteTextViewError(error);

    }

    @Override
    public void onNicknameValidationSuccess() {
    }

    @Override
    public void onNicknameAvailable() {
        onNicknameEntered(nickname, view);
    }

    @Override
    public void onNicknameUnavailable(String error) {
        setAutoCompleteTextViewError(error);
    }
}
