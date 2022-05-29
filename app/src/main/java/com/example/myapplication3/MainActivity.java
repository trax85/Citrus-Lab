package com.example.myapplication3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.topjohnwu.superuser.Shell;
import androidx.viewpager2.widget.ViewPager2;

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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageButton showSheet;
    private LinearLayout linearLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    LinearLayout bottomSheetLayout;
    private boolean bottomSheetState = true;
    ViewPager2 pa;
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

        linearLayout = findViewById(R.id.nested_scroll);
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        pa = findViewById(R.id.viewPager);
        //Disable swipe left and right
        pa.setUserInputEnabled(false);
        //Fragment manager setup
        FragmentManager fm = getSupportFragmentManager();
        VPAdaptor sa = new VPAdaptor(fm, getLifecycle());
        pa.setAdapter(sa);
        //Bottomsheet setup
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(180);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
        showSheet = findViewById(R.id.close_menu);
        showSheet.setOnClickListener(v -> {
            if(bottomSheetState == true) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                showDialog();
            }else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
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
    //This method collapses opened bottomsheet when touched outside the bottomsheet
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

    private void showDialog() {
        final String GREEN = "#00E676";
        final String WHITE = "#FFFFFFFF";
        LinearLayout homelayout = findViewById(R.id.layoutHome);
        LinearLayout displaylayout = findViewById(R.id.layoutDisplay);
        TextView textViewDash = findViewById(R.id.textViewDash);
        TextView textViewDisp = findViewById(R.id.textViewDisp);
        ImageView imageViewDash = findViewById(R.id.imageViewDash);
        ImageView imageViewDisp = findViewById(R.id.imageViewDisp);

        homelayout.setOnClickListener(v -> {
            pa.setCurrentItem(0);
            textViewDash.setTextColor(Color.parseColor(GREEN));
            textViewDisp.setTextColor(Color.parseColor(WHITE));
            imageViewDash.setColorFilter(Color.parseColor(GREEN));
            imageViewDisp.setColorFilter(Color.parseColor(WHITE));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        displaylayout.setOnClickListener(v -> {
            pa.setCurrentItem(1);
            textViewDash.setTextColor(Color.parseColor(WHITE));
            textViewDisp.setTextColor(Color.parseColor(GREEN));
            imageViewDash.setColorFilter(Color.parseColor(WHITE));
            imageViewDisp.setColorFilter(Color.parseColor(GREEN));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

    }
}