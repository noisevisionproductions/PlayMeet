package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.google.android.material.snackbar.Snackbar;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.utilities.CoolDownManager;
import com.noisevisionproductions.playmeet.utilities.ReportPost;

public class AdapterAllPostsHelper {

    public static void getSkillLevel(@NonNull PostModel postModel, @NonNull AdapterAllPosts.MyViewHolder holder, Context context) {
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
                            Snackbar.make(v, context.getString(R.string.reportingToOften), Snackbar.LENGTH_SHORT).setTextColor(Color.RED).show();
                        }
                    }
                    return false;
                });
                menu.show();
            });
        }
    }
}
