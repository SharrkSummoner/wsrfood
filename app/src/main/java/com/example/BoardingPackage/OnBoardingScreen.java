package com.example.BoardingPackage;

import androidx.annotation.AnimRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.MainActivity;
import com.example.SignInScreen;
import com.example.SignUpScreen;
import com.example.SliderAdapter;
import com.example.wsrfood.R;

import org.w3c.dom.Text;

public class OnBoardingScreen extends FragmentActivity {

    private static final int NUM_PAGES = 2;

    ViewPager viewPager;
    LinearLayout dotsLayout;
    SliderAdapter sliderAdapter;
    TextView[] dots;
    Button signIn;
    Button signUp;
    Button skip;
    Animation animation;
    TextView delivery;

    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_on_boarding_screen);

        viewPager = findViewById(R.id.slider);
        dotsLayout = findViewById(R.id.dots);
        signIn = findViewById(R.id.sign_in_btn);
        signUp = findViewById(R.id.sign_up_btn);
        skip = findViewById(R.id.skip_bth);
        delivery = findViewById(R.id.fast_delivery);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDots(0);
        viewPager.addOnPageChangeListener(changeListener);

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

    public void skip(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        OnBoardingScreen.this.startActivity(intent);
        OnBoardingScreen.this.finish();
    }

    private void addDots(int position) {
        dots = new TextView[2];
        dotsLayout.removeAllViews();

        for(int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));

            dots[i].setTextSize(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    20,
                    getResources().getDisplayMetrics()));

            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            for (int i = 0; i < dots.length; i++) {
                if (i != position) {
                    dots[i].setTextColor(getResources().getColor(R.color.white_opacity_25));
                } else {
                    dots[i].setTextColor(getResources().getColor(R.color.white));
                }
            }
        }
    }

    private void showText(TextView textView, @AnimRes int id) {
        if (textView.getVisibility() != View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(this, id);
            textView.startAnimation(animation);
        }
        textView.setVisibility(View.VISIBLE);
    }

    private void hideText(TextView textView, @AnimRes int id) {
        if (textView.getVisibility() == View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(this, id);
            textView.startAnimation(animation);
        }
        textView.setVisibility(View.INVISIBLE);
    }

    private void showButton(Button btn, @AnimRes int id) {
        if (btn.getVisibility() != View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(this, id);
            btn.startAnimation(animation);
        }
        btn.setClickable(true);
        btn.setVisibility(View.VISIBLE);
    }

    private void hideButton(Button btn, @AnimRes int id) {
        if (btn.getVisibility() == View.VISIBLE) {
            animation = AnimationUtils.loadAnimation(this, id);
            btn.startAnimation(animation);
        }
        btn.setClickable(false);
        btn.setVisibility(View.INVISIBLE);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.println(Log.INFO, "INFO", "Connection");
            return true;
        }
        Log.println(Log.INFO, "INFO", "Have not cnonnection");
        return false;
    }

    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            addDots(position);
            if (position == 0) {
                hideButton(signIn, R.anim.disappearance_anim);
                hideButton(signUp, R.anim.disappearance_anim);
                hideButton(skip, R.anim.disappearance_anim);
                showText(delivery, R.anim.appearance_anim);
            } else {
                showButton(signIn, R.anim.appearance_anim);
                showButton(signUp, R.anim.appearance_anim);
                hideText(delivery, R.anim.disappearance_anim);
                if (!isOnline(OnBoardingScreen.this)) {
                    showButton(skip, R.anim.appearance_anim);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    };
}