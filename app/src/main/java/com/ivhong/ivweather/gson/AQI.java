package com.ivhong.ivweather.gson;

/**
 * Created by wangchanghong on 2018/1/3.
 */

public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
