package com.example.kongmian.coolweather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kongmian.coolweather.db.City;
import com.example.kongmian.coolweather.db.County;
import com.example.kongmian.coolweather.db.Province;
import com.example.kongmian.coolweather.gson.Weather;
import com.example.kongmian.coolweather.util.HttpUtil;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by kongmian on 2017/9/20.
 */

public class ChooseAreaFragment extends Fragment{

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private int selectProvince;

    private int selectCity;

    private int selectCounty;

    private String weatherID;

    private int currentLevel = LEVEL_PROVINCE;

    private ProgressDialog progressDialog;

    private Button button;

    private ListView listView;

    private TextView textView;

    private ArrayAdapter<String> arrayAdapter;

    private List<String> datalist = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//         super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.choose_area,container,false);
        button = (Button) view.findViewById(R.id.back_button);
        textView = (TextView) view.findViewById(R.id.title_text);
        listView = (ListView) view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(arrayAdapter);
        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //初始是没有点按的
                if (currentLevel == LEVEL_PROVINCE){

                    Province province = provinceList.get(position);
                    selectProvince = province.getCode();
//                    Log.d("testlog",String.valueOf(selectProvince));
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){

                    City city = cityList.get(position);
                    selectCity = city.getCode();
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    County county = countyList.get(position);
                    weatherID = county.getWeather_id();

                    if(getActivity() instanceof MainActivity){
                        //启动WeatherActivity

                        Log.d("test","mainactivity");
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weatherID",weatherID);
                        startActivity(intent);
                        getActivity().finish();

                    }else if(getActivity() instanceof  WeatherActivity){

                        Log.d("test","weatheractivity");
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                        editor.putString("weatherId",weatherID);
                        editor.apply();

                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeatherId(weatherID);

                    }

                    //这里写跳转到WeatherActivity的逻辑
                }


            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }else if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }
            }
        });

        queryProvinces();
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces(){

        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()> 0){
            datalist.clear();
            for(Province province:provinceList){
                datalist.add(province.getName());
            }
            currentLevel = LEVEL_PROVINCE;
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            textView.setText("中国");

        }else{
            showProgressDialog();
            HttpUtil.sendOkHttpRequset("http://guolin.tech/api/china",new okhttp3.Callback(){


                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {


                        JSONArray jsonArray = new JSONArray(response.body().string());

                        for (int i =0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Province province = new Province();
                            province.setCode(jsonObject.getInt("id"));
                            province.setName(jsonObject.getString("name"));
                            province.save();
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryProvinces();
                                closeProgressDialog();
                            }
                        });

                    } catch (Exception e) {
                        Log.d("test","0");

                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            });
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities(){
        cityList = DataSupport.where("provinceId=?",String.valueOf(selectProvince)).find(City.class);
        if (cityList.size()>0){
//            Log.d("testquery","into if");
            datalist.clear();
            for(City city:cityList){
                datalist.add(city.getName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
            List<Province> provincelist =  DataSupport.where("code=?",String.valueOf(selectProvince)).find(Province.class);
            textView.setText(provincelist.get(0).getName());
            closeProgressDialog();

        }else {
            showProgressDialog();
            HttpUtil.sendOkHttpRequset("http://guolin.tech/api/china/"+selectProvince,new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response) throws IOException {

//                    Log.d("testquery",response.body().string());
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            City city = new City();
                            city.setName(jsonObject.getString("name"));
                            city.setCode(jsonObject.getInt("id"));
                            city.setProvinceId(selectProvince);
                            city.save();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryCities();

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT);
                        }
                    });

                }
            });
        }

    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties(){

        countyList = DataSupport.where("cityId=?",String.valueOf(selectCity)).find(County.class);
        if (countyList.size() > 0){
            datalist.clear();
            for(County county:countyList){
                datalist.add(county.getName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
            List<City> citylist =  DataSupport.where("code=?",String.valueOf(selectCity)).find(City.class);
            textView.setText(citylist.get(0).getName());

            closeProgressDialog();

        }else {
            showProgressDialog();
            HttpUtil.sendOkHttpRequset("http://guolin.tech/api/china/"+selectProvince+"/"+selectCity,new okhttp3.Callback(){

                @Override
                public void onResponse(Call call, Response response) throws IOException {
//                    Log.d("test",response.body().string());
                    try {
//                        Log.d("test", "onResponse: try");
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        for(int i =0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            County county = new County();
                            county.setName(jsonObject.getString("name"));
                            county.setWeather_id(jsonObject.getString("weather_id"));
                            county.setCityId(selectCity);
                            county.save();
                        }


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryCounties();
                            }
                        });

                    } catch (Exception e) {
//                        Log.d("test", "catch: ");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT);
                        }
                    });

                }
            });
        }

    }

}
