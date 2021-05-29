package com.example.fundoonotes.Firebase.Model;

public class FirebaseLabelModel {

    private String labelName;
    private String labelId;

    public FirebaseLabelModel(String labelName, String labelId) {
        this.labelName = labelName;
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }
}
