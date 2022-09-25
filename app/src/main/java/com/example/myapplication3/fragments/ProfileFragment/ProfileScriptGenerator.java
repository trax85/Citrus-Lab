package com.example.myapplication3.fragments.ProfileFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication3.FragmentDataModels.FragmentParameter;
import com.example.myapplication3.R;
import com.example.myapplication3.fragments.HomeFragment.FragmentPersistObject;
import com.example.myapplication3.tools.UtilException;
import com.example.myapplication3.tools.Utils;

import java.util.ArrayList;

public class ProfileScriptGenerator {
    @SuppressLint("SdCardPath")
    private static final String PATH = "/sdcard/citrus_lab";
    private final String fileName;
    private final ProfileFragment fragment;
    private final ArrayList<String> selectedItems;
    private final ArrayList<FragmentParameter> fragments = new ArrayList<>();

    public ProfileScriptGenerator(ProfileFragment fragment, ArrayList<String> selectedItems,
                                  String fileName) {
        this.fragment = fragment;
        this.selectedItems = selectedItems;
        this.fileName = fileName;
    }

    public String makeScript(){
        initData();
        return buildString();
    }

    private void initData(){
        String[] items = fragment.requireActivity().getResources()
                .getStringArray(R.array.profile_array);
        for(int i = 0; i < items.length; i++){
            if(selectedItems.contains(items[i])){
                fragments.add(getFragmentParameter(i));
            }
        }
    }

    private FragmentParameter getFragmentParameter(int index){
        FragmentPersistObject viewModel = new ViewModelProvider(fragment.requireActivity())
                .get(FragmentPersistObject.class);
        switch (index){
            case 0: return viewModel.getDisplayParams();
            case 1: return viewModel.getCpuParams();
            case 2: return viewModel.getGpuParams();
            case 3: return viewModel.getPowerParams();
            case 4: return viewModel.getMemoryParams();
            case 5: return viewModel.getMiscParams();
            default: return null;
        }
    }

    private String buildString(){
        StringBuilder stringBuilder = new StringBuilder();
        for(FragmentParameter parameterObj : fragments){
            ArrayList<String> activityLog = parameterObj.getActivityLog();
            for(String string : activityLog){
                stringBuilder.append(string).append(" ");
            }
        }
        return String.valueOf(stringBuilder);
    }

    public String mkFile(){
        String file = fileName + ".sh";
        String fullPath = PATH + "/" + file;
        checkForFolder();
        file = checkForFile(fullPath);
        appendContentsToFile(fullPath);
        return file;
    }

    private void checkForFolder(){
        try {
            Utils.execCmdRead(0,"ls "+ PATH);
        } catch (UtilException e) {
            Utils.execCmdString("mkdir " + PATH);
            e.printStackTrace();
        }
    }

    private String checkForFile(String fullPath){
        String filename;
        try{
            Utils.read(0, fullPath);
            Log.d("DEBUG","duplicate found");
            Toast.makeText(fragment.getContext(), "File name already exists",
                    Toast.LENGTH_SHORT).show();
            filename = fileName + "(1)" + ".sh";
            Utils.execCmdString("touch " + PATH + "/" + filename);
        }catch (UtilException e){
            Utils.execCmdString("touch " + fullPath);
            filename = fileName + ".sh";
        }
        return filename;
    }

    private void appendContentsToFile(String fullPath){
        String startString = fragment.requireActivity()
                .getResources().getString(R.string.profileScriptStart);
        Utils.execCmdString("echo \"" + startString + "\"" + ">>" + fullPath);

        for(int i = 0; i < fragments.size(); i++){
            String out;
            out = "#" + selectedItems.get(i) + "Settings";
            Utils.execCmdString("echo \"" + out + "\"" + ">>" + fullPath);
            ArrayList<String> activityLog = fragments.get(i).getActivityLog();
            if(activityLog.isEmpty())
                continue;
            for(String string : activityLog){
                Utils.execCmdString("echo \"" + string + "\"" + ">>" + fullPath);
            }
            Utils.execCmdString("echo ##\n" + ">>" + fullPath);
        }
    }
}
