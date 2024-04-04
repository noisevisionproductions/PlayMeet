package com.noisevisionproductions.playmeet.postsManagement.postsMenu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

import java.nio.charset.StandardCharsets;

public class ReportPost {
    private final Context context;
    private String currentUserId;

    public ReportPost(Context context) {
        this.context = context;
    }

    public void showReportDialog(String postId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            currentUserId = firebaseHelper.getCurrentUser().getUid();

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.dialog_report_post, null);

            final AppCompatEditText reportEditText = view.findViewById(R.id.reportEditText);

            AlertDialog.Builder builder = getBuilder(postId, view, reportEditText);
            builder.create().show();
        }
    }

    @NonNull
    private AlertDialog.Builder getBuilder(String postId, View view, AppCompatEditText reportEditText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.reportPost));
        builder.setView(view);
        builder.setPositiveButton(context.getString(R.string.send), (dialog, which) -> {
            if (reportEditText.getText() != null && reportEditText.getText() != null) {
                // do każdego reportu dodaje automatycznie ID postu, który został zreportowany
                String reportText = reportEditText.getText().toString() + "+PostId" + postId;
                submitReportToFirebase(reportText, view);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancelButtonString), (dialog, which) -> dialog.dismiss());
        return builder;
    }

    private void submitReportToFirebase(@NonNull String reportText, @NonNull View view) {
        String reportId = getRefractoredString();
        StorageReference reportReference = FirebaseStorage.getInstance()
                .getReference()
                .child("UserReports")
                .child(reportId);
        byte[] data = reportText.getBytes(StandardCharsets.UTF_8);

        UploadTask uploadTask = reportReference.putBytes(data);

        uploadTask.addOnFailureListener(e -> {
                    Snackbar.make(view, context.getString(R.string.errorWhileSending) + " " + e, Snackbar.LENGTH_SHORT).show();
                    Log.e("Firebase Database error", "Saving report to DB " + e.getMessage());
                })
                .addOnSuccessListener(taskSnapshot -> ToastManager.showToast(view.getContext(), context.getString(R.string.reportSent)));
    }

    @NonNull
    private String getRefractoredString() {
        String currentTime = String.valueOf(System.currentTimeMillis());

        return currentUserId + "=" + currentTime;
    }
}
