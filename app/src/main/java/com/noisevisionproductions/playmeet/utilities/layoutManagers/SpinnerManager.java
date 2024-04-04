package com.noisevisionproductions.playmeet.utilities.layoutManagers;

import android.content.Context;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import com.noisevisionproductions.playmeet.adapters.MySpinnerAdapter;
import com.noisevisionproductions.playmeet.utilities.DifficultyModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpinnerManager {

    public static void setupCitySpinner(@NonNull Context context, @NonNull AppCompatSpinner spinner, @NonNull List<String> cityNames, AdapterView.OnItemSelectedListener listener) {
        if (cityNames.size() > 1) {
            List<String> sortedList = new ArrayList<>(cityNames.subList(1, cityNames.size()));
            Collections.sort(sortedList);
            cityNames = new ArrayList<>(cityNames.subList(0, 1));
            cityNames.addAll(sortedList);
        }

        // Conversion from List<String> to List<Object>, because of my spinner adapter
        List<Object> cityNamesAsObjects = new ArrayList<>(cityNames);

        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, cityNamesAsObjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }

    public static void setupGenderSpinner(@NonNull Context context, @NonNull AppCompatSpinner spinner, int arrayResourceId, AdapterView.OnItemSelectedListener listener) {
        String[] items = context.getResources().getStringArray(arrayResourceId);
        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }

    public static void setupAgeSpinner(@NonNull Context context, @NonNull AppCompatSpinner spinner, int arrayResourceId, AdapterView.OnItemSelectedListener listener) {
        String[] items = context.getResources().getStringArray(arrayResourceId);
        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }

    public static void setupDifficultySpinner(Context context, @NonNull AppCompatSpinner spinner, String[] arrayListXml, AdapterView.OnItemSelectedListener listener) {
        List<DifficultyModel> difficultyModels = new ArrayList<>();
        for (String level : arrayListXml) {
            String[] parts = level.split("\\|");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            difficultyModels.add(new DifficultyModel(id, name));
        }

        // Conversion from List<String> to List<Object>, because of my spinner adapter
        List<Object> difficultiesAsObjects = new ArrayList<>(difficultyModels);
        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, difficultiesAsObjects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }

    public static void setupSportSpinner(Context context, @NonNull AppCompatSpinner spinner, String[] arrayListXml, AdapterView.OnItemSelectedListener listener) {
        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, Arrays.asList(arrayListXml));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }
}
