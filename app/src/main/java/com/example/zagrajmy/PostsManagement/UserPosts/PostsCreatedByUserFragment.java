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

import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.PostsManagement.PageWithPosts.PostDesignAdapter;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsCreatedByUserFragment extends Fragment {
    private Realm realm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();

        View currentView = inflater.inflate(R.layout.posts_created_by_user_fragment, container, false);

        showUserPosts(currentView);

        return currentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void showUserPosts(View view) {
        AppCompatTextView noPosts = view.findViewById(R.id.noPostInfo);
        RecyclerView expandableListOfYourPosts = view.findViewById(R.id.expandableListOfUserPosts);
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();


        assert userFirebase != null;

        List<PostCreating> newUserPosts = new ArrayList<>();

       // RealmResults<PostCreating> userPostsFromRealm = realm.copyFromRealm("postsCreatedByUser");

        RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class).equalTo("userId", userFirebase.getUid()).findAll();

        if (userPostsFromRealm != null) {
            for (PostCreating listOfCreatedByUser : userPostsFromRealm.where().equalTo("isCreatedByUser", true).findAll()){
                newUserPosts.add(realm.copyFromRealm(listOfCreatedByUser));
            }
        }

        if (newUserPosts.isEmpty()) {
            noPosts.setVisibility(View.VISIBLE);
            expandableListOfYourPosts.setVisibility(View.GONE);
        } else {
            expandableListOfYourPosts.setVisibility(View.VISIBLE);
            noPosts.setVisibility(View.GONE);
            PostDesignAdapter postDesignAdapter = new PostDesignAdapter(getContext(), newUserPosts,null);
            expandableListOfYourPosts.setAdapter(postDesignAdapter);
            expandableListOfYourPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }
}

