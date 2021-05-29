package com.example.fundoonotes.Firebase.DataManager;

import android.util.Log;

import com.example.fundoonotes.Firebase.Model.FirebaseLabelModel;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirebaseLabelManager implements LabelManager {

    private static final String TAG = "FirebaseLabelManager";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String COLLECTIONS = "users";
    private final String LABEL_SUBCOLLECTIONS = "labels";
    private final String LABEL_FIELD = "Label";

    @Override
    public void getAllLabels(CallBack<ArrayList<FirebaseLabelModel>> labelListener) {
        ArrayList<FirebaseLabelModel> labelList = new ArrayList<FirebaseLabelModel>();
        firebaseFirestore.collection(COLLECTIONS).document(firebaseUser.getUid())
                .collection(LABEL_SUBCOLLECTIONS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i;
            for (i=0;i<queryDocumentSnapshots.size();i++){
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                Log.e(TAG, "onSuccess: "+ documentSnapshot);
                String label = documentSnapshot.getString(LABEL_FIELD);
                String docID = documentSnapshot.getId();
                FirebaseLabelModel firebaseLabelModel = new FirebaseLabelModel(label,docID);
                labelList.add(firebaseLabelModel);
            }
            labelListener.onSuccess(labelList);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure" + "Labels not Called");
            }
        });
    }

    @Override
    public void addLabel(String label, CallBack<String> addListener) {
        DocumentReference documentReference = firebaseFirestore
                .collection(COLLECTIONS)
                .document(firebaseUser.getUid())
                .collection(LABEL_SUBCOLLECTIONS).document();
        Map<String, Object> note = new HashMap<>();
        note.put(LABEL_FIELD, label);
        note.put("Creation Date", System.currentTimeMillis());
        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              String newLabelId = documentReference.getId();
                                              addListener.onSuccess(newLabelId);
                                              Log.e(TAG, "newLabelId "+ newLabelId );
                                          }
                                      }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void delete(String labelId) {
        firebaseFirestore= FirebaseFirestore.getInstance();
        assert firebaseUser != null;
        DocumentReference documentReference = firebaseFirestore
                .collection(COLLECTIONS)
                .document(firebaseUser.getUid())
                .collection(LABEL_SUBCOLLECTIONS).document(labelId);
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, "onSuccess: Deleted "+ labelId );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure : Error Deleted "+ labelId );
            }
        });
    }
}
