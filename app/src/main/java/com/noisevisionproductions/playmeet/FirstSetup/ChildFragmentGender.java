package com.noisevisionproductions.playmeet.FirstSetup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;
import com.noisevisionproductions.playmeet.Utilities.SpinnerManager;

import java.util.HashMap;

public class ChildFragmentGender extends Fragment {
    public static final String DEFAULT_GENDER = String.valueOf(R.string.chooseGenderForSpinner);
    public static final String ARG_NICKNAME = "nickname";
    public static final String ARG_CITY = "city";
    private String gender;
    private AppCompatButton setInfoButton, cancelButton;
    private AppCompatSpinner genderSpinner;
    private boolean genderSelected = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_gender, container, false);

        setUpUIElements(view);
        chooseGender();
        handleSetInfoButton();
        NavigationUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    public void setUpUIElements(View view) {
        setInfoButton = view.findViewById(R.id.setInfoButton);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    public void handleSetInfoButton() {
        // jeżeli płeć została wybrana z listy, dane użytkownika zostają zapisane w bazie danych
        setInfoButton.setOnClickListener(v -> {
            if (genderSelected) {
                saveUserData();
            } else {
                Toast.makeText(requireContext(), R.string.noGenderChosenError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveUserData() {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // dodaje zebrane argumenty do HashMap, aby wszystkie były jako jeden obiekt ułatwiający zapisanie w bazie danych

        HashMap<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("nickname", getArgument(ARG_NICKNAME));
        userUpdate.put("location", getArgument(ARG_CITY));
        userUpdate.put("gender", gender);

        // tworzę obiekt, który pozwoli na ustawienie różnych informacji o użytkowniku, w moim wypadku jest to nickname
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(getArgument(ARG_NICKNAME))
                .build();

        if (firebaseUser != null) {
            // wykorzystuje obiekt, w którym ustawiałem nickname, aby przypisać go użytkownikowi w Firebase,
            // dzięki temu będę mógł pobierać nickname wraz z UserID
            firebaseUser.updateProfile(profileUpdate);
        }


        // używam klasy FirebaseHelper, aby z łatwością zapisać wszystkie zebrane dane z fragmentów w bazie danych,
        // podając tylko poprzednio powstały hashmap firebaseHelper.updateDataUsingHashMap(userUpdate,
        firebaseHelper.updateDataUsingHashMap(userUpdate,
                aVoid -> handleTransactionSuccess(),
                e -> handleTransactionError(DatabaseError.fromException(e)), "UserModel");
    }

    private void handleTransactionSuccess() {
        // gdy transakcja zakonczy się sukcesem, DialogFragment zostaje usuwany
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogFragment) {
            ((DialogFragment) parentFragment).dismiss();
        }
        // zmieniam wygląd górnego paska
        if (getActivity() instanceof MainMenuPosts) {
            ((MainMenuPosts) getActivity()).onUserInfoUpdated();
        }
        Toast.makeText(requireContext(), "Dane zostały zapisane", Toast.LENGTH_SHORT).show();
    }

    private void handleTransactionError(DatabaseError error) {
        Toast.makeText(requireContext(), "Błąd: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private String getArgument(String data) {
        // metoda pozwala na zdobycie danych, które użytkownik podawał w trakcie fragmentów
        Bundle args = getArguments();
        return args != null ? args.getString(data) : null;
    }

    public void chooseGender() {
        SpinnerManager.setupGenderSpinner(requireContext(), genderSpinner, R.array.list_of_genders, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // zapisuje wybraną z listy płeć, chyba że jest to domyślna pozycja, która jest na indeksie 0,
                // to wyświetlam błąd, aby użytkownik wybrał inną pozycję
                String selectedGender = (String) parent.getItemAtPosition(position);
                if (position > 0) {
                    setSelectedGender(selectedGender);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genderSelected = false;
            }
        });
    }

    public void setSelectedGender(String gender) {
        this.gender = gender;
        genderSelected = !TextUtils.equals(gender, DEFAULT_GENDER);
    }
}
