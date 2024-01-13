package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.Chat.ChatActivity;
import com.noisevisionproductions.playmeet.Chat.PrivateChatModel;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.Firebase.RealmAppConfig;
import com.noisevisionproductions.playmeet.Firebase.RealmDataManager;
import com.noisevisionproductions.playmeet.LoginRegister.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.PostsManagement.MainMenuPosts;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

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
                        privateChatModel.setNickNameOfUser2(userModel.getNickname());
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

    public static void handleSavePostButton(View view, String postId) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        if (firebaseHelper.getCurrentUser() != null) {
            DatabaseReference allPostsReference = FirebaseDatabase.getInstance().getReference().child("PostCreating").child(postId);
            allPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot originalPostSnapshot) {
                    if (originalPostSnapshot.exists()) {
                        DatabaseReference savedPostsReference = FirebaseDatabase.getInstance().getReference().child("SavedPostCreating").child(firebaseHelper.getCurrentUser().getUid()).child(postId);
                        savedPostsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                if (!postSnapshot.exists()) {
                                    // pobieram dane postu, do którego użytkownik chce się zapisać
                                    PostCreating originalPost = originalPostSnapshot.getValue(PostCreating.class);

                                    if (originalPost != null) {
                                        // tworzę kopię postu z bazy danych, aby móc go potem wyświetlić jako zapisany z innym layoutuem
                                        PostCreatingCopy newSavedPost = new PostCreatingCopy();
                                        newSavedPost.setUserId(firebaseHelper.getCurrentUser().getUid());
                                        newSavedPost.setPostId(originalPost.getPostId());
                                        newSavedPost.setSportType(originalPost.getSportType());
                                        newSavedPost.setCityName(originalPost.getCityName());
                                        newSavedPost.setDateTime(originalPost.getDateTime());
                                        newSavedPost.setHourTime(originalPost.getHourTime());
                                        newSavedPost.setSkillLevel(originalPost.getSkillLevel());
                                        newSavedPost.setAdditionalInfo(originalPost.getAdditionalInfo());
                                        newSavedPost.setSavedByUser(true);

                                        // zapisuję wybrany post w bazie danych pod "SavedPostCreating"
                                        savedPostsReference.setValue(newSavedPost, (databaseError, databaseReference) -> {
                                            if (databaseError == null) {
                                                Intent intent = new Intent(view.getContext(), MainMenuPosts.class);
                                                view.getContext().startActivity(intent);
                                                Toast.makeText(view.getContext(), "Zapisano!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("Firebase Save Error", Objects.requireNonNull(databaseError.getMessage()));
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase Read Error", Objects.requireNonNull(error.getMessage()));
                            }
                        });
                    } else {
                        Toast.makeText(view.getContext(), "Post już jest zapisany!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase Read Error", Objects.requireNonNull(error.getMessage()));
                }
            });
        }
    }

    public static void handleMoreInfoButton(FragmentManager fragmentManager, PostCreating postCreating, MyBottomSheetFragment.OnDataPass dataPass) {
        FirebaseAuthManager firebaseAuthManager = new FirebaseAuthManager();
        if (firebaseAuthManager.isUserLoggedIn()) {
            MyBottomSheetFragment bottomSheetFragment = MyBottomSheetFragment.newInstance(postCreating);

            bottomSheetFragment.setDataPass(dataPass);

            bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
        }
    }
}
