package com.noisevisionproductions.playmeet.userManagement.userProfile;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirebaseUserRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnUserModelCompleted;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.dataEncryption.UserModelDecrypt;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends DialogFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return layoutInflater.inflate(R.layout.activity_user_profile, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.closeUserProfileButton).setOnClickListener(v -> dismiss());

        getUserData(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setDialogFragmentMetrics();
        }
    }

    private void setDialogFragmentMetrics() {
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics displayMetrics = new DisplayMetrics();
            if (getActivity() != null && getActivity().getWindowManager() != null) {
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            }

            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            int windowWidth = (int) (width * 0.85);
            int windowHeight = (int) (height * 0.85);

            dialog.getWindow().setLayout(windowWidth, windowHeight);
        }
    }

    private void getUserData(View view) {
        ProgressBar progressBar = view.findViewById(R.id.progressBarLayout);
        LinearLayoutCompat linearLayoutCompat = view.findViewById(R.id.infoUserProfileLayout);
        progressBar.setVisibility(View.VISIBLE);

        if (getArguments() != null) {
            String userId = getArguments().getString(ConstantUserId.USER_ID_KEY);
            if (userId != null) {
                new FirebaseUserRepository().getUserAllData(userId, new OnUserModelCompleted() {
                    @Override
                    public void onSuccess(UserModel userModel) {
                        if (getActivity() == null) return;
                        try {
                            UserModel decryptedUserModel = UserModelDecrypt.decryptUserModel(getContext(), userModel);
                            getActivity().runOnUiThread(() -> {
                                linearLayoutCompat.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);

                                CircleImageView userAvatarUserProfile = view.findViewById(R.id.userAvatarUserProfile);
                                new FirebaseHelper().getUserAvatar(requireContext(), userId, userAvatarUserProfile);

                                AppCompatTextView nicknameUserProfile = view.findViewById(R.id.nicknameUserProfile);
                                nicknameUserProfile.setText(userModel.getNickname());
                                nicknameUserProfile.setVisibility(View.VISIBLE);

                                AppCompatTextView nameUserProfile = view.findViewById(R.id.nameUserProfile);
                                nameUserProfile.setText(decryptedUserModel.getName());

                                AppCompatTextView genderUserProfile = view.findViewById(R.id.genderUserProfile);
                                genderUserProfile.setText(decryptedUserModel.getGender());

                                AppCompatTextView cityUserProfile = view.findViewById(R.id.cityUserProfile);
                                cityUserProfile.setText(decryptedUserModel.getLocation());

                                AppCompatTextView ageUserProfile = view.findViewById(R.id.ageUserProfile);
                                ageUserProfile.setText(decryptedUserModel.getAge());

                                AppCompatTextView aboutUserProfile = view.findViewById(R.id.aboutUserProfile);
                                aboutUserProfile.setText(decryptedUserModel.getAboutMe());
                            });
                        } catch (Exception e) {
                            Log.e("Decryption error", "Error decrypting user data in user post " + e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            linearLayoutCompat.setVisibility(View.VISIBLE);
                            Log.e("Error setting up user profile", "Error setting up user profile " + e.getMessage());
                        });
                    }
                });
            }
        }
    }
}
