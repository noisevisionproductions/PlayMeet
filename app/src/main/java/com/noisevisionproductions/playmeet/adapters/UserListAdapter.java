package com.noisevisionproductions.playmeet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment.ButtonsForChatAndSignIn;
import com.noisevisionproductions.playmeet.userManagement.UserModel;
import com.noisevisionproductions.playmeet.userManagement.userProfile.OpenUserProfile;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private final List<UserModel> userModelList;
    private final FragmentManager fragmentManager;
    private final Context context;

    public UserListAdapter(List<UserModel> userModelList, FragmentManager fragmentManager, Context context) {
        this.userModelList = userModelList;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_list, parent, false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        view.setOnClickListener(v -> {
            int position = userViewHolder.getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                UserModel userModel = userModelList.get(position);
                OpenUserProfile.openUserProfile(userModel.getUserId(), fragmentManager);
            }
        });
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserModel userModel = userModelList.get(position);
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        firebaseHelper.getUserAvatar(context, userModel.getUserId(), holder.userAvatarUsersList);
        holder.nicknameText.setText(userModel.getNickname());
        holder.chatButtonFromAllUsersList.setOnClickListener(v -> firebaseHelper.getExistingChatRoomId(userModel.getUserId(), userModel.getUserId(), chatRoomId -> ButtonsForChatAndSignIn.navigateToChatRoom(v, chatRoomId)));
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatarUsersList;
        private final AppCompatTextView nicknameText;
        private final AppCompatButton chatButtonFromAllUsersList;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatarUsersList = itemView.findViewById(R.id.userAvatarUsersList);
            nicknameText = itemView.findViewById(R.id.nicknameText);
            chatButtonFromAllUsersList = itemView.findViewById(R.id.chatButtonFromAllUsersList);
        }
    }
}
