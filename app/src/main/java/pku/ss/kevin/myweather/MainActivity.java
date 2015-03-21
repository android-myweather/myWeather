package pku.ss.kevin.myweather;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import pku.ss.kevin.util.NetUtil;


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private final static String TAG = "MainActivity";

    private static Handler handler;

    private ImageView updateIV;

    private TextView cityTV;
    private TextView updateTimeTV;
    private TextView humidityTV;
    private TextView pm25TV;
    private TextView qualityTV;
    private TextView weekTV;
    private TextView temperatureTV;
    private TextView windTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        //getSupportActionBar().hide();

        Time t = new Time();
        t.setToNow();
        weekTV = (TextView) findViewById(R.id.week);
        weekTV.setText(toWeek(t.weekDay));

        updateIV = (ImageView) findViewById(R.id.title_update_btn);
        updateIV.setOnClickListener(this);

        //cityTV = (TextView) findViewById(R.id.city);
        //cityTV.setText("11111");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_update_btn:
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code", "101010100");
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                    Log.d(TAG, "网络正常");
                    updateWeather(cityCode);
                } else {
                    Log.d(TAG, "无法连接");
                    Toast.makeText(MainActivity.this, "无法连接", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void updateWeather(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(TAG, address);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Weather weather = (Weather) msg.obj;

                        cityTV = (TextView) findViewById(R.id.city);
                        updateTimeTV = (TextView) findViewById(R.id.update_time);
                        humidityTV = (TextView) findViewById(R.id.humidity);
                        pm25TV = (TextView) findViewById(R.id.pm25);
                        qualityTV = (TextView) findViewById(R.id.quality);
                        temperatureTV = (TextView) findViewById(R.id.temperature);
                        windTV = (TextView) findViewById(R.id.wind);

                        cityTV.setText(weather.getCity());
                        updateTimeTV.setText("今天" + weather.getUpdateTime() + "发布");
                        humidityTV.setText("湿度" + weather.getHumidity());
                        pm25TV.setText(weather.getPm25());
                        qualityTV.setText(weather.getQuality());
                        temperatureTV.setText(weather.getTemperature() + "℃");
                        windTV.setText(weather.getWind());
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
                        Weather weather = queryWeather(responseStr);

                        //向主线程发送消息
                        Message msg = new Message();
                        msg.obj = weather;
                        msg.what = 0;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private Weather queryWeather(String xmlData) {
        Weather weather = new Weather();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            //Log.d(TAG, "Parse XML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city")) {
                            weather.setCity(xmlPullParser.nextText());
                        } else if (xmlPullParser.getName().equals("updatetime")) {
                            weather.setUpdateTime(xmlPullParser.nextText());
                        } else if (xmlPullParser.getName().equals("wendu")) {
                            weather.setTemperature(xmlPullParser.nextText());
                        } else if (xmlPullParser.getName().equals("shidu")) {
                            weather.setHumidity(xmlPullParser.nextText());
                        } else if (xmlPullParser.getName().equals("fengli")) {
                            weather.setWind(xmlPullParser.nextText());
                        } else if (xmlPullParser.getName().equals("pm25")) {
                            weather.setPm25(xmlPullParser.nextText());
                        } else if (xmlPullParser.getName().equals("quality")) {
                            weather.setQuality(xmlPullParser.nextText());
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
        return weather;
    }

    private String toWeek(int weekday) {
        switch (weekday) {
            case 1:
                return "星期一";
            case 2:
                return "星期二";
            case 3:
                return "星期三";
            case 4:
                return "星期四";
            case 5:
                return "星期五";
            case 6:
                return "星期六";
            case 0:
                return "星期天";
            default:
                return "";
        }
    }
}
