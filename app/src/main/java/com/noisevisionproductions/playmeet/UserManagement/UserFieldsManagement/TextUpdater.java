package com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.firebase.auth.FirebaseUser;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.EditableField;

public class TextUpdater {
    public static void updateTextValue(EditableField field, EditText editText, AppCompatSpinner citySpinner, AppCompatSpinner ageSpinner, AppCompatButton editButton, View view, Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        FirebaseUser user = firebaseHelper.getCurrentUser();
        // jeżeli pole jest spinnerem, to pobiera wyznaczoną wartość i zapisuje ją w bazie,
        // tak samo dotyczy sprawdzenie czy jest to pole możliwe do edycji, a jeżeli tak, to czy już jest w trybie edytowania
        if (field.isSpinner) {
            if (field.isEditMode) {
                if (citySpinner.getSelectedItem() != null) {
                    // przekazuje do UserDataManager wybraną pozycję ze spinnera w celu jej zapisania do bazy
                    String newValueCity = (String) citySpinner.getSelectedItem();
                    field.value = newValueCity;
                    UserDataManager.saveUserData(user, field.label, newValueCity, view, context);
                }
                if (ageSpinner.getSelectedItem() != null) {
                    String newValueAge = (String) ageSpinner.getSelectedItem();
                    field.value = newValueAge;
                    UserDataManager.saveUserData(user, field.label, newValueAge, view, context);
                }
            }
        } else {
            // dodaje wymogi takie jak filtry jeśli pole jest edytowalne
            setupTextFieldWithUserInformation(editText, context);
            field.isEditMode = !field.isEditMode;

            if (field.isEditable) {
                if (field.isEditMode) {
                    editButton.setText(R.string.Save);
                } else {
                    editButton.setText(R.string.Edit);
                    editText.setEnabled(false);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(false);
                    editText.setMovementMethod(new ScrollingMovementMethod());

                    String newValue = editText.getText().toString();
                    field.value = newValue;

                    UserDataManager.saveUserData(user, field.label, newValue, view, context);
                }
            }
        }
    }

    private static void setupTextFieldWithUserInformation(EditText editText, Context context) {
        // edycja wprowadzanego tekstu
        editText.setEnabled(true);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setMaxLines(1);
        editText.setMovementMethod(new ScrollingMovementMethod());

        editText.setFilters(getInputFilters());

        // Ustawienie kursora na końcu pola tekstowego
        editText.setSelection(editText.getText().length());

        // Pobranie informacji od użytkownika, jakiego typu jest główna metoda wprowadzania tekstu
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private static InputFilter[] getInputFilters() {
        // filter uniemożliwia w tekście spacji, numerów oraz dużych liter,
        // jedynie pierwsza litera może być duża.
        // maksymalna ilość znaków to 20.
        InputFilter[] filters = new InputFilter[2];
        filters[0] = new InputFilter.LengthFilter(20);
        filters[1] = (source, start, end, dest, dstart, dend) -> {
            if (source.equals("")) {
                return source;
            }
            if (source.toString().matches("[a-zA-Z ]+")) {
                if (dstart == 0 && Character.isLowerCase(source.charAt(0))) {
                    return Character.toUpperCase(source.charAt(0)) + source.subSequence(1, source.length()).toString();
                }
                return source;
            }
            return "";
        };
        return filters;
    }
}