package com.example.myapplication3.fragments.ProfileFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class SetOnBootListener{
    private final String[] listItems = {"Profile 1", "Profile 2", "Profile 3", "Not Selected"};
    private String[] finalList;
    private final Context context;
    private final ProfileFragment fragment;
    private SharedPreferences pref;
    private int setProfile;

    public SetOnBootListener(ProfileFragment fragment, Context context) {
        this.fragment = fragment;
        this.context = context;
    }

    public void setOnBootListenerButton(){
        fragment.setOnBoot.setOnClickListener(v -> setListener());
    }

    private void setListener(){
        updateList();
        int checkedItem = Arrays.asList(finalList).indexOf(listItems[setProfile]);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(fragment.requireActivity());
        builder.setTitle("Choose Option");
        builder.setSingleChoiceItems(finalList, checkedItem, (dialog, which) -> {
            setProfile = Arrays.asList(listItems).indexOf(finalList[which]);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("setOnBoot", setProfile);
            editor.apply();
            dialog.dismiss();
        });
        builder.show();
    }

    private void updateList(){
        pref = context.getSharedPreferences(CustomProfile.TAG, Context.MODE_PRIVATE);
        setProfile = pref.getInt("setOnBoot", 3);

        int count = 0, i;
        for(i = 0; i < listItems.length - 1; i++){
            if(CustomProfile.isProfPresent[i]) {
                Log.d("DEBUG", "count");
                count++;
            }
        }
        if(count == 0){
            finalList = new String[1];
            finalList[count] = listItems[3];
        }else {
            finalList = new String[count + 1];
            for (count = 0, i = 0; i < listItems.length - 1; i++) {
                if (CustomProfile.isProfPresent[i])
                    finalList[count++] = listItems[i];
            }
            finalList[count] = listItems[3];
        }
    }
}
