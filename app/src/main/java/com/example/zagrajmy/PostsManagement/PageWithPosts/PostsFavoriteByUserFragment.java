package com.example.zagrajmy.PostsManagement.PageWithPosts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.zagrajmy.R;

import io.realm.Realm;

public class PostsFavoriteByUserFragment extends Fragment {

    private Realm realm;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        View currentView = inflater.inflate(R.layout.posts_added_as_favorite_fragment, container, false);

        //buttonForUserPosts(currentView);

        return currentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }


}
