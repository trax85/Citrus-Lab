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
import com.example.myapplication3.fragments.GpuFragment.GpuFragment;
import com.example.myapplication3.fragments.HomeFragment.HomeFragment;
import com.example.myapplication3.fragments.MemoryFragment.MemoryFragment;
import com.example.myapplication3.fragments.ProfileFragment.ProfileFragment;

public class VPAdaptor extends FragmentStateAdapter {
    int totalTabs = 7;
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
                return new HomeFragment();
            case 1:
                Log.d(TAG,"DispFragment created");
                return new DisplayFragment();
            case 2:
                Log.d(TAG,"CpuFragment created");
                return new CpuFragment();
            case 3:
                Log.d(TAG, "GpuFragment created");
                return new GpuFragment();
            case 4:
                Log.d(TAG, "MemFragment created");
                return new MemoryFragment();
            case 5:
                Log.d(TAG,"ProfileFragment created");
                return new ProfileFragment();
            case 6:
                Log.d(TAG,"AboutFragment created");
                return new AboutFragment();
            default:
                return null;
        }
    }
    // this counts total number of tabs
    public int getItemCount() {
        return totalTabs;
    }
}
