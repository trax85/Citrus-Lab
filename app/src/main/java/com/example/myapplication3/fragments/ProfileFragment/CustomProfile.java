package com.example.myapplication3.fragments.ProfileFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication3.tools.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomProfile {
    final static String Lable1 = "CustProf1";
    final static String Lable2 = "CustProf2";
    final static String Lable3 = "CustProf3";
    final static String Name1 = "ProfName1";
    final static String Name2 = "ProfName2";
    final static String Name3 = "ProfName3";
    final static String TAG = "CustomProfile";
    SharedPreferences pref;
    boolean[] isProfPresent;
    ProfileFragment fragment;
    boolean[] touched;

    String[] profArr, nameArr, nameTags, lableTags;

    public CustomProfile(ProfileFragment fragment){
        this.fragment = fragment;
        //Initialise Arrays
        nameTags = new String[]{Name1, Name2, Name3};
        lableTags = new String[]{Lable1, Lable2, Lable3};
        nameArr = new String[nameTags.length];
        profArr = new String[lableTags.length];
        isProfPresent = new boolean[profArr.length];
        touched = new boolean[]{false, false, false};
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initCustomProfiles(Context context){
        pref = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);

        for(int i = 0; i < 3; i++) {
            profArr[i] = pref.getString(lableTags[i], "null");
            nameArr[i] = pref.getString(nameTags[i],"Not set");
        }
        for(int i = 0; i < profArr.length; i++)
            isProfPresent[i] = !profArr[i].equals("null");
        initViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initViews(){
        for(int i = 0;i < isProfPresent.length; i++){
            if(isProfPresent[i]){
                setOnClickListeners(i);
                hideAddView(i);
            }else{
                hideView(i);
                fragment.setAddButton(i);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setOnClickListeners(int idx){
        fragment.unSetTextViews[idx].setOnClickListener(v -> {
            clearItem(idx);
            hideView(idx);
        });
        fragment.editTextViews[idx].setOnClickListener(v -> {
            editOpertaion(idx);
        });
        fragment.custProfArr[idx].setOnClickListener(v -> {
            Utils.execCmdWrite(profArr[idx]);
            Toast.makeText(fragment.getContext(), "Applying profile " + idx,
                    Toast.LENGTH_LONG).show();
        });
        fragment.editNameArr[idx].setOnClickListener(v -> renameOperation(idx));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        });builder.setNegativeButton("Cancle", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void editOpertaion(int idx){
        fragment.getFile(idx);
    }

    public void setItem(int idx){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(nameTags[idx], nameArr[idx]);
        editor.putString(lableTags[idx], profArr[idx]);
        editor.apply();
    }

    public void clearItem(int idx){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(lableTags[idx]);
        editor.remove(nameTags[idx]);
        editor.apply();
    }

    //called when profile is cleared
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void hideView(int idx){
        fragment.addProfArr[idx].setVisibility(View.VISIBLE);
        fragment.editTextViews[idx].setVisibility(View.GONE);
        fragment.unSetTextViews[idx].setVisibility(View.GONE);
        fragment.editNameArr[idx].setVisibility(View.INVISIBLE);
        fragment.textViews[idx].setText("Not set");
        fragment.setAddButton(idx);
    }

    //called when profile is added
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void hideAddView(int idx){
        fragment.addProfArr[idx].setVisibility(View.GONE);
        fragment.editTextViews[idx].setVisibility(View.VISIBLE);
        fragment.unSetTextViews[idx].setVisibility(View.VISIBLE);
        fragment.editNameArr[idx].setVisibility(View.VISIBLE);
        fragment.textViews[idx].setText(nameArr[idx]);
        fragment.setAddButton(idx);
        setOnClickListeners(idx);
    }
}
