package com.example.myapplication3.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication3.MainActivity;
import com.example.myapplication3.R;

public class InfoPopupWindow {
    private Fragment fragment;
    private int layoutID;
    PopupWindow popupWindow;

    public InfoPopupWindow(Fragment fragment, int layoutID) {
        this.fragment = fragment;
        this.layoutID = layoutID;
    }

    public void setInfoWindow(ImageView imageView, String infoText){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> imageView.setOnClickListener(v -> {
            setDIm();
            showPopUpWindow(infoText);
        }));

    }

    private void showPopUpWindow(String info){
        View view = View.inflate(fragment.getContext(), R.layout.info_popup_window, null);
        TextView  okButton;
        TextView overflowText;
        overflowText = view.findViewById(R.id.info_text);
        overflowText.setText(info);
        okButton = view.findViewById(R.id.popup_ok);

        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow(view, width, height, true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupWindow.setOnDismissListener(this::unsetDim);
        okButton.setOnClickListener(v -> popupWindow.dismiss());

    }

    private void setDIm(){
        MainActivity.layout.setAlpha((float) 0.5);
    }
    private void unsetDim(){
        MainActivity.layout.setAlpha((float) 1.0);
    }
}
