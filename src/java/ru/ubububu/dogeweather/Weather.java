package ru.ubububu.dogeweather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by andrey on 14.10.15.
 */
public class Weather {
    @SerializedName("id")
    public int id;
    @SerializedName("description")
    public String description;
    @SerializedName("icon")
    public String icon;
}
