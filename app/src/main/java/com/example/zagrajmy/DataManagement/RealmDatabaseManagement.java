package com.example.zagrajmy.DataManagement;

import com.example.zagrajmy.PostCreating;

import io.realm.Realm;

public class RealmDatabaseManagement {
    private final Realm realm = Realm.getDefaultInstance();

    public RealmDatabaseManagement() {

    }

    public void cleanDatabase() {
         // Pobierz instancję Realm
        realm.beginTransaction(); // Rozpocznij transakcję
        realm.deleteAll(); // Usuń wszystkie obiekty z Realm
        realm.commitTransaction(); // Zatwierdź transakcję

    }

    public boolean checkIfIdExists(int id){
        return realm.where(PostCreating.class).equalTo("uniqueId", id).findFirst() != null;
    }


    public void addPost(PostCreating postCreating){
        realm.beginTransaction();
        realm.copyToRealm(postCreating);
        realm.commitTransaction();
    }

    public void updatePost(PostCreating postCreating){

    }

    public void deletePost(PostCreating postCreating){

    }

    public void findPostsByUser(){

    }

}
