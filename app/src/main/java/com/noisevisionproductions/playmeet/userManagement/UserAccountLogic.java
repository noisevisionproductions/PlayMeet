package com.noisevisionproductions.playmeet.userManagement;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.utilities.AESDataEncryption;
import com.noisevisionproductions.playmeet.utilities.NicknameValidation;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.ToastManager;
import com.noisevisionproductions.playmeet.utilities.UserModelDecryptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAccountLogic extends Fragment implements NicknameValidation.NicknameValidationCallback {

    private FirebaseHelper firebaseHelper;
    private CircleImageView avatarImageView;
    private ProgressBar progressBarLayout;
    private AppCompatButton deleteAvatarButton, addPhotoButton;
    private String currentUser, newNickname;
    private AppCompatTextView displayNickname, greetUser;
    private AppCompatSpinner ageSpinner;
    private AppCompatAutoCompleteTextView nameInput, aboutYouInput, cityTextView;
    private AppCompatImageView infoIconUserAccount;
    private LinearLayoutCompat linearLayout;
    private AlertDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_page, container, false);

        setupUI(view);
        greetNickname(view);
        getUserData();
        setUserAvatar();
        chooseCity();

        return view;
    }

    private void setupUI(View view) {
        firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            currentUser = firebaseHelper.getCurrentUser().getUid();
        }

        progressBarLayout = view.findViewById(R.id.progressBarLayout);
        addPhotoButton = view.findViewById(R.id.addPhotoButton);
        deleteAvatarButton = view.findViewById(R.id.deleteAvatarButton);
        avatarImageView = view.findViewById(R.id.userAvatar);
        nameInput = view.findViewById(R.id.nameInput);
        aboutYouInput = view.findViewById(R.id.aboutYouInput);
        cityTextView = view.findViewById(R.id.cityTextField);
        ageSpinner = view.findViewById(R.id.ageSpinner);
        linearLayout = view.findViewById(R.id.linearLayout);
        infoIconUserAccount = view.findViewById(R.id.infoIconUserAccount);

        setButtons(view);
    }

    private void setButtons(View view) {
        ToastManager.createToolTip(getString(R.string.informationAboutProvidedData), infoIconUserAccount);

        AppCompatImageView editNickname = view.findViewById(R.id.editNickname);
        editNickname.setOnClickListener(v -> editNickname());

        deleteAvatarButton.setOnClickListener(v -> {
            deleteUserAvatar();
            avatarImageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.sample_avatar));
            ToastManager.showToast(requireContext(), "Avatar usunięty");
        });

        AppCompatButton saveInfoButton = view.findViewById(R.id.saveInfoButton);
        saveInfoButton.setOnClickListener(v -> {
            try {
                saveUserInfo();
                Objects.requireNonNull(requireActivity().getCurrentFocus()).clearFocus();
            } catch (Exception e) {
                Log.e("Encrypt user data error UserAccountLogic", "Encrypt user data error UserAccountLogic" + e.getMessage());
            }
        });

        LinearLayoutCompat mainLayout = view.findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));
    }

    private void saveUserInfo() throws Exception {
        String name = nameInput.getText() != null ? nameInput.getText().toString() : "";
        String about = aboutYouInput.getText() != null ? aboutYouInput.getText().toString() : "";
        String city = cityTextView.getText() != null ? cityTextView.getText().toString() : "";
        String age = ageSpinner.getSelectedItem() != null ? ageSpinner.getSelectedItem().toString() : "";

        updateUserInfo(name, about, city, age);

        ToastManager.showToast(requireContext(), "Dane zapisane!");
    }

    private void updateUserInfo(String name, String about, String city, String age) throws Exception {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUser);
        AESDataEncryption encryption = new AESDataEncryption(getContext());

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", encryption.encrypt(name));
        userUpdates.put("aboutMe", encryption.encrypt(about));
        if (city.isEmpty() || !ProjectUtils.isCityChosenFromTheList(city, requireContext())) {
            cityTextView.setError("Wybierz prawidłowe miasto");
        } else {
            userUpdates.put("location", encryption.encrypt(city));
        }
        userUpdates.put("age", encryption.encrypt(age));

        userReference.updateChildren(userUpdates).addOnSuccessListener(aVoid -> Log.d("Updating user info in DB", "Dane użytkownika zostały zaktualizowane.")).addOnFailureListener(e -> Log.e("Updating user info in DB", "Błąd podczas aktualizacji danych użytkownika: " + e.getMessage()));
    }

    private void getUserData() {
        progressBarLayout.setVisibility(View.VISIBLE);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUser);
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBarLayout.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        try {
                            UserModel decryptedUserModel = UserModelDecryptor.decryptUserModel(getContext(), userModel);

                            nameInput.setText(decryptedUserModel.getName());
                            aboutYouInput.setText(decryptedUserModel.getAboutMe());
                            cityTextView.setText(decryptedUserModel.getLocation());

                            SpinnerUpdater.updateSpinnerData(ageSpinner, decryptedUserModel.getAge(), requireContext());
                        } catch (Exception e) {
                            Log.e("Decryption error", "Error decrypting user data in user account " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void greetNickname(View view) {
        displayNickname = view.findViewById(R.id.nickname);
        greetUser = view.findViewById(R.id.greetUser);
        firebaseHelper.getData(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        String userNickname = userModel.getNickname();
                        if (userNickname != null) {
                            greetUser.setVisibility(View.VISIBLE);
                            displayNickname.setText(userNickname);
                        } else {
                            greetUser.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Printing user nickname " + error.getMessage());
            }
        }, "UserModel");

        firebaseHelper.getUserAvatar(requireContext(), currentUser, avatarImageView);

    }

    private void editNickname() {
        final AppCompatAutoCompleteTextView editText = new AppCompatAutoCompleteTextView(requireContext());
        int minHeightInDp = 50;
        int minHeightInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minHeightInDp, getResources().getDisplayMetrics());
        editText.setMinHeight(minHeightInPx);
        editText.setHint("Wpisz nowy nick");

        dialog = new AlertDialog.Builder(getContext()).setTitle("Edycja nicku").setView(editText).setPositiveButton("Zapisz", null).setNegativeButton("Anuluj", (dialogInterface, i) -> dialogInterface.dismiss()).create();

        dialog.setOnShowListener(dialogInterface -> {
            // nadpisuję domyślne zachowanie przycisku, aby zapobiec zamykaniu dialogu
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                newNickname = editText.getText().toString().trim().replace(" ", "");
                NicknameValidation.validateNickname(newNickname, this);
            });
        });
        dialog.show();
    }

    private void setUserAvatar() {
        AvatarManagement avatarManagement = new AvatarManagement(this, addPhotoButton, avatarImageView);
        // Ustawianie słuchaczy
        avatarManagement.setupListeners();
        refreshFragment();
    }

    public void deleteUserAvatar() {
        if (currentUser != null) {
            // Usunięcie obrazu avatara z Firebase Storage
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars").child(currentUser);
            storageReference.delete().addOnSuccessListener(aVoid -> {
                // Avatar usunięty ze Storage
            }).addOnFailureListener(e -> Log.e("Avatar", "Error while deleting avatar from Firebase Storage " + e.getMessage()));

            // Usunięcie linku do avatara z Firebase Realtime Database
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUser);
            userReference.child("avatar").removeValue().addOnSuccessListener(aVoid -> {
                // Link do avatara usunięty z bazy danych
            }).addOnFailureListener(e -> Log.e("Avatar", "Error while deleting avatar link from Firebase Database " + e.getMessage()));
        }
    }

    @Override
    public void onNicknameValidationError(String error) {
        ToastManager.showToast(requireContext(), error);
    }

    @Override
    public void onNicknameValidationSuccess() {
        saveNicknameToDB(newNickname, dialog);
    }

    @Override
    public void onNicknameAvailable() {

    }

    @Override
    public void onNicknameUnavailable(String error) {
        ToastManager.showToast(requireContext(), error);
    }

    private void saveNicknameToDB(String newNickname, AlertDialog dialog) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("UserModel").child(currentUser).child("nickname");
        userReference.setValue(newNickname).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ToastManager.showToast(requireContext(), "Nick zapisany pomyślnie");
                dialog.dismiss();
                updateNickname();
            } else {
                ToastManager.showToast(requireContext(), "Błąd przy zapisywaniu nicku");
            }
        });
    }

    private void updateNickname() {
        displayNickname.setText(newNickname);
    }

    private void refreshFragment() {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.detach(this);
        fragmentTransaction.attach(this);
        fragmentTransaction.commit();
    }

    private void chooseCity() {
        // używam specjalnie stworzonej klasy, która irytuje przez cały plik xml,
        // w którym znajdują się miasta i wybiera tylko te, które potrzebuje
        List<String> cityList = new ArrayList<>(CityXmlParser.parseCityNames(requireContext()));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, cityList);
        cityTextView.setAdapter(cityAdapter);
    }
}