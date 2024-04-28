package com.noisevisionproductions.playmeet.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.noisevisionproductions.playmeet.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationManager {
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    public LocationManager(Activity activity) {
        this.activity = activity;
        this.fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    public void getLastLocation(LocationListener listener) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        String city = getAddressFromLocation(location.getLatitude(), location.getLongitude());
                        listener.onLocationReceived(city);
                    } else {
                        listener.onLocationFailed("Location is unavailable");
                    }
                })
                .addOnFailureListener(e -> listener.onLocationFailed("Error while getting location: " + e.getMessage()));
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                result = address.getLocality();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
        if (isLocationPermissionGranted()) {
            new AlertDialog.Builder(activity)
                    .setTitle(R.string.locationIsRequired)
                    .setMessage(R.string.locationIsRequiredContinuation)
                    .setPositiveButton(R.string.yes, (dialog, which) -> ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public interface LocationListener {
        void onLocationReceived(String city);

        void onLocationFailed(String errorMessage);
    }
}
