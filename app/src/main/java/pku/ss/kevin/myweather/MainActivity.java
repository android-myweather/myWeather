package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.zip.GZIPInputStream;

import pku.ss.kevin.bean.TodayWeather;
import pku.ss.kevin.util.NetUtil;

public class MainActivity extends Activity {

    private static final String TAG = "MyWeather";

    private static final int UPDATE_TODAY_WEATHER = 1;

    private static Handler handler;
    private String currentCity;

    private TextView titleCityTv;
    private TextView cityTv;
    private TextView updateTimeTv;
    private TextView humidityTv;
    private ImageView airImg;
    private TextView pm25Tv;
    private TextView qualityTv;
    private ImageView weatherImg;
    private TextView dateTv;
    private TextView temperatureTv;
    private TextView weatherTv;
    private TextView windTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        initView();

        currentCity = "101010100";
        ImageView cityManagerImg = (ImageView) findViewById(R.id.title_city_manager);
        cityManagerImg.setOnClickListener(listener);

        ImageView updateImg = (ImageView) findViewById(R.id.title_update);
        updateImg.setOnClickListener(listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            currentCity = data.getStringExtra("city");
            Log.d(TAG, currentCity);
            updateTodayWeather(currentCity);
        }
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.title_city_manager:
                    Intent intent = new Intent(MainActivity.this, CityManager.class);
                    startActivityForResult(intent, 1);
                    break;
                case R.id.title_update:
                    if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                        updateTodayWeather(currentCity);
                    } else {
                        Toast.makeText(MainActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    private void initView() {
        titleCityTv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        updateTimeTv = (TextView) findViewById(R.id.update_time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        airImg = (ImageView) findViewById(R.id.air_image);
        pm25Tv = (TextView) findViewById(R.id.pm25);
        qualityTv = (TextView) findViewById(R.id.quality);
        weatherImg = (ImageView) findViewById(R.id.weather_image);
        dateTv = (TextView) findViewById(R.id.date);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        weatherTv = (TextView) findViewById(R.id.weather);
        windTv = (TextView) findViewById(R.id.wind);

        titleCityTv.setText("N/A");
        cityTv.setText("N/A");
        updateTimeTv.setText("N/A");
        humidityTv.setText("N/A");
        pm25Tv.setText("N/A");
        qualityTv.setText("N/A");
        dateTv.setText("N/A");
        temperatureTv.setText("N/A");
        weatherTv.setText("N/A");
        windTv.setText("N/A");
    }

    private void updateTodayWeather(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(TAG, address);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_TODAY_WEATHER:
                        updateTodayWeatherView((TodayWeather) msg.obj);
                        Toast.makeText(MainActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(address);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        InputStream responseStream = entity.getContent();

                        responseStream = new GZIPInputStream(responseStream);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));

                        StringBuilder response = new StringBuilder();
                        String str;
                        while ((str = reader.readLine()) != null) {
                            response.append(str);
                        }
                        String responseStr = response.toString();

                        //解析XML获取天气状态
                        TodayWeather todayWeather = queryTodayWeather(responseStr);
                        //Log.d(TAG, todayWeather.toString());

                        //向主线程发送消息
                        if (todayWeather != null) {
                            Message msg = new Message();
                            msg.obj = todayWeather;
                            msg.what = UPDATE_TODAY_WEATHER;
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateTodayWeatherView(TodayWeather todayWeather) {
        titleCityTv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        updateTimeTv.setText("今天" + todayWeather.getUpdateTime() + "发布");
        humidityTv.setText("湿度:" + todayWeather.getHumidity());
        pm25Tv.setText(todayWeather.getPm25());

        Log.d(TAG, Integer.toString(getAirImg(todayWeather.getQuality())));
        airImg.setImageResource(getAirImg(todayWeather.getQuality()));

        qualityTv.setText(todayWeather.getQuality());
        dateTv.setText(todayWeather.getDate().substring(2));
        temperatureTv.setText(todayWeather.getLow() + "~" + todayWeather.getHigh());
        weatherTv.setText(todayWeather.getDayType() + "转" + todayWeather.getNightType());
        weatherImg.setImageResource(getWeatherImg(todayWeather.getDayType()));
        windTv.setText(todayWeather.getWindDirection() + " " + todayWeather.getWindStrength());
    }

    private TodayWeather queryTodayWeather(String xmlData) {
        TodayWeather todayWeather = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            int fengliCount = 0;
            int fengxiangCount = 0;
            int dateCount = 0;
            int lowCount = 0;
            int highCount = 0;
            int typeCount = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = xmlPullParser.getName();
                        switch (tag) {
                            case "resp":
                                todayWeather = new TodayWeather();
                                break;
                            case "city":
                                todayWeather.setCity(xmlPullParser.nextText());
                                break;
                            case "updatetime":
                                todayWeather.setUpdateTime(xmlPullParser.nextText());
                                break;
                            case "wendu":
                                todayWeather.setTemperature(xmlPullParser.nextText());
                                break;
                            case "fengli":
                                if (fengliCount == 0)
                                    todayWeather.setWindStrength(xmlPullParser.nextText());
                                fengliCount++;
                                break;
                            case "shidu":
                                todayWeather.setHumidity(xmlPullParser.nextText());
                                break;
                            case "fengxiang":
                                if (fengxiangCount == 0)
                                    todayWeather.setWindDirection(xmlPullParser.nextText());
                                fengxiangCount++;
                                break;
                            case "pm25":
                                todayWeather.setPm25(xmlPullParser.nextText());
                                break;
                            case "quality":
                                todayWeather.setQuality(xmlPullParser.nextText());
                                break;
                            case "date":
                                if (dateCount == 0)
                                    todayWeather.setDate(xmlPullParser.nextText());
                                dateCount++;
                                break;
                            case "low":
                                if (lowCount == 0)
                                    todayWeather.setLow(xmlPullParser.nextText().substring(3));
                                lowCount++;
                                break;
                            case "high":
                                if (highCount == 0)
                                    todayWeather.setHigh(xmlPullParser.nextText().substring(3));
                                highCount++;
                                break;
                            case "type":
                                if (typeCount == 0)
                                    todayWeather.setDayType(xmlPullParser.nextText());
                                else if (typeCount == 1)
                                    todayWeather.setNightType(xmlPullParser.nextText());
                                typeCount++;
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    private int getAirImg(String airQuality) {
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

    private int getWeatherImg(String weather) {
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
