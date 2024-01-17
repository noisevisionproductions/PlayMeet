package com.noisevisionproductions.playmeet.PostsManagement.AllPostsManagement;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.noisevisionproductions.playmeet.Firebase.FirebaseHelper;
import com.noisevisionproductions.playmeet.PostCreating;
import com.noisevisionproductions.playmeet.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapterAllPosts extends RecyclerView.Adapter<PostsAdapterAllPosts.MyViewHolder> {

    private final List<PostCreating> listOfPostCreating;
    private final FragmentManager fragmentManager;
    private final Context context;

    public PostsAdapterAllPosts(List<PostCreating> listOfPostCreating, FragmentManager fragmentManager, Context context) {
        this.listOfPostCreating = listOfPostCreating;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PostCreating postCreating = listOfPostCreating.get(position);
        String userId = postCreating.getUserId();
        setUserAvatar(holder, userId, context);

        holder.sportNames.setText(postCreating.getSportType());
        holder.cityNames.setText(postCreating.getCityName());
        holder.skillLevel.setText(postCreating.getSkillLevel());
        holder.addInfo.setText(postCreating.getAdditionalInfo());

        // oba obiekty z layoutu reagują na kliknięcie, bo 1 to strzałka, a 2 to pasek na długości postu, który jest odpowiedzialny za pozycję strzałki
        holder.arrowDownOpenMenuButton.setOnClickListener(v -> ButtonHelperAllPosts.handleMoreInfoButton(fragmentManager, postCreating, data -> {
        }, context));
        holder.arrowDownOpenMenu.setOnClickListener(v -> ButtonHelperAllPosts.handleMoreInfoButton(fragmentManager, postCreating, data -> {
        }, context));
    }

    private void setUserAvatar(MyViewHolder holder, String userId, Context context) {
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.getUserAvatar(context, userId, holder.userAvatar);
    }

    @NonNull
    @Override
    public PostsAdapterAllPosts.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_design_all_content, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return listOfPostCreating.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView userAvatar;
        private final TextInputEditText sportNames, cityNames, skillLevel, addInfo;
        protected final ConstraintLayout arrowDownOpenMenu;
        protected final LinearLayoutCompat extraInfoContainer;
        final CardView layoutOfPost;
        protected final AppCompatButton arrowDownOpenMenuButton, savePostButton, chatButton;

        public MyViewHolder(View v) {
            super(v);
            userAvatar = v.findViewById(R.id.userAvatar);
            userAvatar.setFocusable(false);

            sportNames = v.findViewById(R.id.sportNames);
            sportNames.setFocusable(false);

            cityNames = v.findViewById(R.id.chosenCity);
            cityNames.setFocusable(false);

            skillLevel = v.findViewById(R.id.skilLevel);
            skillLevel.setFocusable(false);

            addInfo = v.findViewById(R.id.addInfoPost);
            addInfo.setFocusable(false);

            layoutOfPost = v.findViewById(R.id.layoutOfPost);

            arrowDownOpenMenu = v.findViewById(R.id.arrowDownOpenMenu);

            extraInfoContainer = v.findViewById(R.id.extraInfoContainer);

            arrowDownOpenMenuButton = v.findViewById(R.id.arrowDownOpenMenuButton);

            savePostButton = v.findViewById(R.id.savePostButton);

            chatButton = v.findViewById(R.id.chatButton);
        }
    }
}
