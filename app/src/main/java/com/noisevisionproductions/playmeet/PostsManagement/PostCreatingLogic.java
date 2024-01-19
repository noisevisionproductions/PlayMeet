package com.noisevisionproductions.playmeet.PostsManagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.Adapters.MySpinnerAdapter;
import com.noisevisionproductions.playmeet.DataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Utilities.DateChoosingLogic;
import com.noisevisionproductions.playmeet.Utilities.NavigationUtils;
import com.noisevisionproductions.playmeet.Utilities.SpinnerManager;

import java.util.Arrays;
import java.util.Objects;

public class PostCreatingLogic extends SidePanelBaseActivity {
    private final PostCreating postCreating = new PostCreating();
    private FirebaseHelper firebaseHelper;
    private AppCompatSpinner sportSpinner, citySpinner, skillSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post_creating);
        firebaseHelper = new FirebaseHelper();

        setupDrawerLayout();
        setupNavigationView();

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        NavigationUtils.backToMainMenuButton(button, this);

        setSportType();
        setCityName();
        setSkillLevel();
        setDate();
        setHour();
        createPost();

        ConstraintLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnClickListener(v -> NavigationUtils.hideSoftKeyboard(this));
    }

    private void createPost() {
        AppCompatButton createPost = findViewById(R.id.submitPost);

        createPost.setOnClickListener(view -> {
            if (isValidSportSelection() && isValidCitySelection() && isValidSkillSelection()) {
                if (firebaseHelper.getCurrentUser() != null) {
                    postCreating.setIsCreatedByUser(true);
                    postCreating.setUserId(firebaseHelper.getCurrentUser().getUid());
                    setHowManyPeopleNeeded();
                    setAdditionalInfo();
                    setUniqueId();
                } else {
                    Toast.makeText(PostCreatingLogic.this, "Użytkownik nie autoryzowany", Toast.LENGTH_SHORT).show();
                }

            } else {
                handleInvalidSelection();
                NavigationUtils.createSnackBarUsingView(view, "Uzupełnij wymagane pola");
            }
        });
    }

    private void setUniqueId() {
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("PostCreating");
        String postId = postReference.push().getKey();
        postCreating.setPostId(postId);

        if (postId != null) {
            postReference.child(postId).setValue(postCreating)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(PostCreatingLogic.this, "Post utworzony!", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(PostCreatingLogic.this, MainMenuPosts.class);
                            startActivity(intent);
                        } else {
                            Log.e("FirebaseHelper", "Błąd podczas dodawania posta do bazy danych", task.getException());
                        }
                    });
        }
    }

    private void setSportType() {
        String[] items = getResources().getStringArray(R.array.arrays_sport_names);
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner = findViewById(R.id.arrays_sport_names);
        sportSpinner.setAdapter(adapter);

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSport = (String) adapterView.getItemAtPosition(position);
                if (position > 0) {
                    postCreating.setSportType(selectedSport);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setCityName() {
        citySpinner = findViewById(R.id.cities_in_poland);

        SpinnerManager.setupCitySpinner(this, citySpinner, CityXmlParser.parseCityNames(this), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = (String) parent.getItemAtPosition(position);
                if (position > 0) {
                    postCreating.setCityName(selectedCity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSkillLevel() {
        String[] items = getResources().getStringArray(R.array.arrays_skill_level);
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillSpinner = findViewById(R.id.arrays_skill_level);
        skillSpinner.setAdapter(adapter);
        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSkillLevel = (String) adapterView.getItemAtPosition(position);
                if (position > 0) {
                    postCreating.setSkillLevel(selectedSkillLevel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setHowManyPeopleNeeded() {
        TextInputEditText peopleNeeded = findViewById(R.id.howManyPeopleNeeded);

        String typedInfo = Objects.requireNonNull(peopleNeeded.getText()).toString();
        if (!typedInfo.isEmpty()) {
            postCreating.setHowManyPeopleNeeded(Integer.parseInt(typedInfo));
        }
    }

    private void setAdditionalInfo() {
        TextInputEditText addInfo = findViewById(R.id.addInfo);

        String typedInfo = Objects.requireNonNull(addInfo.getText()).toString();
        if (!typedInfo.isEmpty()) {
            if (!typedInfo.equals("0")) {
                postCreating.setAdditionalInfo(typedInfo);
            }
        }
    }

    private void setDate() {
        TextInputEditText chooseDate = findViewById(R.id.chooseDate);
        chooseDate.setFocusable(false);

        DateChoosingLogic dateChoosingLogic = new DateChoosingLogic(this, postCreating);
        chooseDate.setOnClickListener(v -> dateChoosingLogic.pickDate(chooseDate));
    }

    private void setHour() {
        TextInputEditText chooseHour = findViewById(R.id.chooseHour);
        chooseHour.setFocusable(false);

        DateChoosingLogic dateChoosingLogic = new DateChoosingLogic(this, postCreating);
        chooseHour.setOnClickListener(v -> dateChoosingLogic.pickHour(chooseHour));
    }

    private boolean isValidSportSelection() {
        return !sportSpinner.getSelectedItem().equals("Wybierz sport");
    }

    private boolean isValidCitySelection() {
        return !citySpinner.getSelectedItem().equals("Wybierz miasto");
    }

    private boolean isValidSkillSelection() {
        return !skillSpinner.getSelectedItem().equals("Wybierz poziom");
    }

    private void handleInvalidSelection() {
        if (!isValidSportSelection()) {
            setSpinnerError(sportSpinner, "Wybierz sport");
        } else {
            clearSpinnerError(sportSpinner);
        }
        if (!isValidCitySelection()) {
            setSpinnerError(citySpinner, "Wybierz miasto");
        } else {
            clearSpinnerError(citySpinner);
        }
        if (!isValidSkillSelection()) {
            setSpinnerError(skillSpinner, "Wybierz poziom");
        } else {
            clearSpinnerError(skillSpinner);
        }
    }

    private void setSpinnerError(AppCompatSpinner spinner, String errorText) {
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView textView) {
            textView.setError(errorText);
        }
    }

    private void clearSpinnerError(AppCompatSpinner spinner) {
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView textView) {
            textView.setError(null);
        }
    }
}