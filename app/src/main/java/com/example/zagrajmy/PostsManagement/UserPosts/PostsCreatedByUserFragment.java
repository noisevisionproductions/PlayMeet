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
import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.PostCreating;
import com.example.zagrajmy.Adapters.PostDesignAdapterForUserActivity;
import com.example.zagrajmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PostsCreatedByUserFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View currentView = inflater.inflate(R.layout.posts_created_by_user_fragment, container, false);

        showUserPosts(currentView);

        getAddPostButton();
        return currentView;
    }

    public void showUserPosts(View view) {
        AppCompatTextView noPosts = view.findViewById(R.id.noPostInfo);
        RecyclerView expandableListOfYourPosts = view.findViewById(R.id.expandableListOfUserPosts);
        FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();

        if (AuthenticationManager.isUserLoggedIn() && userFirebase != null) {
            List<PostCreating> newUserPosts = new ArrayList<>();

            try (Realm realm = Realm.getDefaultInstance()) {
                RealmResults<PostCreating> userPostsFromRealm = realm.where(PostCreating.class).equalTo("userId", userFirebase.getUid()).findAll();

                if (userPostsFromRealm != null) {
                    for (PostCreating listOfCreatedByUser : userPostsFromRealm.where().equalTo("isCreatedByUser", true).findAll()) {
                        newUserPosts.add(realm.copyFromRealm(listOfCreatedByUser));
                    }
                }
            }
            if (newUserPosts.isEmpty()) {
                noPosts.setVisibility(View.VISIBLE);
                expandableListOfYourPosts.setVisibility(View.GONE);
            } else {
                expandableListOfYourPosts.setVisibility(View.VISIBLE);
                noPosts.setVisibility(View.GONE);
                PostDesignAdapterForUserActivity postDesignAdapterForUserActivity = new PostDesignAdapterForUserActivity(getContext(), newUserPosts);
                expandableListOfYourPosts.setAdapter(postDesignAdapterForUserActivity);
                expandableListOfYourPosts.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getParentFragmentManager().beginTransaction().add(R.id.layoutOfCreatedPosts, myFragment).commit();
    }
}
