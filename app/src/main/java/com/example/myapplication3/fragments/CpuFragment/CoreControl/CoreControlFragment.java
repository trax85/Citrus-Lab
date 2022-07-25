package com.example.myapplication3.fragments.CpuFragment.CoreControl;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication3.R;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.Arrays;


public class CoreControlFragment extends Fragment {
    final static String TAG = "CoreControlFrag";
    String coreControlPath = "/sys/devices/system/cpu/";
    final String GREY = "#757575";
    final String PURPLE = "#FFBB86FC";
    final String GREY_DEEP = "#616060";
    final String PURPLE_DEEP = "#A175FF";

    String[] coresArr;
    int[] coreStateArr;
    ImageView imageView1,imageView2,imageView3,imageView4,imageView5
            ,imageView6,imageView7,imageView8,imageViewBack;
    TextView textView1, textView2, textView3, textView4, textView5, textView6
            ,textView7, textView8;
    ImageView[] imageViews;
    TextView[] textViews;
    boolean isCpusetinited = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_core_control, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        coreStateArr = new int[8];
        init();
        if(!isCpusetinited)
            return;
        initButtons(view);
        setUpListners();
    }

    @Override
    public void onResume() {
        super.onResume();
        initState();
    }

    public void init(){
        try {
            coresArr = Utils.readGetArr("find " +
                    coreControlPath + " -name 'cpu*[0-9]' | cut -d'/' -f 6");
            isCpusetinited = true;
        } catch (UtilException e) {
            return;
        }
        Arrays.sort(coresArr);
    }

    public void initButtons(View view){
        imageView1 = view.findViewById(R.id.core_1);
        imageView2 = view.findViewById(R.id.core_2);
        imageView3 = view.findViewById(R.id.core_3);
        imageView4 = view.findViewById(R.id.core_4);
        imageView5 = view.findViewById(R.id.core_5);
        imageView6 = view.findViewById(R.id.core_6);
        imageView7 = view.findViewById(R.id.core_7);
        imageView8 = view.findViewById(R.id.core_8);
        imageViews = new ImageView[]{imageView1, imageView2, imageView3, imageView4, imageView5,
                imageView6, imageView7, imageView8};
        textView1 = view.findViewById(R.id.text_core_1);
        textView2 = view.findViewById(R.id.text_core_2);
        textView3 = view.findViewById(R.id.text_core_3);
        textView4 = view.findViewById(R.id.text_core_4);
        textView5 = view.findViewById(R.id.text_core_5);
        textView6 = view.findViewById(R.id.text_core_6);
        textView7 = view.findViewById(R.id.text_core_7);
        textView8 = view.findViewById(R.id.text_core_8);
        textViews = new TextView[]{textView1, textView2, textView3, textView4, textView5,
                textView6, textView7, textView8};
        imageViewBack = view.findViewById(R.id.ic_core_ctl_back);
    }

    public void setUpListners(){
        for(int i = 1; i < imageViews.length; i++){
            int finalI = i;
            imageViews[i].setOnClickListener(v -> {
                if(coreStateArr[finalI] == 1) //If core Online the Offline it and vice versa
                    setState(finalI,0);
                else
                    setState(finalI, 1);
            });
        }
        textView1.setOnClickListener(v -> Toast.makeText(getActivity(),
                "Cannot turn off Core 1", Toast.LENGTH_SHORT).show());
        imageViewBack.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
    }

    public void initState(){
        Arrays.fill(coreStateArr, -1);
        for(int i = 0; i < coresArr.length; i++){
            coreStateArr[i] = getState(i);
        }
        for(int i = 0; i < coreStateArr.length; i++){
            if(coreStateArr[i] == -1)
                disableCore(i);
            else if(coreStateArr[i] == 0)
                setUI(i, 0);
            else
                setUI(i, 1);
        }
    }

    public int getState(int pos){
        String out;
        try {
            out = Utils.read(0,coreControlPath
                    +coresArr[pos] + "/online");
        } catch (UtilException e) {
            out = "0";
        }
        return Integer.parseInt(out);
    }

    public void disableCore(int pos){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            imageViews[pos].setVisibility(View.GONE);
            textViews[pos].setVisibility(View.GONE);
        });
    }

    public void setState(int pos, int state){
        Utils.execCmdWrite("chmod 644 " + coreControlPath + coresArr[pos]+ "/online");
        Utils.write(String.valueOf(state), coreControlPath
                + coresArr[pos] + "/online");
        Utils.execCmdWrite("chmod 444 " + coreControlPath + coresArr[pos]+ "/online");
        coreStateArr[pos] = state;
        setUI(pos, state);
    }
    //Don't call this without calling setState unless you are still initialising
    public void setUI(int pos, int state) {
        String colour,colour_ext, text;
        if (state == 1) {
            colour = PURPLE;
            colour_ext = PURPLE_DEEP;
            text = "CORE " + (pos + 1) + " ONLINE";
        } else {
            colour = GREY;
            colour_ext = GREY_DEEP;
            text = "CORE " + (pos + 1) + " OFFLINE";
        }
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            imageViews[pos].setColorFilter(Color.parseColor(colour_ext));
            textViews[pos].setTextColor(Color.parseColor(colour));
            textViews[pos].setText(text);
        });
    }
}