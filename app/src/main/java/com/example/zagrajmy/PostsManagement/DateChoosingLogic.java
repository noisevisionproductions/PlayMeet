package com.example.zagrajmy.PostsManagement;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class DateChoosingLogic {
    private final Context context;
    private final PostCreating postCreating;

    public DateChoosingLogic(Context context, PostCreating postCreating) {
        this.postCreating = postCreating;
        this.context = context;
    }

    public void pickDate(final TextInputEditText textInputEditText) {
        final Calendar calendar = Calendar.getInstance();
        int chosenYear = calendar.get(Calendar.YEAR);
        int chosenMonth = calendar.get(Calendar.MONTH);
        int chosenDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, day) -> {
            Calendar dateOfTheUser = Calendar.getInstance();
            dateOfTheUser.set(year, month, day);

            String date = context.getString(R.string.date_format, day, month + 1, year);
            textInputEditText.setText(date);
            postCreating.setDateTime(date);

        }, chosenYear, chosenMonth, chosenDay);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void pickHour(final TextInputEditText textInputEditText){
        int hour = 12;
        int minute = 30;
        boolean is24HourFormat = true;
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minuteOfHour) -> {
            String hour1 = context.getString(R.string.hour_format, hourOfDay, minuteOfHour);
            textInputEditText.setText(hour1);
            postCreating.setHourTime(hour1);
        }, hour, minute, is24HourFormat);
        timePickerDialog.show();

    }
}