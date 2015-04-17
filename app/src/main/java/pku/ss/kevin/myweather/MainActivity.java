package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

public class MainActivity extends Activity implements View.OnClickListener, GestureDetector.OnGestureListener {

    private static final String TAG = "MyWeather";

    private String currentCityName;
    private String currentCityCode;

    private float startX;

    private ProgressBar updateProgress;
    private ImageView updateImg;

    private ViewFlipper forecastFv;
    Animation leftInAnimation;
    Animation leftOutAnimation;
    Animation rightInAnimation;
    Animation rightOutAnimation;

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

        ImageView cityManagerImg = (ImageView) findViewById(R.id.title_city_manager);
        cityManagerImg.setOnClickListener(this);

        updateImg = (ImageView) findViewById(R.id.title_update);
        updateImg.setOnClickListener(this);
        updateProgress = (ProgressBar) findViewById(R.id.title_update_progress);

        forecastFv = (ViewFlipper) findViewById(R.id.weather_forecast);
        //动画效果
        leftInAnimation = AnimationUtils.loadAnimation(this, R.anim.left_in);
        leftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.left_out);
        rightInAnimation = AnimationUtils.loadAnimation(this, R.anim.right_in);
        rightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.right_out);

        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("city", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("code", currentCityCode);
        editor.putString("name", currentCityName);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            currentCityCode = data.getStringExtra("code");
            currentCityName = data.getStringExtra("name");
//            updateTodayWeather(currentCityCode);
            new UpdateWeatherBackground().execute(currentCityCode);
        }
    }

    long lastClick;
    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClick <= 1000) {
            // Toast.makeText(MainActivity.this, "更新中...", Toast.LENGTH_SHORT).show();
            return;
        }
        lastClick = System.currentTimeMillis();

        switch (v.getId()) {
            case R.id.title_city_manager:
                Intent intent = new Intent(MainActivity.this, CityManager.class);
                intent.putExtra("name", currentCityName);
                startActivityForResult(intent, 1);
                break;
            case R.id.title_update:
                if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                    // updateTodayWeather(currentCityCode);
                    new UpdateWeatherBackground().execute(currentCityCode);
                } else {
                    Toast.makeText(MainActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (event.getX() < startX) { // 向左滑动
                    forecastFv.setInAnimation(this, R.anim.left_in);
                    forecastFv.setOutAnimation(this, R.anim.left_out);
                    forecastFv.showNext();
                } else if (event.getX() > startX) { // 向右滑动
                    forecastFv.setInAnimation(this, R.anim.right_in);
                    forecastFv.setOutAnimation(this, R.anim.right_out);
                    forecastFv.showPrevious();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

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

        if (NetUtil.getNetworkState(MainActivity.this) == NetUtil.NETWORK_NONE) {
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
            Toast.makeText(MainActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("city", MODE_PRIVATE);
            currentCityCode = sharedPreferences.getString("code", "101010100");
            currentCityName = sharedPreferences.getString("name", "北京");
            new UpdateWeatherBackground().execute(currentCityCode);
        }
    }

    private class UpdateWeatherBackground extends AsyncTask<String, Integer, TodayWeather> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "开始更新");
            updateImg.setVisibility(View.INVISIBLE);
            updateProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected TodayWeather doInBackground(String... urls) {
            String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + urls[0];
            Log.d(TAG, address);
            TodayWeather todayWeather = null;
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
                    todayWeather = queryTodayWeather(responseStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            publishProgress();
            return todayWeather;
        }

        protected void onProgressUpdate(Integer... progress) {
//            updateImg.setVisibility(View.VISIBLE);
//            updateProgress.setVisibility(View.INVISIBLE);
        }

        protected void onPostExecute(TodayWeather result) {
            updateTodayWeatherView(result);
            Log.d(TAG, "更新完成");
            updateImg.setVisibility(View.VISIBLE);
            updateProgress.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(), "更新成功", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTodayWeatherView(TodayWeather todayWeather) {
        titleCityTv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        updateTimeTv.setText("今天" + todayWeather.getUpdateTime() + "发布");
        humidityTv.setText("湿度:" + todayWeather.getHumidity());
        pm25Tv.setText(todayWeather.getPm25());
        airImg.setImageResource(getAirImg(todayWeather.getQuality()));
        qualityTv.setText(todayWeather.getQuality());
        dateTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow() + "~" + todayWeather.getHigh());
        weatherTv.setText(todayWeather.getDayType() + "转" + todayWeather.getNightType());
        weatherImg.setImageResource(getWeatherImg(todayWeather.getDayType()));
        windTv.setText(todayWeather.getWindDirection() + " " + todayWeather.getWindStrength());
    }

    // 这个得改
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
