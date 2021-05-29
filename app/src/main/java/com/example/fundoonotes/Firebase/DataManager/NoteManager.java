package com.example.fundoonotes.Firebase.DataManager;

import com.example.fundoonotes.Firebase.Model.FirebaseLabelModel;
import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.HelperClasses.CallBack;

import java.util.ArrayList;

public interface NoteManager {

    void getAllNotes(CallBack<ArrayList<FirebaseNoteModel>> listener);
    void addNote(String title, String description, CallBack<String> addListener);
    //    void moveToTrash(String fromPath, String toPath, String noteId);

}
