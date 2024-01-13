package com.noisevisionproductions.playmeet.PostsManagement.UserPosts;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;

import java.util.List;

public class PostsAdapterCreatedByUser extends RecyclerView.Adapter<PostsAdapterCreatedByUser.MyViewHolder> {
    private final List<PostCreating> listOfPostCreating;
    private final Context context;
    private final AppCompatTextView noPostInfo;

    public PostsAdapterCreatedByUser(Context context, List<PostCreating> listOfPostCreating, AppCompatTextView noPostInfo) {
        this.listOfPostCreating = listOfPostCreating;
        this.context = context;
        this.noPostInfo = noPostInfo;
    }

    @NonNull
    @Override
    public PostsAdapterCreatedByUser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_user_activity, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapterCreatedByUser.MyViewHolder holder, int position) {
        PostCreating posts = listOfPostCreating.get(position);

        holder.uniquePostId.setText(String.valueOf(posts.getPostId()));
        holder.sportNames.setText(posts.getSportType());
        holder.cityNames.setText(posts.getCityName());
        holder.skillLevel.setText(posts.getSkillLevel());
        holder.addInfo.setText(posts.getAdditionalInfo());
        holder.chosenDate.setText(posts.getDateTime());
        holder.chosenHour.setText(posts.getHourTime());

        extraInfo(holder);
        deletePostButton(holder, position);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreating.size();
    }

    public void extraInfo(PostsAdapterCreatedByUser.MyViewHolder holder) {
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

    public void deletePostButton(PostsAdapterCreatedByUser.MyViewHolder holder, int position) {
        PostCreating postCreating = listOfPostCreating.get(position);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        String currentUserId = firebaseHelper.getCurrentUser().getUid();
        String postId = postCreating.getPostId();
        DatabaseReference postReference = FirebaseDatabase.getInstance().getReference("PostCreating").child(postId);

        if (postCreating.getUserId().equals(currentUserId)) {
            // jeżeli post jest stworzony przez zalogowanego użytkownika,
            // to zmieniam napis na przycisku na "Usuń post"
            holder.deletePost.setText(R.string.deletePost);
            holder.chatButton.setVisibility(View.GONE);
            // usuwam post z listy oraz z bazy danych
            holder.deletePost.setOnClickListener(v -> postReference.removeValue().addOnCompleteListener(task -> {
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
                        new Handler().postDelayed(() -> noPostInfo.setVisibility(View.VISIBLE), 100);
                    }
                } else {
                    Log.e("PostsAdapterCreatedByUser", "Błąd podczas usuwania z bazy danych", task.getException());
                }
            }));
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextInputEditText uniquePostId, sportNames, cityNames, skillLevel, addInfo, chosenDate, chosenHour;
        private final CardView cardView;
        private final ConstraintLayout arrowDownOpenMenu;
        private final LinearLayoutCompat extraInfoContainer;
        private final AppCompatButton arrowDownOpenMenuButton, deletePost, chatButton;

        public MyViewHolder(View v) {
            super(v);
            uniquePostId = v.findViewById(R.id.uniquePostId);
            uniquePostId.setFocusable(false);

            sportNames = v.findViewById(R.id.sportNames);
            sportNames.setFocusable(false);

            cityNames = v.findViewById(R.id.chosenCity);
            cityNames.setFocusable(false);

            skillLevel = v.findViewById(R.id.skilLevel);
            skillLevel.setFocusable(false);

            addInfo = v.findViewById(R.id.addInfoPost);
            addInfo.setFocusable(false);

            chosenDate = v.findViewById(R.id.chosenDate);
            chosenDate.setFocusable(false);

            chosenHour = v.findViewById(R.id.chosenHour);
            chosenHour.setFocusable(false);

            arrowDownOpenMenu = v.findViewById(R.id.arrowDownOpenMenu);

            extraInfoContainer = v.findViewById(R.id.extraInfoContainer);

            cardView = v.findViewById(R.id.layoutOfPost);

            arrowDownOpenMenuButton = v.findViewById(R.id.arrowDownOpenMenuButton);

            deletePost = v.findViewById(R.id.deletePost);

            chatButton = v.findViewById(R.id.chatButton);
        }
    }
}