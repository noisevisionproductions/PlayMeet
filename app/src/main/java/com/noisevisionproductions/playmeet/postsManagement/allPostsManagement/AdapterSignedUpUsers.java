package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.UserModelDecryptor;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSignedUpUsers extends RecyclerView.Adapter<AdapterSignedUpUsers.ViewHolder> {
    private final List<UserModel> signedUpUserFields;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final Context context;

    public AdapterSignedUpUsers(List<UserModel> signedUpUserFields, Context context) {
        this.signedUpUserFields = signedUpUserFields;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_signed_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel userModel = signedUpUserFields.get(position);
        getUserAvatar(userModel.getUserId(), holder);
        holder.nicknameText.setText(userModel.getNickname());
        holder.progressBarLayout.setVisibility(View.VISIBLE);
        holder.signedUsersLayout.setVisibility(View.GONE);
        AsyncTask.execute(() -> {
            try {
                UserModel decryptedUserModel = UserModelDecryptor.decryptUserModel(context, userModel);
                // Aktualizuj interfejs użytkownika w wątku UI
                mainThreadHandler.post(() -> {
                    holder.progressBarLayout.setVisibility(View.GONE);
                    holder.signedUsersLayout.setVisibility(View.VISIBLE);
                    holder.cityText.setText(decryptedUserModel.getLocation());
                    holder.genderText.setText(decryptedUserModel.getGender());
                });
            } catch (Exception e) {
                mainThreadHandler.post(() -> {
                    holder.progressBarLayout.setVisibility(View.GONE);
                    UserModelDecryptor.getDercyptionError("signed users adapter error " + e.getMessage());
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return signedUpUserFields.size();
    }

    private void getUserAvatar(@NonNull String userId, @NonNull ViewHolder holder) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final AppCompatTextView nicknameText, cityText, genderText;
        private final ProgressBar progressBarLayout;
        private final LinearLayoutCompat signedUsersLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            nicknameText = itemView.findViewById(R.id.nicknameText);
            cityText = itemView.findViewById(R.id.cityText);
            genderText = itemView.findViewById(R.id.genderText);
            progressBarLayout = itemView.findViewById(R.id.progressBarLayout);
            signedUsersLayout = itemView.findViewById(R.id.signedUsersLayout);
        }
    }
}
