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
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.SpinnerManager;
import com.noisevisionproductions.playmeet.utilities.ToastManager;
import com.noisevisionproductions.playmeet.utilities.dataEncryption.AESDataEncryption;

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

    public void saveUserData() {
        try {
            AESDataEncryption encryption = new AESDataEncryption(getContext());
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                UserProfileChangeRequest profileUpdateFirstSetup = new UserProfileChangeRequest.Builder()
                        .setDisplayName(getArgument(ARG_NICKNAME))
                        .build();

                HashMap<String, Object> userUpdate = new HashMap<>();
                userUpdate.put("nickname", getArgument(ARG_NICKNAME));
                userUpdate.put("location", encryption.encrypt(Objects.requireNonNull(getArgument(ARG_CITY))));
                userUpdate.put("gender", encryption.encrypt(gender));

                FirebaseUserRepository userRepository = new FirebaseUserRepository();
                userRepository.updateUser(firebaseUser.getUid(), userUpdate, new OnCompletionListener() {
                    @Override
                    public void onSuccess() {
                        handleTransactionSuccess();
                        firebaseUser.updateProfile(profileUpdateFirstSetup);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        handleTransactionError(DatabaseError.fromException(e));
                    }
                });
            }
        } catch (Exception e) {
            Log.e("Saving first user setup info", "First user setup error " + e.getMessage());
        }
    }

    private void handleTransactionSuccess() {
        // gdy transakcja zakonczy się sukcesem, DialogFragment zostaje usuwany
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof DialogFragment) {
            ((DialogFragment) parentFragment).dismiss();
        }
        // zmieniam wygląd górnego paska
        if (getActivity() instanceof ActivityMainMenu) {
            ((ActivityMainMenu) getActivity()).onUserInfoUpdated();
        }
        ToastManager.showToast(requireContext(), getString(R.string.dataSaved));
    }

    private void handleTransactionError(@NonNull DatabaseError error) {
        ToastManager.showToast(requireContext(), getString(R.string.error) + error.getMessage());
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
