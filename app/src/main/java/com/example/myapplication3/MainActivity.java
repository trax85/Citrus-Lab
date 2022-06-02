package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.topjohnwu.superuser.Shell;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinearLayout bottomSheetLayout;
    private LinearLayout linearLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView textViewDash, textViewDisp, textViewAbout;
    private ImageView imageViewDash, imageViewDisp, imageViewAbout;
    private LinearLayout homeLayout, displayLayout, aboutLayout ,cpuLayout;
    private List<TextView> textViewList;
    private List<ImageView> imageViewList;

    private boolean bottomSheetState = true;
    private ViewPager2 pa;
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

        textViewDash = findViewById(R.id.textViewDash);
        textViewDisp = findViewById(R.id.textViewDisp);
        textViewAbout = findViewById(R.id.textViewAbout);
        imageViewDash = findViewById(R.id.imageViewDash);
        imageViewDisp = findViewById(R.id.imageViewDisp);
        imageViewAbout = findViewById(R.id.imageViewAbout);
        textViewList = new ArrayList<>();
        imageViewList = new ArrayList<>();
        initList();

        homeLayout = findViewById(R.id.layoutHome);
        displayLayout = findViewById(R.id.layoutDisplay);
        cpuLayout = findViewById(R.id.layoutCpu);
        aboutLayout = findViewById(R.id.layoutAbout);

        linearLayout = findViewById(R.id.nested_scroll);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        pa = findViewById(R.id.viewPager);
        //Disable swipe left and right
        pa.setUserInputEnabled(false);
        pa.setOffscreenPageLimit(4);
        //Fragment manager & bottom sheet setup
        FragmentManager fm = getSupportFragmentManager();
        VPAdaptor sa = new VPAdaptor(fm, getLifecycle());
        pa.setAdapter(sa);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(180);
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

    private void initList(){
        Log.d(TAG,"List initialised");
        textViewList.add(textViewDash);
        textViewList.add(textViewDisp);
        textViewList.add(textViewAbout);
        imageViewList.add(imageViewDash);
        imageViewList.add(imageViewDisp);
        imageViewList.add(imageViewAbout);
    }

    private void setUi(int posistion){
        final String GREEN = "#00E676";
        final String WHITE = "#FFFFFFFF";
        for(int i = 0;i < textViewList.size(); i++){
            textViewList.get(i).setTextColor(Color.parseColor(WHITE));
            imageViewList.get(i).setColorFilter(Color.parseColor(WHITE));
        }
        textViewList.get(posistion).setTextColor(Color.parseColor(GREEN));
        imageViewList.get(posistion).setColorFilter(Color.parseColor(GREEN));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void setBottomPageListeners() {
        homeLayout.setOnClickListener(v -> {
            Log.d(TAG,"Home Fragment started");
            pa.setCurrentItem(0);
            setUi(0);
        });
        displayLayout.setOnClickListener(v -> {
            Log.d(TAG,"Display Fragment started");
            pa.setCurrentItem(1);
            setUi(1);
        });
        cpuLayout.setOnClickListener(v -> {
            Log.d(TAG,"Cpu Fragment started");
            pa.setCurrentItem(2);
            //setUi(2);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        aboutLayout.setOnClickListener(v -> {
            Log.d(TAG,"Info Fragment started");
            pa.setCurrentItem(3);
            setUi(2);
        });
    }
}