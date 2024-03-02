package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterAllPosts extends RecyclerView.Adapter<AdapterAllPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final FragmentManager fragmentManager;
    private final Context context;

    public AdapterAllPosts(List<PostCreating> listOfPostCreating, FragmentManager fragmentManager, Context context) {
        this.listOfPostCreating = listOfPostCreating;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        setPostAnimation(holder);
        PostCreating postCreating = listOfPostCreating.get(position);
        String userId = postCreating.getUserId();
        setUserAvatar(holder, userId, context);

        getSkillLevel(postCreating, holder);
        reportPost(holder, postCreating.getPostId());

        getPeopleStatus(postCreating.getPostId(), holder);

        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.addInfo.setText(postCreating.getAdditionalInfo());

        // po kliknieciu w post, otwiera wiecej informacji o nim
        holder.layoutOfPost.setOnClickListener(v -> ButtonsPostsAdapters.handleMoreInfoButton(fragmentManager, postCreating, context));
    }

    @NonNull
    @Override
    public AdapterAllPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreating.size();
    }

    private void setUserAvatar(@NonNull MyViewHolder holder, @NonNull String userId, @NonNull Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    private void getSkillLevel(@NonNull PostCreating postCreating, @NonNull MyViewHolder holder) {
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

    private void getPeopleStatus(@NonNull String postId, @NonNull MyViewHolder holder) {
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

    private void reportPost(@NonNull MyViewHolder holder, String postId) {
        CoolDownManager cooldownManager = new CoolDownManager(context);

        if (FirebaseAuthManager.isUserLoggedInUsingGoogle() || FirebaseAuthManager.isUserLoggedIn()) {
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
                            Snackbar.make(v, "Zbyt częste zgłaszanie", Snackbar.LENGTH_SHORT)
                                    .setTextColor(Color.RED).show();
                        }
                    }
                    return false;
                });
                menu.show();
            });
        }
    }

    private void setPostAnimation(@NonNull MyViewHolder holder) {
        Animation postAnimation = AnimationUtils.loadAnimation(holder.layoutOfPost.getContext(), R.anim.post_loading_animation);
        holder.layoutOfPost.setOnHoverListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                view.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(300)
                        .start();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                view.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(300)
                        .start();
            }
            return false;
        });
        holder.layoutOfPost.setAnimation(postAnimation);
        holder.layoutOfPost.startAnimation(postAnimation);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final AppCompatTextView sportNames, cityNames, addInfo, numberOfPeople;
        private final AppCompatImageView skillLevel, overflowIcon;
        private final CardView layoutOfPost;

        public MyViewHolder(@NonNull View v) {
            super(v);
            userAvatar = v.findViewById(R.id.userAvatar);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            skillLevel = v.findViewById(R.id.skillLevel);
            addInfo = v.findViewById(R.id.addInfoPost);
            numberOfPeople = v.findViewById(R.id.numberOfPeople);
            layoutOfPost = v.findViewById(R.id.layoutOfPost);
            overflowIcon = v.findViewById(R.id.overflowIcon);
        }
    }
}
