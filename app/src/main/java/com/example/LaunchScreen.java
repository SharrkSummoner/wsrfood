package com.example;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.BoardingPackage.OnBoardingScreen;
import com.example.wsrfood.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class LaunchScreen extends AppCompatActivity {

    public final String[] resIs = {""}; //answer from server
    public static String version = new String(""); //version
    boolean versionExists = false; //exists versions
    File internalStorageDir; //storage with local data
    FileReader reader; //read file versions
    File versionsFile; //versions file
    File menuItemsFile; //data file


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set size fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.activity_launch_screen);

        //set local data
        internalStorageDir = getFilesDir();

        //csv file with versions
        versionsFile = new File(internalStorageDir, "versions.csv");
        try {
            if (!versionsFile.exists()) {
                versionsFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set reader
        try {
            reader = new FileReader(internalStorageDir + "/versions.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //if version file exist = true
        versionExists = ifLocalVersionExists();

        menuItemsFile = new File(internalStorageDir, "menuItems.csv");
        try {
            if (!menuItemsFile.exists()) {
                menuItemsFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //animation loading
        ImageView img = findViewById(R.id.loading);
        Animation animation = AnimationUtils.loadAnimation(
                this, R.anim.progressbar_animation);
        img.clearAnimation();

        //chek internet connection
        Thread thread = new Thread() {
            public void run() {
                Thread.currentThread().setName("Check connection");
                int loadingState = 0;
                while (loadingState != 3) {
                    while (loadingState == 0) {
                        try {
                            if (!isOnline(LaunchScreen.this) && !tryGetVersion()) {
                                loadingState = 1;
                                makeMessageOnScreen("Ошибка подключения к интернету");
                                if (versionExists) {
                                    makeMessageOnScreen("Загрузка локальных данных");
                                    loadingState = 3;
                                    Intent intent = new Intent(
                                            LaunchScreen.this,
                                            OnBoardingScreen.class);
                                    startActivity(intent);
                                    LaunchScreen.this.finish();
                                    overridePendingTransition(
                                            android.R.anim.fade_in,
                                            android.R.anim.fade_out);
                                }
                            } else {
                                img.startAnimation(animation);
                                try {
                                    if (tryGetVersion()) {
                                        loadingState = 3;

                                        updateLocalVersion();

                                        Intent intent = new Intent(
                                                LaunchScreen.this,
                                                OnBoardingScreen.class);
                                        startActivity(intent);
                                        LaunchScreen.this.finish();
                                        overridePendingTransition(
                                                android.R.anim.fade_in,
                                                android.R.anim.fade_out);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    while (loadingState == 1) {
                        img.startAnimation(animation);
                        if (isOnline(LaunchScreen.this)) {
                            img.startAnimation(animation);
                            loadingState = 0;
                        }
                    }
                }
            }
        };
        thread.start();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            Log.println(Log.INFO, "INFO", "Connection");
            return true;
        }

        Log.println(Log.INFO, "INFO", "Have not connetcion");
        return false;
    }

    private void makeMessageOnScreen(String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(
                        LaunchScreen.this,
                        msg,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    //connect to server
    public boolean tryGetVersion() throws JSONException {
        String url = "https://food.madskill.ru/dishes/version";

        RequestQueue requestQueue = Volley.newRequestQueue(LaunchScreen.this);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                result -> resIs[0] = result,
                error -> resIs[0] = error.getMessage());
        requestQueue.add(request);

        for (int i = 0; i < 5; i++) {
            Log.println(Log.INFO, "INFO", "Waiting request");

            if (!resIs[0].equals("") && resIs[0].charAt(0) != 'j') {
                Log.println(Log.INFO, "INFO", resIs[0]);

                if (!resIs[0].equals(version)) {
                    try {
                        FileOutputStream fos = new FileOutputStream(versionsFile);
                        fos.write(resIs[0].getBytes(StandardCharsets.UTF_8));
                        fos.close();
                        getDishesData();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        makeMessageOnScreen("Ошибка подключения к серверу");
        return false;
    }

    public void updateLocalVersion() {
        if (versionsFile.exists()) {
            try {
                Scanner reader_sc = new Scanner(reader);
                version = reader_sc.nextLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.println(Log.ERROR, "INFO", "updateLocalVersion: File don't exists");
        }
    }

    public boolean ifLocalVersionExists() {
        updateLocalVersion();
        if (versionsFile.exists()) {
            return !(versionsFile.equals("") || version.isEmpty());
        } else {
            return false;
        }
    }

    private void getDishesData() {
        String dishesUrl = "https://food.madskill.ru/dishes?version=1.01";

        JSONArray jsonArray = new JSONArray();

        RequestQueue requestQueue = Volley.newRequestQueue(LaunchScreen.this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, dishesUrl, null,
                response -> addDishesData(response),
                error -> Toast.makeText(
                        LaunchScreen.this,
                        "Не удалось получить данные с сервера",
                        Toast.LENGTH_LONG).show());
        requestQueue.add(request);
    }

    private void addDishesData(JSONArray jsonArray) {
        JSONObject jsonObject;

        int dishId = 0;
        String category = "";
        String nameDish = "";
        String iconResource = "";
        int price = 0;
        String version = "";

        String imageUrl = "images: http://food.madskill.ru/up/images/";
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    jsonObject = jsonArray.getJSONObject(i);
                    dishId = jsonObject.getInt("dishId");
                    category = jsonObject.getString("category");
                    nameDish = jsonObject.getString("nameDish");
                    price = Integer.parseInt(jsonObject.getString("price"));
                    iconResource = jsonObject.getString("icon");
                    version = jsonObject.getString("icon");
                    Log.println(Log.ERROR, "API", menuItemsFile.getPath()
                            + " : " + dishId + ","
                            + category + ","
                            + nameDish + ","
                            + price + ","
                            + "logo_100" + ","
                            + version + "\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Files.write(Paths.get(menuItemsFile.getPath()), (dishId + ","
                        + category + ","
                        + nameDish + ","
                        + price + ","
                        + "logo_100" + ","
                        + version + ",\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}