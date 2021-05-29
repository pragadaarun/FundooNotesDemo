package com.example.fundoonotes.Firebase.DataManager;

import com.example.fundoonotes.Firebase.Model.FirebaseLabelModel;
import com.example.fundoonotes.HelperClasses.CallBack;

import java.util.ArrayList;

public interface LabelManager {

    void getAllLabels(CallBack<ArrayList<FirebaseLabelModel>> labelListener);
    void addLabel(String label, CallBack<String> addListener);
    void delete(String labelId);
}
