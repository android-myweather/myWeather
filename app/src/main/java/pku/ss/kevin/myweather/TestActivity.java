package pku.ss.kevin.myweather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends Activity implements View.OnClickListener {

    protected static final String ACTION = "pku.ss.kevin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        Button btn = (Button) findViewById(R.id.broadcast);
        btn.setOnClickListener(this);
        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        Button unregister = (Button) findViewById(R.id.unregister);
        unregister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.broadcast:
                break;
            case R.id.register:
                break;
            case R.id.unregister:
                break;
        }
    }
}
