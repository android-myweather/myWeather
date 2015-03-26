package pku.ss.kevin.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pku.ss.kevin.bean.City;
import pku.ss.kevin.db.CityDB;

public class MyApplication extends Application {

    private static final String TAG = "MyAPP";

    private static Application mApplication;

    private CityDB mCityDB;
    private List<City> mCityList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "MyApplication->onCreate");
        mApplication = this;

        mCityDB = openCityDB();
        initCityList();
    }

    public static Application getInstance() {
        return mApplication;
    }

    private CityDB openCityDB() {
        String path = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName() + File.separator + "databases"
                + File.separator + CityDB.CITY_DB_NAME;
        Log.d(TAG, path);
        File db = new File(path);
        if (!db.exists()) {
            Log.d(TAG, "db does noe exist");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

    private void initCityList() {
        mCityList = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
        for (City city : mCityList) {
            String cityName = city.getCity();
            Log.d(TAG, cityName);
        }
        return true;
    }
}
