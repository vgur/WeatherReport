package com.example.a16719756.weatherreport;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/* Данные из запроса */
public class WeatherForecast {
    @SerializedName("list")
    private List<Weather> items;

    public WeatherForecast(List<Weather> items) {
        this.items = items;
    }

    public List<Weather> getItems() {
        return items;
    }
}
