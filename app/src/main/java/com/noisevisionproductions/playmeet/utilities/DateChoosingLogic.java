package com.noisevisionproductions.playmeet.utilities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;

import java.util.Calendar;

public class DateChoosingLogic {
    private final Context context;
    private final PostModel postModel;

    public DateChoosingLogic(Context context, PostModel postModel) {
        this.postModel = postModel;
        this.context = context;
    }

    public void pickDate(@NonNull final TextInputEditText textInputEditText) {
        final Calendar calendar = Calendar.getInstance();
        int chosenYear = calendar.get(Calendar.YEAR);
        int chosenMonth = calendar.get(Calendar.MONTH);
        int chosenDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, day) -> {
            Calendar dateOfTheUser = Calendar.getInstance();
            dateOfTheUser.set(year, month, day);

            String date = context.getString(R.string.date_format, day, month + 1, year);
            textInputEditText.setText(date);
            postModel.setDateTime(date);

        }, chosenYear, chosenMonth, chosenDay);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Wyczyść", ((dialog, which) -> textInputEditText.setText("")));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    public void pickHour(@NonNull final TextInputEditText textInputEditText) {
        int hour = 12;
        int minute = 30;
        boolean is24HourFormat = true;

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minuteOfHour) -> {
            String formattedHour = context.getString(R.string.hour_format, hourOfDay, minuteOfHour);
            textInputEditText.setText(formattedHour);
            postModel.setHourTime(formattedHour);
        }, hour, minute, is24HourFormat);

        timePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Wyczyść", (((dialog, which) -> textInputEditText.setText(""))));
        timePickerDialog.show();
    }

    public void noDateGiven() {
        postModel.setDateTime(context.getString(R.string.hourDateDoesntMatter));
    }

    public void noHourGiven() {
        postModel.setHourTime(context.getString(R.string.hourDateDoesntMatter));
    }
}
