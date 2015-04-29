package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import pku.ss.kevin.bean.TodayWeather;
import pku.ss.kevin.util.NetUtil;
import pku.ss.kevin.util.ParseUtil;
import pku.ss.kevin.util.UIUtil;

public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "MyWeather";

    private String currentCityName;
    private String currentCityCode;

    private long lastClick;

    private ProgressBar updateProgress;
    private ImageView updateImg;
    private ImageView shareImg;

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

    private ViewPager forecastVp;
    private List<View> forecastViews;
    private List<ImageView> dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        ImageView cityManagerImg = (ImageView) findViewById(R.id.title_city_manager);
        cityManagerImg.setOnClickListener(this);

        updateImg = (ImageView) findViewById(R.id.title_update);
        updateImg.setOnClickListener(this);
        updateProgress = (ProgressBar) findViewById(R.id.title_update_progress);

        shareImg = (ImageView) findViewById(R.id.title_share);
        shareImg.setOnClickListener(this);

        initForecastViews();
        initDots();
        forecastVp = (ViewPager) findViewById(R.id.forecast);
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(forecastViews);
        forecastVp.setAdapter(vpAdapter);
        forecastVp.setOnPageChangeListener(this);

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
            case R.id.title_share:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "天气预报");
                intent.putExtra(Intent.EXTRA_TEXT, "今天（" + dateTv.getText() + "）" + cityTv.getText() + "天气："
                        + weatherTv.getText() + " " + temperatureTv.getText());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getTitle()));
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
                    todayWeather = ParseUtil.queryTodayWeather(responseStr);
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

    private void initForecastViews() {
        forecastViews = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        forecastViews.add(inflater.inflate(R.layout.forecast1, forecastVp));
        forecastViews.add(inflater.inflate(R.layout.forecast1, forecastVp));
    }

    private void initDots() {
        dots = new ArrayList<>();
        dots.add((ImageView) findViewById(R.id.indicator1));
        dots.add((ImageView) findViewById(R.id.indicator2));
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
        weatherTv.setText(todayWeather.getDayType() + "转" + todayWeather.getNightType());
        weatherImg.setImageResource(UIUtil.getWeatherImg(todayWeather.getDayType()));
        windTv.setText(todayWeather.getWindDirection() + " " + todayWeather.getWindStrength());
    }

}
