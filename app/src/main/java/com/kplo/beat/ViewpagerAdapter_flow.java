package com.kplo.beat;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ViewpagerAdapter_flow extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {
    private ArrayList<Fragment> items;
    String fed_id;
    private ArrayList<String> name = new ArrayList<>();
    public ViewpagerAdapter_flow(FragmentManager fm,String feed_id) {
        super(fm);
        fed_id = feed_id;
        items = new ArrayList<Fragment>();
        FirstFragment_flow fragment1 = FirstFragment_flow.newInstance(feed_id);
        SecondFragment_flow fragment2 = SecondFragment_flow.newInstance(feed_id);
        items.add(fragment1);
        items.add(fragment2);
        name.add("팔로워");
        name.add("팔로잉");

    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return name.get(position);
    }

    @Override
    public Fragment getItem(int position) {
    // Returning the current tabs
        switch (position) {
            case 0:
                FirstFragment_flow fragment1 = new FirstFragment_flow();
                fragment1 = FirstFragment_flow.newInstance(fed_id);
                return fragment1;
            case 1:
                SecondFragment_flow fragment2 = new SecondFragment_flow();
                fragment2 = SecondFragment_flow.newInstance(fed_id);
                return fragment2;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        Log.e("호","onPageScrolled"+position);
    }

    @Override
    public void onPageSelected(int position) {
        Log.e("호","포지션"+position);
        switch (position){
            case 0:
                Log.e("호","포지션"+position);
                break;
            case 1:
                Log.e("호","포지션"+position);
                break;
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}
