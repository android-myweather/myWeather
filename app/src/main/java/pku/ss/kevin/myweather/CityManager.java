package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pku.ss.kevin.app.MyApplication;
import pku.ss.kevin.bean.City;


public class CityManager extends Activity implements View.OnClickListener {

    private static final String TAG = "MyWeather";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_manager);

        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("name") != null) {
            TextView titleNameTv = (TextView) findViewById(R.id.title_name);
            titleNameTv.setText("当前城市："+intent.getStringExtra("name"));
        }

        ImageView backImg = (ImageView) findViewById(R.id.title_back);
        backImg.setOnClickListener(this);

        MyApplication myApp = MyApplication.getInstance();
        final List<City> list = myApp.getCityDB().getAllCity();

        int len = list.size();
        final String[] cities = new String[len];
        for (int i = 0; i < len; i++) {
            cities[i] = list.get(i).getCity();
            //cities[i] = list.get(i).getProvince() + " " + list.get(i).getCity();
        }

        ListView cityLv = (ListView) findViewById(R.id.city_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CityManager.this, android.R.layout.simple_list_item_1, cities);
        cityLv.setAdapter(adapter);
        cityLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                City city = findCityByName(list, cities[position]);
                intent.putExtra("code", city.getNumber());
                intent.putExtra("name", cities[position]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    private City findCityByName(List<City> list, String name) {
        for (City city : list) {
            if (city.getCity().equals(name))
                return city;
        }
        return null;
    }
}
