package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import pku.ss.kevin.app.MyApplication;
import pku.ss.kevin.bean.TodayWeather;
import pku.ss.kevin.bean.Weather;
import pku.ss.kevin.bean.WeatherInfo;
import pku.ss.kevin.db.CityDB;
import pku.ss.kevin.util.LogUtil;
import pku.ss.kevin.util.NetUtil;
import pku.ss.kevin.util.ParseUtil;
import pku.ss.kevin.util.UIUtil;

public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private String currentCityName;
    private String currentCityCode;
    private String zhishu = "";

    private long lastClick;

    private ProgressBar updateProgress;
    private ImageView updateImg;
    private ImageView locateImg;

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
    private TextView todayTemperatureTv;

    private ViewPager forecastVp;
    private List<View> forecastViews;
    private List<ImageView> dots;

    private TextView date_1;
    private ImageView weather_image_1;
    private TextView temperature_1;
    private TextView weather_1;
    private TextView wind_1;
    private TextView date0;
    private ImageView weather_image0;
    private TextView temperature0;
    private TextView weather0;
    private TextView wind0;
    private TextView date1;
    private ImageView weather_image1;
    private TextView temperature1;
    private TextView weather1;
    private TextView wind1;
    private TextView date2;
    private ImageView weather_image2;
    private TextView temperature2;
    private TextView weather2;
    private TextView wind2;
    private TextView date3;
    private ImageView weather_image3;
    private TextView temperature3;
    private TextView weather3;
    private TextView wind3;
    private TextView date4;
    private ImageView weather_image4;
    private TextView temperature4;
    private TextView weather4;
    private TextView wind4;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        SharedPreferences preferences = getSharedPreferences("phone", Context.MODE_PRIVATE);
        //判断是不是首次运行
        if (preferences.getBoolean("firststart", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            //将登录标志位设置为false，下次登录时不在显示首次登录界面
            editor.putBoolean("firststart", false);
            editor.commit();
            Intent intent = new Intent();
//            Intent intent = new Intent("pku.ss.kevin.myweather.FirstActivity");
            intent.setClass(getApplicationContext(),FirstActivity.class);
            startActivity(intent);
            finish();
        }

        ImageView cityManagerImg = (ImageView) findViewById(R.id.title_city_manager);
        cityManagerImg.setOnClickListener(this);
        updateImg = (ImageView) findViewById(R.id.title_update);
        updateImg.setOnClickListener(this);
        updateProgress = (ProgressBar) findViewById(R.id.title_update_progress);
        ImageView shareImg = (ImageView) findViewById(R.id.title_share);
        shareImg.setOnClickListener(this);
        locateImg = (ImageView) findViewById(R.id.title_location);
        locateImg.setOnClickListener(this);
        TextView recommend_button = (TextView) findViewById(R.id.recommend_button);
        recommend_button.setOnClickListener(this);
        todayTemperatureTv = (TextView) findViewById(R.id.today_temperature);

        initForecastViews();
        initDots();
        forecastVp = (ViewPager) findViewById(R.id.forecast);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(forecastViews);
        forecastVp.setAdapter(vpAdapter);
        forecastVp.setOnPageChangeListener(this);

        initView();
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("code") != null && intent.getStringExtra("name") != null) {
            currentCityCode = intent.getStringExtra("code");
            currentCityName = intent.getStringExtra("name");
            new UpdateWeatherBackground().execute(currentCityCode);
        }

        mLocationClient = new LocationClient(MyApplication.getInstance());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);
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
            new UpdateWeatherBackground().execute(currentCityCode);
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - lastClick <= 1000) {
            // Toast.makeText(MainActivity.this, "更新中...", Toast.LENGTH_SHORT).show();
            return;
        }
        lastClick = System.currentTimeMillis();

        switch (v.getId()) {
            case R.id.title_location:
                mLocationClient.start();
                mLocationClient.requestLocation();
                break;
            case R.id.title_city_manager:
//                Intent intent = new Intent(MainActivity.this, CityActivity.class);
                Intent intent = new Intent(MainActivity.this, SelectCity.class);
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
            case R.id.title_share:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "天气预报");
                intent.putExtra(Intent.EXTRA_TEXT, "今天（" + dateTv.getText() + "）" + cityTv.getText() + "天气："
                        + weatherTv.getText() + " " + temperatureTv.getText());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
                break;
            case R.id.recommend_button:
                Intent intent1 = new Intent(MainActivity.this, RecommendActivity.class);
                intent1.putExtra("name", zhishu);
                startActivityForResult(intent1, 1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dots.size(); i++) {
            if (i == position) dots.get(i).setImageResource(R.drawable.page_indicator_focused);
            else dots.get(i).setImageResource(R.drawable.page_indicator_unfocused);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class UpdateWeatherBackground extends AsyncTask<String, Integer, WeatherInfo> {

        @Override
        protected void onPreExecute() {
            Log.d(LogUtil.TAG, "开始更新");
            updateImg.setVisibility(View.INVISIBLE);
            updateProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected WeatherInfo doInBackground(String... urls) {
            String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + urls[0];
            Log.d(LogUtil.TAG, address);
            WeatherInfo weather = null;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(address);
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);//连接时间
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);//数据传输时间
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
                    weather = ParseUtil.queryWeatherInfo(responseStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(MainActivity.this, "网络超时", Toast.LENGTH_SHORT).show();
            }
//            publishProgress();
            return weather;
        }

        protected void onProgressUpdate(Integer... progress) {
//            updateImg.setVisibility(View.VISIBLE);
//            updateProgress.setVisibility(View.INVISIBLE);
        }

        protected void onPostExecute(WeatherInfo result) {
            if (result != null) {
                updateWeatherView(result);
                Log.d(LogUtil.TAG, "更新完成");
                updateImg.setVisibility(View.VISIBLE);
                updateProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(getBaseContext(), "更新成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            StringBuilder sb = new StringBuilder(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());

                // 刷新界面
                String city = location.getCity();
                city = city.substring(0, city.length() - 1);
                if (NetUtil.getNetworkState(MainActivity.this) != NetUtil.NETWORK_NONE) {
                    new UpdateWeatherBackground().execute(new CityDB(MainActivity.this).getCityCode((city)));
                } else {
                    Toast.makeText(MainActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
                }
            }
            Log.d(LogUtil.TAG, sb.toString());
        }
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

        // 这段代码太可怕了
        date_1 = (TextView) forecastViews.get(0).findViewById(R.id.date_1);
        weather_image_1 = (ImageView) forecastViews.get(0).findViewById(R.id.weather_image_1);
        temperature_1 = (TextView) forecastViews.get(0).findViewById(R.id.temperature_1);
        weather_1 = (TextView) forecastViews.get(0).findViewById(R.id.weather_1);
        wind_1 = (TextView) forecastViews.get(0).findViewById(R.id.wind_1);
        date0 = (TextView) forecastViews.get(0).findViewById(R.id.date0);
        weather_image0 = (ImageView) forecastViews.get(0).findViewById(R.id.weather_image0);
        temperature0 = (TextView) forecastViews.get(0).findViewById(R.id.temperature0);
        weather0 = (TextView) forecastViews.get(0).findViewById(R.id.weather0);
        wind0 = (TextView) forecastViews.get(0).findViewById(R.id.wind0);
        date1 = (TextView) forecastViews.get(0).findViewById(R.id.date1);
        weather_image1 = (ImageView) forecastViews.get(0).findViewById(R.id.weather_image1);
        temperature1 = (TextView) forecastViews.get(0).findViewById(R.id.temperature1);
        weather1 = (TextView) forecastViews.get(0).findViewById(R.id.weather1);
        wind1 = (TextView) forecastViews.get(0).findViewById(R.id.wind1);
        date2 = (TextView) forecastViews.get(1).findViewById(R.id.date2);
        weather_image2 = (ImageView) forecastViews.get(1).findViewById(R.id.weather_image2);
        temperature2 = (TextView) forecastViews.get(1).findViewById(R.id.temperature2);
        weather2 = (TextView) forecastViews.get(1).findViewById(R.id.weather2);
        wind2 = (TextView) forecastViews.get(1).findViewById(R.id.wind2);
        date3 = (TextView) forecastViews.get(1).findViewById(R.id.date3);
        weather_image3 = (ImageView) forecastViews.get(1).findViewById(R.id.weather_image3);
        temperature3 = (TextView) forecastViews.get(1).findViewById(R.id.temperature3);
        weather3 = (TextView) forecastViews.get(1).findViewById(R.id.weather3);
        wind3 = (TextView) forecastViews.get(1).findViewById(R.id.wind3);
        date4 = (TextView) forecastViews.get(1).findViewById(R.id.date4);
        weather_image4 = (ImageView) forecastViews.get(1).findViewById(R.id.weather_image4);
        temperature4 = (TextView) forecastViews.get(1).findViewById(R.id.temperature4);
        weather4 = (TextView) forecastViews.get(1).findViewById(R.id.weather4);
        wind4 = (TextView) forecastViews.get(1).findViewById(R.id.wind4);

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
            todayTemperatureTv.setText("N/A");
            Toast.makeText(MainActivity.this, "无法连接网络", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("city", MODE_PRIVATE);
            currentCityCode = sharedPreferences.getString("code", "101010100");
            currentCityName = sharedPreferences.getString("name", "北京");
            new UpdateWeatherBackground().execute(currentCityCode);
        }
    }

    private void initForecastViews() {
        forecastViews = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        forecastViews.add(inflater.inflate(R.layout.forecast1, forecastVp));
        forecastViews.add(inflater.inflate(R.layout.forecast2, forecastVp));
    }

    private void initDots() {
        dots = new ArrayList<>();
        dots.add((ImageView) findViewById(R.id.indicator1));
        dots.add((ImageView) findViewById(R.id.indicator2));
    }

    private void updateWeatherView(WeatherInfo weather) {
        updateTodayWeatherView(weather);
        updateWeatherForecastView(weather);
    }

    private void updateTodayWeatherView(TodayWeather todayWeather) {
        titleCityTv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        updateTimeTv.setText("今天" + todayWeather.getUpdateTime() + "发布");
        humidityTv.setText("湿度:" + todayWeather.getHumidity());
        pm25Tv.setText(todayWeather.getPm25());
        airImg.setImageResource(UIUtil.getAirImg(todayWeather.getQuality()));
        qualityTv.setText(todayWeather.getQuality());
        dateTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getLow() + "~" + todayWeather.getHigh());
        if (todayWeather.getDayType().equals(todayWeather.getNightType()))
            weatherTv.setText(todayWeather.getDayType());
        else
            weatherTv.setText(todayWeather.getDayType() + "转" + todayWeather.getNightType());
        weatherImg.setImageResource(UIUtil.getWeatherImg(todayWeather.getDayType()));
        windTv.setText(todayWeather.getWindDirection() + " " + todayWeather.getWindStrength());
        todayTemperatureTv.setText("温度：" + todayWeather.getTemperature() + "℃");
        int j = 0;
        for (j = 0; j < 4; j++) {
            zhishu = zhishu + todayWeather.getName(j) + ":" + todayWeather.getValue(j) + "\n" + todayWeather.getDetail(j) + "\n\n";
        }
    }

    private void updateWeatherForecastView(WeatherInfo weather) {
        //*********************假6日天气信息********************
//        date_1.setText("22日星期一");
//        weather_image_1.setImageResource(R.drawable.biz_plugin_weather_duoyun);
//        temperature_1.setText("22℃~31℃");
//        weather_1.setText("多云转雷阵雨");
//        wind_1.setText("无持续风向1级");
//
//        date0.setText(weather.getDate());
//        weather_image0.setImageResource(UIUtil.getWeatherImg(weather.getDayType()));
//        temperature0.setText(weather.getLow() + "~" + weather.getHigh());
//        if (weather.getDayType().equals(weather.getNightType()))
//            weather0.setText(weather.getDayType());
//        else
//            weather0.setText(weather.getDayType() + "转" + weather.getNightType());
//        wind0.setText(weather.getWindDirection() + " " + weather.getWindStrength());
//
//        date1.setText("24日星期三");
//        weather_image1.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
//        temperature1.setText("22℃~30℃");
//        weather1.setText("雷阵雨");
//        wind1.setText("无持续风向1级");
//
//        date2.setText("25日星期四");
//        weather_image2.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
//        temperature2.setText("22℃~31℃");
//        weather2.setText("雷阵雨转阴");
//        wind2.setText("无持续风向1级");
//
//        date3.setText("26日星期五");
//        weather_image3.setImageResource(R.drawable.biz_plugin_weather_yin);
//        temperature3.setText("22℃~32℃");
//        weather3.setText("阴");
//        wind3.setText("无持续风向1级");
//
//        date4.setText("27日星期六");
//        weather_image4.setImageResource(R.drawable.biz_plugin_weather_qing);
//        temperature4.setText("22℃~33℃");
//        weather4.setText("晴");
//        wind4.setText("无持续风向1级");

        //***************真实6日天气信息（需完善）************************
        Weather yesterday = weather.getYesterday();
        date_1.setText(yesterday.getDate());
        weather_image_1.setImageResource(UIUtil.getWeatherImg(yesterday.getDayType()));
        temperature_1.setText(yesterday.getLow() + "~" + yesterday.getHigh());
        if (yesterday.getDayType().equals(yesterday.getNightType()))
            weather_1.setText(yesterday.getDayType());
        else
            weather_1.setText(yesterday.getDayType() + "转" + yesterday.getNightType());
//        wind_1.setText(yesterday.getWindDirection() + " " + yesterday.getWindStrength());
        wind_1.setText(yesterday.getWindDirection());

        date0.setText(weather.getDate());
        weather_image0.setImageResource(UIUtil.getWeatherImg(weather.getDayType()));
        temperature0.setText(weather.getLow() + "~" + weather.getHigh());
        if (weather.getDayType().equals(weather.getNightType()))
            weather0.setText(weather.getDayType());
        else
            weather0.setText(weather.getDayType() + "转" + weather.getNightType());
//        wind0.setText(weather.getWindDirection() + " " + weather.getWindStrength());
        wind0.setText(weather.getWindDirection());

        Weather day1 = weather.getDay1();
        date1.setText(day1.getDate());
        weather_image1.setImageResource(UIUtil.getWeatherImg(day1.getDayType()));
        temperature1.setText(day1.getLow() + "~" + day1.getHigh());
        if (day1.getDayType().equals(day1.getNightType()))
            weather1.setText(day1.getDayType());
        else
            weather1.setText(day1.getDayType() + "转" + day1.getNightType());
//        wind1.setText(day1.getWindDirection() + " " + day1.getWindStrength());
        wind1.setText(weather.getWindDirection());

        Weather day2 = weather.getDay2();
        date2.setText(day2.getDate());
        weather_image2.setImageResource(UIUtil.getWeatherImg(day2.getDayType()));
        temperature2.setText(day2.getLow() + "~" + day2.getHigh());
        if (day2.getDayType().equals(day2.getNightType()))
            weather2.setText(day2.getDayType());
        else
            weather2.setText(day2.getDayType() + "转" + day2.getNightType());
//        wind2.setText(day2.getWindDirection() + " " + day2.getWindStrength());
        wind2.setText(weather.getWindDirection());

        Weather day3 = weather.getDay3();
        date3.setText(day3.getDate());
        weather_image3.setImageResource(UIUtil.getWeatherImg(day3.getDayType()));
        temperature3.setText(day3.getLow() + "~" + day3.getHigh());
        if (day3.getDayType().equals(day3.getNightType()))
            weather3.setText(day3.getDayType());
        else
            weather3.setText(day3.getDayType() + "转" + day3.getNightType());
//        wind3.setText(day3.getWindDirection() + " " + day3.getWindStrength());
        wind3.setText(weather.getWindDirection());

        Weather day4 = weather.getDay4();
        date4.setText(day4.getDate());
        weather_image4.setImageResource(UIUtil.getWeatherImg(day4.getDayType()));
        temperature4.setText(day4.getLow() + "~" + day4.getHigh());
        if (day4.getDayType().equals(day4.getNightType()))
            weather4.setText(day4.getDayType());
        else
            weather4.setText(day4.getDayType() + "转" + day4.getNightType());
//        wind4.setText(day4.getWindDirection() + " " + day4.getWindStrength());
        wind4.setText(weather.getWindDirection());
    }

}
