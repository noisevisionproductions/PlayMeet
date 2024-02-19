package com.noisevisionproductions.playmeet.firstSetup;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.noisevisionproductions.playmeet.adapters.ToastManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.postsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.utilities.AESDataEncryption;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.SpinnerManager;

import java.util.HashMap;
import java.util.Objects;

public class ChildFragmentGender extends Fragment {
    public static final String DEFAULT_GENDER = String.valueOf(R.string.chooseGenderForSpinner);
    public static final String ARG_NICKNAME = "nickname";
    public static final String ARG_CITY = "city";
    private String gender;
    private AppCompatButton setInfoButton, cancelButton;
    private AppCompatSpinner genderSpinner;
    private boolean genderSelected = false;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_gender, container, false);

        setUpUIElements(view);
        chooseGender();
        handleSetInfoButton();
        ProjectUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    public void setUpUIElements(@NonNull View view) {
        setInfoButton = view.findViewById(R.id.setInfoButton);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    public void handleSetInfoButton() {
        // jeżeli płeć została wybrana z listy, dane użytkownika zostają zapisane w bazie danych
        setInfoButton.setOnClickListener(v -> {
            if (genderSelected) {
                try {
                    saveUserData();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                ToastManager.showToast(requireContext(), getString(R.string.noGenderChosenError));
            }
        });
    }

    public void saveUserData() throws Exception {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // dodaje zebrane argumenty do HashMap, aby wszystkie były jako jeden obiekt ułatwiający zapisanie w bazie danych
        HashMap<String, Object> userUpdate = new HashMap<>();
        userUpdate.put("nickname", getArgument(ARG_NICKNAME));
        userUpdate.put("location", AESDataEncryption.encrypt(Objects.requireNonNull(getArgument(ARG_CITY))));
        userUpdate.put("gender", AESDataEncryption.encrypt(gender));

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
        ToastManager.showToast(requireContext(), "Dane zostały zapisane");
    }

    private void handleTransactionError(@NonNull DatabaseError error) {
        ToastManager.showToast(requireContext(), "Błąd: " + error.getMessage());
        Log.e("Firebase RealmTime Database error", "Saving first setup data in DB " + error.getMessage());
    }

    @Nullable
    private String getArgument(String data) {
        // metoda pozwala na zdobycie danych, które użytkownik podawał w trakcie fragmentów
        Bundle args = getArguments();
        return args != null ? args.getString(data) : null;
    }

    public void chooseGender() {
        SpinnerManager.setupGenderSpinner(requireContext(), genderSpinner, R.array.list_of_genders, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> parent, View view, int position, long id) {
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
