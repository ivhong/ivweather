package com.ivhong.ivweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ivhong.ivweather.db.City;
import com.ivhong.ivweather.db.County;
import com.ivhong.ivweather.db.Province;
import com.ivhong.ivweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangchanghong on 2017/12/29.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            ArrayList<JsonObject> jsonObjects = new Gson().fromJson(response, new TypeToken<ArrayList<JsonObject>>(){}.getType());
            for(JsonObject jsonObject:jsonObjects){
                Province province = new Province();
                province.setProvinceName(jsonObject.get("name").getAsString());
                province.setProvinceCode(jsonObject.get("id").getAsInt());
                province.save();
            }
        }
        return true;
    }


    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            ArrayList<JsonObject> jsonObjects = new Gson().fromJson(response, new TypeToken<ArrayList<JsonObject>>(){}.getType());
            for(JsonObject jsonObject:jsonObjects){
                City city = new City();
                city.setCityName(jsonObject.get("name").getAsString());
                city.setCityCode(jsonObject.get("id").getAsInt());
                city.setProvinceId(provinceId);
                city.save();
            }
        }
        return true;
    }


    public static boolean handleCountyResponse(String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            ArrayList<JsonObject> jsonObjects = new Gson().fromJson(response, new TypeToken<ArrayList<JsonObject>>(){}.getType());
            for(JsonObject jsonObject:jsonObjects){
                County county = new County();
                county.setCountyName(jsonObject.get("name").getAsString());
                county.setWeatherId(jsonObject.get("weather_id").getAsString());
                county.setCityId(cityId);
                county.save();
            }
        }
        return true;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }
}
