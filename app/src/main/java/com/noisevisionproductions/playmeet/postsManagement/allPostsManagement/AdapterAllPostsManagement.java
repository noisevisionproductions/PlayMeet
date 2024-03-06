package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.utilities.CoolDownManager;
import com.noisevisionproductions.playmeet.utilities.ReportPost;

public class AdapterAllPostsManagement {

    public static void setUserAvatar(@NonNull AdapterAllPosts.MyViewHolder holder, @NonNull String userId, @NonNull Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    public static void getSkillLevel(@NonNull PostCreating postCreating, @NonNull AdapterAllPosts.MyViewHolder holder) {
        String skillLevel = postCreating.getSkillLevel();
        int drawableId = switch (skillLevel) {
            case "Pierwszy raz" -> R.drawable.d1_10;
            case "Nowicjusz" -> R.drawable.d2_10;
            case "Początkujący" -> R.drawable.d3_10;
            case "Amator" -> R.drawable.d4_10;
            case "Średnio-zaawansowany" -> R.drawable.d5_10;
            case "Zaawansowany" -> R.drawable.d6_10;
            case "Doświadczony" -> R.drawable.d7_10;
            case "Weteran" -> R.drawable.d8_10;
            case "Ekspert" -> R.drawable.d9_10;
            case "Profesjonalista" -> R.drawable.d10_10;
            default -> 0;
        };
        holder.skillLevel.setImageResource(drawableId);
    }

    public static void getPeopleStatus(@NonNull String postId, @NonNull AdapterAllPosts.MyViewHolder holder) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getJoinedPeopleStatus(postId, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String peopleStatus = snapshot.getValue(String.class);
                    holder.numberOfPeople.setText(peopleStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase RealmTime Database error", "Showing number of people on posts in adapter " + error.getMessage());
            }
        });
    }

    public static void reportPost(@NonNull AdapterAllPosts.MyViewHolder holder, String postId, Context context) {
        CoolDownManager cooldownManager = new CoolDownManager(context);

        if (FirebaseAuthManager.isUserLoggedIn()) {
            holder.overflowIcon.setVisibility(View.VISIBLE);
            holder.overflowIcon.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(context, v);
                menu.inflate(R.menu.menu_item_dots);
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_overflow) {
                        if (cooldownManager.canSendReport()) {
                            ReportPost reportPost = new ReportPost(v.getContext());
                            reportPost.show(postId);
                            return true;
                        } else {
                            Snackbar.make(v, "Zbyt częste zgłaszanie", Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
                        }
                    }
                    return false;
                });
                menu.show();
            });
        }
    }

    public static void setPostAnimation(@NonNull AdapterAllPosts.MyViewHolder holder) {
        Animation postAnimation = AnimationUtils.loadAnimation(holder.layoutOfPost.getContext(), R.anim.post_loading_animation);
        holder.layoutOfPost.setOnHoverListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            }
            return false;
        });
        holder.layoutOfPost.setAnimation(postAnimation);
        holder.layoutOfPost.startAnimation(postAnimation);
    }
}
