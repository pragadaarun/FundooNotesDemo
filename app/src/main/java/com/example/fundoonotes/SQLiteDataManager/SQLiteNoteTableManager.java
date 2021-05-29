
package com.example.fundoonotes.SQLiteDataManager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.fundoonotes.Firebase.Model.FirebaseNoteModel;
import com.example.fundoonotes.Firebase.Model.FirebaseUserModel;

import java.util.ArrayList;

import static com.example.fundoonotes.SQLiteDataManager.DatabaseHelper.NOTES_LIST;

public class SQLiteNoteTableManager implements NoteTableManager {

    private static final String TAG = "SQLiteNoteTableManager";
    private DatabaseHelper databaseHelper; // = DatabaseHelper(context);

    public SQLiteNoteTableManager(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public boolean addUser(FirebaseUserModel userModel) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long insert = 0;
        db.beginTransaction();
        try {
            ContentValues userDetails = new ContentValues();
            userDetails.put(DatabaseHelper.KEY_UID, userModel.getUserName());
            userDetails.put(DatabaseHelper.USER_NAME, userModel.getUserName());
            userDetails.put(DatabaseHelper.USER_EMAIL, userModel.getUserEmail());

            insert = db.insert(DatabaseHelper.USERS, null, userDetails);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
        return (insert > 0);
    }

    @Override
    public boolean addNote(FirebaseNoteModel noteModel) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long insert = 0;
        db.beginTransaction();
        try {

            ContentValues notesDetails = new ContentValues();
            notesDetails.put(DatabaseHelper.KEY_UID, noteModel.getUserId());
            notesDetails.put(DatabaseHelper.KEY_ID, noteModel.getNoteID());
            notesDetails.put(DatabaseHelper.KEY_TITLE, noteModel.getTitle());
            notesDetails.put(DatabaseHelper.KEY_NOTE, noteModel.getDescription());

            insert = db.insert(NOTES_LIST, null, notesDetails);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
        return insert > 0;
    }

    @Override
    public ArrayList<FirebaseNoteModel> getAllNotes() {
        ArrayList<FirebaseNoteModel> notesList = new ArrayList<FirebaseNoteModel>();
        String query = "SELECT * FROM " + NOTES_LIST;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor;

        try {
            cursor = db.rawQuery(query, null);
        } catch (SQLiteException e) {
            return new ArrayList();
        }

        String userId;
        String noteId;
        String title;
        String description;
        long creationTime;
        if(cursor.moveToFirst()) {
            do {
                userId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_UID));
                noteId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID));
                title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_TITLE));
                description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_NOTE));
                creationTime = 0;

                FirebaseNoteModel note = new FirebaseNoteModel(userId, noteId, title, description, creationTime);
                notesList.add(note);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return notesList;
    }

    @Override
    public boolean deleteNote(String noteId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues noteToDelete = new ContentValues();
        noteToDelete.put(DatabaseHelper.KEY_ID, noteId);
        int delete = db.delete(NOTES_LIST, DatabaseHelper.KEY_ID, (String[])(null));
        db.close();
        return delete > 0;
    }

    @Override
    public void deleteAllNotes() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL("DELETE FROM "  + NOTES_LIST);
        db.close();
    }

    @Override
    public void addAllNotes(ArrayList<FirebaseNoteModel> notes) {
        for(int i=0; i < notes.size(); i++) {
            addNote(notes.get(i));
        }
    }
}
