package com.example.fundoonotes.SQLiteDataManager;

import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.Firebase.Model.FirebaseUserModel;

import java.util.ArrayList;

public interface NoteTableManager {
    boolean addUser(FirebaseUserModel userModel);
    boolean addNote(FirebaseNoteModel noteModel);
    ArrayList<FirebaseNoteModel> getAllNotes();
    boolean deleteNote(String noteId);
    void deleteAllNotes();
    void addAllNotes(ArrayList<FirebaseNoteModel> notes);
}
