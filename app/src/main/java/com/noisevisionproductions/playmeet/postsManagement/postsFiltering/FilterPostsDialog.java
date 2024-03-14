package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.adapters.MySpinnerAdapterForFilterMenu;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.FirestoreRecyclerViewHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterPostsDialog {
    private final FragmentManager fragmentManager;
    private final Context context;
    private final AppCompatButton filterButton;
    private AutoCompleteTextView cityTextView;
    private Spinner spinnerSport, spinnerDifficulty;
    private EditText postIdText;
    private final RecyclerView recyclerView;
    private Query baseQuery;
    private final View view;
    @NonNull
    private final boolean[] checkedItems;

    public FilterPostsDialog(AppCompatButton filterButton, RecyclerView recyclerView, FragmentManager fragmentManager, Context context, View view) {
        this.filterButton = filterButton;
        this.checkedItems = new boolean[4];
        this.recyclerView = recyclerView;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.view = view;
    }

    @NonNull
    private AlertDialog.Builder createDialogBuilder(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Filtruj posty");
        return builder;
    }

    @NonNull
    private LinearLayout createLayout(Activity activity, @NonNull Spinner spinnerSport, @NonNull AutoCompleteTextView textViewCity, @NonNull Spinner spinnerDifficulty, EditText postIdText) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 50, 50, 50);

        spinnerSport.setLayoutParams(lp);
        textViewCity.setLayoutParams(lp);
        spinnerDifficulty.setLayoutParams(lp);

        layout.addView(spinnerSport);
        layout.addView(textViewCity);
        layout.addView(spinnerDifficulty);
        layout.addView(postIdText);

        return layout;
    }

    public void filterPostsWindow(@NonNull Activity activity) {
        filterButton.setSelected(true);

        AlertDialog.Builder builder = createDialogBuilder(activity);
        spinnerSport = createSportSpinner(activity);
        cityTextView = createCityTextView(activity);
        spinnerDifficulty = createDifficultySpinner(activity);
        postIdText = createTextFieldForPostID(activity);

        LinearLayout layout = createLayout(activity, spinnerSport, cityTextView, spinnerDifficulty, postIdText);
        builder.setView(layout);

        setDialogButtons(builder);

        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialog1 -> {
            boolean isAnyOptionChecked = false;
            for (boolean isChecked : checkedItems) {
                if (isChecked) {
                    isAnyOptionChecked = true;
                    break;
                }
            }
            if (!isAnyOptionChecked) {
                filterButton.setSelected(false);
            }
        });
        dialog.show();
    }

    private void setDialogButtons(@NonNull AlertDialog.Builder builder) {
        // tworzy AlertDialog z przyciskami do filtrowania
        final CharSequence[] items = {"Sport", "Miasto", "Poziom gry", "ID postu"};

        // na starcie chowa wszystkie opcje filtrowania
        spinnerSport.setVisibility(View.GONE);
        cityTextView.setVisibility(View.GONE);
        spinnerDifficulty.setVisibility(View.GONE);

        // ustawianie maksymalnej dlugości tekstu jako 6 do postId
        InputFilter[] textFilter = new InputFilter[1];
        textFilter[0] = new InputFilter.LengthFilter(40);
        postIdText.setFilters(textFilter);
        postIdText.setHint("Wprowadź ID postu...");
        postIdText.setGravity(Gravity.CENTER);
        postIdText.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                postIdText.setHint("");
            } else {
                postIdText.setHint("Wprowadź ID postu...");
            }
        });
        postIdText.setVisibility(View.GONE);

        //zaleznie od tego, co użytkownik zaznaczył, aby brało pod uwage podczas filtrowania,
        // na tej podstawie chowa lub pokazuje obiekty potrzebne do filtrowania
        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            if (which == 0) {
                spinnerSport.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (which == 1) {
                cityTextView.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (which == 2) {
                spinnerDifficulty.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (which == 3) {
                postIdText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
            checkedItems[which] = isChecked;
        });

        //logika ustawiania przycisku "OK"
        builder.setPositiveButton("OK", (dialog, which) -> activateFilters());
        builder.setNegativeButton("Anuluj", (dialog, which) -> {
            filterButton.setSelected(false);
            dialog.cancel();
        });
    }

    private void activateFilters() {
        List<Filter> activeFilters = new ArrayList<>();
        if (checkedItems[0]) {
            Filter sportFilter = FilterFactory.createFilter("Sport", true, spinnerSport.getSelectedItem().toString());
            activeFilters.add(sportFilter);
        }
        if (checkedItems[1]) {
            Filter cityFilter = FilterFactory.createFilter("City", true, cityTextView.getText().toString());
            activeFilters.add(cityFilter);
        }
        if (checkedItems[2]) {
            Filter difficultyFilter = FilterFactory.createFilter("Difficulty", true, spinnerDifficulty.getSelectedItem().toString());
            activeFilters.add(difficultyFilter);
        }
        if (checkedItems[3]) {
            Filter postIdFilter = FilterFactory.createFilter("PostID", true, postIdText.getText().toString());
            activeFilters.add(postIdFilter);
        }

        filterPostsByQuery(activeFilters);
        filterButton.setSelected(!activeFilters.isEmpty());
        deleteFilters();
    }

    @NonNull
    private EditText createTextFieldForPostID(Activity activity) {
        EditText text = new EditText(activity);
        text.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        text.setHeight(200);
        return text;
    }

    @NonNull
    private Spinner createDifficultySpinner(@NonNull Activity activity) {
        String[] items = activity.getResources().getStringArray(R.array.arrays_skill_level_for_filtering);
        Spinner spinner = new Spinner(activity);
        MySpinnerAdapterForFilterMenu adapter = new MySpinnerAdapterForFilterMenu(activity, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    @NonNull
    private Spinner createSportSpinner(@NonNull Activity activity) {
        String[] items = activity.getResources().getStringArray(R.array.arrays_sport_names_for_filtering);
        Spinner spinner = new Spinner(activity);
        MySpinnerAdapterForFilterMenu adapter = new MySpinnerAdapterForFilterMenu(activity, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    @NonNull
    private AutoCompleteTextView createCityTextView(@NonNull Activity activity) {
        AutoCompleteTextView textViewCity = new AutoCompleteTextView(activity);
        textViewCity.setHint(activity.getString(R.string.provideCityHint));
        textViewCity.setMinHeight(getMinHeightDPScale(activity));
        textViewCity.setTextColor(activity.getColor(R.color.text));
        textViewCity.setDropDownBackgroundResource(R.drawable.rounded_menu_background_for_spinner);

        List<String> cityList = new ArrayList<>(CityXmlParser.parseCityNames(activity.getApplicationContext()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity.getApplicationContext(), android.R.layout.simple_list_item_1, cityList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setTextColor(activity.getColor(R.color.text));
                return view;
            }
        };
        textViewCity.setAdapter(adapter);
        return textViewCity;
    }

    private Integer getMinHeightDPScale(Activity activity) {
        int minHeightInDp = 60;
        float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (minHeightInDp * scale + 0.5f);
    }

    private void filterPostsByQuery(List<Filter> activeFilters) {
        baseQuery = FirebaseFirestore.getInstance().collection("PostCreating");

        for (Filter filter : activeFilters) {
            baseQuery = filter.applyFilter(baseQuery);
        }
        FirestoreRecyclerViewHelper.setupRecyclerView(baseQuery, recyclerView, fragmentManager, context, (LifecycleOwner) context, view);
    }

    public void deleteFilters() {
        AppCompatButton deleteFilters = view.findViewById(R.id.deleteFilters);
        deleteFilters.setOnClickListener(v -> {
            baseQuery = FirebaseFirestore.getInstance().collection("PostCreating");
            FirestoreRecyclerViewHelper.setupRecyclerView(baseQuery, recyclerView, fragmentManager, context, (LifecycleOwner) context, view);
            filterButton.setSelected(false);
        });
    }
}
