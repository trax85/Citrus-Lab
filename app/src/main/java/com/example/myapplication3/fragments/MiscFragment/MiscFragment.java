package com.example.myapplication3.fragments.MiscFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication3.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class MiscFragment extends Fragment {
    private final static String TAG = "MiscFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_misc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2 mViewPager;
        mViewPager = view.findViewById(R.id.viewPager2_misc);
        mViewPager.setAdapter(new MiscVPAdapter(getActivity()));

        TabLayout tabLayout = view.findViewById(R.id.tablayout);
        new TabLayoutMediator(tabLayout, mViewPager,
                (tab, position) -> {
                    tab.setText(((MiscVPAdapter)(mViewPager.getAdapter())).mFragmentNames[position]);
                }
        ).attach();
    }
}