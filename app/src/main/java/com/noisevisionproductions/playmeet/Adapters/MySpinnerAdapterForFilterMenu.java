package com.noisevisionproductions.playmeet.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class MySpinnerAdapterForFilterMenu extends ArrayAdapter<String> {
    public MySpinnerAdapterForFilterMenu(@NonNull Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }

    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
