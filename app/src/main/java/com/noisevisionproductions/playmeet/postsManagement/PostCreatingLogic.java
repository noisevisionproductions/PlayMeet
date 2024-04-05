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
import com.google.firebase.firestore.DocumentReference;
import com.noisevisionproductions.playmeet.ActivityMainMenu;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnPostCreatedListener;
import com.noisevisionproductions.playmeet.utilities.DateChoosingLogic;
import com.noisevisionproductions.playmeet.utilities.DifficultyModel;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.SpinnerManager;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

import java.util.Objects;

public class PostCreatingLogic extends Fragment {
    private final PostModel postModel = new PostModel();
    private final FirebaseHelper firebaseHelper = new FirebaseHelper();
    private AppCompatAutoCompleteTextView cityTextView;
    private AppCompatSpinner sportSpinner, skillSpinner;
    private DateChoosingLogic dateChoosingLogic;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_creating, container, false);

        setSportType(view);
        setSkillLevel(view);
        setCityName(view);
        setDate(view);
        setHour(view);
        checkIfPostCanBeCreated(view);
        setButtons(view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateChoosingLogic = new DateChoosingLogic(requireContext(), postModel);
    }

    private void checkIfPostCanBeCreated(View view) {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        AppCompatButton createPost = view.findViewById(R.id.submitPost);

        createPost.setOnClickListener(v -> {
            if (isValidHowManyPeopleNeeded(view) && isValidSportSelection() && isValidCitySelection() && isValidSkillSelection()) {
                if (firebaseHelper.getCurrentUser() != null) {
                    firestorePostRepository.checkPostLimit(firebaseHelper.getCurrentUser().getUid(), canCreatePost -> {
                        if (canCreatePost) {
                            String selectedCity = cityTextView.getText().toString();
                            postModel.setCityName(selectedCity);
                            createNewPost(view);
                        } else {
                            ToastManager.showToast(requireContext(), getString(R.string.postsCreatingLimitReached));
                        }
                    });
                } else {
                    ToastManager.showToast(requireContext(), getString(R.string.userDoNotExists));
                }
            } else {
                handleInvalidSelection();
                ProjectUtils.createSnackBarUsingViewVeryShort(view, getString(R.string.fillAllNeededFields));
            }
        });
    }

    private void createNewPost(View view) {
        setHowManyPeopleNeeded(view);
        postModel.setCreatedByUser(true);
        postModel.setIsActivityFull(false);
        if (firebaseHelper.getCurrentUser() != null) {
            postModel.setUserId(firebaseHelper.getCurrentUser().getUid());
            setAdditionalInfo(view);
            savePostToDB();
        }
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

    private void setSportType(View view) {
        sportSpinner = view.findViewById(R.id.arrays_sport_names);
        String[] arrayListXml = getResources().getStringArray(R.array.arrays_sport_names);
        SpinnerManager.setupSportSpinner(requireContext(), sportSpinner, arrayListXml, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSport = (String) parent.getItemAtPosition(position);
                if (position > 0) {
                    postModel.setSportType(selectedSport);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCityName(View view) {
        cityTextView = view.findViewById(R.id.cities_in_poland);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, CityXmlParser.parseCityNames(requireContext()));
        cityTextView.setAdapter(adapter);
    }

    private void setSkillLevel(View view) {
        skillSpinner = view.findViewById(R.id.arrays_skill_level);
        String[] levels = getResources().getStringArray(R.array.arrays_skill_level);
        SpinnerManager.setupDifficultySpinner(requireContext(), skillSpinner, levels, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DifficultyModel selectedDifficulty = (DifficultyModel) parent.getItemAtPosition(position);
                if (position > 0) {
                    int selectedId = selectedDifficulty.id();
                    postModel.setSkillLevel(selectedId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isValidHowManyPeopleNeeded(View view) {
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

    private void setHowManyPeopleNeeded(View view) {
        TextInputEditText peopleNeededEditText = view.findViewById(R.id.howManyPeopleNeeded);
        String typedInfo = Objects.requireNonNull(peopleNeededEditText.getText()).toString();
        if (!typedInfo.isEmpty()) {
            int numberOfPeople = Integer.parseInt(typedInfo);
            if (numberOfPeople > 0) {
                postModel.setHowManyPeopleNeeded(numberOfPeople);
            }
        }
    }

    private void setAdditionalInfo(View view) {
        TextInputEditText addInfo = view.findViewById(R.id.addInfo);

        String typedInfo = Objects.requireNonNull(addInfo.getText()).toString();
        if (!typedInfo.isEmpty()) {
            if (!typedInfo.equals("0")) {
                postModel.setAdditionalInfo(typedInfo);
            }
        }
    }

    private void setDate(View view) {
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

    private void setHour(View view) {
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

    private void setButtons(View view) {
        AppCompatImageView infoIcon = view.findViewById(R.id.infoIcon);
        ToastManager.createToolTip(getString(R.string.limitOfPosts), infoIcon);

        LinearLayoutCompat linearLayout = view.findViewById(R.id.linearLayoutPostCreating);
        linearLayout.setOnClickListener(v -> ProjectUtils.hideSoftKeyboard(requireActivity()));
    }
}