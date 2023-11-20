package com.example.zagrajmy.PostsManagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.zagrajmy.DataManagement.CityXmlParser;
import com.example.zagrajmy.DataManagement.RealmDatabaseManagement;
import com.example.zagrajmy.Design.MySpinnerAdapter;
import com.example.zagrajmy.NavigationUtils;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.example.zagrajmy.UserManagement.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PostCreatingLogic extends AppCompatActivity {
    RealmDatabaseManagement realmDatabaseManagement = new RealmDatabaseManagement();
    PostCreating postCreating = new PostCreating();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creating);

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
        realmDatabaseManagement.closeRealmDatabase();
    }

    public void createPost() {
        User user = new User();
        Button createPost = findViewById(R.id.submitPost);
        createPost.setOnClickListener(view -> {
            setUniqueId();
            setAdditionalInfo();

            postCreating.setUserId(user.getUserId());
            realmDatabaseManagement.addPost(postCreating);

            Toast.makeText(PostCreatingLogic.this, "Post utworzony!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(PostCreatingLogic.this, PostsOfTheGames.class);
            startActivity(intent);
        });
    }

    public void setUniqueId() {
        UniqueIdGenerator uniqueIdGenerator = new UniqueIdGenerator();

        int uniqueId;
        do {
            uniqueId = uniqueIdGenerator.generateUniqueId();
        } while (realmDatabaseManagement.checkIfIdExists(uniqueId));

        postCreating.setUniqueId(uniqueId);
    }

    public void setSportType() {
        String[] items = getResources().getStringArray(R.array.arrays_sport_names);
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.arrays_sport_names);
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
        List<String> cityNames = CityXmlParser.parseCityNames(this);
        if (cityNames.size() > 1) {
            List<String> sortedList = new ArrayList<>(cityNames.subList(1, cityNames.size()));
            Collections.sort(sortedList);
            cityNames = new ArrayList<>(cityNames.subList(0, 1));
            cityNames.addAll(sortedList);
        }
       // Collections.sort(cityNames);

        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, cityNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        AppCompatSpinner chooseCity = (AppCompatSpinner) findViewById(R.id.cities_in_poland);
        chooseCity.setAdapter(adapter);

        chooseCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedCity = (String) adapterView.getItemAtPosition(i);

                postCreating.setCityName(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setSkillLevel() {
        String[] items = getResources().getStringArray(R.array.arrays_skill_level);
        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        AppCompatSpinner spinner = (AppCompatSpinner) findViewById(R.id.arrays_skill_level);
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