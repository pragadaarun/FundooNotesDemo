package com.example.fundoonotes.Firebase.Model;

import com.example.fundoonotes.Firebase.DataManager.FirebaseLabelManager;
import com.example.fundoonotes.Firebase.DataManager.FirebaseNoteManager;
import com.example.fundoonotes.Firebase.DataManager.LabelManager;
import com.example.fundoonotes.Firebase.DataManager.NoteManager;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.example.fundoonotes.HelperClasses.ViewState;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LabelViewModel extends ViewModel {
    public MutableLiveData<ViewState<ArrayList<FirebaseLabelModel>>> labelMutableLiveData =
            new MutableLiveData<>();
    private static final String TAG = "LabelViewModel";
    private LabelManager labelManager;

    public LabelViewModel() {
        labelManager = new FirebaseLabelManager();
        loadLabel();
    }

    private void loadLabel() {
        labelMutableLiveData.setValue(new ViewState.Loading<>());
        labelManager.getAllLabels(new CallBack<ArrayList<FirebaseLabelModel>>() {
            @Override
            public void onSuccess(ArrayList<FirebaseLabelModel> data) {
                labelMutableLiveData.setValue(new ViewState.Success<>(data));
            }

            @Override
            public void onFailure(Exception exception) {
                labelMutableLiveData.setValue(new ViewState.Failure<>(exception));

            }
        });
    }
}
