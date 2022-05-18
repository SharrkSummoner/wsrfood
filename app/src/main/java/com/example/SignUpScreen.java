package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.MaskFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.wsrfood.R;

public class SignUpScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        EditText inputField = (EditText) findViewById(R.id.maskphone);
        inputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher("RU"));
    }

    public void signup(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        SignUpScreen.this.startActivity(intent);
        SignUpScreen.this.finish();
    }

    public void back(View view) {
        Intent intent = new Intent(this, SignInScreen.class);
        SignUpScreen.this.startActivity(intent);
        SignUpScreen.this.finish();
    }
}