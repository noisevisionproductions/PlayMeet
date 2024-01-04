package com.example.zagrajmy.Adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zagrajmy.Realm.RealmAuthenticationManager;
import com.example.zagrajmy.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ExtraInfoContainerForAllPosts {
/*    private final List<PostsAdapterAllPosts.MyViewHolder> openMenus = new ArrayList<>();
    private static final int OPEN_MENU_HEIGHT_DP = 300;
    private static final int CLOSED_MENU_HEIGHT_DP = 240;


    public void handleExtraInfo(PostsAdapterAllPosts.MyViewHolder holder, Context context) {
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

    private void toggleExtraInfo(PostsAdapterAllPosts.MyViewHolder holder, Context context) {
        if (holder.isExtraInfoOpen) {
            closeMenu(holder, context);
        } else {
            openMenu(holder, context);
        }
    }

    private void openMenu(PostsAdapterAllPosts.MyViewHolder holder, Context context) {
        closeAllMenus(context);

        holder.extraInfoContainer.setVisibility(View.VISIBLE);

        ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
        layoutParams.height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, OPEN_MENU_HEIGHT_DP, context.getResources().getDisplayMetrics());
        holder.cardView.setLayoutParams(layoutParams);

        holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_up_24);
        holder.isExtraInfoOpen = true;
        openMenus.add(holder);
    }

    private void closeMenu(PostsAdapterAllPosts.MyViewHolder holder, Context context) {

        holder.extraInfoContainer.setVisibility(View.GONE);

        ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
        layoutParams.height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, CLOSED_MENU_HEIGHT_DP, context.getResources().getDisplayMetrics());
        holder.cardView.setLayoutParams(layoutParams);

        holder.arrowDownOpenMenuButton.setBackgroundResource(R.drawable.baseline_keyboard_arrow_down_24);
        holder.isExtraInfoOpen = false;
        openMenus.remove(holder);
    }

    public void closeAllMenus(Context context) {
        for (PostsAdapterAllPosts.MyViewHolder openMenu : openMenus) {
            closeMenu(openMenu, context);
        }
        openMenus.clear();
    }

    private static void showToast(Context context) {
        Toast.makeText(context.getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników", Toast.LENGTH_SHORT).show();
    }*/
}
