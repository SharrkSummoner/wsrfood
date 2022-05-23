package com.example;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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

import androidx.appcompat.app.AppCompatActivity;

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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Scanner;

@SuppressLint("CustomSplashScreen")
public class LaunchScreenActivity extends AppCompatActivity {

    public final String[] resIs = {""}; // Переменная для хранения ответа от сервера

    public static String version = new String(""); // Переменная хранящая версии

    boolean versionExists = false; // Переменная наличия версий

    File internalStorageDir; // Переменная для хранения пути, где будут хранится локальные данные

    FileReader reader; // Создаём поток вывода для чтения из файла версий

    File versionsFile; // Переменная хранящая объект файла с данными о версиях блюд

    File menuItemsFile; // Переменная хранящая объект файла с данными о блюдах

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Разворачиваем страницу на весь экран
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launch_screen);

        // Работа с файлами -----------------------------------------------------------------------

        internalStorageDir = getFilesDir(); // Получаем путь для хранения локальных данных

        // Файл версий блюд <----------------------------------------------------------------------

        versionsFile = new File(internalStorageDir, "versions.csv"); // Создаём объект файла для хранения локальных данных
        try {
            if (!versionsFile.exists()) {
                versionsFile.createNewFile(); // Создаём файл для хранения локальных данных
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try { // Настраиваем считыватель файлов для чтения версий
            reader = new FileReader(internalStorageDir + "/versions.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        versionExists = ifLocalVersionsExists(); // Обновляем переменную которая хранит истинну если файл файл версий существует
        // [конец] Файл версий блюд <--------------------------------------------------------------
        // Файл блюд <-----------------------------------------------------------------------------

        menuItemsFile = new File(internalStorageDir, "menuItems.csv"); // Создаём объект файла для хранения локальных данных
        try {
            if (!menuItemsFile.exists()) {
                menuItemsFile.createNewFile(); // Создаём файл для хранения локальных данных
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // [конец] Файл блюд <---------------------------------------------------------------------
        // [конец] Работа с файлами ---------------------------------------------------------------

        // Анимация загрузки ----------------------------------------------------------------------
        ImageView img = findViewById(R.id.loading); // Привязываемся к изображению загрузки на экране
        // создаем анимацию
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.progressbar_animation);
        // запуск анимации для изображения загрузки
        img.startAnimation(animation);
        // [конец] Анимация загрузки --------------------------------------------------------------

        // Создаём поток для проверки сети интернет
        Thread thread = new Thread() { // Тело потока

            public void run() { // Выполнение потока
                // Настройка потока
                Thread.currentThread().setName("Check connection"); // Задаём название потока

                /* Пояснение:
                 * Переменная check скачет между значениями:
                 * 0 - ОНЛАЙН
                 * 1 - ОФФЛАЙН
                 * 3 - ПОДКЛЮЧЕНО
                 * До тех пор пока не удасться подключится к серверу (3), будут предприниматься попытки
                 * подключения, если интернет есть (0), если интернета нет (1), попыток подключится не будет.
                 * Тем неменее, если есть уже загруженные локальные данные, то при отвутствии интернета (1)
                 * будет осуществлён переход на приветственный экран, как при успешном подключении. */

                int check = 0; // Проверка 0 отсутствия интернета

                while (check != 3) { // Пока проверка не будет равна 3 т е пока не удасться подключится к серверу
                    while (check == 0) { // Проверка отсутствия подключения к интернету
                        if (!isOnline(LaunchScreenActivity.this)) { // Если нет подключения к сети
                            img.clearAnimation(); // Отключение анимации загрузки
                            check = 1; // Переход к проверке наличия интернета
                            makeMessageOnScreen("Ошибка подключения к интернету");
                            if (versionExists) { //  Если существуют локальные данные
                                makeMessageOnScreen("Загрузка локальных данных");
                                check = 3; // Выход из цикла
                                // Переход на приветственный экран
                                startActivity(new Intent(LaunchScreenActivity.this, OnBoardingScreen.class));
                            }
                        } else {
                            // Если интернет есть, то попытаться подключится к серверку
                            try { // Попытка подключения к серверу
                                if (tryGetVersion()) { // Если удалось получить ответ
                                    check = 3; // Выход из цикла

                                    updateLocalVersion(); // Обновляем переменную версий из локальных данных

                                    // Переход на приветственный экран
                                    startActivity(new Intent(LaunchScreenActivity.this, OnBoardingScreen.class));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    while (check == 1) { // Проверка подключения к интернету
                        if (isOnline(LaunchScreenActivity.this)) {  // Если есть подключение к сети
                            img.startAnimation(animation); // Включение анимации
                            check = 0; // Переход к проверке отсутствия интернета
                        }
                    }
                }
            }

            // Методы потока ----------------------------------------------------------------------

            // Версии <----------------------------------------------------------------------------

            // Функция проверки подключения к сети
            public boolean isOnline(Context context) {
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // Создаём менеджер подключения записываем в него службы подключения
                NetworkInfo netInfo = cm.getActiveNetworkInfo(); // Подключаем и записываем информацию о сети
                if (netInfo != null && netInfo.isConnectedOrConnecting()) { // Если данные получены и есть подключение к сети
                    Log.println(Log.INFO, "INFO", "Connection"); // Записываем в логи состояние сети
                    return true; // Подключение к сети удалось
                } // Иначе
                Log.println(Log.INFO, "INFO", "Have not cnonnection"); // Записываем в логи состояние сети
                return false; // Подключение к сети не удалось
            }

            // Функция подключения к серверу
            public boolean tryGetVersion() throws JSONException {

                String url = "https://food.madskill.ru/dishes/version"; // Ссылка

                RequestQueue requestQueue = Volley.newRequestQueue(LaunchScreenActivity.this); // Запрос

                // Конструктор запроса
                /* Метод GET
                 * Ссылка url
                 * При получении результата записать в ответ resIs ответ от сервера
                 * При получении ошибки записать в ответ resIs текст ошибки */
                StringRequest request = new StringRequest(Request.Method.GET, url,
                        result -> resIs[0] = result,
                        error -> resIs[0] = error.getMessage()
                ); // Инструкция по выполнению в случае ошибок или успешного получения ответа
                requestQueue.add(request); // Отправка запроса

                /* Пояснение:
                 * Если сервер не отвечает или выдаёт ошибки, то выводим сообщение о том, что произошла
                 * ошибка подключения к серверу, так же поступаем вновь, если сервер не ответил в течении
                 * 10 секунд. Если сервер ответил, сравниваем ответ с локальными версиями, если не совпадает
                 * обновляем локальные версии. Затем выходим из цикла. */

                // Когда сервер не ответил или произошла ошибка
                if (resIs[0].equals("") && resIs[0].substring(0, 0).equals("j")) {
                    makeMessageOnScreen("Ошибка подключения к серверу"); // Выводим сообщение на экран
                }

                // Пока сервер не ответит в течении 10 секунд ждать
                for (int i = 0; i < 10; i++) {
                    // Выводим в логи информацию о том, что программа ждёт ответа от сервера
                    Log.println(Log.INFO, "INFO", "Waiting request.");

                    if (!resIs[0].equals("") && !resIs[0].substring(0, 1).equals("j")) { // Когда сервер ответил без ошибок
                        Log.println(Log.INFO, "INFO", resIs[0]); // Выводим полученный результат в логи

                        // Если локальные версии меню не совпадают с сетевыми, то обновляем версии
                        if (!resIs[0].equals(version)) {
                            try { // Запись полученного результата в файл
                                // Создаём поток вывода файла
                                FileOutputStream fos = new FileOutputStream(versionsFile);
                                // Записываем строку в файл
                                fos.write(resIs[0].getBytes());
                                // Закрываем поток вывода файла
                                fos.close();
                                getDishesData();
                            } catch (Exception e) {
                            }
                        }
                        return true; // Подключение успешно
                    } else { // Если сервер не ответил или произошла ошибка
                        try {
                            Thread.sleep(1000); // Задержка в 1 секунду
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } // Если сервер так и не ответил за 10 секунд
                makeMessageOnScreen("Ошибка подключения к серверу"); // Выводим сообщение на экран
                return false; // Подключение не успешно
            }
            // [конец] Версии <--------------------------------------------------------------------
            // Блюда <-----------------------------------------------------------------------------

            // Метод получения блюд с свервера
            private void getDishesData() {
                String baseUrl = "https://food.madskill.ru/dishes?version=1.01";

                JSONArray json = new JSONArray();

                RequestQueue requestQueue = Volley.newRequestQueue(LaunchScreenActivity.this);
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                        response ->
                                addDishesData(response),
                        error -> Toast.makeText(LaunchScreenActivity.this, "Не удалось получить данные с сервера.", Toast.LENGTH_LONG).show());
                requestQueue.add(request);
            }

            // Метод сохранения блюд
            private void addDishesData(JSONArray jsonArray) {
                JSONObject jsonObject;

                int id = 0;
                String category = "";
                String nameDish = "";
                String iconResource = "";
                int price = 0;
                String version = "";

                String baseImageUrl = "https://food.madskill.ru/up/images/";
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            jsonObject = jsonArray.getJSONObject(i);
                            id = jsonObject.getInt("dishId");
                            category = jsonObject.getString("category");
                            nameDish = jsonObject.getString("nameDish");
                            price = Integer.parseInt(jsonObject.getString("price"));
                            iconResource = jsonObject.getString("icon");
                            version = jsonObject.getString("version");
                            Log.println(Log.ERROR, "API", menuItemsFile.getPath()+" : "+id + ","
                                    + category + ","
                                    + nameDish + ","
                                    + price + ","
                                    + "logo_100" + ","
                                    + version + ",\n");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // Создаём поток вывода файла
                        //FileOutputStream fosMenu = new FileOutputStream(menuItemsFile);
                        // Записываем строку в файл
                        Files.write(Paths.get(menuItemsFile.getPath()), (id + ","
                                + category + ","
                                + nameDish + ","
                                + price + ","
                                + "logo_100" + ","
                                + version + ",\n").getBytes(), StandardOpenOption.APPEND);
                        // Закрываем поток вывода файла
                        //fosMenu.close();
                        //menuItems.add(new MenuItem(nameDish, price, baseImageUrl.concat(iconResource)));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // [конец] Блюда <---------------------------------------------------------------------
            // Вспомогательные <-------------------------------------------------------------------

            // Процедура вывода сообщений на экран в основном потоке
            private void makeMessageOnScreen(String msg) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(LaunchScreenActivity.this, msg, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            // [конец] Вспомогательные <-----------------------------------------------------------
            // [конец] Методы потока --------------------------------------------------------------
        };
        thread.start(); // Запуск потока
    }

    // Методы -------------------------------------------------------------------------------------
    // Работа с локальными данными <---------------------------------------------------------------
    // Версии <<-----------------------------------------------------------------------------------

    // Если есть локально сохранённые версии возвращает булевую истинну, иначе лож
    public boolean ifLocalVersionsExists() {
        updateLocalVersion(); // Обновляем переменную версий
        if (versionsFile.exists()) { // Если файл существует
            if (!(versionsFile.equals("") || version.isEmpty())) { // Если переменная версий не пустая
                return true; // Возвращаем истинну
            } else { // Иначе
                return false; // Возвращаем лож
            }
        } else { // Если файла не существует возвращаем лож
            return false;
        }
    }

    // Обновление переменной с версиями
    public void updateLocalVersion() {
        if (versionsFile.exists()) { // Если файл существует
            try { // Чтение файла
                // читаем построчно
                Scanner reader_text = new Scanner(reader); // Создаём сканер
                version = reader_text.nextLine(); // Записываем первую строку файла в переменную версий
            } catch (Exception e) { // В случае ошибок
                e.printStackTrace(); // Вывод ошибок в консоль
            }
        } else { // Если файл не существует выводим информацию в логи
            Log.println(Log.ERROR, "INFO", "updateLocalVersion: File don,t exists!");
        }
    }
    // [конец] Версии <<---------------------------------------------------------------------------
    // [конец] Работа с локальными данными <-------------------------------------------------------
    // [конец] Методы -----------------------------------------------------------------------------
}