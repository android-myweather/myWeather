package pku.ss.kevin.myweather;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import pku.ss.kevin.app.MyApplication;
import pku.ss.kevin.bean.City;


public class RecommendActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);



        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("name") != null) {
            TextView name1 = (TextView) findViewById(R.id.name1);
            name1.setText(intent.getStringExtra("name"));
        }

        ImageView backImg = (ImageView) findViewById(R.id.recommend_back);
        backImg.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recommend_back:
                finish();
                break;
        }
    }

}

