package com.example.myapplication3;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication3.fragments.AboutFragment.AboutFragment;
import com.example.myapplication3.fragments.CpuFragment.CpuFragment;
import com.example.myapplication3.fragments.DisplayFragment.DisplayFragment;
import com.example.myapplication3.fragments.HomeFragment.HomeFragment;

public class VPAdaptor extends FragmentStateAdapter {
    int totalTabs = 4;
    final static String TAG = "VPAdapter";
    public VPAdaptor(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    // this is for fragment tabs
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Log.d(TAG,"HomeFragment created");
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                Log.d(TAG,"DispFragment created");
                DisplayFragment displayFragment = new DisplayFragment();
                return displayFragment;
            case 2:
                Log.d(TAG,"CpuFragment created");
                CpuFragment cpuFragment = new CpuFragment();
                return cpuFragment;
            case 3:
                Log.d(TAG,"AboutFragment created");
                AboutFragment aboutFragment = new AboutFragment();
                return aboutFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    public int getItemCount() {
        return totalTabs;
    }
}
