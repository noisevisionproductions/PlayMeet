package com.example.zagrajmy.FirstSetup;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.zagrajmy.DataManagement.CityXmlParser;
import com.example.zagrajmy.Utilities.NavigationUtils;
import com.example.zagrajmy.R;
import com.example.zagrajmy.Utilities.SpinnerManager;

public class ChildFragmentCity extends Fragment {
    private AppCompatButton setCity, cancelButton;
    private AppCompatSpinner citySpinner;
    private AppCompatTextView stepNumber;
    private String city;
    private boolean citySelected = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_city, container, false);

        setUpUIElements(view);
        changeFragmentToGender(view);
        chooseCity();
        NavigationUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    public void setUpUIElements(View view) {
        setCity = view.findViewById(R.id.setCity);
        citySpinner = view.findViewById(R.id.citySpinner);
        stepNumber = view.findViewById(R.id.stepNumber);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    public void changeFragmentToGender(View view) {
        setCity.setOnClickListener(v -> {
            if (citySelected) {
                onCityChosen(city, view);
            } else {
                Toast.makeText(requireContext(), "Proszę wybrać miasto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chooseCity() {
        SpinnerManager.setupCitySpinner(requireContext(), citySpinner, CityXmlParser.parseCityNames(requireContext()), new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = (String) parent.getItemAtPosition(position);
                setChosenCity(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void onCityChosen(String city, View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLayout();

                FrameLayout cityFragmentLayout = view.findViewById(R.id.cityFragmentLayout);

                ChildFragmentGender childFragmentGender = new ChildFragmentGender();
                Bundle args = new Bundle();
                args.putString("nickname", getNickname());
                args.putString("city", city);
                childFragmentGender.setArguments(args);

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container, childFragmentGender, "tag_child_fragment_gender");

                Fragment previousFragment = getParentFragmentManager().findFragmentByTag("tag_child_fragment_city");
                if (previousFragment != null) {
                    fragmentTransaction.remove(previousFragment);
                }
                cityFragmentLayout.setVisibility(View.INVISIBLE);

                fragmentTransaction.commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    public String getNickname() {
        Bundle args = getArguments();
        return args != null ? args.getString("nickname") : null;
    }

    public void setChosenCity(String city) {
        this.city = city;
        citySelected = !TextUtils.equals(city, getString(R.string.choose_city_string_from_spinner));
    }

    public void hideLayout() {
        setCity.setVisibility(View.GONE);
        citySpinner.setVisibility(View.GONE);
        stepNumber.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }
}
