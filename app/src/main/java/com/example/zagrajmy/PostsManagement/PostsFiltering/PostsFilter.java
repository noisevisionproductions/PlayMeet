package com.example.zagrajmy.PostsManagement.PostsFiltering;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Adapters.MySpinnerAdapterForFilterMenu;
import com.example.zagrajmy.Adapters.PostsAdapterAllPosts;
import com.example.zagrajmy.DataManagement.CityXmlParser;
import com.example.zagrajmy.DataManagement.PostDiffCallback;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PostsFilter {
    private final List<PostCreating> originalPosts = new ArrayList<>();
    private final List<PostCreating> posts;
    private final RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> adapter;
    private final AppCompatButton filterButton;
    private final AppCompatButton deleteFilters;
    private Spinner spinnerSport;
    private Spinner spinnerCity;
    private Spinner spinnerDifficulty;
    private EditText postIdText;
    private final boolean[] checkedItems;

    public PostsFilter(RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> adapter, List<PostCreating> posts, AppCompatButton filterButton, AppCompatButton deleteFilters) {

        this.adapter = adapter;
        this.posts = posts;
        this.filterButton = filterButton;
        this.deleteFilters = deleteFilters;
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
        filterButton.setOnClickListener(v -> {
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
        });
    }

    private void setDialogButtons(AlertDialog.Builder builder) {
        final CharSequence[] items = {"Sport", "Miasto", "Poziom gry", "Numer postu"};

        spinnerSport.setVisibility(View.GONE);
        spinnerCity.setVisibility(View.GONE);
        spinnerDifficulty.setVisibility(View.GONE);

        InputFilter[] textFilter = new InputFilter[1];
        textFilter[0] = new InputFilter.LengthFilter(6);
        postIdText.setFilters(textFilter);
        postIdText.setHint("Wprowadź numer postu...");
        postIdText.setVisibility(View.GONE);

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

        builder.setPositiveButton("OK", (dialog, which) -> {

            String selectedSport = spinnerSport.getSelectedItem().toString();
            String selectedCity = spinnerCity.getSelectedItem().toString();
            String selectedDifficulty = spinnerDifficulty.getSelectedItem().toString();
            String selectedPostId = postIdText.getText().toString();

            Filter sportFilter = new SportFilter(checkedItems[0], selectedSport);
            Filter cityFilter = new CityFilter(checkedItems[1], selectedCity);
            Filter difficultyFilter = new DifficultyFilter(checkedItems[2], selectedDifficulty);
            if (!selectedPostId.isEmpty()) {
                Filter postIdFilter = new PostIDFilter(checkedItems[3], selectedPostId);
                List<Filter> newFilters = Arrays.asList(sportFilter, cityFilter, difficultyFilter, postIdFilter);
                filterPostsByQuery(newFilters);
            } else {
                List<Filter> newFilters = Arrays.asList(sportFilter, cityFilter, difficultyFilter);
                filterPostsByQuery(newFilters);
            }
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.cancel());
    }

    private EditText createTextFieldForPostID(Activity activity) {
        EditText text = new EditText(activity);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
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
    }

    public static void filterPostsLogic(RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> adapter, List<PostCreating> posts, PostFilter filter) {
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
            posts.addAll(originalPosts);
            diffResult.dispatchUpdatesTo(adapter);
            filterButton.setSelected(false);
        });
    }

}
