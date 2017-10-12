package com.example.kongmian.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by kongmian on 2017/9/21.
 */

public class City extends DataSupport{
    private int id;
    private String name;
    private int code;
    private int provinceId;

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
