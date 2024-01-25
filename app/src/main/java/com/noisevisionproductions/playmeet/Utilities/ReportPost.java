package com.noisevisionproductions.playmeet.Utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;

import java.nio.charset.StandardCharsets;

public class ReportPost {
    private final Context context;
    private String currentUserId;

    public ReportPost(Context context) {
        this.context = context;
    }

    public void show(String postId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        currentUserId = firebaseHelper.getCurrentUser().getUid();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.dialog_report_post, null);

        final AppCompatEditText reportEditText = view.findViewById(R.id.reportEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Zgłoś post");
        builder.setView(view);
        builder.setPositiveButton("Wyślij", (dialog, which) -> {
            if (reportEditText.getText() != null && reportEditText.getText() != null) {
                // do każdego reportu dodaje automatycznie ID postu, który został zreportowany
                String reportText = reportEditText.getText().toString() + "+PostId" + postId;
                submitReportToFirebase(reportText, view);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Anuluj", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void submitReportToFirebase(String reportText, View view) {
        String reportId = getRefractoredString();
        StorageReference reportReference = FirebaseStorage.getInstance().getReference().child("UserReports").child(reportId);
        byte[] data = reportText.getBytes(StandardCharsets.UTF_8);

        UploadTask uploadTask = reportReference.putBytes(data);

        uploadTask.addOnFailureListener(e -> {
                    Snackbar.make(view, "Wystąpił błąd podczas wysyłania " + e, Snackbar.LENGTH_SHORT).show();
                    Log.e("Firebase Database error", "Saving report to DB " + e.getMessage());
                })
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(view.getContext(), "Zgłoszenie wysłane!", Toast.LENGTH_SHORT).show());
    }

    private String getRefractoredString() {
        String currentTime = String.valueOf(System.currentTimeMillis());

        return currentUserId + "=" + currentTime;
    }
}
