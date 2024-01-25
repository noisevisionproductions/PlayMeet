package com.noisevisionproductions.playmeet.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class CoolDownManager {
    private static final String COOLDOWN_PREFS = "CooldownPrefs";
    private static final String LAST_REPORT_TIME_KEY = "LastReportTime";
    private static final long COOLDOWN_TIME_MILLIS = 180000; // 3 minuty
    private final SharedPreferences preferences;

    public CoolDownManager(Context context) {
        preferences = context.getSharedPreferences(COOLDOWN_PREFS, Context.MODE_PRIVATE);
    }

    public boolean canSendReport() {
        long currentTimeMillis = System.currentTimeMillis();
        long lastReportTimeMillis = preferences.getLong(LAST_REPORT_TIME_KEY, 0);
        long timeSinceLastReport = currentTimeMillis - lastReportTimeMillis;

        if (timeSinceLastReport >= COOLDOWN_TIME_MILLIS) {
            // Odpowiedni czas minął, użytkownik może wysłać kolejne zgłoszenie
            preferences.edit().putLong(LAST_REPORT_TIME_KEY, currentTimeMillis).apply();
            return true;
        } else {
            // Użytkownik nie może wysłać zgłoszenia, ponieważ cooldown jeszcze nie minął
            return false;
        }
    }
}
