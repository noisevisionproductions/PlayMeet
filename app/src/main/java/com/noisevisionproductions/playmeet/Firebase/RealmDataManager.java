package com.noisevisionproductions.playmeet.Firebase;

import com.noisevisionproductions.playmeet.Chat.ChatMessageModel;
import com.noisevisionproductions.playmeet.Chat.PrivateChatModel;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmDataManager {
    private static RealmDataManager instance;
    private final Realm realm;

    public RealmDataManager() {
        realm = Realm.getDefaultInstance();
    }

    public static RealmDataManager getInstance() {
        if (instance == null) {
            instance = new RealmDataManager();
        }
        return instance;
    }


    public boolean checkIfIdExists(int id) {
        try (Realm realm = Realm.getDefaultInstance()) {
            return realm.where(PostCreating.class).equalTo("postId", id).findFirst() != null;
        }
    }

    public void addPostToDatabase(PostCreating postCreating) {
        try (Realm realm = Realm.getDefaultInstance()) {

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(postCreating);
            realm.commitTransaction();
        }
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


}
