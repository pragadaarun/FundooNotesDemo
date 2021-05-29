package com.example.fundoonotes.Firebase.DataManager;

import com.example.fundoonotes.Firebase.Model.FirebaseUserModel;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

public class FirebaseUserManager {

    private static final String TAG = "FirebaseUserManager";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public void getUserDetails(CallBack<FirebaseUserModel> listener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        String userName = (String) documentSnapshots.getString("name");
                        String userEmail = (String) documentSnapshots.getString("email");

                        FirebaseUserModel firebaseUserModel = new FirebaseUserModel(userName, userEmail);
                        listener.onSuccess(firebaseUserModel);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });

    }
}
