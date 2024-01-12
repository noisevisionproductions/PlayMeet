package com.example.playmeet.Utilities;

import android.text.InputFilter;
import android.text.Spanned;

public class InputFilterManagement {


    public static InputFilter[] createNameFilters() {
        InputFilter[] filters = new InputFilter[2];
        filters[0] = new InputFilter.LengthFilter(50);
        filters[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("")) {
                    return source;
                }
                if (source.toString().matches("[a-zA-Z ]+")) {
                    return source;
                }
                return "";
            }
        };
        return filters;
    }
}
