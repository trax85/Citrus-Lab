package com.example.myapplication3.fragments.ProfileFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomProfile {
    private final String [] labelTags = new String[]{"CustProf1", "CustProf2", "CustProf3"};
    private final String[] nameTags = new String[]{"ProfName1", "ProfName2", "ProfName3"};
    final static String TAG = "CustomProfile";
    private SharedPreferences pref;
    public static boolean[] isProfPresent;
    private final ProfileFragment fragment;
    boolean[] touched;

    String[] profArr, nameArr;

    public CustomProfile(ProfileFragment fragment){
        this.fragment = fragment;
        //Initialise Arrays
        profArr = new String[labelTags.length];
        nameArr = new String[nameTags.length];
        isProfPresent = new boolean[profArr.length];
        touched = new boolean[]{false, false, false};
    }

    public void initCustomProfiles(Context context){
        pref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        for(int i = 0; i < 3; i++) {
            profArr[i] = pref.getString(labelTags[i], "null");
            nameArr[i] = pref.getString(nameTags[i],"Not set");
        }
        for(int i = 0; i < profArr.length; i++)
            isProfPresent[i] = !profArr[i].equals("null");
        initViews();
    }

    public void initViews(){
        Handler handler = new Handler(Looper.getMainLooper());
        for(int i = 0;i < isProfPresent.length; i++){
            if(isProfPresent[i]){
                Log.d(TAG,"profile found");
                setOnClickListeners(i);
                hideAddView(i);
            }else{
                Log.d(TAG,"no profile");
                hideView(i);
                int finalI = i;
                handler.post(() -> fragment.setAddButton(finalI));
            }
        }
    }

    public void setOnClickListeners(int idx){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            fragment.editTextViews[idx].setOnClickListener(v -> fragment.showPopUpWindow(idx));
            fragment.custProfArr[idx].setOnClickListener(v -> {
                Utils.execCmdString(profArr[idx]);
                Toast.makeText(fragment.getContext(), "Applying profile " + (1 + idx),
                        Toast.LENGTH_LONG).show();
            });
            fragment.editNameArr[idx].setOnClickListener(v -> renameOperation(idx));
        });
    }

    public void renameOperation(int idx){
        MaterialAlertDialogBuilder builder = new
                MaterialAlertDialogBuilder(fragment.requireActivity());
        final EditText weightInput = new EditText(fragment.getActivity());

        builder.setTitle("Edit Name");
        weightInput.setInputType(InputType.TYPE_CLASS_TEXT);
        weightInput.setHint(fragment.textViews[idx].getText().toString());
        builder.setView(weightInput);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String str = weightInput.getText().toString();
            fragment.textViews[idx].setText(str);
            nameArr[idx] = str;
            setItem(idx);
        });builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public void setItem(int idx){
        Log.d(TAG, "setItem");
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(nameTags[idx], nameArr[idx]);
        editor.putString(labelTags[idx], profArr[idx]);
        editor.apply();
    }

    public void clearItem(int idx){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(labelTags[idx]);
        editor.remove(nameTags[idx]);
        editor.apply();
    }

    //called when profile is cleared
    @SuppressLint("SetTextI18n")
    public void hideView(int idx){
        fragment.addProfArr[idx].setVisibility(View.VISIBLE);
        fragment.editTextViews[idx].setVisibility(View.GONE);
        fragment.editNameArr[idx].setVisibility(View.INVISIBLE);
        fragment.textViews[idx].setText("Not set");
        fragment.setAddButton(idx);
        isProfPresent[idx] = false;
    }

    //called when profile is added
    public void hideAddView(int idx){
        fragment.addProfArr[idx].setVisibility(View.GONE);
        fragment.editTextViews[idx].setVisibility(View.VISIBLE);
        fragment.editNameArr[idx].setVisibility(View.VISIBLE);
        fragment.textViews[idx].setText(nameArr[idx]);
        fragment.setAddButton(idx);
        isProfPresent[idx] = true;
        setOnClickListeners(idx);
    }
}
