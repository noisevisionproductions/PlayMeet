package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.PostInfo;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.utilities.dataEncryption.UserModelDecrypt;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSignedUpUsers extends RecyclerView.Adapter<AdapterSignedUpUsers.ViewHolder> {
    private final List<UserModel> signedUpUserFields;
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final Context context;
    private final PostInfo postInfo;

    public AdapterSignedUpUsers(List<UserModel> signedUpUserFields, Context context, PostInfo postInfo) {
        this.signedUpUserFields = signedUpUserFields;
        this.context = context;
        this.postInfo = postInfo;
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
        unlockChatButton(holder, userModel, position);
        holder.nicknameText.setText(userModel.getNickname());
        holder.progressBarLayout.setVisibility(View.VISIBLE);
        holder.signedUsersLayout.setVisibility(View.GONE);
        AsyncTask.execute(() -> {
            try {
                UserModel decryptedUserModel = UserModelDecrypt.decryptUserModel(context, userModel);
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
                    UserModelDecrypt.getDecryptionError("signed users adapter error " + e.getMessage());
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return signedUpUserFields.size();
    }

    private void unlockChatButton(ViewHolder holder, UserModel userModel, int position) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();
            if (this.postInfo.getUserId().equals(currentUserId)) {
                holder.chatButtonSignedUsers.setVisibility(View.VISIBLE);
                holder.kickUserButton.setVisibility(View.VISIBLE);
                holder.chatButtonSignedUsers.setOnClickListener(v -> firebaseHelper.getExistingChatRoomId(this.postInfo.getUserId(), userModel.getUserId(), chatRoomId -> ButtonsForChatAndSignIn.navigateToChatRoom(v, chatRoomId)));
                createDialog(holder, position, userModel);
            } else {
                holder.chatButtonSignedUsers.setVisibility(View.GONE);
                holder.kickUserButton.setVisibility(View.GONE);
            }
        }
    }

    private void createDialog(ViewHolder holder, int position, UserModel userModel) {
        holder.kickUserButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setMessage(context.getString(R.string.doYouReallyWantToDelete) + userModel.getNickname() + context.getString(R.string.fromActivity))
                .setPositiveButton(context.getString(R.string.yes), (dialog, which) -> {
                    removeRegistration(userModel);
                    signedUpUserFields.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, signedUpUserFields.size());
                })
                .setNegativeButton(context.getString(R.string.no), null)
                .show());
    }

    private void removeRegistration(UserModel userModel) {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        firestorePostRepository.removeUserFromRegistration(this.postInfo.getPostId(), userModel.getUserId(), new OnCompletionListener() {
            @Override
            public void onSuccess() {
                ToastManager.showToast(context, context.getString(R.string.userRemoved));
            }

            @Override
            public void onFailure(Exception e) {
                ToastManager.showToast(context, context.getString(R.string.error) + e.getMessage());
                Log.e("Firebase Update Error", "Removing signed up user when saved post is removed " + e.getMessage());
            }
        });
    }

    private void getUserAvatar(@NonNull String userId, @NonNull ViewHolder holder) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final AppCompatTextView nicknameText, cityText, genderText;
        private final ProgressBar progressBarLayout;
        private final LinearLayoutCompat signedUsersLayout;
        private final AppCompatButton chatButtonSignedUsers, kickUserButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            nicknameText = itemView.findViewById(R.id.nicknameText);
            cityText = itemView.findViewById(R.id.cityText);
            genderText = itemView.findViewById(R.id.genderText);
            progressBarLayout = itemView.findViewById(R.id.progressBarLayout);
            signedUsersLayout = itemView.findViewById(R.id.signedUsersLayout);
            chatButtonSignedUsers = itemView.findViewById(R.id.chatButtonSignedUsers);
            kickUserButton = itemView.findViewById(R.id.kickUserButton);
        }
    }
}
