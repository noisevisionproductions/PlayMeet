package com.example.zagrajmy.PostsManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.zagrajmy.Adapters.MySpinnerAdapter;
import com.example.zagrajmy.DataManagement.CityXmlParser;
import com.example.zagrajmy.Realm.RealmDataManager;
import com.example.zagrajmy.Design.SidePanelBaseActivity;
import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Utilities.DateChoosingLogic;
import com.example.zagrajmy.Utilities.SpinnerManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Objects;

import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class PostCreatingLogic extends SidePanelBaseActivity {
    private final RealmDataManager realmDataManager = RealmDataManager.getInstance();
    private final PostCreating postCreating = new PostCreating();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_post_creating);

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realmDataManager.closeRealmDatabase();
    }

    public void createPost() {
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        AppCompatButton createPost = findViewById(R.id.submitPost);

        createPost.setOnClickListener(view -> {
            setUniqueId();
            setAdditionalInfo();

            if (user != null) {
                postCreating.setIsCreatedByUser(true);
                postCreating.setUserId(user.getId());
                realmDataManager.addPostToDatabase(postCreating);
            }

            Toast.makeText(PostCreatingLogic.this, "Post utworzony!", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(PostCreatingLogic.this, MainMenuPosts.class);
            startActivity(intent);
        });
    }

    public void setUniqueId() {
        UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();

        int postId;
        do {
            postId = uniqueIdGenerator.generateUniqueId();
        } while (realmDataManager.checkIfIdExists(postId));

        postCreating.setPostId(postId);
    }

    public void setSportType() {
        String[] items = getResources().getStringArray(R.array.arrays_sport_names);
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AppCompatSpinner spinner = findViewById(R.id.arrays_sport_names);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSport = (String) adapterView.getItemAtPosition(i);

                postCreating.setSportType(selectedSport);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void setCityName() {
        AppCompatSpinner chooseCity = findViewById(R.id.cities_in_poland);

        SpinnerManager.setupCitySpinner(this, chooseCity, CityXmlParser.parseCityNames(this), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = (String) parent.getItemAtPosition(position);
                postCreating.setCityName(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setSkillLevel() {
        String[] items = getResources().getStringArray(R.array.arrays_skill_level);
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AppCompatSpinner spinner = findViewById(R.id.arrays_skill_level);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedSkillLevel = (String) adapterView.getItemAtPosition(i);

                postCreating.setSkillLevel(selectedSkillLevel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setAdditionalInfo() {
        TextInputEditText addInfo = findViewById(R.id.addInfo);

        String typedInfo = Objects.requireNonNull(addInfo.getText()).toString();
        postCreating.setAdditionalInfo(typedInfo);
    }

    public void setDate() {
        TextInputEditText chooseDate = findViewById(R.id.chooseDate);
        chooseDate.setFocusable(false);

        DateChoosingLogic dateChoosingLogic = new DateChoosingLogic(this, postCreating);
        chooseDate.setOnClickListener(v -> dateChoosingLogic.pickDate(chooseDate));
    }

    public void setHour() {
        TextInputEditText chooseHour = findViewById(R.id.chooseHour);
        chooseHour.setFocusable(false);

        DateChoosingLogic dateChoosingLogic = new DateChoosingLogic(this, postCreating);
        chooseHour.setOnClickListener(v -> dateChoosingLogic.pickHour(chooseHour));
    }
}