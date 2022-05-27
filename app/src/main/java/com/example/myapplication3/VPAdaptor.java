package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication3.fragments.DisplayFragment.DisplayFragment;
import com.example.myapplication3.fragments.HomeFragment.HomeFragment;

public class VPAdaptor extends FragmentStateAdapter {
    int totalTabs = 2;

    public VPAdaptor(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                DisplayFragment displayFragment = new DisplayFragment();
                return displayFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    public int getItemCount() {
        return totalTabs;
    }
}
