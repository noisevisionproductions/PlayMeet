package com.noisevisionproductions.playmeet.PostsManagement.UserPosts;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.noisevisionproductions.playmeet.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterSavedByUserPosts extends RecyclerView.Adapter<AdapterSavedByUserPosts.MyViewHolder> {
    private final List<PostCreatingCopy> listOfPostCreatingCopy;
    private final Context context;
    private final AppCompatTextView noPostInfo;

    public AdapterSavedByUserPosts(Context context, List<PostCreatingCopy> listOfPostCreatingCopy, AppCompatTextView noPostInfo) {
        this.listOfPostCreatingCopy = listOfPostCreatingCopy;
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
        String userId = posts.getUserIdCreator();

        // holder.uniquePostIdForButtonDesign.setText(String.valueOf(posts.getPostId()));
        holder.sportNames.setText(posts.getSportType());
        holder.cityNames.setText(posts.getCityName());
        holder.skillLevel.setText(posts.getSkillLevel());
        // holder.addInfoPostForButtonDesign.setText(posts.getAdditionalInfo());
        holder.chosenDate.setText(posts.getDateTime());
        //holder.chosenHourForLayoutDesign.setText(posts.getHourTime());

        extraInfo(holder);
        deletePostButton(holder, position);
        setUserAvatar(holder, userId, context);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreatingCopy.size();
    }

    public void extraInfo(AdapterSavedByUserPosts.MyViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
        holder.arrowDownOpenMenu.setOnClickListener(v -> {
            if (holder.extraInfoContainer.getVisibility() == View.GONE) {
                holder.extraInfoContainer.setVisibility(View.VISIBLE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
            } else {
                holder.extraInfoContainer.setVisibility(View.GONE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
        });
        holder.arrowDownOpenMenuButton.setOnClickListener(v -> {
            if (holder.extraInfoContainer.getVisibility() == View.GONE) {
                holder.extraInfoContainer.setVisibility(View.VISIBLE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
            } else {
                holder.extraInfoContainer.setVisibility(View.GONE);
                layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, context.getResources().getDisplayMetrics());
                holder.cardView.requestLayout();
                holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
            }
        });
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
                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        if (error != null) {
                            Log.e("Firebase Update Error", "Removing signed up user when saved post is removed " + error.getMessage());
                        }
                    }
                });
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
                } else {
                    Log.e("PostsAdapterSavedByUser", "Błąd podczas usuwania z bazy danych " + task.getException());
                }
            }
        }));

    }

    private void setUserAvatar(MyViewHolder holder, String userId, Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final AppCompatTextView uniquePostIdForButtonDesign, sportNames, cityNames, skillLevel, addInfoPostForButtonDesign, chosenDate, chosenHourForLayoutDesign;
        private final CardView cardView;
        private final ConstraintLayout arrowDownOpenMenu;
        private final LinearLayoutCompat extraInfoContainer;
        private final AppCompatButton arrowDownOpenMenuButton, deletePost;

        public MyViewHolder(View v) {
            super(v);
            userAvatar = v.findViewById(R.id.userAvatar);
            uniquePostIdForButtonDesign = v.findViewById(R.id.uniquePostIdForButtonDesign);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            skillLevel = v.findViewById(R.id.skilLevel);
            addInfoPostForButtonDesign = v.findViewById(R.id.addInfoPost);
            chosenDate = v.findViewById(R.id.chosenDate);
            chosenHourForLayoutDesign = v.findViewById(R.id.chosenHourForLayoutDesign);
            arrowDownOpenMenu = v.findViewById(R.id.arrowDownOpenMenu);

            extraInfoContainer = v.findViewById(R.id.extraInfoContainer);

            cardView = v.findViewById(R.id.layoutOfPost);

            arrowDownOpenMenuButton = v.findViewById(R.id.arrowDownOpenMenuButton);

            deletePost = v.findViewById(R.id.deletePost);
        }
    }
}