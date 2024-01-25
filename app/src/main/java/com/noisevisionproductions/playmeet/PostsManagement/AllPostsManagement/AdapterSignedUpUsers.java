package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

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
        holder.cityText.setText(userModel.getLocation());
        holder.genderText.setText(userModel.getGender());
    }

    @Override
    public int getItemCount() {
        return signedUpUserFields.size();
    }

    private void getUserAvatar(String userId, ViewHolder holder) {
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
