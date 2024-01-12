package com.noisevisionproductions.playmeet.FirstSetup;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Realm.RealmAppConfig;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;
import com.noisevisionproductions.playmeet.Utilities.SpinnerManager;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class ChildFragmentGender extends Fragment {
    public static final String DEFAULT_GENDER = String.valueOf(R.string.chooseGenderForSpinner);
    public static final String ARG_NICKNAME = "nickname";
    public static final String ARG_CITY = "city";
    private AppCompatButton setInfoButton, cancelButton;
    private AppCompatSpinner genderSpinner;
    private String gender;
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
        App app = RealmAppConfig.getApp();
        User currentUser = app.currentUser();

        if (currentUser != null) {
            try (Realm realm = Realm.getDefaultInstance()) {
                // transakcja udana
                realm.executeTransactionAsync(realm1 -> {
                    UserModel userModel = realm1.where(UserModel.class)
                            .equalTo("userId", currentUser.getId())
                            .findFirst();

                    if (userModel != null) {
                        if (userModel.getNickName() == null) {
                            userModel.setNickName(getArgument(ARG_NICKNAME));
                        }

                        if (getArgument(ARG_CITY) != null) {
                            userModel.setLocation(getArgument(ARG_CITY));
                        }

                        if (gender != null) {
                            userModel.setGender(gender);
                        }

                        Log.d("daneee", getArgument(ARG_NICKNAME) + " " + getArgument(ARG_CITY) + " " + gender);
                    }
                }, this::handleTransactionSuccess, error -> Log.e("Realm Transaction", "Error: " + error.getMessage()));
            }
        }
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

    private String getArgument(String data) {
        // metoda pozwala na zdobycie danych, które użytkownik podawał w trakcie fragmentów
        Bundle args = getArguments();
        return args != null ? args.getString(data) : null;
    }

    public void chooseGender() {
        SpinnerManager.setupGenderSpinner(requireContext(), genderSpinner, R.array.list_of_genders, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
