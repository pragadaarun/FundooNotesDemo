package com.example.fundoonotes.HelperClasses;

import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;

public interface AddNoteListener{
    void onNoteAdded(FirebaseNoteModel note);
}
