package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pku.ss.kevin.app.MyApplication;
import pku.ss.kevin.bean.City;
import pku.ss.kevin.bean.MyFilter;
import pku.ss.kevin.db.CityDB;
import pku.ss.kevin.util.LogUtil;
import pku.ss.kevin.util.NetUtil;

public class FirstActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher {

    private List<City> cityList = new ArrayList<>();
    private  ListView cityListView;
    private MyFilter filterAdapter;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.first);

        ImageView locateImg = (ImageView) findViewById(R.id.f_title_location);
        locateImg.setOnClickListener(this);

        mLocationClient = new LocationClient(MyApplication.getInstance());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数

        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        mLocationClient.setLocOption(option);

        initView();
    }

    private void initView(){
        cityListView = (ListView) findViewById(R.id.f_city_list);
        MyApplication myApp = (MyApplication) this.getApplicationContext();
        cityListView.setOnItemClickListener(this);
        EditText searchEdit = (EditText) findViewById(R.id.f_search_edit);
        searchEdit.addTextChangedListener(this);
        cityList = myApp.getCityDB().getAllCity();
//        for (City city : myApp.getCityList()){
//            cityList.add(city);
//        }
        filterAdapter = new MyFilter(this,cityList);
        cityListView.setAdapter(filterAdapter);
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.f_title_location:
                mLocationClient.start();
                mLocationClient.requestLocation();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String cityNumber = filterAdapter.getItem(position).getNumber();
        String cityName = filterAdapter.getItem(position).getCity();
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("code",cityNumber);
        intent.putExtra("name", cityName);
        Log.d(LogUtil.TAG, cityName);
        Log.d(LogUtil.TAG, cityNumber);
        startActivity(intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        String aa = s.toString();
        if (aa.length() > 0){
            List<City> resCityList = new ArrayList<>();
            Pattern p;
            List<City> firstCityList = new ArrayList<>();   //����ƥ��
            List<City> secondCityList = new ArrayList<>();  //�ڶ���ƥ��
            List<City> thirdCityList = new ArrayList<>();   //������ƥ��
            List<City> forthCityList = new ArrayList<>();   //�����ֺ�ƥ��
            for(int i=0;i<cityList.size();i++){
                City city = cityList.get(i);
                p = Pattern.compile(aa);
                Matcher matcher = p.matcher(city.getCity());
                if(matcher.find()) {
                    p = Pattern.compile("^" + aa);
                    matcher = p.matcher(city.getCity());
                    if (matcher.find()) {
                        firstCityList.add(city);
                    } else {
                        p = Pattern.compile("^[\u4e00-\u9fa5]" + aa);
                        matcher = p.matcher(city.getCity());
                        if (matcher.find()) {
                            secondCityList.add(city);
                        } else {
                            p = Pattern.compile("^[\u4e00-\u9fa5]{2}" + aa);
                            matcher = p.matcher(city.getCity());
                            if (matcher.find()) {
                                thirdCityList.add(city);
                            } else {
                                forthCityList.add(city);
                            }
                        }
                    }
                }
            }//endfor
            resCityList.addAll(firstCityList);
            resCityList.addAll(secondCityList);
            resCityList.addAll(thirdCityList);
            resCityList.addAll(forthCityList);
            filterAdapter = new MyFilter(this, resCityList);
            cityListView.setAdapter(filterAdapter);
        }
        else {
            filterAdapter = new MyFilter(this, cityList);
            cityListView.setAdapter(filterAdapter);
        }


    }

    @Override
    public void afterTextChanged(Editable s) {
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
                Intent intent = new Intent(FirstActivity.this,MainActivity.class);
                intent.putExtra("code",new CityDB(FirstActivity.this).getCityCode((city)));
                intent.putExtra("name", city);
                Log.d(LogUtil.TAG, city);
                startActivity(intent);
                finish();
            }
            Log.d(LogUtil.TAG, sb.toString());
        }
    }
}
