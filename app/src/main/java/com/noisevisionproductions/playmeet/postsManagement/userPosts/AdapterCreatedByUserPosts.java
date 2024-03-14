package com.noisevisionproductions.playmeet.postsManagement.userPosts;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
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

import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.firebase.FirestorePostRepository;
import com.noisevisionproductions.playmeet.firebase.interfaces.OnCompletionListener;
import com.noisevisionproductions.playmeet.firebase.interfaces.ViewHolderUpdater;
import com.noisevisionproductions.playmeet.postsManagement.allPostsManagement.bottomSheetFragment.ButtonsForChatAndSignIn;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCreatedByUserPosts extends RecyclerView.Adapter<AdapterCreatedByUserPosts.MyViewHolder> {
    private final List<PostModel> listOfPostModel;
    private final FragmentManager fragmentManager;
    private final Context context;
    private final AppCompatTextView noPostInfo;

    public AdapterCreatedByUserPosts(Context context, FragmentManager fragmentManager, List<PostModel> listOfPostModel, AppCompatTextView noPostInfo) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.listOfPostModel = listOfPostModel;
        this.noPostInfo = noPostInfo;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCreatedByUserPosts.MyViewHolder holder, int position) {
        holder.applyAnimation();
        PostModel postModel = listOfPostModel.get(position);

        String userId = postModel.getUserId();
        getSkillLevel(postModel, holder);

        holder.setUserAvatar(context, userId);
        holder.sportNames.setText(postModel.getSportType());
        holder.cityNames.setText(postModel.getCityName());
        holder.addInfo.setText(postModel.getAdditionalInfo());

        removeAndUpdatePost(holder, position, postModel);
        PostHelperSignedUpUser.getPeopleStatus(postModel.getPostId(), holder);

        // po kliknieciu w post, otwiera wiecej informacji o nim
        holder.layoutOfPost.setOnClickListener(v -> ButtonsForChatAndSignIn.handleMoreInfoButton(fragmentManager, postModel, context));
    }

    @NonNull
    @Override
    public AdapterCreatedByUserPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        PostHelperSignedUpUser.makePostSmaller(v, parent, listOfPostModel);
        return new AdapterCreatedByUserPosts.MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return listOfPostModel.size();
    }

    private void getSkillLevel(@NonNull PostModel postModel, @NonNull MyViewHolder holder) {
        String skillLevel = postModel.getSkillLevel();
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

    private void removeAndUpdatePost(MyViewHolder holder, int position, PostModel postModel) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();

        if (firebaseHelper.getCurrentUser() != null) {
            String currentUserId = firebaseHelper.getCurrentUser().getUid();
            String postId = postModel.getPostId();

            holder.deleteIcon.setOnClickListener(v -> {
                if (postModel.getUserId().equals(currentUserId)) {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage("Czy na pewno chcesz usunąć ten post?")
                            .setPositiveButton("Tak", (dialog, which) -> {
                                deletePostFromDB(postId);
                                listOfPostModel.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, listOfPostModel.size());
                                if (listOfPostModel.isEmpty()) {
                                    new Handler().postDelayed(() -> noPostInfo.setVisibility(View.VISIBLE), 100);
                                }
                            })
                            .setNegativeButton("Nie", null).show();
                }
            });
        }
    }

    private void deletePostFromDB(String postId) {
        FirestorePostRepository firestorePostRepository = new FirestorePostRepository();
        firestorePostRepository.deleteUserPost(postId, new OnCompletionListener() {
            @Override
            public void onSuccess() {
                ToastManager.showToast(context, "Post został usunięty");
                Log.d("Removing post", "Post removed");
            }

            @Override
            public void onFailure(Exception e) {
                ToastManager.showToast(context, "Błąd podczas usuwania postu " + e.getMessage());
                Log.e("Removing post", "Error removing post from DB" + e.getMessage());
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements ViewHolderUpdater {
        private final CircleImageView userAvatar;
        private final AppCompatTextView sportNames;
        private final AppCompatTextView cityNames;
        private final AppCompatTextView addInfo;
        protected final AppCompatTextView numberOfPeople;
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

        @Override
        public void updatePeopleStatus(String status) {
            this.numberOfPeople.setText(status);
        }

        @Override
        public void applyAnimation() {
            Animation postAnimation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.post_loading_animation);
            itemView.setOnHoverListener((view, motionEvent) -> {
                if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                    view.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                }
                return false;
            });
            itemView.setAnimation(postAnimation);
            itemView.startAnimation(postAnimation);
        }

        @Override
        public void setUserAvatar(Context context, String userId) {
            FirebaseHelper firebaseHelper = new FirebaseHelper();
            firebaseHelper.getUserAvatar(context, userId, this.userAvatar);
        }
    }
}