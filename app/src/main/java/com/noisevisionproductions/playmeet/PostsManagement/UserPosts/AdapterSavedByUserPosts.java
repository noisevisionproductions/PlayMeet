package com.noisevisionproductions.playmeet.PostsManagement.UserPosts;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostCreatingCopy;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.ChatButtonHandler;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.UserManagement.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSavedByUserPosts extends RecyclerView.Adapter<AdapterSavedByUserPosts.MyViewHolder> {
    private final List<PostCreatingCopy> listOfPostCreatingCopy;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final AppCompatTextView noPostInfo;

    public AdapterSavedByUserPosts(Context context, FragmentManager fragmentManager, List<PostCreatingCopy> listOfPostCreatingCopy, AppCompatTextView noPostInfo) {
        this.listOfPostCreatingCopy = listOfPostCreatingCopy;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.noPostInfo = noPostInfo;
    }

    @NonNull
    @Override
    public AdapterSavedByUserPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_user_activity, parent, false);
        return new AdapterSavedByUserPosts.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSavedByUserPosts.MyViewHolder holder, int position) {
        PostCreatingCopy posts = listOfPostCreatingCopy.get(position);
        String userId = posts.getUserId();
        holder.sportNames.setText(posts.getSportType());
        holder.cityNames.setText(posts.getCityName());

        holder.layoutOfPost.setOnClickListener(v -> ChatButtonHandler.handleMoreInfoButton(fragmentManager, posts, context));
        deletePostButton(holder, position);
        if (userId != null){
            setUserAvatar(holder, userId, context);

        }
    }

    @Override
    public int getItemCount() {
        return listOfPostCreatingCopy.size();
    }

    public void deletePostButton(AdapterSavedByUserPosts.MyViewHolder holder, int position) {
        PostCreatingCopy postCreatingCopy = listOfPostCreatingCopy.get(position);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();
        String postId = postCreatingCopy.getPostId();
        DatabaseReference savedPostCreating = FirebaseDatabase.getInstance().getReference("SavedPostCreating").child(currentUserId).child(postId);

        // zmieniam napis na przycisku na "Usuń post"
        holder.deletePost.setText(R.string.signOutFromThePost);
        // usuwam post z listy oraz z bazy danych
        holder.deletePost.setOnClickListener(v -> savedPostCreating.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference postReference = FirebaseDatabase.getInstance().getReference()
                        .child("PostCreating")
                        .child(postId);
                postReference.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        PostCreating postCreating = currentData.getValue(PostCreating.class);
                        if (postCreating != null && postCreating.getPeopleSignedUp() > 0) {
                            postCreating.deleteSignedUpUser(currentUserId);
                            postCreating.setActivityFull(false);
                            currentData.setValue(postCreating);

                            decrementCurrentUserJoinedPostsCount(currentUserId);
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        if (error != null) {
                            Log.e("Firebase Update Error", "Removing signed up user when saved post is removed " + error.getMessage());
                        } else {
                            listOfPostCreatingCopy.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, listOfPostCreatingCopy.size());
                            if (listOfPostCreatingCopy.isEmpty()) {
                                // jeżeli zostaną usunięte wszystkie posty z listy,
                                // to dzięki przesłaniu noPostInfo w konstruktorze,
                                // wyświetlam informację o braku stworzonych postów
                                // postanowiłem do tego stworzyć Handler, aby napis
                                // pojawiał się z lekkim opóźnieniem, bo bez tego layout dziwnie się zachowuje
                                new Handler().postDelayed(() -> noPostInfo.setVisibility(View.VISIBLE), 100);
                            }
                        }
                    }
                });
            } else {
                Log.e("PostsAdapterSavedByUser", "Błąd podczas usuwania z bazy danych " + task.getException());
            }
        }));
    }

    private void decrementCurrentUserJoinedPostsCount(String currentUserId) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("UserModel").child(currentUserId);
        userReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                UserModel userModel = currentData.getValue(UserModel.class);
                if (userModel != null) {
                    userModel.decrementJoinedPostsCount();
                    currentData.setValue(userModel);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e("Firebase Update Error", "decrementing joined posts count in current user " + error.getMessage());
                }
            }
        });
    }

    private void setUserAvatar(MyViewHolder holder, String userId, Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final CardView layoutOfPost;
        private final AppCompatTextView sportNames, cityNames;
        private final AppCompatButton deletePost;

        public MyViewHolder(View v) {
            super(v);
            layoutOfPost = v.findViewById(R.id.layoutOfPost);
            userAvatar = v.findViewById(R.id.userAvatar);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            deletePost = v.findViewById(R.id.deletePost);
        }
    }
}