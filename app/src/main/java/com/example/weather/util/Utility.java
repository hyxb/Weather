package com.example.weather.util;

import android.text.TextUtils;
import android.util.Log;
import com.example.weather.db.City;
import com.example.weather.db.County;
import com.example.weather.db.Province;
import com.example.weather.gson.Weather;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    public static boolean handleProvinceReponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);

                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvincename(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                LogUtil.logInfo("HandleResponse","保存省级名称至数据库");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static  boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);

                LogUtil.logDebug("接收的城市JSON"+allCities.toString());

                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.setCityCode(cityObject.getInt("id"));
                    city.save();
                    LogUtil.logDebug(city.getCityName()+"SAVE!");
                }
                LogUtil.logInfo("HandleResponse","保存市级名称至数据库");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);

                LogUtil.logDebug("接收的县级地名JSON"+allCounties.toString());

                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.save();
                    LogUtil.logDebug(county.getCountyName()+"SAVE!");
                }
                LogUtil.logInfo("HandleResponse","保存县级名称至数据库");
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
