package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostsDisplay;
import com.noisevisionproductions.playmeet.loginRegister.LoginAndRegisterActivity;
import com.noisevisionproductions.playmeet.postsManagement.postsFiltering.FilterPostsDialog;
import com.noisevisionproductions.playmeet.utilities.LocationManager;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

public class PostsOfTheGamesFragment extends Fragment {
    private FirestorePostsDisplay firestorePostsDisplay;
    private AppCompatButton filterButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_posts_list, container, false);

        setupView(view);
        getUserLocation();
        handleBackPressed();
        filterAllPosts(view);

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> refreshData(view), 100));

        return view;
    }

    private void setupView(@NonNull View view) {
        filterButton = view.findViewById(R.id.postsFilter);
        recyclerView = view.findViewById(R.id.recycler_view_posts);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void getUserLocation() {
        LocationManager locationManager = new LocationManager(getActivity());
        if (!locationManager.isLocationPermissionGranted()) {
            locationManager.getLastLocation(new LocationManager.LocationListener() {
                @Override
                public void onLocationReceived(String city) {
                    setupFirestorePostsDisplayAndRecyclerView(city);
                }

                @Override
                public void onLocationFailed(String errorMessage) {
                    ToastManager.showToast(requireContext(), errorMessage);
                }
            });
        } else {
            setupFirestorePostsDisplayAndRecyclerView(null);
        }

    }

    private void setupFirestorePostsDisplayAndRecyclerView(String city) {
        boolean isUserLoggedIn = FirebaseAuthManager.isUserLoggedIn();
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        String uid = firebaseHelper.getCurrentUser() != null ? firebaseHelper.getCurrentUser().getUid() : null;

        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            boolean isLocationEnabled = sharedPreferences.getBoolean("isLocationEnabled", false); // false jest domyślną wartością

            if (city == null || !isLocationEnabled) {
                firestorePostsDisplay = new FirestorePostsDisplay(isUserLoggedIn, uid);
            } else {
                ToastManager.showToast(requireContext(), getString(R.string.activitiesFromTheCity) + city);
                firestorePostsDisplay = new FirestorePostsDisplay(isUserLoggedIn, uid, city);
            }
        }
        setRecyclerView(view);
    }

    private void setRecyclerView(@NonNull View view) {
        Query query = firestorePostsDisplay.getQuery();
        FirestoreRecyclerViewHelper.setupRecyclerView(query, recyclerView, getChildFragmentManager(), getContext(), this, view);
    }

    private void refreshData(@NonNull View view) {
        int refreshLength = 600;
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(() -> {
            setRecyclerView(view);

            filterButton.setSelected(false);

            swipeRefreshLayout.setRefreshing(false);
        }, refreshLength);
    }

    private void filterAllPosts(@NonNull View view) {
        filterButton.setOnClickListener(v -> {
            FilterPostsDialog filterPostsDialog = new FilterPostsDialog(filterButton, recyclerView, getChildFragmentManager(), getContext(), view);
            filterPostsDialog.filterPostsWindow(requireActivity());
        });
    }

    private void handleBackPressed() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        };
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), callback);
    }

    private void showExitDialog() {
        if (FirebaseAuthManager.isUserLoggedIn()) {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.exit))
                    .setMessage(getString(R.string.logoutOrCloseApp))
                    .setPositiveButton(getString(R.string.logoutAccount), (dialog, which) -> {
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            firebaseAuth.signOut();
                            ToastManager.showToast(requireContext(), getString(R.string.logoutSuccessful));
                            Intent intent = new Intent(requireContext(), LoginAndRegisterActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }).setNegativeButton(getString(R.string.exit), (dialog, which) -> requireActivity()
                            .finishAffinity())
                    .setNeutralButton(getString(R.string.cancelButtonString), null)
                    .show();
        } else {
            Intent intent = new Intent(requireContext(), LoginAndRegisterActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
