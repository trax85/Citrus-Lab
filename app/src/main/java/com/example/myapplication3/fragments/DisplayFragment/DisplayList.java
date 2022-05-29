package com.example.myapplication3.fragments.DisplayFragment;

import android.widget.Button;

public class DisplayList {
    String PrimaryContent, SecondaryContent, FilePath;
    int ActionSet, ActionUnset;
    public DisplayList(String PrimaryContent, String SecondaryContent, String FilePath, int ActionSet,
                       int ActionUnset){
        this.PrimaryContent = PrimaryContent;
        this.SecondaryContent = SecondaryContent;
        this.FilePath = FilePath;
        this.ActionSet = ActionSet;
        this.ActionUnset = ActionUnset;
    }

    public String getPrimaryContent() {
        return PrimaryContent;
    }

    public String getSecondaryContent() {
        return SecondaryContent;
    }

    public String getFilePath() {
        return FilePath;
    }

    public int getActionSet() {
        return ActionSet;
    }

    public int getActionUnset() {
        return ActionUnset;
    }
}
