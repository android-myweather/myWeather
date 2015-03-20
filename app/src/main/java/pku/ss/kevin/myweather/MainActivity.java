package pku.ss.kevin.myweather;

import android.content.SharedPreferences;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import pku.ss.kevin.util.NetUtil;


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private final static String TAG = "MainActivity";
    private ImageView updateImageView;
    private TextView weekTextView;
    private TextView temperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        //getSupportActionBar().hide();

        Time t = new Time();
        t.setToNow();
        weekTextView = (TextView) findViewById(R.id.week);
        weekTextView.setText(toWeek(t.weekDay));

        updateImageView = (ImageView) findViewById(R.id.title_update_btn);
        updateImageView.setOnClickListener(this);

        Toast.makeText(MainActivity.this, Integer.toString(NetUtil.getNetworkState(this)), Toast.LENGTH_LONG).show();

//        switch (NetUtil.getNetworkState(this)) {
//            case NetUtil.NETWORK_NONE:
//                Log.d(TAG, "NETWORK_NONE");
//                break;
//            case NetUtil.NETWORK_WIFI:
//                Log.d(TAG, "NETWORK_WIFI");
//                break;
//            case NetUtil.NETWORK_MOBILE:
//                Log.d(TAG, "NETWORK_MOBILE");
//                break;
//    }

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
                Log.d(TAG, "Main City Code: " + cityCode);
                if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
                    Log.d(TAG, "网络正常");
                    queryWeatherCode(cityCode);
                } else {
                    Log.d(TAG, "无法连接");
                    queryWeatherCode(cityCode);
                    Toast.makeText(MainActivity.this, "无法连接", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d(TAG, address);
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
                        Log.d(TAG, responseStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String toWeek(int weekday) {
        switch (weekday) {
            case 0:
                return "星期一";
            case 1:
                return "星期二";
            case 2:
                return "星期三";
            case 3:
                return "星期四";
            case 4:
                return "星期五";
            case 5:
                return "星期六";
            case 6:
                return "星期天";
            default:
                return "";
        }
    }
}
