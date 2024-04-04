package com.noisevisionproductions.playmeet.notifications;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

import java.util.HashMap;
import java.util.Map;

public class NotificationPermissionDialog {
    private final ComponentActivity activity;
    private final ActivityResultLauncher<String> requestPermissionLauncherForNotifications;

    public NotificationPermissionDialog(ComponentActivity activity) {
        this.activity = activity;
        this.requestPermissionLauncherForNotifications = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        FirebaseMessaging.getInstance().subscribeToTopic("notifications");
                        getFCMToken();
                    } else {
                        ToastManager.showToast(activity, activity.getString(R.string.notificationsTurnedOff));
                    }
                });
    }

    public void requestNotificationsPermission() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        boolean hasDeniedWithNeverAskAgain = sharedPreferences.getBoolean("DeniedNotificationPermission", false);

        if (hasDeniedWithNeverAskAgain) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                FirebaseMessaging.getInstance().subscribeToTopic("chat_messages");
                getFCMToken();
            } else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showDialog();
            } else {
                requestPermissionLauncherForNotifications.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic("chat_messages");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void showDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.weNeedYourConsent))
                .setMessage(activity.getString(R.string.weNeedYourConsentForNotifications))
                .setPositiveButton("OK", ((dialog, which) -> requestPermissionLauncherForNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)))
                .setNegativeButton(activity.getString(R.string.noThanks), ((dialog, which) -> dialog.dismiss()))
                .setNegativeButton(activity.getString(R.string.doNotShowAgain), (((dialog, which) -> doNotAskForPermission())))
                .create()
                .show();
    }

    private void doNotAskForPermission() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("DeniedNotificationPermission", true);
        editor.apply();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                if (firebaseHelper.getCurrentUser() != null) {
                    FirebaseUserRepository firebaseUserRepository = new FirebaseUserRepository();
                    String token = task.getResult();
                    Map<String, Object> tokenUpdate = new HashMap<>();
                    tokenUpdate.put("fcmToken", token);
                    firebaseUserRepository.updateUser(firebaseHelper.getCurrentUser().getUid(), tokenUpdate, new OnCompletionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d("UpdateToken", "Token FCM updated.");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("UpdateToken", "Token FCM error: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }
}