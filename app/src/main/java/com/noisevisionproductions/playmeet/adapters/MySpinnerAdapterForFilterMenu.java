package com.noisevisionproductions.playmeet.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

// jest to drugi adapter do spinnera, poświęcony do menu filtrowania postów, ponieważ używam do tego AlertDialog
public class MySpinnerAdapterForFilterMenu extends ArrayAdapter<String> {
    public MySpinnerAdapterForFilterMenu(@NonNull Context context, int resource, @NonNull List<String> items) {
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
