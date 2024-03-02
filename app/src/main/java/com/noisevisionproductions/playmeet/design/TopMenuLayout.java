package com.noisevisionproductions.playmeet.design;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.noisevisionproductions.playmeet.AppOptions;
import com.noisevisionproductions.playmeet.R;
import com.noisevisionproductions.playmeet.utilities.OpinionFromUser;
import com.noisevisionproductions.playmeet.utilities.ToastManager;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class TopMenuLayout extends AppCompatActivity {
    protected CircleImageView userAvatar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_menu, menu);
        setAppLogo();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_settings) {
            if (!this.getClass().getName().equals(AppOptions.class.getName())) {
                Intent intent = new Intent(getApplicationContext(), AppOptions.class);
                startActivity(intent);
            }
            return true;
        }
        if (item.getItemId() == R.id.feedback) {
            if (!this.getClass().getName().equals(OpinionFromUser.class.getName())) {
                Intent intent = new Intent(getApplicationContext(), OpinionFromUser.class);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setAppLogo() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);

            LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());
            View customView = inflater.inflate(R.layout.action_bar_logo, new LinearLayout(actionBar.getThemedContext()), false);
            actionBar.setCustomView(customView);

            AppCompatImageView imageView = customView.findViewById(R.id.appLogo);
            imageView.setImageResource(R.mipmap.app_logo_round);

            ToastManager.createToolTip("https://playmeet.eu", imageView);
        }
    }
}
