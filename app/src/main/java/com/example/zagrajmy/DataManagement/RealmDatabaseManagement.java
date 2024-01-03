package com.example.zagrajmy.DataManagement;

import com.example.zagrajmy.Chat.ChatMessageModel;
import com.example.zagrajmy.Chat.PrivateChatModel;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostCreatingCopy;
import com.example.zagrajmy.Realm.RealmAppConfig;
import com.example.zagrajmy.UserManagement.UserModel;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.User;

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
        try (Realm realm = Realm.getDefaultInstance()) {
            return realm.where(com.example.zagrajmy.PostCreating.class).equalTo("postId", id).findFirst() != null;

        }
    }

    public void findPostCreatedByUser() {
        realm.where(PostCreating.class)
                .equalTo("isCreatedByUser", true)
                .findFirst();
        // realm.close();
    }

    public void addPostToDatabase(PostCreating postCreating) {
        try (Realm realm = Realm.getDefaultInstance()) {

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(postCreating);
            realm.commitTransaction();
        }
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

    public void addUser(UserModel userModel) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            realm.insertOrUpdate(userModel);
            realm.commitTransaction();
        }
    }

    public void updateUser(UserModel userModel) {

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
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(r -> {
                PostCreating post = r.where(PostCreating.class)
                        .equalTo("postId", postId)
                        .findFirst();
                if (post != null) {
                    post.deleteFromRealm();
                }
            });
        }
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
            App realmApp = RealmAppConfig.getApp();
            User user = realmApp.currentUser();
            if (user != null) {
                // TODO znowu nickname
                /*String nicknameText = user.getDisplayName();
                UserModel userModelClass1 = realm1.where(UserModel.class).equalTo("nickName", nicknameText).findFirst();
                if (userModelClass1 == null) {
                    realm1.createObject(UserModel.class, nicknameText);
                }*/
            }
        });
    }


    public void deleteUser(UserModel userModel) {
    }

    public void findPostsByUser() {

    }

}
