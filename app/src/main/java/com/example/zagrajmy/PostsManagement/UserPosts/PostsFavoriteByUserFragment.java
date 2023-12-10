package com.example.zagrajmy.PostsManagement.UserPosts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zagrajmy.Design.ButtonAddPostFragment;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.Adapters.PostDesignAdapterForUserActivity;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsFavoriteByUserFragment extends Fragment {

    private Realm realm;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        View currentView = inflater.inflate(R.layout.posts_added_as_favorite_fragment, container, false);

        showSavedPosts(currentView);

        getAddPostButton();
        return currentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void showSavedPosts(View view){
        AppCompatTextView noPostInfo = view.findViewById(R.id.noPostInfo);
        RecyclerView rexpandableListOfSavedPosts = view.findViewById(R.id.expandableListOfSavedPosts);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<PostCreating> newSavedPosts = new ArrayList<>();

        assert user != null;
        RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class)
                .equalTo("userId", user.getUid())
                .equalTo("isPostSavedByUser", true)
                .findAll();
        if (userPostsFromRealm != null) {
            newSavedPosts.addAll(userPostsFromRealm);
        }


        if (newSavedPosts.isEmpty()) {
            noPostInfo.setVisibility(View.VISIBLE);
            rexpandableListOfSavedPosts.setVisibility(View.GONE);
        } else {
            rexpandableListOfSavedPosts.setVisibility(View.VISIBLE);
            noPostInfo.setVisibility(View.GONE);
            PostDesignAdapterForUserActivity postDesignAdapterForUserActivity = new PostDesignAdapterForUserActivity(getContext(), newSavedPosts);
            rexpandableListOfSavedPosts.setAdapter(postDesignAdapterForUserActivity);
            rexpandableListOfSavedPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }
    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getParentFragmentManager().beginTransaction().add(R.id.layoutOfAddedPost, myFragment).commit();
    }
}
