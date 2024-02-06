package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.dataManagement.PostDiffCallback;
import com.noisevisionproductions.playmeet.design.ButtonAddPostFragment;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;

import java.util.ArrayList;
import java.util.List;

public class PostsCreatedByUserFragment extends Fragment {
    private final List<PostCreating> postsCreatedByUser = new ArrayList<>();
    private ProgressBar progressBar;
    private AdapterCreatedByUserPosts adapterCreatedByUserPosts;
    private RecyclerView expandableListOfYourPosts;
    private AppCompatTextView noPostsInfo, howUserPostLooksLike;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_created_by_user, container, false);

        setupView(view);
        showUserPosts();
        getAddPostButton();

        return view;
    }

    public void setupView(@NonNull View view) {
        noPostsInfo = view.findViewById(R.id.noPostInfo);
        howUserPostLooksLike = view.findViewById(R.id.howUserPostLooksLike);
        progressBar = view.findViewById(R.id.progressBarLayout);

        expandableListOfYourPosts = view.findViewById(R.id.expandableListOfUserPosts);
        adapterCreatedByUserPosts = new AdapterCreatedByUserPosts(getContext(), getChildFragmentManager(), postsCreatedByUser, expandableListOfYourPosts, howUserPostLooksLike, noPostsInfo);
        expandableListOfYourPosts.setAdapter(adapterCreatedByUserPosts);
        expandableListOfYourPosts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void showUserPosts() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String currentUserId = firebaseUser.getUid();
            if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
                // uzyskuje referencję do danych PostCreating stworzonego w Firebase
                DatabaseReference userPostsReference = FirebaseDatabase.getInstance().getReference("PostCreating");
                // nastepnie pobieram posty z bazy, które mają takie samo userId, co aktualnie zalogowany użytkownik
                userPostsReference.orderByChild("userId").equalTo(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<PostCreating> newUserPosts = new ArrayList<>();
                        // wszystkie posty przypisane do użytkownika dodaję do listy newUserPosts, aby później je wyświetlić zaktualizowane za pomocą  DiffUtil
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            PostCreating postCreating = postSnapshot.getValue(PostCreating.class);
                            if (postCreating != null) {
                                newUserPosts.add(postCreating);
                            }
                        }
                        if (newUserPosts.isEmpty()) {
                            // jeżeli nie ma żadnych postów, to wyświetlam informację na temat pustej listy
                            expandableListOfYourPosts.setVisibility(View.GONE);
                            noPostsInfo.setVisibility(View.VISIBLE);
                            howUserPostLooksLike.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            howUserPostLooksLike.setVisibility(View.VISIBLE);
                            expandableListOfYourPosts.setVisibility(View.VISIBLE);
                            noPostsInfo.setVisibility(View.GONE);
                            updatePostsUsingDiffUtil(newUserPosts);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase RealmTime Database error", "Printing posts that User created " + error.getMessage());
                    }
                });
            }
        } else {
            expandableListOfYourPosts.setVisibility(View.GONE);
            howUserPostLooksLike.setVisibility(View.GONE);
            noPostsInfo.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void updatePostsUsingDiffUtil(@NonNull List<PostCreating> newPosts) {
        final List<PostCreating> oldPosts = new ArrayList<>(this.postsCreatedByUser);
        new Thread(() -> {
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(oldPosts, newPosts));
            new Handler(Looper.getMainLooper()).post(() -> {
                postsCreatedByUser.clear();
                postsCreatedByUser.addAll(newPosts);
                diffResult.dispatchUpdatesTo(adapterCreatedByUserPosts);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    public void getAddPostButton() {
        ButtonAddPostFragment myFragment = new ButtonAddPostFragment();
        getParentFragmentManager().beginTransaction().add(R.id.layoutOfCreatedPosts, myFragment).commit();
    }
}
