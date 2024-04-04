package com.noisevisionproductions.playmeet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.noisevisionproductions.playmeet.utilities.DifficultyModel;

import java.util.List;

/**
 * Custom adapter for Spinner, which supports various types of objects.
 * Allows for displaying Spinner elements with custom styles and text.
 */
public class MySpinnerAdapter extends ArrayAdapter<Object> {

    /**
     * @param context  Context, in which adapter is used.
     * @param resource ID of layout, which has to be used to inflate view for single element.
     * @param items    List with objects that has to be shown in the Spinner.
     */
    public MySpinnerAdapter(@NonNull Context context, int resource, @NonNull List<Object> items) {
        super(context, resource, items);
    }

    /**
     * Changes the view of current Spinner position (not expanded).
     * I'm setting up the text and how it should behave based on the type of object.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // wygląd spinnera w domyślnej pozycji
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);

        setTextBasedOnItem(textView, position);

        return view;
    }

    /**
     * Layout of expanded Spinner menu.
     * I'm making sure that when the spinner is on the index 0, then the text inside is
     * unable to click on and is grayed out. (because the first item on the list is always text, that explains
     * current spinner, for example "Choose city").
     */
    @NonNull
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        // wygląd rozwijanego menu
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);

        if (position == 0) {
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setTextColor(Color.GRAY);
            textView.setEnabled(false);
        } else {
            textView.setTextColor(Color.parseColor("#e9e8f0"));
        }

        setTextBasedOnItem(textView, position);

        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        // Wyłącza pierwszy element
        return position != 0;
    }

    private void setTextBasedOnItem(TextView textView, int position) {
        Object item = getItem(position);
        if (item instanceof String) {
            textView.setText((String) item);
        } else if (item instanceof DifficultyModel) {
            textView.setText(((DifficultyModel) item).name());
        }
    }
}
