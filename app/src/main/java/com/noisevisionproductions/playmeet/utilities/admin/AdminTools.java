package com.noisevisionproductions.playmeet.utilities.admin;

import android.content.Context;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.PostInfo;
import com.noisevisionproductions.playmeet.utilities.layoutManagers.ToastManager;

public class AdminTools {
    private PostInfo postInfo;
    private Context context;

    public AdminTools() {

    }

    public AdminTools(PostInfo postInfo, Context context) {
        this.postInfo = postInfo;
        this.context = context;
    }

    public void deletePostAsAdmin(View view) {
        AppCompatImageView deletePostIconAsAdmin = view.findViewById(R.id.deletePostIconAsAdmin);
        AdminManager adminManager = new AdminManager();
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        if (firebaseHelper.getCurrentUser() != null) {
            adminManager.checkAdmin(firebaseHelper.getCurrentUser().getUid(), isAdmin -> {
                if (isAdmin) {
                    deletePostIconAsAdmin.setVisibility(View.VISIBLE);
                    deletePostIconAsAdmin.setOnClickListener(v -> deletePostCallback());
                } else {
                    deletePostIconAsAdmin.setVisibility(View.GONE);
                }
            });
        }
    }

    private void deletePostCallback() {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        firestorePostRepository.deleteUserPost(postInfo.getPostId(), new OnCompletionListener() {
            @Override
            public void onSuccess() {
                ToastManager.showToast(context, context.getString(R.string.postDeleted));
            }

            @Override
            public void onFailure(Exception e) {
                ToastManager.showToast(context, context.getString(R.string.errorWhileDeletingPost) + e.getMessage());
            }
        });
    }

}
