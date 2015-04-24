package pku.ss.kevin.myweather;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity implements ViewPager.OnPageChangeListener {

    private List<View> views = new ArrayList<>();
    private ViewPager vp;
    private List<ImageView> dots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);

        initViews();

        initDots();
        ViewPagerAdapter vpAdapter = new ViewPagerAdapter(views);
        vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views.add(inflater.inflate(R.layout.forecast1, vp));
        views.add(inflater.inflate(R.layout.forecast2, vp));
    }

    private void initDots() {
        dots.add((ImageView) findViewById(R.id.p1));
        dots.add((ImageView) findViewById(R.id.p2));
        dots.add((ImageView) findViewById(R.id.p3));
        dots.add((ImageView) findViewById(R.id.p4));
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
}
