package com.noisevisionproductions.playmeet.postsManagement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.adapters.MySpinnerAdapter;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnPostCreatedListener;
import com.noisevisionproductions.playmeet.utilities.DateChoosingLogic;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public class PostCreatingLogic extends Fragment {
    private View view;
    private final PostModel postModel = new PostModel();
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private AppCompatAutoCompleteTextView cityTextView;
    private AppCompatSpinner sportSpinner, skillSpinner;
    private DateChoosingLogic dateChoosingLogic;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_creating, container, false);

        setSportType();
        setSkillLevel();
        setCityName();
        setDate();
        setHour();
        checkIfPostCanBeCreated(view);
        setButtons();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateChoosingLogic = new DateChoosingLogic(requireContext(), postModel);
    }

    private void checkIfPostCanBeCreated(View view) {
        AppCompatButton createPost = view.findViewById(R.id.submitPost);

        createPost.setOnClickListener(v -> {
            if (isValidHowManyPeopleNeeded() && isValidSportSelection() && isValidCitySelection() && isValidSkillSelection()) {
                if (firebaseHelper.getCurrentUser() != null) {
                    checkPostLimit(firebaseHelper.getCurrentUser().getUid(), canCreatePost -> {
                        if (canCreatePost) {
                            String selectedCity = cityTextView.getText().toString();
                            postModel.setCityName(selectedCity);
                            createNewPost();
                        } else {
                            ToastManager.showToast(requireContext(), getString(R.string.postsCreatingLimitReached));
                        }
                    });
                } else {
                    ToastManager.showToast(requireContext(), getString(R.string.userDontExists));
                }
            } else {
                handleInvalidSelection();
                ProjectUtils.createSnackBarUsingViewVeryShort(view, getString(R.string.fillAllNeededFields));
            }
        });
    }

    private void createNewPost() {
        setHowManyPeopleNeeded();
        postModel.setCreatedByUser(true);
        postModel.setIsActivityFull(false);
        if (firebaseHelper.getCurrentUser() != null) {
            postModel.setUserId(firebaseHelper.getCurrentUser().getUid());
            setAdditionalInfo();
            savePostToDB();
        }
    }

    private void checkPostLimit(String userId, @NonNull Consumer<Boolean> callback) {
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

    private void savePostToDB() {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        firestorePostRepository.addPost(postModel, new OnPostCreatedListener() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String postId = documentReference.getId();
                postModel.setPostId(postId);
                firestorePostRepository.updatePost(postId, postModel, new OnCompletionListener() {
                    @Override
                    public void onSuccess() {
                        ToastManager.showToast(requireContext(), getString(R.string.postCreated));
                        Intent intent = new Intent(requireContext(), ActivityMainMenu.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ToastManager.showToast(requireContext(), getString(R.string.errorWhileCreatingPost) + " " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("insertPost", "Error saving post in DB" + e.getMessage());
            }
        });
    }

    private void setSportType() {
        String[] items = getResources().getStringArray(R.array.arrays_sport_names);
        MySpinnerAdapter adapter = new MySpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportSpinner = view.findViewById(R.id.arrays_sport_names);
        sportSpinner.setAdapter(adapter);

        sportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSport = (String) adapterView.getItemAtPosition(position);
                if (position > 0) {
                    postModel.setSportType(selectedSport);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setCityName() {
        cityTextView = view.findViewById(R.id.cities_in_poland);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, CityXmlParser.parseCityNames(requireContext()));
        cityTextView.setAdapter(adapter);
    }

    private void setSkillLevel() {
        String[] items = getResources().getStringArray(R.array.arrays_skill_level);
        MySpinnerAdapter adapter = new MySpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skillSpinner = view.findViewById(R.id.arrays_skill_level);
        skillSpinner.setAdapter(adapter);
        skillSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(@NonNull AdapterView<?> adapterView, View view, int position, long l) {
                String selectedSkillLevel = (String) adapterView.getItemAtPosition(position);

                if (position > 0) {

                    postModel.setSkillLevel(selectedSkillLevel);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean isValidHowManyPeopleNeeded() {
        TextInputEditText peopleNeededEditText = view.findViewById(R.id.howManyPeopleNeeded);
        String typedInfo = Objects.requireNonNull(peopleNeededEditText.getText()).toString();

        if (!typedInfo.isEmpty()) {
            int numberOfPeople = Integer.parseInt(typedInfo);
            // Check if the number of people is greater than zero
            if (numberOfPeople > 0) {
                return true;
            } else {
                peopleNeededEditText.setError(getString(R.string.provideCorrectNumber));
                return false;
            }
        } else {
            peopleNeededEditText.setError(getString(R.string.provideNumberOfPeople));
            return false;
        }
    }

    private void setHowManyPeopleNeeded() {
        TextInputEditText peopleNeededEditText = view.findViewById(R.id.howManyPeopleNeeded);
        String typedInfo = Objects.requireNonNull(peopleNeededEditText.getText()).toString();
        if (!typedInfo.isEmpty()) {
            int numberOfPeople = Integer.parseInt(typedInfo);
            if (numberOfPeople > 0) {
                postModel.setHowManyPeopleNeeded(numberOfPeople);
            }
        }
    }

    private void setAdditionalInfo() {
        TextInputEditText addInfo = view.findViewById(R.id.addInfo);

        String typedInfo = Objects.requireNonNull(addInfo.getText()).toString();
        if (!typedInfo.isEmpty()) {
            if (!typedInfo.equals("0")) {
                postModel.setAdditionalInfo(typedInfo);
            }
        }
    }

    private void setDate() {
        TextInputEditText chooseDate = view.findViewById(R.id.chooseDate);
        chooseDate.setFocusable(false);
        chooseDate.setOnClickListener(v -> dateChoosingLogic.pickDate(chooseDate));

        AppCompatCheckBox dateNegotiable = view.findViewById(R.id.dateNegotiable);
        dateNegotiable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chooseDate.setEnabled(false);
                chooseDate.setHintTextColor(Color.GRAY);
            } else {
                chooseDate.setEnabled(true);
                chooseDate.setHintTextColor(Color.LTGRAY);
            }
        });
        dateChoosingLogic.noDateGiven();
    }

    private void setHour() {
        TextInputEditText chooseHour = view.findViewById(R.id.chooseHour);
        chooseHour.setFocusable(false);
        chooseHour.setOnClickListener(v -> dateChoosingLogic.pickHour(chooseHour));

        AppCompatCheckBox hourNegotiable = view.findViewById(R.id.hourNegotiable);
        hourNegotiable.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                chooseHour.setEnabled(false);
                chooseHour.setHintTextColor(Color.GRAY);

            } else {
                chooseHour.setEnabled(true);
                chooseHour.setHintTextColor(Color.LTGRAY);
            }
        }));
        dateChoosingLogic.noHourGiven();
    }

    private boolean isValidSportSelection() {
        return !sportSpinner.getSelectedItem().equals(getString(R.string.provideCorrectSport));
    }

    private boolean isValidCitySelection() {
        String city = cityTextView.getText().toString();
        return !city.isEmpty() && ProjectUtils.isCityChosenFromTheList(city, requireContext());
    }


    private boolean isValidSkillSelection() {
        return !skillSpinner.getSelectedItem().equals(getString(R.string.provideCorrectDifficulty));
    }

    private void handleInvalidSelection() {
        if (!isValidSportSelection()) {
            setSpinnerError(sportSpinner, getString(R.string.provideCorrectSport));
        } else {
            clearSpinnerError(sportSpinner);
        }
        if (!isValidCitySelection()) {
            cityTextView.setError(getString(R.string.provideCorrectCityOrChooseFromTheList));
        } else {
            cityTextView.setError(null);
        }
        if (!isValidSkillSelection()) {
            setSpinnerError(skillSpinner, getString(R.string.provideCorrectDifficulty));
        } else {
            clearSpinnerError(skillSpinner);
        }
    }

    private void setSpinnerError(@NonNull AppCompatSpinner spinner, String errorText) {
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView textView) {
            textView.setError(errorText);
        }
    }

    private void clearSpinnerError(@NonNull AppCompatSpinner spinner) {
        View selectedView = spinner.getSelectedView();
        if (selectedView instanceof TextView textView) {
            textView.setError(null);
        }
    }

    private void setButtons() {
        AppCompatImageView infoIcon = view.findViewById(R.id.infoIcon);
        ToastManager.createToolTip(getString(R.string.limitOfPosts), infoIcon);

        LinearLayoutCompat linearLayout = view.findViewById(R.id.linearLayoutPostCreating);
        linearLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));
    }
}