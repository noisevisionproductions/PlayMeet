package com.noisevisionproductions.playmeet.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

// Ustawiam wlasciwosci AppSpinnerAdapter przy tworzeniu postów
public class MySpinnerAdapter extends ArrayAdapter<String> {

    public MySpinnerAdapter(@NonNull Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // wygląd spinnera w domyślnej pozycji
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);

        textView.setTextColor(Color.WHITE);

        return view;
    }

    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        // wygląd rozwijanego menu
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);

        // jeżeli spinner jest na indeksie 0 (1 pozycja na spinerze), to ustawiam tekst na przezroczysty tryb, z kolorem szarym oraz niemożliwym do kliknięcia, ponieważ w plikach .xml stworzonych przeze mnie, w których są dane, które spinner wyświetla, są na pierwszej pozycji informacje na temat kontentu spinnera, np. "Wybierz płeć"
        if (position == 0) {
            textView.setBackgroundColor(Color.TRANSPARENT);
            textView.setTextColor(Color.GRAY);
            textView.setEnabled(false);
        } else {
            // jeżeli spinner jest na innym indeksie niż 0, to ustawiam tekst na domyślny kolor
            textView.setTextColor(Color.BLACK);
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        // Wyłącza pierwszy element
        return position != 0;
    }
}
