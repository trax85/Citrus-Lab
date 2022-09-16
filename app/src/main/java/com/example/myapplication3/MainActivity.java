package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication3.fragments.HomeFragment.FragmentPersistObject;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.topjohnwu.superuser.Shell;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private LinearLayout bottomSheetLayout;
    private LinearLayout linearLayout;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView textViewDash, textViewDisp, textViewCpu, textViewGpu, textViewMem,
            textViewPower, textViewProfile,textViewMisc;
    private ImageView imageViewDash, imageViewDisp, imageViewCpu, imageViewGpu ,imageViewMem,
            imageViewPower, imageViewProfile, imageViewMisc, imageViewAbout;
    private LinearLayout homeLayout, displayLayout ,cpuLayout, memLayout, gpuLayout,
            powerLayout, profileLayout, miscLayout;
    private TextView menuTitle;
    private TextView[] textViewArr;
    private ImageView[] imageViewArr;
    private LinearLayout[] linearLayoutArr;
    private boolean pageChanged = false;
    private ViewPager2 pa;
    ExecutorService service;
    private Animation animationFade;
    VPAdaptor vpAdaptor;
    private int pageToSet;
    public static CoordinatorLayout layout;
    private static final String TAG = "HomeActivity";

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
        initViews();
        initViewList();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        animationFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        //Disable swipe left and right
        pa.setUserInputEnabled(false);
        pa.setOffscreenPageLimit(1);
        //Fragment manager & bottom sheet setup
        FragmentManager fm = getSupportFragmentManager();
        vpAdaptor = new VPAdaptor(fm, getLifecycle());
        pa.setAdapter(vpAdaptor);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(178);

        FragmentPersistObject model = new ViewModelProvider(this).get(FragmentPersistObject.class);
        service = Executors.newSingleThreadExecutor();
        Intent intent = getIntent();
        pageToSet = intent.getIntExtra("page", 0);
        closeSheetListener();
        setBottomPageListeners();
        setBottomSheetCallBack();
        setUi(pageToSet);
        pa.setCurrentItem(pageToSet, false);
    }

    //This method collapses opened bottomsheet when touched outside the bottomsheet area
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                bottomSheetLayout.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    Log.d("DEBUG", "OUTSIDE");
                    imageViewAbout.setVisibility(View.INVISIBLE);
                    menuTitle.setVisibility(View.INVISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void initViews(){
        textViewDash = findViewById(R.id.textViewDash);
        textViewDisp = findViewById(R.id.textViewDisp);
        textViewCpu = findViewById(R.id.textViewCpu);
        textViewGpu = findViewById(R.id.textViewGpu);
        textViewMem = findViewById(R.id.textViewMem);
        textViewPower = findViewById(R.id.textViewPow);
        textViewProfile = findViewById(R.id.textViewProfile);
        textViewMisc = findViewById(R.id.textViewMisc);

        imageViewDash = findViewById(R.id.imageViewDash);
        imageViewDisp = findViewById(R.id.imageViewDisp);
        imageViewCpu = findViewById(R.id.imageViewCpu);
        imageViewGpu = findViewById(R.id.imageViewGpu);
        imageViewMem = findViewById(R.id.imageViewMem);
        imageViewPower = findViewById(R.id.imageViewPow);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        imageViewMisc = findViewById(R.id.imageViewMisc);
        imageViewAbout = findViewById(R.id.about);

        homeLayout = findViewById(R.id.layoutHome);
        displayLayout = findViewById(R.id.layoutDisplay);
        cpuLayout = findViewById(R.id.layoutCpu);
        gpuLayout = findViewById(R.id.layoutGpu);
        memLayout = findViewById(R.id.layoutMem);
        powerLayout = findViewById(R.id.layoutPower);
        profileLayout = findViewById(R.id.layoutProfile);
        miscLayout = findViewById(R.id.layoutMisc);

        linearLayout = findViewById(R.id.nested_scroll);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        pa = findViewById(R.id.viewPager);
        imageViewAbout.setVisibility(View.INVISIBLE);

        layout = findViewById(R.id.activity_main);
        menuTitle = findViewById(R.id.menu_title);
    }

    private void initViewList(){
        Log.d(TAG,"List initialised");
        textViewArr = new TextView[]{textViewDash, textViewDisp, textViewCpu, textViewGpu,
                textViewMem, textViewPower, textViewProfile, textViewMisc};
        imageViewArr = new ImageView[]{imageViewDash, imageViewDisp, imageViewCpu, imageViewGpu,
                imageViewMem, imageViewPower, imageViewProfile, imageViewMisc};
        linearLayoutArr = new LinearLayout[]{homeLayout, displayLayout, cpuLayout, gpuLayout,
                 memLayout, powerLayout, profileLayout, miscLayout};
    }

    private void setUi(int position){
        final String PURPLE = "#FFA175FF";
        final String WHITE = "#FFFFFFFF";
        final String GREY = "#494949";
        final String PURPLE_LIGHT = "#48D28BFF";

        for(int i = 0;i < vpAdaptor.totalTabs - 1; i++){
            if(i == position){
                textViewArr[i].setTextColor(Color.parseColor(PURPLE));
                imageViewArr[i].setColorFilter(Color.parseColor(PURPLE));
                linearLayoutArr[position].getBackground().setTint(Color.parseColor(PURPLE_LIGHT));
                imageViewAbout.setVisibility(View.INVISIBLE);
                menuTitle.setVisibility(View.INVISIBLE);
                continue;
            }

            textViewArr[i].setTextColor(Color.parseColor(WHITE));
            imageViewArr[i].setColorFilter(Color.parseColor(WHITE));
            linearLayoutArr[i].getBackground().setTint(Color.parseColor(GREY));
        }
    }

    private void setBottomPageListeners() {
        for(int i = 0; i < vpAdaptor.totalTabs - 1; i++){
            int finalI = i;
            linearLayoutArr[i].setOnClickListener(v -> {
                vpAdaptor.curTab = finalI;
                setUi(finalI);
                pageChanged = true;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            });
        }
    }

    private void setBottomSheetCallBack(){
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    if(pageChanged) {
                        service.execute(() -> {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                pa.startAnimation(animationFade);
                                pa.setCurrentItem(vpAdaptor.curTab, false);
                            });
                            pageChanged = false;
                        });
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if ( slideOffset>=0 && slideOffset<=1 ) {
                    linearLayout.setAlpha( 1f - (slideOffset * 0.5f) );
                }
            }
        });
    }

    public void closeSheetListener(){
        ImageButton menuButton = findViewById(R.id.close_menu);
        menuButton.setOnClickListener(v -> {
            if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                imageViewAbout.setVisibility(View.VISIBLE);
                menuTitle.setVisibility(View.VISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                imageViewAbout.setOnClickListener(v1 -> aboutButtonAction());
            }else{
                imageViewAbout.setVisibility(View.INVISIBLE);
                menuTitle.setVisibility(View.INVISIBLE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    public void aboutButtonAction(){
        int setCurTab = vpAdaptor.totalTabs - 1;
        setUi(setCurTab);
        vpAdaptor.curTab = setCurTab;
        pageChanged = true;
        imageViewAbout.setVisibility(View.INVISIBLE);
        menuTitle.setVisibility(View.INVISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}