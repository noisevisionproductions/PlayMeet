package com.example.zagrajmy.DataManagement;

import com.example.zagrajmy.PostCreating;
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

    public static RealmDatabaseManagement getInstance(){
        if (instance == null){
            instance = new RealmDatabaseManagement();
        }
        return instance;
    }

    //Usuwa wszystko z bazy danych realm - do testów
    public void deleteAllRealmDataUseForTestingOnly() {
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }
    //Usuwa wszystko z bazy danych realm - do testów

    public void realmMigrationResetDatabaseOnlyForTesting(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
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

    public void addUser(User user){
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public void isUserAvailable(){

        //realm.copyToRealmOrUpdate();
    }

    public User getUserId(String userId){
        realm.beginTransaction();
        User user = realm.where(User.class).equalTo("userId", userId).findFirst();
        realm.commitTransaction();
        return user;
    }

    public void getPosts(PostCreating postCreating){
       // Realm realm = Realm.getDefaultInstance();

        RealmResults<PostCreating> allPosts = realm.where(PostCreating.class).findAll();
        if (allPosts != null) {
            List<PostCreating> posts = new ArrayList<>(realm.copyFromRealm(allPosts));
        }
        realm.close();
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
