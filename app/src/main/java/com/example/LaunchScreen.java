package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.BoardingPackage.OnBoardingScreen;
import com.example.wsrfood.R;

public class LaunchScreen extends AppCompatActivity {

    private final int LAUNCH_DISPLAY_LENGHT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchScreen.this, OnBoardingScreen.class);
                LaunchScreen.this.startActivity(intent);
                LaunchScreen.this.finish();
            }
        }, LAUNCH_DISPLAY_LENGHT);
    }
}