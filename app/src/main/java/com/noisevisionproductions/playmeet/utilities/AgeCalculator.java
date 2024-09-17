package com.noisevisionproductions.playmeet.utilities;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class AgeCalculator {

    public static int calculateAge(String birthdateString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthdate = LocalDate.parse(birthdateString, dateTimeFormatter);
        LocalDate today = LocalDate.now();
        return Period.between(birthdate, today).getYears();
    }
}
