package pku.ss.kevin.util;

import pku.ss.kevin.myweather.R;

public class UIUtil {

    public static int getAirImg(String airQuality) {
        switch (airQuality) {
            case "优":
                return R.drawable.biz_plugin_weather_0_50;
            case "良":
                return R.drawable.biz_plugin_weather_51_100;
            case "轻度污染":
                return R.drawable.biz_plugin_weather_101_150;
            case "中度污染":
                return R.drawable.biz_plugin_weather_151_200;
            case "重度污染":
                return R.drawable.biz_plugin_weather_201_300;
            case "严重污染":
                return R.drawable.biz_plugin_weather_greater_300;
        }
        return R.drawable.biz_plugin_weather_0_50;
    }

    public static int getWeatherImg(String weather) {
        switch (weather) {
            case "暴雪":
                return R.drawable.biz_plugin_weather_baoxue;
            case "暴雨":
                return R.drawable.biz_plugin_weather_baoyu;
            case "大暴雨":
                return R.drawable.biz_plugin_weather_dabaoyu;
            case "大雪":
                return R.drawable.biz_plugin_weather_daxue;
            case "大雨":
                return R.drawable.biz_plugin_weather_dayu;
            case "多云":
                return R.drawable.biz_plugin_weather_duoyun;
            case "雷阵雨":
                return R.drawable.biz_plugin_weather_leizhenyu;
            case "雷阵雨冰雹":
                return R.drawable.biz_plugin_weather_leizhenyubingbao;
            case "晴":
                return R.drawable.biz_plugin_weather_qing;
            case "沙尘暴":
                return R.drawable.biz_plugin_weather_shachenbao;
            case "特大暴雨":
                return R.drawable.biz_plugin_weather_tedabaoyu;
            case "雾":
                return R.drawable.biz_plugin_weather_wu;
            case "小雪":
                return R.drawable.biz_plugin_weather_xiaoxue;
            case "小雨":
                return R.drawable.biz_plugin_weather_xiaoyu;
            case "阴":
                return R.drawable.biz_plugin_weather_yin;
            case "雨夹雪":
                return R.drawable.biz_plugin_weather_yujiaxue;
            case "阵雪":
                return R.drawable.biz_plugin_weather_zhenxue;
            case "阵雨":
                return R.drawable.biz_plugin_weather_zhenyu;
            case "中雪":
                return R.drawable.biz_plugin_weather_zhongxue;
            case "中雨":
                return R.drawable.biz_plugin_weather_zhongyu;
        }
        return R.drawable.biz_plugin_weather_qing;
    }

}
