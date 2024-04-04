package com.noisevisionproductions.playmeet.postsManagement.allPostsManagement;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.noisevisionproductions.playmeet.PostModel;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.firebase.FirebaseAuthManager;
import com.noisevisionproductions.playmeet.postsManagement.postsMenu.ExplainPostDialog;
import com.noisevisionproductions.playmeet.postsManagement.postsMenu.ReportPost;
import com.noisevisionproductions.playmeet.utilities.CoolDownManager;

public class AdapterAllPostsHelper {

    public static void getSkillLevel(@NonNull PostModel postModel, @NonNull ImageView skillLevelImageView) {
        int difficultyId = postModel.getSkillLevel();
        int drawableId = getDrawableIdForDifficulty(difficultyId);
        skillLevelImageView.setImageResource(drawableId);
    }

    private static int getDrawableIdForDifficulty(int difficultyId) {
        return switch (difficultyId) {
            case 1 -> R.drawable.d1_10;
            case 2 -> R.drawable.d2_10;
            case 3 -> R.drawable.d3_10;
            case 4 -> R.drawable.d4_10;
            case 5 -> R.drawable.d5_10;
            case 6 -> R.drawable.d6_10;
            case 7 -> R.drawable.d7_10;
            case 8 -> R.drawable.d8_10;
            case 9 -> R.drawable.d9_10;
            case 10 -> R.drawable.d10_10;
            default -> 0;
        };
    }

    public static void openPostMenu(@NonNull AdapterAllPosts.MyViewHolder holder, String postId, Context context, FragmentManager fragmentManager) {
        CoolDownManager cooldownManager = new CoolDownManager(context);

        if (FirebaseAuthManager.isUserLoggedIn()) {
            holder.menuIconButton.setVisibility(View.VISIBLE);
            holder.menuIconButton.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(context, v);
                menu.inflate(R.menu.menu_item_dots);
                menu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.reportPost) {
                        if (cooldownManager.canSendReport()) {
                            ReportPost reportPost = new ReportPost(v.getContext());
                            reportPost.showReportDialog(postId);
                            return true;
                        } else {
                            Snackbar
                                    .make(v, context.getString(R.string.reportingToOften), Snackbar.LENGTH_SHORT)
                                    .setTextColor(Color.RED)
                                    .show();
                        }
                    }
                    if (item.getItemId() == R.id.help) {
                        ExplainPostDialog explainPostDialog = new ExplainPostDialog();
                        explainPostDialog.show(fragmentManager, "ExplainPostDialog");
                    }
                    return false;
                });
                menu.show();
            });
        }
    }
}
