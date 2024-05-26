package com.noisevisionproductions.playmeet.loginRegister.onboarding;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager.widget.ViewPager;

import com.noisevisionproductions.playmeet.R;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout layoutDots;
    private int[] layouts;
    private AppCompatButton nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.viewPager);
        layoutDots = findViewById(R.id.layoutDots);
        nextButton = findViewById(R.id.nextButton);

        layouts = new int[]{
                R.layout.onboarding_step1,
                R.layout.onboarding_step2,
                R.layout.onboarding_step3};

        addBottomDots(0);

        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(this, layouts);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        nextButton.setOnClickListener(v -> {
            int current = getItem();
            if (current < layouts.length) {
                viewPager.setCurrentItem(current);
            } else {
                finish();
            }
        });
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        layoutDots.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•", Html.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(Color.parseColor("#000000"));
            layoutDots.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(Color.parseColor("#FFFFFF"));
    }

    private int getItem() {
        return viewPager.getCurrentItem() + 1;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {
                nextButton.setText(getString(R.string.loginButton));
            } else {
                nextButton.setText(getString(R.string.nextStep));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
}