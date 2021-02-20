package com.kplo.beat;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewpagerAdapter_Main extends FragmentPagerAdapter {
    private ArrayList<Fragment> items;
    private ArrayList<String> name = new ArrayList<>();
    public ViewpagerAdapter_Main(FragmentManager fm) {
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(new FirstFragment_Main());
        items.add(new SecondFragment_Main());
        name.add("뮤직");
        name.add("활동");

    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return name.get(position);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                return new FirstFragment_Main();
            case 1:
                return new SecondFragment_Main();
        }
        return null;
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
