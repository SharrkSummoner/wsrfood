package com.example.BoardingPackage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.SignInScreen;
import com.example.SignUpScreen;
import com.example.wsrfood.R;

public class OnBoardingScreen extends FragmentActivity {

    private static final int NUM_PAGES = 2;

    private ViewPager2 viewPager;

    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding_screen);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    public void signIn(View view) {
        Intent intent = new Intent(this, SignInScreen.class);
        OnBoardingScreen.this.startActivity(intent);
        OnBoardingScreen.this.finish();
    }

    public void signUp(View view) {
        Intent intent = new Intent(this, SignUpScreen.class);
        OnBoardingScreen.this.startActivity(intent);
        OnBoardingScreen.this.finish();
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(OnBoardingScreen onBoardingScreen) {

            super(onBoardingScreen);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new Page1();
                case 1:
                    return new Page2();
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }
}