package com.example.a16719756.weatherreport;

import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.List;

public class Weather {

    public class WeatherTemp {
        Double temp;
        Double temp_min;
        Double temp_max;
    }

    public class WeatherDescription {
        String icon;
        String description;
    }

    @SerializedName("main")
    private WeatherTemp temp;

    @SerializedName("weather")
    private List<WeatherDescription> desctiption;

    @SerializedName("name")
    private String city;

    @SerializedName("dt")
    private long timestamp;

    public Weather(WeatherTemp temp, List<WeatherDescription> description) {
        this.temp = temp;
        this.desctiption = description;
    }

    public Calendar getDate() {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timestamp * 1000);
        return date;
    }


    public String getTempWithDegree() { return String.valueOf(temp.temp.intValue()) + "\u00B0"; }

    public String getCity() { return city; }

    public String getIcon() { return desctiption.get(0).icon; }

    public String getDescription() { return desctiption.get(0).description; }


    public String getIconUrl() {
        return "http://openweathermap.org/img/w/" + desctiption.get(0).icon + ".png";
    }
}
