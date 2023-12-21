package com.example.zagrajmy.Adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zagrajmy.LoginRegister.AuthenticationManager;
import com.example.zagrajmy.R;

public class ExtraInfoContainerForAllPosts {
    public ExtraInfoContainerForAllPosts() {
    }

    public static void handleExtraInfo(PostDesignAdapterForAllPosts.MyViewHolder holder, Context context) {
        ViewGroup.LayoutParams layoutParams = holder.cardView.getLayoutParams();
        holder.arrowDownOpenMenu.setOnClickListener(v -> {
            if (AuthenticationManager.isUserLoggedIn()) {
                toggleExtraInfo(holder, layoutParams, context);
            } else {
                Toast.makeText(context.getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników!", Toast.LENGTH_SHORT).show();
            }
        });
        holder.arrowDownOpenMenuButton.setOnClickListener(v -> {
            if (AuthenticationManager.isUserLoggedIn()) {
                toggleExtraInfo(holder, layoutParams, context);
            } else {
                Toast.makeText(context.getApplicationContext(), "Dostępne jedynie dla zalogowanych użytkowników!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void toggleExtraInfo(PostDesignAdapterForAllPosts.MyViewHolder holder, ViewGroup.LayoutParams layoutParams, Context context) {
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
    }
}
