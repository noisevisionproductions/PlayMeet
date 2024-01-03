package com.example.zagrajmy.FirstSetup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zagrajmy.Adapters.MySpinnerAdapter;
import com.example.zagrajmy.DataManagement.CityXmlParser;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChildFragmentCity extends Fragment {
    private AppCompatButton setCity;
    private AppCompatSpinner autoCompleteCity;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_city, container, false);

        setCity = view.findViewById(R.id.setCity);
        autoCompleteCity = view.findViewById(R.id.autoCompleteCity);

        changeFragmentToGender(view);
        chooseCity();
        return view;
    }

    public void changeFragmentToGender(View view) {
        setCity.setOnClickListener(v -> onCityChosen("test", view));
    }

    public void chooseCity() {
        List<String> cityNames = CityXmlParser.parseCityNames(requireContext());
        // tworzenie osobnej listy dla pierwszego elementu.
        // Element, który jest pierwszy na liście cityNames to "Wybierz miasto".
        // Dlatego musi być pomijane w sortowaniu listy
        if (cityNames.size() > 1) {
            List<String> sortedList = new ArrayList<>(cityNames.subList(1, cityNames.size()));
            Collections.sort(sortedList);
            cityNames = new ArrayList<>(cityNames.subList(0, 1));
            cityNames.addAll(sortedList);
        }

        MySpinnerAdapter mySpinnerAdapter = new MySpinnerAdapter(requireContext(), android.R.layout.simple_spinner_item, cityNames);
        mySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteCity.setAdapter(mySpinnerAdapter);
    }

    public void onCityChosen(String city, View view) {

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setCity.setVisibility(View.GONE);
                autoCompleteCity.setVisibility(View.GONE);

                ChildFragmentGender childFragmentGender = new ChildFragmentGender();

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);

                fragmentTransaction.replace(R.id.cityFragmentLayout, childFragmentGender);
                fragmentTransaction.commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);


    }
}
