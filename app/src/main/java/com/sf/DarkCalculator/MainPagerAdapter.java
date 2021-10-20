package com.sf.DarkCalculator;

import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;



public class MainPagerAdapter extends PagerAdapter {
    private List<View> pageList;

    public MainPagerAdapter(List<View> pageList) {
        super();
        this.pageList = pageList;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return pageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = pageList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pageList.get(position));
    }
}
