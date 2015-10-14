package ru.ubububu.dogeweather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by andrey on 14.10.15.
 */
public class DefaultWeather {
    @SerializedName("cod")
    public String code;
    @SerializedName("message")
    public String message;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("sys")
    public Sys sys;
    @SerializedName("weather")
    public List<Weather> weather;
    @SerializedName("main")
    public Main main;
}
