package com.noisevisionproductions.playmeet.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class CoolDownManager {
    private static final String COOLDOWN_PREFS = "CooldownPrefs";
    private static final String LAST_REPORT_TIME_KEY = "LastReportTime";
    private static final long COOLDOWN_REPORT_TIME = 180000; // 3 minuty
    private final SharedPreferences preferences;

    public CoolDownManager(@NonNull Context context) {
        preferences = context.getSharedPreferences(COOLDOWN_PREFS, Context.MODE_PRIVATE);
    }

    public boolean canSendReport() {
        long currentTimeMillis = System.currentTimeMillis();
        long lastReportTimeMillis = preferences.getLong(LAST_REPORT_TIME_KEY, 0);
        long timeSinceLastReport = currentTimeMillis - lastReportTimeMillis;

        if (timeSinceLastReport >= COOLDOWN_REPORT_TIME) {
            // Odpowiedni czas minął, użytkownik może wysłać kolejne zgłoszenie
            preferences.edit().putLong(LAST_REPORT_TIME_KEY, currentTimeMillis).apply();
            return true;
        } else {
            // Użytkownik nie może wysłać zgłoszenia, ponieważ cooldown jeszcze nie minął
            return false;
        }
    }
}
