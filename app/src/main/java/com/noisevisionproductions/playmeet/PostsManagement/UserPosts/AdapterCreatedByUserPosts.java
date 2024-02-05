package com.noisevisionproductions.playmeet.PostsManagement.UserPosts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement.ChatButtonHandler;
import com.noisevisionproductions.playmeet.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCreatedByUserPosts extends RecyclerView.Adapter<AdapterCreatedByUserPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final FragmentManager fragmentManager;
    private final Context context;
    private final AppCompatTextView noPostInfo, howUserPostLooksLike;

    public AdapterCreatedByUserPosts(Context context, FragmentManager fragmentManager, List<PostCreating> listOfPostCreating, RecyclerView recyclerView, AppCompatTextView howUserPostLooksLike, AppCompatTextView noPostInfo) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.listOfPostCreating = listOfPostCreating;
        this.howUserPostLooksLike = howUserPostLooksLike;
        this.noPostInfo = noPostInfo;

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(getCallBack());
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

        //getPeopleStatus(postCreating.getPostId(), holder);

        String peopleStatus = postCreating.getPeopleStatus();
        holder.numberOfPeople.setText(peopleStatus);

        // po kliknieciu w post, otwiera wiecej informacji o nim
        holder.layoutOfPost.setOnClickListener(v -> ChatButtonHandler.handleMoreInfoButton(fragmentManager, postCreating, context));
    }

    @NonNull
    @Override
    public AdapterCreatedByUserPosts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new AdapterCreatedByUserPosts.MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreating.size();
    }

    private void setUserAvatar(AdapterCreatedByUserPosts.MyViewHolder holder, String userId, Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    private ItemTouchHelper.SimpleCallback getCallBack() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView1, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                PostCreating postCreating = listOfPostCreating.get(position);
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                String currentUserId = firebaseHelper.getCurrentUser().getUid();
                String postId = postCreating.getPostId();
                DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("PostCreating").child(postId);

                if (postCreating.getUserId().equals(currentUserId)) {
                    // usuwam post użytkownika poprzez przesunięcie go w bok
                    postReference.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            listOfPostCreating.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, listOfPostCreating.size());
                            if (listOfPostCreating.isEmpty()) {
                                // jeżeli zostaną usunięte wszystkie posty z listy,
                                // to dzięki przesłaniu noPostInfo w konstruktorze,
                                // wyświetlam informację o braku stworzonych postów
                                // postanowiłem do tego stworzyć Handler, aby napis
                                // pojawiał się z lekkim opóźnieniem, bo bez tego layout dziwnie się zachowuje
                                new Handler().postDelayed(() -> {
                                    howUserPostLooksLike.setVisibility(View.GONE);
                                    noPostInfo.setVisibility(View.VISIBLE);
                                }, 100);
                            }
                        } else {
                            Log.e("PostsAdapterCreatedByUser", "Error when deleting created post by user " + R.string.error, task.getException());
                        }
                    });
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                Drawable deleteIcon = ContextCompat.getDrawable(context, R.drawable.trash_bin);
                ColorDrawable background = new ColorDrawable(Color.parseColor("#ac2614"));

                if (deleteIcon != null) {
                    int iconSize = (int) (itemView.getHeight() * 0.8);
                    int iconMargin = (itemView.getHeight() - iconSize) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + iconSize;
                    if (dX < 0) {
                        int iconLeft = itemView.getRight() - iconMargin - iconSize;
                        int iconRight = itemView.getRight() - iconMargin;
                        int iconTopBound = itemView.getTop() + iconMargin;
                        int iconBottomBound = itemView.getBottom() - iconMargin;
                        background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(canvas);

                        deleteIcon.setBounds(Math.max(iconLeft, itemView.getRight() + (int) dX), Math.max(iconTop, iconTopBound),
                                Math.max(iconRight, itemView.getRight()), Math.min(iconBottom, iconBottomBound));
                        deleteIcon.draw(canvas);
                    } else {
                        background.setBounds(0, 0, 0, 0);
                    }
                }
            }
        };
    }

    private void getSkillLevel(PostCreating postCreating, MyViewHolder holder) {
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

    private void setPostAnimation(MyViewHolder holder) {
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
        private final AppCompatImageView skillLevel;
        private final CardView layoutOfPost;

        public MyViewHolder(View v) {
            super(v);
            userAvatar = v.findViewById(R.id.userAvatar);
            sportNames = v.findViewById(R.id.sportNames);
            cityNames = v.findViewById(R.id.chosenCity);
            skillLevel = v.findViewById(R.id.skillLevel);
            addInfo = v.findViewById(R.id.addInfoPost);
            numberOfPeople = v.findViewById(R.id.numberOfPeople);
            layoutOfPost = v.findViewById(R.id.layoutOfPost);
            AppCompatImageView overflowIcon = v.findViewById(R.id.overflowIcon);
            overflowIcon.setVisibility(View.GONE);
        }
    }
}