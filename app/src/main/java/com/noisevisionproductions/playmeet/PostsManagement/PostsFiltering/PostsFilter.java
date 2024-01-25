package com.noisevisionproductions.playmeet.PostsManagement.PostsFiltering;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.Adapters.MySpinnerAdapterForFilterMenu;
import com.noisevisionproductions.playmeet.DataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.DataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.AdapterAllPosts;
import com.noisevisionproductions.playmeet.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PostsFilter {
    private final List<PostCreating> originalPosts = new ArrayList<>();
    private final List<PostCreating> posts;
    private final RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> adapter;
    private final AppCompatButton filterButton, deleteFilters;
    private final AppCompatTextView noPostFound;
    private Spinner spinnerSport, spinnerCity, spinnerDifficulty;
    private EditText postIdText;
    private final boolean[] checkedItems;

    public PostsFilter(RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> adapter, List<PostCreating> posts, AppCompatButton filterButton, AppCompatButton deleteFilters, AppCompatTextView noPostFound) {
        this.adapter = adapter;
        this.posts = posts;
        this.filterButton = filterButton;
        this.deleteFilters = deleteFilters;
        this.noPostFound = noPostFound;
        this.checkedItems = new boolean[4];
        for (PostCreating post : posts) {
            originalPosts.add(post.copyOfAllPosts());
        }
    }

    private AlertDialog.Builder createDialogBuilder(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("Filtruj posty");
        return builder;
    }

    private LinearLayout createLayout(Activity activity, Spinner spinnerSport, Spinner spinnerCity, Spinner spinnerDifficulty, EditText postIdText) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(50, 50, 50, 50);

        spinnerSport.setLayoutParams(lp);
        spinnerCity.setLayoutParams(lp);
        spinnerDifficulty.setLayoutParams(lp);

        layout.addView(spinnerSport);
        layout.addView(spinnerCity);
        layout.addView(spinnerDifficulty);
        layout.addView(postIdText);

        return layout;
    }

    public void filterPostsWindow(Activity activity) {
        filterButton.setSelected(true);

        AlertDialog.Builder builder = createDialogBuilder(activity);
        spinnerSport = createSportSpinner(activity);
        spinnerCity = createCitySpinner(activity);
        spinnerDifficulty = createDifficultySpinner(activity);
        postIdText = createTextFieldForPostID(activity);

        LinearLayout layout = createLayout(activity, spinnerSport, spinnerCity, spinnerDifficulty, postIdText);
        builder.setView(layout);

        setDialogButtons(builder);
        deleteFilters();

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

    private void setDialogButtons(AlertDialog.Builder builder) {
        // tworzy AlertDialog z przyciskami do filtrowania
        final CharSequence[] items = {"Sport", "Miasto", "Poziom gry", "ID postu"};

        // na starcie chowa wszystkie opcje filtrowania
        spinnerSport.setVisibility(View.GONE);
        spinnerCity.setVisibility(View.GONE);
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
                spinnerCity.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (which == 2) {
                spinnerDifficulty.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            } else if (which == 3) {
                postIdText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
            checkedItems[which] = isChecked;
        });

        //logika ustawiania przycisku "OK"
        builder.setPositiveButton("OK", (dialog, which) -> {

            // pobiera wybrane / wprowadzone wartości
            String selectedSport = spinnerSport.getSelectedItem().toString();
            String selectedCity = spinnerCity.getSelectedItem().toString();
            String selectedDifficulty = spinnerDifficulty.getSelectedItem().toString();
            String selectedPostId = postIdText.getText().toString();

            boolean anyFilterChecked = false;
            for (boolean isChecked : checkedItems) {
                if (isChecked) {
                    anyFilterChecked = true;
                    break;
                }
            }

            // na podstawie wprowadzonych wartości, filtruje posty
            Filter sportFilter = new SportFilter(checkedItems[0], selectedSport);
            Filter cityFilter = new CityFilter(checkedItems[1], selectedCity);
            Filter difficultyFilter = new DifficultyFilter(checkedItems[2], selectedDifficulty);
            Filter postIdFilter = new PostIDFilter(checkedItems[3], selectedPostId);

            List<Filter> newFilters = Arrays.asList(sportFilter, cityFilter, difficultyFilter, postIdFilter);
            filterPostsByQuery(newFilters);

            filterButton.setSelected(anyFilterChecked);
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> {
            filterButton.setSelected(false);
            dialog.cancel();
        });
    }

    private EditText createTextFieldForPostID(Activity activity) {
        EditText text = new EditText(activity);
        text.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        return text;
    }

    private Spinner createDifficultySpinner(Activity activity) {
        String[] items = activity.getResources().getStringArray(R.array.arrays_skill_level_for_filtering);
        Spinner spinner = new Spinner(activity);
        MySpinnerAdapterForFilterMenu adapter = new MySpinnerAdapterForFilterMenu(activity, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private Spinner createSportSpinner(Activity activity) {
        String[] items = activity.getResources().getStringArray(R.array.arrays_sport_names_for_filtering);
        Spinner spinner = new Spinner(activity);
        MySpinnerAdapterForFilterMenu adapter = new MySpinnerAdapterForFilterMenu(activity, android.R.layout.simple_spinner_item, Arrays.asList(items));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    private Spinner createCitySpinner(Activity activity) {
        Spinner spinner = new Spinner(activity);
        List<String> cityNames = CityXmlParser.parseCityNames(activity);
        Collections.sort(cityNames);

        MySpinnerAdapterForFilterMenu adapter = new MySpinnerAdapterForFilterMenu(activity, android.R.layout.simple_spinner_item, cityNames);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        return spinner;
    }

    public interface PostFilter {
        boolean filter(PostCreating post);
    }

    public void filterPostsByQuery(List<Filter> filters) {
        filterPostsLogic(adapter, posts, post -> {
            for (Filter filter : filters) {
                if (filter.isEnabled() && !filter.apply(post)) {
                    return false;
                }
            }
            return true;
        });

        // Set the filterButton based on the overall state of filter options
        filterButton.setSelected(false);

        if (posts.isEmpty()) {
            noPostFound.setVisibility(View.VISIBLE);
        } else {
            noPostFound.setVisibility(View.GONE);
        }
    }

    public static void filterPostsLogic(RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> adapter, List<PostCreating> posts, PostFilter filter) {
        List<PostCreating> filteredPosts = new ArrayList<>();

        for (PostCreating post : posts) {
            if (filter.filter(post)) {
                filteredPosts.add(post);
            }
        }

        PostDiffCallback diffCallback = new PostDiffCallback(posts, filteredPosts);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        posts.clear();
        posts.addAll(filteredPosts);
        diffResult.dispatchUpdatesTo(adapter);
    }

    public void deleteFilters() {
        deleteFilters.setOnClickListener(v -> {
            Arrays.fill(checkedItems, false);
            List<PostCreating> newPosts = new ArrayList<>(originalPosts);
            PostDiffCallback diffCallback = new PostDiffCallback(posts, newPosts);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
            posts.clear();
            posts.addAll(newPosts);
            diffResult.dispatchUpdatesTo(adapter);
            filterButton.setSelected(false);
            noPostFound.setVisibility(View.GONE);
        });
    }
}
