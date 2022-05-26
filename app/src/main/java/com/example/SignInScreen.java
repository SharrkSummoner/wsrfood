package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wsrfood.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Scanner;

public class SignInScreen extends AppCompatActivity {

    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in_screen);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

    }

    public void login(View view) {
        if (email.getText().toString().isEmpty() ||
        password.getText().toString().isEmpty()) {
            createDialog(this, "Не все поля заполнены.");
        } else {
            if (!checkEmail(email.getText().toString())) {
                createDialog(this, "Неверно введена почта.");
            } else {
                tryLogin();
            }
        }
    }

    public void forgot_pass(View view) {
        Intent intent = new Intent(this, SignUpScreen.class);
        SignInScreen.this.startActivity(intent);
        SignInScreen.this.finish();
    }

    private boolean checkEmail(String email) {
        return email.matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,3})$");
    }

    public void tryLogin() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", mail);
            jsonObject.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "https://food.madskill.ru/auth/login";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                response -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    SignInScreen.this.startActivity(intent);
                    SignInScreen.this.finish();
                },
                error -> createDialog(this, "Неверная почта или пароль:\n" + error.getMessage()));
        requestQueue.add(request);
    }

    public void createDialog(Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}