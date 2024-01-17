package com.noisevisionproductions.playmeet.UserManagement;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvatarManagement {
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private final FirebaseHelper firebaseHelper;
    private final AppCompatActivity activity;
    private final AppCompatButton uploadAvatarButton;

    public AvatarManagement(AppCompatActivity activity, AppCompatButton uploadAvatarButton) {
        this.activity = activity;
        this.uploadAvatarButton = uploadAvatarButton;
        this.firebaseHelper = new FirebaseHelper();
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityResultLauncher.launch(intent);
    }

    public void setupListener() {
        activityResultLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                o -> {
                    if (o.getResultCode() == Activity.RESULT_OK) {
                        Intent data = o.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            uploadImageToFirebaseStorage(selectedImageUri);
                        }
                    }
                }
        );

        uploadAvatarButton.setOnClickListener(v -> pickImageFromGallery());
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        String userId = firebaseHelper.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars").child(userId);

        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = rotateImage(imageUri, bitmap);
            int width = 200;
            if (bitmap != null) {
                int height = (int) (bitmap.getHeight() * ((double) width / bitmap.getWidth()));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();

                storageReference.putBytes(data).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> saveImageUrlToUserModel(downloadUri.toString())))
                        .addOnFailureListener(e -> Toast.makeText(activity, "Błąd!", Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImage(Uri imageUri, Bitmap bitmap) throws IOException {
        InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
        if (inputStream != null) {
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90);
                case ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180);
                case ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270);
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return null;
    }

    private void saveImageUrlToUserModel(String imageUrl) {
        if (imageUrl != null) {
            String userId = firebaseHelper.getCurrentUser().getUid();
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(userId);

            userReference.child("avatar").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = activity.getIntent();
                        activity.finish();
                        activity.startActivity(intent);
                        Toast.makeText(activity, "Zapisano avatar!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(activity, "Błąd!", Toast.LENGTH_SHORT).show());
        }
    }
}
