package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.UserModelDecryptor;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSignedUpUsers extends RecyclerView.Adapter<AdapterSignedUpUsers.ViewHolder> {
    private final List<UserModel> signedUpUserFields;
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
        AsyncTask.execute(() -> {
            try {
                UserModel decryptedUserModel = UserModelDecryptor.decryptUserModel(userModel);
                // Aktualizuj interfejs użytkownika w wątku UI
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.cityText.setText(decryptedUserModel.getLocation());
                    holder.genderText.setText(decryptedUserModel.getGender());
                });

            } catch (Exception e) {
                UserModelDecryptor.getDercyptionError("signed users adapter error " + e.getMessage());
            }
        });
        holder.cityText.setText(userModel.getLocation());
        holder.genderText.setText(userModel.getGender());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            nicknameText = itemView.findViewById(R.id.nicknameText);
            cityText = itemView.findViewById(R.id.cityText);
            genderText = itemView.findViewById(R.id.genderText);
        }
    }
}
