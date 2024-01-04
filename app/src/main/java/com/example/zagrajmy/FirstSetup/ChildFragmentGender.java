package com.example.zagrajmy.FirstSetup;

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

import com.example.zagrajmy.Adapters.MySpinnerAdapter;
import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.UserManagement.UserModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

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
                }, this::handleTransactionSuccess, error -> {
                    Log.e("Realm Transaction", "Error: " + error.getMessage());
                });
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
        // tworzenie adaptera Spinner w celu wyświetlenia listy płci z pliku res/values/list_of_genders
        String[] genders = getResources().getStringArray(R.array.list_of_genders);
        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, Arrays.asList(genders));
        mySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        genderSpinner.setAdapter(mySpinnerAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // pierwsza pozycja w pliku z listą płci, to "Wybierz płeć", upewniam się, że jest pomijana
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
