package com.example.myapplication3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.topjohnwu.superuser.Shell;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ImageButton bottomsheet;
    ViewPager2 pa;

    static {
        // Set settings before the main shell can be created
        Shell.enableVerboseLogging = BuildConfig.DEBUG;
        Shell.setDefaultBuilder(Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pa = findViewById(R.id.viewPager);
        FragmentManager fm = getSupportFragmentManager();
        VPAdaptor sa = new VPAdaptor(fm, getLifecycle());
        pa.setAdapter(sa);
        bottomsheet = findViewById(R.id.menu);
        bottomsheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        /*tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pa.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/
        pa.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        /*Shell.getShell(shell -> {
            // The main shell is now constructed and cached
            // Exit splash screen and enter main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Shell.cmd("su");*/
    }
    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout homelayout = dialog.findViewById(R.id.layoutHome);
        LinearLayout displaylayout = dialog.findViewById(R.id.layoutDisplay);
        LinearLayout blanklayout = dialog.findViewById(R.id.layoutEdit3);
        ImageButton imageButton = dialog.findViewById(R.id.close_menu);

        imageButton.setOnClickListener(v -> dialog.dismiss());
        homelayout.setOnClickListener(v -> {
            pa.setCurrentItem(0);
            homelayout.setBackgroundColor(Color.parseColor("#00E676"));
            dialog.dismiss();
        });
        displaylayout.setOnClickListener(v -> {
            pa.setCurrentItem(1);
            displaylayout.setBackgroundColor(Color.parseColor("#00E676"));
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}