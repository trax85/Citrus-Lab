package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.topjohnwu.superuser.Shell;

import androidx.viewpager2.widget.ViewPager2;

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
    LinearLayout bottomSheetLayout;
    private LinearLayout linearLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView textViewDash, textViewDisp, textViewCpu, textViewAbout;
    private ImageView imageViewDash, imageViewDisp, imageViewCpu, imageViewAbout;
    private LinearLayout homeLayout, displayLayout, aboutLayout ,cpuLayout;
    private TextView[] textViewArr;
    private ImageView[] imageViewArr;
    private LinearLayout[] linearLayoutArr;
    private boolean bottomSheetState = true;
    private ViewPager2 pa;
    ExecutorService service;
    private Animation aniFade;
    int tabCount = 4;
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        initViews();
        initList();
        aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);

        //Disable swipe left and right
        pa.setUserInputEnabled(false);
        pa.setOffscreenPageLimit(2);
        //Fragment manager & bottom sheet setup
        FragmentManager fm = getSupportFragmentManager();
        VPAdaptor sa = new VPAdaptor(fm, getLifecycle());
        pa.setAdapter(sa);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(178);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetState = true;
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetState = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if ( slideOffset>=0 && slideOffset<=1 ) {
                    linearLayout.setAlpha( 1f - (slideOffset * 0.5f) );
                }
            }
        });
        ImageButton showSheet = findViewById(R.id.close_menu);
        showSheet.setOnClickListener(v -> {
            if(bottomSheetState) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        setBottomPageListeners();
        service = Executors.newSingleThreadExecutor();

        setUi(0);
        pa.setCurrentItem(0);
        // if input received anywhere on the screen other than bottomsheet
        /*Shell.getShell(shell -> {
            // The main shell is now constructed and cached
            // Exit splash screen and enter main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Shell.cmd("su");*/
    }

    //This method collapses opened bottomsheet when touched outside the bottomsheet area
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (bottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheetLayout.getGlobalVisibleRect(outRect);

                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void initViews(){
        textViewDash = findViewById(R.id.textViewDash);
        textViewDisp = findViewById(R.id.textViewDisp);
        textViewCpu = findViewById(R.id.textViewCpu);
        textViewAbout = findViewById(R.id.textViewAbout);
        imageViewDash = findViewById(R.id.imageViewDash);
        imageViewDisp = findViewById(R.id.imageViewDisp);
        imageViewCpu = findViewById(R.id.imageViewCpu);
        imageViewAbout = findViewById(R.id.imageViewAbout);

        homeLayout = findViewById(R.id.layoutHome);
        displayLayout = findViewById(R.id.layoutDisplay);
        cpuLayout = findViewById(R.id.layoutCpu);
        aboutLayout = findViewById(R.id.layoutAbout);

        linearLayout = findViewById(R.id.nested_scroll);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        pa = findViewById(R.id.viewPager);
    }

    private void initList(){
        Log.d(TAG,"List initialised");
        textViewArr = new TextView[]{textViewDash, textViewDisp, textViewCpu, textViewAbout};
        imageViewArr = new ImageView[]{imageViewDash, imageViewDisp, imageViewCpu, imageViewAbout};
        linearLayoutArr = new LinearLayout[]{homeLayout, displayLayout, cpuLayout, aboutLayout};
    }

    private void setUi(int position){
        final String PURPLE = "#FFA175FF";
        final String WHITE = "#FFFFFFFF";
        final String GREY = "#494949";
        final String PURPLE_LIGHT = "#48D28BFF";
        Log.d(TAG, "Postiton:" + position);
        for(int i = 0;i < tabCount; i++){
            if(i == position){
                textViewArr[i].setTextColor(Color.parseColor(PURPLE));
                imageViewArr[i].setColorFilter(Color.parseColor(PURPLE));
                linearLayoutArr[position].getBackground().setTint(Color.parseColor(PURPLE_LIGHT));
                continue;
            }
            textViewArr[i].setTextColor(Color.parseColor(WHITE));
            imageViewArr[i].setColorFilter(Color.parseColor(WHITE));
            linearLayoutArr[i].getBackground().setTint(Color.parseColor(GREY));
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setBottomPageListeners() {
        for(int i = 0; i < tabCount; i++){
            int finalI = i;
            linearLayoutArr[i].setOnClickListener(v -> {
                pa.setCurrentItem(finalI);
                service.execute(() -> {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        pa.startAnimation(aniFade);
                    });
                });
                setUi(finalI);
            });
        }
    }
}