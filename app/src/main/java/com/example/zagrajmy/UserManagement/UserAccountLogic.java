package com.example.zagrajmy.UserManagement;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class UserAccountLogic extends SidePanelBaseActivity {

    private List<EditableField> editableFields;

    public UserAccountLogic() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        ConstraintLayout layoutOfUserData = findViewById(R.id.layoutOfUserData);
        layoutOfUserData.setOnClickListener(v -> hideKeyboardOnTextFocusCancel());

        setupDrawerLayout();
        setupNavigationView();

        greetNickname();
        getAddPostButton();
        setupEditableFields();
        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);
        View mainView = findViewById(android.R.id.content);
    }

    private void setupEditableFields() {
        initEditableFields();
        addEditableFieldsToLayout();
    }

    private void initEditableFields() {
        editableFields = new ArrayList<>();
        editableFields.add(new EditableField("Twoje imię:", "test", true, false));
        editableFields.add(new EditableField("Twoja płeć:", "test", true, false));
        editableFields.add(new EditableField("Skąd pochodzisz:", "test", true, false));
        editableFields.add(new EditableField("Ulubione sporty:", "test", true, false));
        editableFields.add(new EditableField("Coś o sobie:", "test", true, false));
    }

    private void addEditableFieldsToLayout() {
        LinearLayoutCompat linearLayoutCompat = findViewById(R.id.linearLayout);

        // ustawianie tekstu danych użytkownika
        for (EditableField field : editableFields) {
            // ustawienie wszystkich obiektów obok siebie
            LinearLayoutCompat rowLayout = new LinearLayoutCompat(this);
            rowLayout.setOrientation(LinearLayoutCompat.HORIZONTAL);

            // edycja tekstu danych podanych przez użytkownika
            EditText view = new EditText(this);
            view.setEnabled(false);
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);

            LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                    0,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );
            layoutParams.weight = 1;
            view.setLayoutParams(layoutParams);

            view.setText(field.value);

            // ustawianie tekstu z labela, tzn informacji o jakich danych mowa, np. "Twoje imię"
            TextView labelTextView = createLabelTextView(field.label);

            linearLayoutCompat.addView(labelTextView);
            rowLayout.addView(view);

            AppCompatButton editButton = createEditButton();
            rowLayout.addView(editButton);

            if (field.isEditable) {
                editButton.setOnClickListener(v -> handleEditButtonClick(field, view, editButton));
            }

            linearLayoutCompat.addView(rowLayout);
        }
    }

    private TextView createLabelTextView(String labelText) {
        // ustawianie tekstu z labela, tzn informacji o jakich danych mowa, np. "Twoje imię"
        TextView labelTextView = new TextView(this);
        labelTextView.setText(labelText);
        Typeface typeface = Typeface.create("serif", Typeface.BOLD);
        labelTextView.setTypeface(typeface);
        return labelTextView;
    }

    private void setupTextFieldWithUserInformation(EditText editText) {
        // edycja wprowadzanego tekstu
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setMaxLines(1);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(50);
        editText.setFilters(filters);

        // Ustawienie kursora na końcu pola tekstowego
        editText.setSelection(editText.getText().length());

        // Pobranie informacji od użytkownika, jakiego typu jest główna metoda wprowadzania tekstu
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private AppCompatButton createEditButton() {
        // ustawianie przycisku, ktore zajmuje się edycją danych użytkownika w danym polu
        AppCompatButton editTextButton = new AppCompatButton(this);
        editTextButton.setText("Edytuj");
        return editTextButton;
    }

    private void handleEditButtonClick(EditableField field, View view, AppCompatButton editButton) {
        if (view instanceof EditText) {
            setupTextFieldWithUserInformation((EditText) view);
            field.isEditMode = !field.isEditMode;

            if (field.isEditable) {
                if (field.isEditMode) {
                    editButton.setText("Zapisz");
                } else {
                    editButton.setText("Edytuj");
                }
            }
        }
    }

    public void hideKeyboardOnTextFocusCancel() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void greetNickname() {
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        try (Realm realm = Realm.getDefaultInstance()) {
            if (user != null) {
                UserModel userModel = realm.where(UserModel.class)
                        .equalTo("userId", user.getId())
                        .findFirst();
                if (userModel != null) {
                    String nick = userModel.getNickName();
                    AppCompatTextView displayNickname = findViewById(R.id.nickname);
                    displayNickname.setText(nick);
                }
            }

        }
    }


    public void setUserAvatar() {
        CircleImageView userAvatar = findViewById(R.id.userAvatar);
        AppCompatButton uploadAvatar = findViewById(R.id.uploadAvatar);
        uploadAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layoutOfUserData, myFragment).commit();
    }

}
