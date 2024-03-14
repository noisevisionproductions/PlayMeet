package com.noisevisionproductions.playmeet.firstSetup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.utilities.ProjectUtils;

import java.util.ArrayList;
import java.util.List;

public class ChildFragmentCity extends Fragment {
    private AppCompatButton setCityButton, cancelButton;
    private AppCompatAutoCompleteTextView cityTextView;
    private AppCompatTextView stepNumber;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_city, container, false);

        setUpUIElements(view);
        changeFragmentToGender(view);
        chooseCity();
        ProjectUtils.handleCancelButtonForFragments(cancelButton, getParentFragment());

        return view;
    }

    private void setUpUIElements(@NonNull View view) {
        setCityButton = view.findViewById(R.id.setCity);
        cityTextView = view.findViewById(R.id.cityTextField);
        stepNumber = view.findViewById(R.id.stepNumber);
        cancelButton = view.findViewById(R.id.cancelButton);
    }

    private void changeFragmentToGender(@NonNull View view) {
        setCityButton.setOnClickListener(v -> {
            String enteredCity = cityTextView.getText().toString();
            if (enteredCity.isEmpty()) {
                cityTextView.setError("Wprowadź miasto");
                cityTextView.requestFocus();
            } else if (ProjectUtils.isCityChosenFromTheList(enteredCity, requireContext())) {
                onCityChosen(enteredCity, view);
            } else {
                cityTextView.setError("Wybierz miasto z listy");
                cityTextView.requestFocus();
            }
        });
    }

    private void chooseCity() {
        // używam specjalnie stworzonej klasy, która irytuje przez cały plik xml,
        // w którym znajdują się miasta i wybiera tylko te, które potrzebuje
        List<String> cityList = new ArrayList<>(CityXmlParser.parseCityNames(requireContext()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, cityList);
        cityTextView.setAdapter(adapter);
    }

    private void onCityChosen(String city, @NonNull View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_left);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hideLayout();

                FrameLayout cityFragmentLayout = view.findViewById(R.id.cityFragmentLayout);

                // tworzę nowy fragment, który pojawi się jako kolejny i będzie zastępować aktualny
                ChildFragmentGender childFragmentGender = getChildFragmentGender();

                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container, childFragmentGender, "tag_child_fragment_gender");

                Fragment previousFragment = getParentFragmentManager().findFragmentByTag("tag_child_fragment_city");
                if (previousFragment != null) {
                    fragmentTransaction.remove(previousFragment);
                }
                cityFragmentLayout.setVisibility(View.INVISIBLE);
                // ustawiam aktualny fragment jako niewidzialny, aby animacja była płynna i wyglądała jak należy

                fragmentTransaction.commit();
            }

            @NonNull
            private ChildFragmentGender getChildFragmentGender() {
                ChildFragmentGender childFragmentGender = new ChildFragmentGender();
                // dodaje do Bundle nickname z poprzedniego fragmentu oraz city z aktualnego.
                // Dzięki temu oba te Stringi będą dostępne w ostatnim fragmencie
                Bundle args = new Bundle();
                args.putString("nickname", getNickname());
                args.putString("city", city);
                childFragmentGender.setArguments(args);
                return childFragmentGender;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    // pobieram ustawiony nickname z poprzedniego fragmentu
    @Nullable
    public String getNickname() {
        Bundle args = getArguments();
        return args != null ? args.getString("nickname") : null;
    }

    private void hideLayout() {
        setCityButton.setVisibility(View.GONE);
        cityTextView.setVisibility(View.GONE);
        stepNumber.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
    }
}
