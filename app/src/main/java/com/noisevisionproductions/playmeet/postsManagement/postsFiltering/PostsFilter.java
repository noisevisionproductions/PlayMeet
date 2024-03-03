package com.noisevisionproductions.playmeet.postsManagement.postsFiltering;

import android.app.Activity;
import android.app.AlertDialog;
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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.adapters.MySpinnerAdapterForFilterMenu;
import com.noisevisionproductions.playmeet.dataManagement.CityXmlParser;
import com.noisevisionproductions.playmeet.dataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.AdapterAllPosts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PostsFilter {
    private final List<PostCreating> originalPosts = new ArrayList<>();
    private final List<PostCreating> posts;
    private final RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> adapter;
    private final AppCompatButton filterButton, deleteFilters;
    private final AppCompatTextView noPostFound;
    private AutoCompleteTextView cityTextView;
    private Spinner spinnerSport, spinnerDifficulty;
    private EditText postIdText;
    @NonNull
    private final boolean[] checkedItems;

    public PostsFilter(RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> adapter, @NonNull List<PostCreating> posts, AppCompatButton filterButton, AppCompatButton deleteFilters, AppCompatTextView noPostFound) {
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
        builder.setPositiveButton("OK", (dialog, which) -> {

            // pobiera wybrane / wprowadzone wartości
            String selectedSport = spinnerSport.getSelectedItem().toString();
            String selectedCity = cityTextView.getText().toString();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity.getApplicationContext(), android.R.layout.simple_list_item_1, cityList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(activity.getColor(R.color.text));
                return view;
            }
        };
        textViewCity.setAdapter(adapter);


        return textViewCity;
    }

    public interface PostFilter {
        boolean filter(PostCreating post);
    }

    public void filterPostsByQuery(@NonNull List<Filter> filters) {
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

    public static void filterPostsLogic(@NonNull RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> adapter, @NonNull List<PostCreating> posts, @NonNull PostFilter filter) {
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

    private Integer getMinHeightDPScale(Activity activity) {
        int minHeightInDp = 60;
        float scale = activity.getResources().getDisplayMetrics().density;
        return (int) (minHeightInDp * scale + 0.5f);
    }
}