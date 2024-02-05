package com.noisevisionproductions.playmeet.PostsManagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Adapters.MySpinnerAdapter;
import com.noisevisionproductions.playmeet.Adapters.ToastManager;
import com.noisevisionproductions.playmeet.DataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.Design.SidePanelBaseActivity;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Utilities.DateChoosingLogic;
import com.noisevisionproductions.playmeet.Utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.Utilities.SpinnerManager;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class PostCreatingLogic extends SidePanelBaseActivity {
    private final PostCreating postCreating = new PostCreating();
    private FirebaseHelper firebaseHelper;
    private AppCompatSpinner sportSpinner, citySpinner, skillSpinner;
    private DateChoosingLogic dateChoosingLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post_creating);
        firebaseHelper = new FirebaseHelper();
        dateChoosingLogic = new DateChoosingLogic(this, postCreating);

        setupDrawerLayout();
        setupNavigationView();

        AppCompatButton button = findViewById(R.id.backToMainMenu);
        ProjectUtils.backToMainMenuButton(button, this);

        setSportType();
        setCityName();
        setSkillLevel();
        setDate();
        setHour();
        checkIfPostCanBeCreated();

        LinearLayoutCompat linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(this));
    }

    private void checkIfPostCanBeCreated() {
        AppCompatButton createPost = findViewById(R.id.submitPost);

        createPost.setOnClickListener(view -> {
            if (isValidHowManyPeopleNeeded() && isValidSportSelection() && isValidCitySelection() && isValidSkillSelection()) {
                if (firebaseHelper.getCurrentUser() != null) {
                    checkPostLimit(firebaseHelper.getCurrentUser().getUid(), canCreatePost -> {
                        if (canCreatePost) {
                            createNewPost();
                        } else {
                            ToastManager.showToast(PostCreatingLogic.this, "Osiągnięto limit tworzenia postów");
                        }
                    });
                } else {
                    ToastManager.showToast(PostCreatingLogic.this, "Użytkownik nie autoryzowany");
                }
            } else {
                handleInvalidSelection();
                ProjectUtils.createSnackBarUsingViewVeryShort(view, "Uzupełnij wymagane pola");
            }
        });
    }

    private void createNewPost() {
        setHowManyPeopleNeeded();
        postCreating.setIsCreatedByUser(true);
        postCreating.setUserId(firebaseHelper.getCurrentUser().getUid());
        setAdditionalInfo();
        setUniqueId();
    }

    private void checkPostLimit(String userId, Consumer<Boolean> callback) {
        DatabaseReference postsReference = FirebaseDatabase.getInstance().getReference("PostCreating");
        Query query = postsReference.orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int postsCount = (int) snapshot.getChildrenCount();
                callback.accept(postsCount < 3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseHelper", "Error checking post limit", error.toException());
                callback.accept(false);
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
                            ToastManager.showToast(PostCreatingLogic.this, "Post utworzony!");

                            Intent intent = new Intent(PostCreatingLogic.this, MainMenuPosts.class);
                            startActivity(intent);
                        } else {
                            Log.e("FirebaseHelper", "Creating post error ", task.getException());
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

    private boolean isValidHowManyPeopleNeeded() {
        TextInputEditText peopleNeededEditText = findViewById(R.id.howManyPeopleNeeded);
        String typedInfo = Objects.requireNonNull(peopleNeededEditText.getText()).toString();

        if (!typedInfo.isEmpty()) {
            int numberOfPeople = Integer.parseInt(typedInfo);
            // Check if the number of people is greater than zero
            if (numberOfPeople > 0) {
                return true;
            } else {
                peopleNeededEditText.setError("Podaj poprawną liczbę osób");
                return false;
            }
        } else {
            peopleNeededEditText.setError("Pojad liczbę osób");
            return false;
        }
    }

    private void setHowManyPeopleNeeded() {
        TextInputEditText peopleNeededEditText = findViewById(R.id.howManyPeopleNeeded);
        String typedInfo = Objects.requireNonNull(peopleNeededEditText.getText()).toString();
        if (!typedInfo.isEmpty()) {
            int numberOfPeople = Integer.parseInt(typedInfo);
            if (numberOfPeople > 0) {
                postCreating.setHowManyPeopleNeeded(numberOfPeople);
            }
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
        chooseDate.setOnClickListener(v -> dateChoosingLogic.pickDate(chooseDate));

        AppCompatCheckBox dateNegotiable = findViewById(R.id.dateNegotiable);
        dateNegotiable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                dateChoosingLogic.noDateGiven();
                chooseDate.setEnabled(false);
                chooseDate.setHintTextColor(Color.GRAY);
            } else {
                chooseDate.setEnabled(true);
                chooseDate.setHintTextColor(Color.LTGRAY);
            }
        });
    }

    private void setHour() {
        TextInputEditText chooseHour = findViewById(R.id.chooseHour);
        chooseHour.setFocusable(false);
        chooseHour.setOnClickListener(v -> dateChoosingLogic.pickHour(chooseHour));

        AppCompatCheckBox hourNegotiable = findViewById(R.id.hourNegotiable);
        hourNegotiable.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                dateChoosingLogic.noHourGiven();
                chooseHour.setEnabled(false);
                chooseHour.setHintTextColor(Color.GRAY);

            } else {
                chooseHour.setEnabled(true);
                chooseHour.setHintTextColor(Color.LTGRAY);
            }
        }));
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