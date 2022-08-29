package com.example.myapplication3.fragments.MiscFragment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication3.fragments.MiscFragment.TempFragment.TempFragment;

public class MiscVPAdapter extends FragmentStateAdapter {
    final private String TAG = "MiscVPA";
    private final Fragment[] mFragments = new Fragment[] {
            new NetworkFragment(),
            new TempFragment(),
    };
    public final String[] mFragmentNames = new String[] {
            "Network",
            "Temperature"
    };

    public MiscVPAdapter(FragmentActivity fa){
        super(fa);
    }

    @Override
    public int getItemCount() {
        return mFragments.length;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments[position];
    }
}
