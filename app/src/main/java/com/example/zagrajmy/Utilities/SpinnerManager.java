package com.example.zagrajmy.Utilities;

import android.content.Context;
import android.widget.AdapterView;

import androidx.appcompat.widget.AppCompatSpinner;

import com.example.zagrajmy.Adapters.MySpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpinnerManager {

    public static void setupCitySpinner(Context context, AppCompatSpinner spinner, List<String> cityNames, AdapterView.OnItemSelectedListener listener) {
        if (cityNames.size() > 1) {
            List<String> sortedList = new ArrayList<>(cityNames.subList(1, cityNames.size()));
            Collections.sort(sortedList);
            cityNames = new ArrayList<>(cityNames.subList(0, 1));
            cityNames.addAll(sortedList);
        }

        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, cityNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }

    public static void setupGenderSpinner(Context context, AppCompatSpinner spinner, int arrayResourceId, AdapterView.OnItemSelectedListener listener) {
        String[] items = context.getResources().getStringArray(arrayResourceId);
        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }

    public static void setupAgeSpinner(Context context, AppCompatSpinner spinner, int arrayResourceId, AdapterView.OnItemSelectedListener listener) {
        String[] items = context.getResources().getStringArray(arrayResourceId);
        MySpinnerAdapter adapter = new MySpinnerAdapter(context, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(listener);
    }
}
