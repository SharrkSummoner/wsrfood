package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.wsrfood.R;

public class SignInScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);
    }

    public void login(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        SignInScreen.this.startActivity(intent);
        SignInScreen.this.finish();
    }

    public void forgot_pass(View view) {
        Intent intent = new Intent(this, SignUpScreen.class);
        SignInScreen.this.startActivity(intent);
        SignInScreen.this.finish();
    }
}