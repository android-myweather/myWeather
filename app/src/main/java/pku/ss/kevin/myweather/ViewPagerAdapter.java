package pku.ss.kevin.myweather;

import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

//    private static final String TAG = "MyWeather";

    private List<View> views;

    public ViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
//        Log.d(TAG, "PagerAdapter->getCount " + views.size());
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        Log.d(TAG, "PagerAdapter->instantiateItem " + "Page" + (position + 1) + " " + views.get(position));
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        Log.d(TAG, "PagerAdapter->destroyItem " + "Page" + (position + 1) + " " + object);
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
//        Log.d(TAG, "PagerAdapter->isViewFromObject " + view + " " + object + " " + (view == object));
        return view == object;
    }
}