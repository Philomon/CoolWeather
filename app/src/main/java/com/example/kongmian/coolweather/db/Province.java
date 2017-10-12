package com.example.kongmian.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by kongmian on 2017/9/21.
 */

public class Province extends DataSupport{

    private int id;
    private String name;
    private int code;

    public String getName(){
        return  this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getCode(){
        return  this.code;
    }

    public void setCode(int code){
        this.code = code;
    }



}
