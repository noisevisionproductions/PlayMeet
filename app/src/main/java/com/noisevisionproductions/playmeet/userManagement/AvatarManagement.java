package com.noisevisionproductions.playmeet.userManagement;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.noisevisionproductions.playmeet.adapters.ToastManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvatarManagement {
    private ActivityResultLauncher<Intent> chooseImageFromGallery;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private final AppCompatActivity activity;
    private final AppCompatButton uploadAvatarButton;
    @Nullable
    private Uri currentImageUri;
    private String currentUserId;

    public AvatarManagement(AppCompatActivity activity, AppCompatButton uploadAvatarButton) {
        this.activity = activity;
        this.uploadAvatarButton = uploadAvatarButton;
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            currentUserId = firebaseHelper.getCurrentUser().getUid();
        }
    }

    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        chooseImageFromGallery.launch(intent);
    }

    public void setupListeners() {
        // ustawiam słuchaczy na przyciski do wyboru avatara
        // jeżeli użytkowik wybrał opcję wyboru zdjęcia z galerii, zapisuję go do bazy danych
        chooseImageFromGallery = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        currentImageUri = result.getData().getData();
                        if (currentImageUri != null) {
                            uploadImageToFirebaseStorage(currentImageUri);
                        }
                    }
                }
        );
        // po wybraniu opcji zrobienia zdjęcia, zapisuję je w bazie danych
        takePictureLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    if (result) {
                        if (currentImageUri != null) {
                            uploadImageToFirebaseStorage(currentImageUri);
                        }
                    }
                }
        );

        uploadAvatarButton.setOnClickListener(v -> chooseImageSource());
    }

    private void chooseImageSource() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // Niestandardowy layout dla elementów AlertDialog
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_list_item_1,
                new CharSequence[]{"Aparat", "Galeria"}
        ) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };

        // niestandardowy widok dla tytułu alertu
        TextView titleView = new TextView(activity);
        titleView.setText(R.string.chooseImageSource);
        titleView.setGravity(Gravity.CENTER);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        builder.setCustomTitle(titleView);

        builder.setAdapter(adapter, (dialog, which) -> {
            switch (which) {
                case 0 ->
                    // uruchomienie aparatu
                        takePicture();
                case 1 ->
                    // wybór z galerii
                        pickImageFromGallery();
            }
        });
        builder.show();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    final File photoFile = createImageFile();
                    handler.post(() -> {
                        try {
                            // Execute the remaining code on the main thread after file creation
                            currentImageUri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".provider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                            takePictureLauncher.launch(currentImageUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Making photo", "Taking photo by user " + e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("Making photo", "Creating image file " + e.getMessage());
                }
            });
        }
    }


    @NonNull
    private File createImageFile() throws IOException {
        // dodaje do nazwy pliku format, który jest datą i czasem w celu unikalnej nazwy
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void uploadImageToFirebaseStorage(@NonNull Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars").child(currentUserId);
        try {
            // przygotowuję obraz, konwertując go na Bitmap, który będzie potem wysłany do bazy danych i sprawdzam, czy obraz wymaga odwrócenia, czy nie,
            // bo z jakiegoś powodu po wybraniu obrazu z galerii, czasami aplikacaj go odwraca.
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

                storageReference.putBytes(data)
                        .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                                .addOnSuccessListener(downloadUri -> saveImageUrlToUserModel(downloadUri.toString())))
                        .addOnFailureListener(e -> {
                            getErrorToast(e);
                            Log.e("Avatar", "Uploading new user avatar to DB " + e.getMessage());
                        });
            }
        } catch (IOException e) {
            Log.e("Avatar", "Uploading new user avatar to DB " + e.getMessage());
        }
    }

    @Nullable
    private Bitmap rotateImage(@NonNull Uri imageUri, @NonNull Bitmap bitmap) throws IOException {
        //String imagePath = getImagePathFromUri(imageUri);
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

    private void saveImageUrlToUserModel(@Nullable String imageUrl) {
        if (imageUrl != null) {
            DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId);

            userReference.child("avatar").setValue(imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        Intent intent = activity.getIntent();
                        activity.finish();
                        activity.startActivity(intent);
                        ToastManager.showToast(activity, "Avatar zapisany");
                    })
                    .addOnFailureListener(e -> {
                        getErrorToast(e);
                        Log.e("Avatar", "Uploading avatar URL to DB " + e.getMessage());
                    });
        }
    }

    private void getErrorToast(@NonNull Exception e) {
        ToastManager.showToast(activity, "Błąd!" + e.getMessage());
    }
}
