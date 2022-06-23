package com.example.mymusicapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class pageAdapter extends FragmentPagerAdapter {
    private int counter;

    public pageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
       this.counter = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new frag1();
            case 1: return new frag2();
            case 2: return new frag3();
            default:return null;
        }

    }

    @Override
    public int getCount() {
        return counter;
    }
}
