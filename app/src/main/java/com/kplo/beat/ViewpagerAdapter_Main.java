package com.kplo.beat;

import android.os.Messenger;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewpagerAdapter_Main extends FragmentPagerAdapter {
    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    public ViewpagerAdapter_Main(FragmentManager fm, Messenger msg) {
        super(fm);

        FirstFragment_Main fragment1 = FirstFragment_Main.newInstance("test",msg);
        SecondFragment_Main fragment2 = SecondFragment_Main.newInstance("test","test");
        arrayList.add(fragment1);
        arrayList.add(fragment2);
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
                return new SecondFragment_Main();/*
            case 2:
                return new ThirdFragment_Main();*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
