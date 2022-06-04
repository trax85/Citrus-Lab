package com.example.myapplication3.fragments.DisplayFragment;

public class DisplayDataModel {
    String PrimaryContent, SecondaryContent;
    //Feed in multiple paths
    String[] FilePath;
    int[] ActionSet;
    public DisplayDataModel(String PrimaryContent, String SecondaryContent, String[] FilePath, int[] ActionSet){
        this.PrimaryContent = PrimaryContent;
        this.SecondaryContent = SecondaryContent;
        this.FilePath = FilePath;
        this.ActionSet = ActionSet;
    }

    public String getPrimaryContent() {
        return PrimaryContent;
    }

    public String getSecondaryContent() {
        return SecondaryContent;
    }

    public String[] getFilePath() {
        return FilePath;
    }

    public int[] getActionSet() {
        return ActionSet;
    }
}
