package com.example.kongmian.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by kongmian on 2017/9/21.
 */

public class County extends DataSupport{


    private int id;
    private String name;
    private String weather_id;
    private int cityId;

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }



}
