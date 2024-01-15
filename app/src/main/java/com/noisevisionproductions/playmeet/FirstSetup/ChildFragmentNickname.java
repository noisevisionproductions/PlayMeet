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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;

public class ChildFragmentNickname extends Fragment {
    private AppCompatAutoCompleteTextView getNicknameInput;
    private TextInputLayout getNicknameInputLayout;
    private AppCompatButton setUserInfoButton, cancelButton;
    private AppCompatTextView stepNumber;
    private String nickname;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_nickname, container, false);

        setUpUIElements(view);
        hideKeyboard(view);
        handleSetNicknameButton();

        NavigationUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    public void setUpUIElements(View view) {
        getNicknameInput = view.findViewById(R.id.getNicknameInput);
        setUserInfoButton = view.findViewById(R.id.setUserInfoButtonFirstTime);
        getNicknameInputLayout = view.findViewById(R.id.getNicknameInputLayout);
        stepNumber = view.findViewById(R.id.stepNumber);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    public void hideKeyboard(View view) {
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

    public void handleSetNicknameButton() {
        // podczas wprowadzania nicku, spacje zostają automatycznie usuwane
        deleteSpaces();

        // po kliknięciu w przycisk, który ustawia nick, to najpierw sprawdza walidację tego Nicku, czy spełnia warunki
        setUserInfoButton.setOnClickListener(v -> isNicknameAvailable());
    }

    public void setNickname(View view) {
        onNicknameEntered(nickname, view);
    }

    public void isNicknameAvailable() {
        if (validateNickname()) {
            // jeżeli walidacja nicku przebiegła pomyślnie, to sprawdzam dostępność nicku w bazie danych
            DatabaseReference nicknameReference = FirebaseDatabase.getInstance().getReference().child("UserModel");
            // sprawdzam, czy istnieje w bazie obiekt w kolekcji UserModel, który ma wartość nickname, która porównuje ją z podanym nickiem
            Query query = nicknameReference.orderByChild("nickname").equalTo(nickname);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // odpowiednie informowanie użytkownika, czy nickname jest dostępny czy nie
                        setAutoCompleteTextViewError("Nazwa użytkownika jest zajęta");
                        getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor));
                    } else {
                        getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.successColor));
                        setNickname(requireView());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private boolean validateNickname() {
// minimalna oraz maksymalna ilość znaków dla nicku
        int minLength = 3;
        int maxLength = 30;
        nickname = getNicknameInput.getText().toString();

        if (nickname.isEmpty()) {
// walidacja pustego pola tekstowego
            setAutoCompleteTextViewError("Pole nie może być puste");
            getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor));
            return false;
        } else if (nickname.length() < minLength || nickname.length() > maxLength) {
// walidacja długości nicku
            setAutoCompleteTextViewError("Nazwa użytkownika powinna mieć od " + minLength + " do " + maxLength + " znaków");
            getNicknameInput.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor));
            return false;
        } else {
            setAutoCompleteTextViewError(null);
// jeżeli walidacja przebiegła pomyślnie, to usuwam bledy

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

    public void setAutoCompleteTextViewError(String error) {
        getNicknameInput.setError(error);
        getNicknameInput.requestFocus();
    }

    public void onNicknameEntered(String nickname, View view) {
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

// usuwam stary fragment, aby nowy był kompatybilny i nie było żadnych problemów                 fragmentTransaction.remove(previousFragment);
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
        getNicknameInputLayout.setVisibility(View.GONE);
        stepNumber.setVisibility(View.GONE);
    }
}
