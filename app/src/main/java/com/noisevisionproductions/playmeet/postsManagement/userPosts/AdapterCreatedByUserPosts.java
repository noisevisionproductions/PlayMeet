package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.ButtonsForChatAndSignIn;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCreatedByUserPosts extends RecyclerView.Adapter<AdapterCreatedByUserPosts.MyViewHolder> {
    private final List<PostCreating> listOfPostCreating;
    private final FragmentManager fragmentManager;
    private final Context context;
    private final AppCompatTextView noPostInfo;

    public AdapterCreatedByUserPosts(Context context, FragmentManager fragmentManager, List<PostCreating> listOfPostCreating, AppCompatTextView noPostInfo) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.listOfPostCreating = listOfPostCreating;
        this.noPostInfo = noPostInfo;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCreatedByUserPosts.MyViewHolder holder, int position) {
        setPostAnimation(holder);
        PostCreating postCreating = listOfPostCreating.get(position);
        String userId = postCreating.getUserId();
        setUserAvatar(holder, userId, context);

        getSkillLevel(postCreating, holder);
        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.addInfo.setText(postCreating.getAdditionalInfo());

        removePost(holder, position);

        String peopleStatus = postCreating.getPeopleStatus();
        holder.numberOfPeople.setText(peopleStatus);

        // po kliknieciu w post, otwiera wiecej informacji o nim
        holder.layoutOfPost.setOnClickListener(v -> ButtonsForChatAndSignIn.handleMoreInfoButton(fragmentManager, postCreating, context));
    }

    @NonNull
    @Override
    public AdapterCreatedByUserPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        makePostSmaller(v, parent);
        return new AdapterCreatedByUserPosts.MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreating.size();
    }

    private void setUserAvatar(@NonNull AdapterCreatedByUserPosts.MyViewHolder holder, @NonNull String userId, @NonNull Context context) {
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

    private void setPostAnimation(@NonNull MyViewHolder holder) {
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

    private void makePostSmaller(View view, ViewGroup parent) {
        if (listOfPostCreating.size() > 1) {
            DisplayMetrics displayMetrics = parent.getContext().getResources().getDisplayMetrics();
            int width = (int) (displayMetrics.widthPixels * 0.9);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
            layoutParams.width = width;
            view.setLayoutParams(layoutParams);
        }
    }

    private void removePost(MyViewHolder holder, int position) {
        PostCreating postCreating = listOfPostCreating.get(position);
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();
            String postId = postCreating.getPostId();
            DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("PostCreating").child(postId);

            holder.deleteIcon.setOnClickListener(v -> {
                if (postCreating.getUserId().equals(currentUserId)) {
                    new AlertDialog.Builder(v.getContext()).setMessage("Czy na pewno chcesz usunąć ten post?").setPositiveButton("Tak", ((dialog, which) -> postReference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            ToastManager.showToast(v.getContext(), "Post usunięty");
                            listOfPostCreating.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, listOfPostCreating.size());
                            if (listOfPostCreating.isEmpty()) {
                                // jeżeli zostaną usunięte wszystkie posty z listy,
                                // to dzięki przesłaniu noPostInfo w konstruktorze,
                                // wyświetlam informację o braku stworzonych postów
                                // postanowiłem do tego stworzyć Handler, aby napis
                                // pojawiał się z lekkim opóźnieniem, bo bez tego layout dziwnie się zachowuje
                                new Handler().postDelayed(() -> noPostInfo.setVisibility(View.VISIBLE), 100);
                            }
                        } else {
                            Log.e("PostsAdapterCreatedByUser", "Error when deleting created post by user " + R.string.error, task.getException());
                        }
                    }))).setNegativeButton("Nie", null).show();
                }
            });
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final AppCompatTextView sportNames, cityNames, addInfo, numberOfPeople;
        private final AppCompatImageView skillLevel, deleteIcon;
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
            deleteIcon = v.findViewById(R.id.deleteIcon);
            deleteIcon.setVisibility(View.VISIBLE);
            AppCompatImageView overflowIcon = v.findViewById(R.id.overflowIcon);
            overflowIcon.setVisibility(View.GONE);
        }
    }
}