package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.MaskFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wsrfood.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpScreen extends AppCompatActivity {

    EditText email;
    EditText password;
    EditText name;
    EditText maskphone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up_screen);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        maskphone = findViewById(R.id.maskphone);

        EditText inputField = (EditText) findViewById(R.id.maskphone);
        inputField.addTextChangedListener(new PhoneNumberFormattingTextWatcher("RU"));
    }

    public void signup(View view) {
        if (email.getText().toString().isEmpty()
                || password.getText().toString().isEmpty()
                || name.getText().toString().isEmpty()
                || maskphone.getText().toString().isEmpty()
        ) {
            createDialog(this, "Не все поля заполнены.");
        } else {
            if (!checkEmail(email.getText().toString())) {
                createDialog(this, "Неверно введена почта.");
            } else {
                trySignUp();
            }
        }
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

    private boolean checkEmail(String email) {
        return email.matches("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,3})$");
    }

    public void trySignUp() {
        String mail = email.getText().toString();
        String pass = password.getText().toString();
        String fullname = name.getText().toString();

        String url = "https://food.madskill.ru/auth/register";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    SignUpScreen.this.startActivity(intent);
                    SignUpScreen.this.finish();
                },
                error -> createDialog(this, "Не удалось зарегестироваться\n" + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", mail);
                params.put("password", pass);
                params.put("login", mail);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public void back(View view) {
        Intent intent = new Intent(this, SignInScreen.class);
        SignUpScreen.this.startActivity(intent);
        SignUpScreen.this.finish();
    }
}