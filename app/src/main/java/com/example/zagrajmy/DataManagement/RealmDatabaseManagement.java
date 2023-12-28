package com.example.zagrajmy.DataManagement;

import com.example.zagrajmy.Chat.ChatMessageModel;
import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostCreatingCopy;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmDatabaseManagement {
    private static RealmDatabaseManagement instance;
    private final Realm realm;

    public RealmDatabaseManagement() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmDatabaseManagement getInstance() {
        if (instance == null) {
            instance = new RealmDatabaseManagement();
        }
        return instance;
    }

    public void deleteMessagesAndChatRooms() {
        realm.executeTransactionAsync(realm1 -> {
            RealmResults<PrivateChatModel> privateChatModels = realm1.where(PrivateChatModel.class)
                    .findAll();
            privateChatModels.deleteAllFromRealm();

            RealmResults<ChatMessageModel> chatMessageModels = realm1.where(ChatMessageModel.class)
                    .findAll();
            chatMessageModels.deleteAllFromRealm();
        });
        realm.close();
    }

    //Usuwa wszystko z bazy danych realm - do testów
    public void deleteAllRealmDataUseForTestingOnly() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
    //Usuwa wszystko z bazy danych realm - do testów

    public void realmMigrationResetDatabaseOnlyForTesting() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    public void closeRealmDatabase() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    public boolean checkIfIdExists(int id) {
        return realm.where(com.example.zagrajmy.PostCreating.class).equalTo("postId", id).findFirst() != null;
    }

    public void findPostCreatedByUser() {
        realm.where(PostCreating.class)
                .equalTo("isCreatedByUser", true)
                .findFirst();
        // realm.close();
    }

    public void addPostToDatabase(PostCreating postCreating) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(postCreating);
        realm.commitTransaction();
    }

    public void addCopyOfPostToDatabase(PostCreatingCopy postCreatingCopy) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(postCreatingCopy);
        realm.commitTransaction();
    }

    public void createMessageInDatabase(ChatMessageModel chatMessageModel) {
        realm.beginTransaction();
        realm.insertOrUpdate(chatMessageModel);
        realm.commitTransaction();
    }

    public void createChatroomInDatabase(PrivateChatModel privateChatModel) {
        realm.beginTransaction();
        realm.insertOrUpdate(privateChatModel);
        realm.commitTransaction();
    }

    public void addUser(User user) {
        realm.beginTransaction();
        realm.insertOrUpdate(user);
        realm.commitTransaction();
    }

    public void savePostToDatabaseAsSignedIn(PostCreating postCreating) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(postCreating);
        realm.commitTransaction();
    }

    public void isUserAvailable() {

        //realm.copyToRealmOrUpdate();
    }


    public void getPosts(PostCreating postCreating) {
        // Realm realm = Realm.getDefaultInstance();

        RealmResults<com.example.zagrajmy.PostCreating> allPosts = realm.where(com.example.zagrajmy.PostCreating.class).findAll();
        if (allPosts != null) {
            List<com.example.zagrajmy.PostCreating> posts = new ArrayList<>(realm.copyFromRealm(allPosts));
        }
        realm.close();
    }


    public void updatePost() {

    }

    public void deletePost(Integer postId) {
        realm.executeTransactionAsync(realm -> {
            PostCreating post = realm.where(PostCreating.class)
                    .equalTo("postId", postId)
                    .findFirst();
            if (post != null) {
                post.deleteFromRealm();
            }
        });
    }
    public void removeUserFromPost(String uuid) {
        realm.executeTransactionAsync(realm -> {
            PostCreatingCopy post = realm.where(PostCreatingCopy.class)
                    .equalTo("postUuid", uuid)
                    .findFirst();
            if (post != null) {
                post.deleteFromRealm();
            }
        });
    }
    public void createUser() {
        realm.executeTransactionAsync(realm1 -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String nicknameText = user.getDisplayName();
                User userClass1 = realm1.where(User.class).equalTo("nickName", nicknameText).findFirst();
                if (userClass1 == null) {
                    realm1.createObject(User.class, nicknameText);
                }
            }
        });
    }

    public void updateUser(User user) {
    }

    public void deleteUser(User user) {
    }

    public void findPostsByUser() {

    }

}
