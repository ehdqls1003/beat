package com.kplo.beat;

import android.os.Messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewpagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> arrayList = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    String feed_id;

    public ViewpagerAdapter(@NonNull FragmentManager fm, String feed_id, Messenger msg)
    {
        super(fm);
        FirstFragment fragment1 = FirstFragment.newInstance(feed_id);
        SecondFragment fragment2 = SecondFragment.newInstance(feed_id,msg);
        arrayList.add(fragment1);
        arrayList.add(fragment2);
        /*arrayList.add(new ThirdFragment());*/

        name.add("스토리");
        name.add("곡");
        /*name.add("3번 탭");*/
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return name.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        return arrayList.get(position);
    }

    @Override
    public int getCount()
    {
        return arrayList.size();
    }



}