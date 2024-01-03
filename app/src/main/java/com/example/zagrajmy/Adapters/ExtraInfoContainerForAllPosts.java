package com.example.zagrajmy.Adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.R;

import java.util.ArrayList;
import java.util.List;

public class ExtraInfoContainerForAllPosts {
    private static final List<PostsAdapterAllPosts.MyViewHolder> openMenus = new ArrayList<>();
    private static final int OPEN_MENU_HEIGHT_DP = 300;
    private static final int CLOSED_MENU_HEIGHT_DP = 240;


    public static void handleExtraInfo(PostsAdapterAllPosts.MyViewHolder holder, Context context) {
        RealmAuthenticationManager authenticationManager = new RealmAuthenticationManager();

        holder.arrowDownOpenMenu.setOnClickListener(v -> {
            if (authenticationManager.isUserLoggedIn()) {
                toggleExtraInfo(holder, context);
            } else {
                showToast(context);
            }
        });

        holder.arrowDownOpenMenuButton.setOnClickListener(v -> {
            if (authenticationManager.isUserLoggedIn()) {
                toggleExtraInfo(holder, context);
            } else {
                showToast(context);
            }
        });
    }

    private static void toggleExtraInfo(PostsAdapterAllPosts.MyViewHolder holder, Context context) {
        if (holder.isExtraInfoOpen) {
            closeMenu(holder, context);
        } else {
            openMenu(holder, context);
        }
    }

    private static void openMenu(PostsAdapterAllPosts.MyViewHolder holder, Context context) {
        closeAllMenus(context);

        holder.extraInfoContainer.setVisibility(View.VISIBLE);
        //postCreating.setExtraInfoOpen(true);

        holder.cardView.getLayoutParams().height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, OPEN_MENU_HEIGHT_DP, context.getResources().getDisplayMetrics());
        holder.cardView.requestLayout();
        holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
        openMenus.add(holder);

    }

    private static void closeMenu(PostsAdapterAllPosts.MyViewHolder holder, Context context) {

        holder.extraInfoContainer.setVisibility(View.GONE);
        //postCreating.setExtraInfoOpen(false);

        holder.cardView.getLayoutParams().height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CLOSED_MENU_HEIGHT_DP, context.getResources().getDisplayMetrics());
        holder.cardView.requestLayout();
        holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
        openMenus.remove(holder);

    }

    private static void closeAllMenus(Context context) {
        for (PostsAdapterAllPosts.MyViewHolder openMenu : openMenus) {
            closeMenu(openMenu, context);
        }
    }

    private static void showToast(Context context) {
        Toast.makeText(context.getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników", Toast.LENGTH_SHORT).show();
    }
}
