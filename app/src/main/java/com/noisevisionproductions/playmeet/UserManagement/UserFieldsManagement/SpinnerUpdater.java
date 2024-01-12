package com.noisevisionproductions.playmeet.UserManagement.UserFieldsManagement;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.widget.AppCompatSpinner;

import com.noisevisionproductions.playmeet.Adapters.MySpinnerAdapter;
import com.noisevisionproductions.playmeet.DataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.Utilities.SpinnerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpinnerUpdater {
    public static void updateSpinnerData(AppCompatSpinner spinner, String selectedValue, Context context) {
        if (spinner.getId() == R.id.citySpinner) {

            List<String> cityNames = new ArrayList<>(CityXmlParser.parseCityNames(context));

            if (cityNames.size() > 1) {
                List<String> sortedList = new ArrayList<>(cityNames.subList(1, cityNames.size()));
                Collections.sort(sortedList);
                cityNames = new ArrayList<>(cityNames.subList(0, 1));
                cityNames.addAll(sortedList);
            }
            SpinnerManager.setupCitySpinner(context, spinner, cityNames, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        if (spinner.getId() == R.id.ageSpinner) {

            SpinnerManager.setupAgeSpinner(context, spinner, R.array.age_array, new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        MySpinnerAdapter spinnerAdapter = (MySpinnerAdapter) spinner.getAdapter();

        if (spinnerAdapter != null) {
            int count = spinnerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                String item = spinnerAdapter.getItem(i);

                if (item != null && item.equals(selectedValue)) {
                    spinner.setSelection(i);
                    break;
                }
            }

        }
    }
}
