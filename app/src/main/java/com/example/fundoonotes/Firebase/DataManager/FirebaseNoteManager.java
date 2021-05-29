package com.example.fundoonotes.Firebase.DataManager;

import android.util.Log;

import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.HelperClasses.CallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class FirebaseNoteManager implements NoteManager {

    private static final String TAG = "FirebaseNoteManager";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String COLLECTIONS = "users";
    private final String NOTES_COLLECTIONS = "notes";
    private final String NOTE_TITLE = "title";
    private final String NOTE_DESCRIPTION = "description";
    DocumentReference fromCollection, toDocument;
    String newNoteID;

    @Override
    public void getAllNotes(CallBack<ArrayList<FirebaseNoteModel>> listener) {
        ArrayList<FirebaseNoteModel> notesList = new ArrayList<FirebaseNoteModel>();
        String userId = firebaseUser.getUid();
        firebaseFirestore.collection(COLLECTIONS).document(userId)
                .collection(NOTES_COLLECTIONS).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (int index = 0; index < queryDocumentSnapshots.size(); index++) {
                            Log.e(TAG,
                                    "onSuccess: " + queryDocumentSnapshots
                                            .getDocuments().get(index));

                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(index);

                            String title = documentSnapshot.getString(NOTE_TITLE);
                            String description = documentSnapshot.getString(NOTE_DESCRIPTION);
                            String noteId = documentSnapshot.getId();
                            long timestamp = documentSnapshot.getLong("creationDate");

                            Log.e(TAG,"AllNotes " + title + " " + noteId);

                            FirebaseNoteModel note = new FirebaseNoteModel(userId, noteId, title, description, timestamp);
                            notesList.add(note);
                        }
                        listener.onSuccess(notesList);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });
    }

    @Override
    public void addNote(String title, String description, CallBack<String> addListener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = firebaseFirestore
                .collection(COLLECTIONS)
                .document(firebaseUser.getUid())
                .collection(NOTES_COLLECTIONS).document();
        Map<String, Object> note = new HashMap<>();
        note.put(NOTE_TITLE, title);
        note.put(NOTE_DESCRIPTION, description);
        note.put("creationDate", System.currentTimeMillis());

        documentReference.set(note)
                .addOnSuccessListener(aVoid -> {
                    newNoteID = documentReference.getId();
                    addListener.onSuccess(newNoteID);
                    Log.e(TAG, "newNoteID "+ newNoteID );
                }).addOnFailureListener(addListener::onFailure
                );
    }

    public void moveToTrash(String fromPath, String toPath, String noteId) {
        if(fromPath == "Notes"){
            fromCollection = firebaseFirestore
                    .collection(COLLECTIONS).document(firebaseUser.getUid())
                    .collection(NOTES_COLLECTIONS).document(noteId);
        }
        if(toPath == "Trash"){
            String TRASH_COLLECTIONS = "trash";
            toDocument = firebaseFirestore.collection(COLLECTIONS).document(firebaseUser.getUid())
                    .collection(TRASH_COLLECTIONS).document();
        }
        fromCollection.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        toDocument.set(document.getData())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        fromCollection.delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Note successfully deleted" + noteId);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document", e);
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
