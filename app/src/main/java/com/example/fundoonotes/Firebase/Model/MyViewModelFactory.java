package com.example.fundoonotes.Firebase.Model;

import com.example.fundoonotes.DashBoard.Fragments.Notes.NotesViewModel;
import com.example.fundoonotes.SQLiteDataManager.DatabaseHelper;
import com.example.fundoonotes.SQLiteDataManager.NoteTableManager;
import com.example.fundoonotes.SQLiteDataManager.SQLiteNoteTableManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory implements ViewModelProvider.Factory {


    private final NoteTableManager noteTableManager;

    public MyViewModelFactory(NoteTableManager noteTableManager) {
        this.noteTableManager = noteTableManager;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T t = null;
        if(modelClass.isAssignableFrom(NotesViewModel.class))
            return (T) new NotesViewModel(noteTableManager);
        throw new IllegalStateException("Illegal View Model Instance is expected");
    }
}
