package com.example.zagrajmy.DataManagement;

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.UserManagement.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.realm.Realm;

public class RealmDatabaseManagement {
    private final Realm realm = Realm.getDefaultInstance();

    public RealmDatabaseManagement() {

    }

    //Usuwa wszystko z bazy danych realm - do testów
    public void deleteAllRealmDataUseForTestingOnly() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public void closeRealmDatabase() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }

    public boolean checkIfIdExists(int id) {
        return realm.where(PostCreating.class).equalTo("uniqueId", id).findFirst() != null;
    }

    public void addPost(PostCreating postCreating) {
        realm.beginTransaction();
        realm.copyToRealm(postCreating);
        realm.commitTransaction();
    }

    public void updatePost(PostCreating postCreating) {

    }

    public void deletePost(PostCreating postCreating) {

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
