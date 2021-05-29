package com.example.fundoonotes.SQLiteDataManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="fundooNotes.db";
    public static final String USERS = "users";
    public static final String KEY_UID = "userId";
    public static final String USER_NAME = "userName";
    public static final String USER_EMAIL = "userEmail";
    public static final String NOTES_LIST = "notesList";
    public static final String KEY_ID = "docID";
    public static final String KEY_NOTE ="note";
    public static final String KEY_TITLE ="title";
    public static DatabaseHelper INSTANCE = null;
    public static Object MUTEX = "noObject";
    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public static DatabaseHelper getInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (MUTEX) {
                if(INSTANCE == null) {
                    INSTANCE = new DatabaseHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_USER_TABLE = ("CREATE TABLE " + USERS + "("
                + KEY_UID + " TEXT PRIMARY KEY,"
                + USER_NAME + "TEXT,"
                + USER_EMAIL + "TEXT" + ")");

        String CREATE_NOTE_TABLE = ("CREATE TABLE " + NOTES_LIST + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_NOTE + " TEXT,"
                + KEY_UID + " TEXT,"
                + " FOREIGN KEY (" + KEY_UID + ")"
                + " REFERENCES " + USERS + "("
                +  KEY_UID + "))");

        db.execSQL(CREATE_NOTE_TABLE);
        db.execSQL(CREATE_USER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS);
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_LIST);
            onCreate(db);
        }
    }
}