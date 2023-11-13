package com.example.zagrajmy.Design;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.zagrajmy.R;

import java.util.List;
/*
* Klasa potrzebna w razie gdybym chcial AppCompatSpinner przy tworzeniu postow */
public class SpinnerAdapter extends ArrayAdapter<String> {
    public SpinnerAdapter(@NonNull Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_of_cities, parent, false);
        }
        String item = getItem(position);
        TextView textView = (TextView) convertView;
        textView.setText(item);
        return convertView;
    }



}
