package com.example.a16719756.weatherreport;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {
    TextView temperature;
    TextView descript;

    ImageView image;
    LinearLayout forecast;
    WeatherAPI.ApiInterface api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.temperature);
        descript = findViewById(R.id.descript);

        image = findViewById(R.id.ivImage);
        forecast = findViewById(R.id.llForecast);

        api = WeatherAPI.getClient().create(WeatherAPI.ApiInterface.class);

    }

    public void getWeather(View v) {

        // Дмитров - координаты
        Double lat = 56.3448500;
        Double lng = 37.5204100;
        String units = "metric";
        String key = WeatherAPI.KEY;

        // Прогноз сегодня
        Call<Weather> callToday = api.getWeather(lat, lng, units, key);
        callToday.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Weather data = response.body();

                if (response.isSuccessful()) {
                    temperature.setText(data.getCity() + " " + data.getTempWithDegree());
                    descript.setText(data.getDescription());
                    saveWeather(data.getDate().getTime().toString(), data.getTempWithDegree(), data.getDescription());
                    Glide.with(MainActivity.this).load(data.getIconUrl()).into(image);
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Log.e("WeatherReport", t.toString());
            }
        });

        // Прогноз неделя
        Call<WeatherForecast> callForecast = api.getForecast(lat, lng, units, key);
        callForecast.enqueue(new Callback<WeatherForecast>() {
            @Override
            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
                WeatherForecast data = response.body();

                if (response.isSuccessful()) {
                    SimpleDateFormat formatDayOfWeek = new SimpleDateFormat("E");

                    LinearLayout.LayoutParams paramsImageView = new LinearLayout.LayoutParams(160, 160);

                    forecast.removeAllViews();

                    for (Weather day : data.getItems()) {
                        // Выводим только одно значение
                        if (day.getDate().get(Calendar.HOUR_OF_DAY) == 15) {

                            LinearLayout childLayout = new LinearLayout(MainActivity.this);
                            childLayout.setOrientation(LinearLayout.VERTICAL);

                            // День недели
                            TextView tvDay = new TextView(MainActivity.this);
                            String dayOfWeek = formatDayOfWeek.format(day.getDate().getTime());
                            tvDay.setText(dayOfWeek);
                            childLayout.addView(tvDay);

                            // Картинка
                            ImageView ivIcon = new ImageView(MainActivity.this);
                            ivIcon.setLayoutParams(paramsImageView);
                            Glide.with(MainActivity.this).load(day.getIconUrl()).into(ivIcon);
                            childLayout.addView(ivIcon);

                            // Температура
                            TextView tvTemp = new TextView(MainActivity.this);
                            tvTemp.setText(day.getTempWithDegree());
                            childLayout.addView(tvTemp);

                            forecast.addView(childLayout);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherForecast> call, Throwable t) {
                Log.e("WeatherReport", t.toString());
            }
        });

    }

    public void showHistory(View view) {
        String URL = "content://com.example.WeatherReport.WeatherProvider";

        Uri weather = Uri.parse(URL);
        Cursor c = getContentResolver().query(weather, null, null, null, "date");
        String str = "";
        if (c.moveToFirst()) {
            do{
                str = c.getString(c.getColumnIndex(WeatherProvider._ID)) +
                        ", " +  c.getString((c.getColumnIndex(WeatherProvider.DATE))) +
                        ", " + c.getString(c.getColumnIndex( WeatherProvider.TEMPR)) +
                        ", " + c.getString(c.getColumnIndex( WeatherProvider.DESCR));
                Toast.makeText(this,
                        str, Toast.LENGTH_SHORT).show();
                Log.d("---> ", str);
            } while (c.moveToNext());
        }
    }

    // Сохранение прогноза в БД
    private void saveWeather(String date, String tempr, String descr){
        ContentValues values = new ContentValues();
        values.put(WeatherProvider.DATE, date);
        values.put(WeatherProvider.TEMPR, tempr);
        values.put(WeatherProvider.DESCR, descr);
        Uri uri = getContentResolver().insert(
                WeatherProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();

    }

}