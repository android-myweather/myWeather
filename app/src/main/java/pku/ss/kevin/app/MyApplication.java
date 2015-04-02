package pku.ss.kevin.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pku.ss.kevin.db.CityDB;

public class MyApplication extends Application {

    private static final String TAG = "MyWeather";

    private static MyApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        loadCityDB();
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    public CityDB getCityDB() {
        return new CityDB(this);
    }

    private void loadCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "databases"
                + File.separator + CityDB.CITY_DB_NAME;
        File db = new File(path);
        if (!db.exists()) {
            Log.d(TAG, "city.db does noe exist");
            try {
                InputStream is = getAssets().open("city.db");
                db.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(db);

                int len;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
