package com.example.zagrajmy.PostsManagement.AllPostsManagement;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.example.zagrajmy.Chat.ChatActivity;
import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostCreatingCopy;
import com.example.zagrajmy.PostsManagement.MainMenuPosts;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.Realm.RealmDataManager;
import com.example.zagrajmy.UserManagement.UserModel;

import java.util.Objects;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

public class ButtonHelperAllPosts {
    public static void handleChatButtonClick(View view, String userId) {
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        if (user != null) {
            String user2 = user.getId();

            try (Realm realm = Realm.getDefaultInstance()) {

                PrivateChatModel existingChatRoom = realm.where(PrivateChatModel.class)
                        .beginGroup()
                        .equalTo("userIdThatCreatedPost", userId)
                        .equalTo("user2", user2)
                        .endGroup()
                        .findFirst();

                // checking if room already exist
                RealmDataManager realmDataManager = RealmDataManager.getInstance();

                String currentRoomId;
                if (existingChatRoom == null) {
                    PrivateChatModel privateChatModel = new PrivateChatModel();
                    privateChatModel.setUserIdThatCreatedPost(userId);

                    Log.d("debug", privateChatModel.getRoomId());
                    privateChatModel.setUser2(user2);

                    UserModel userModel = realm.where(UserModel.class)
                            .equalTo("userId", user.getId())
                            .findFirst();

                    if (userModel != null) {
                        privateChatModel.setNickNameOfUser2(userModel.getNickName());
                    }
                    currentRoomId = privateChatModel.getRoomId();

                    realmDataManager.createChatroomInDatabase(privateChatModel);
                } else {
                    currentRoomId = existingChatRoom.getRoomId();
                    realmDataManager.createChatroomInDatabase(existingChatRoom);
                }

                realm.executeTransactionAsync(realm1 -> {
                }, () -> {
                    Intent intent = new Intent(view.getContext(), ChatActivity.class);
                    intent.putExtra("roomId", currentRoomId);
                    view.getContext().startActivity(intent);
                }, error -> Log.e("Realm Transaction Error", Objects.requireNonNull(error.getMessage())));
            }
        }
    }


    public static void handleSavePostButton(View view, int postId) {
        App realmApp = RealmAppConfig.getApp();
        User user = realmApp.currentUser();

        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(realm1 -> {

                PostCreating clickedPost = realm1.where(PostCreating.class)
                        .equalTo("postId", postId)
                        .findFirst();
                if (clickedPost != null && user != null) {
                    PostCreatingCopy existingPost = realm1.where(PostCreatingCopy.class)
                            .equalTo("postId", postId)
                            .findFirst();

                    if (existingPost == null) {
                        PostCreatingCopy newPost = new PostCreatingCopy();
                        newPost.setUserId(user.getId());
                        newPost.setPostId(clickedPost.getPostId());
                        newPost.setSportType(clickedPost.getSportType());
                        newPost.setCityName(clickedPost.getCityName());
                        newPost.setDateTime(clickedPost.getDateTime());
                        newPost.setHourTime(clickedPost.getHourTime());
                        newPost.setSkillLevel(clickedPost.getSkillLevel());
                        newPost.setAdditionalInfo(clickedPost.getAdditionalInfo());
                        newPost.setSavedByUser(true);

                        realm1.insertOrUpdate(newPost);
                    }
                }
            }, () -> {
                Intent intent = new Intent(view.getContext(), MainMenuPosts.class);
                view.getContext().startActivity(intent);
                Toast.makeText(view.getContext(), "Zapisano!", Toast.LENGTH_SHORT).show();

            }, error -> Log.e("Realm Transaction Error", Objects.requireNonNull(error.getMessage())));
        }
    }

    public static void handleMoreInfoButton(FragmentManager fragmentManager, PostCreating postCreating, String userId, MyBottomSheetFragment.OnDataPass dataPass) {
        MyBottomSheetFragment bottomSheetFragment = MyBottomSheetFragment.newInstance(postCreating, userId);

        bottomSheetFragment.setDataPass(dataPass);

        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
    }
}
