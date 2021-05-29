package com.example.fundoonotes.Firebase.Model;

public class FirebaseNoteModel {

    private String userId;
    private String noteID;
    private String title;
    private String description;
    private long creationTime;

    public FirebaseNoteModel(String userId, String noteID, String title, String description, long creationTime)
    {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.noteID = noteID;
        this.creationTime = creationTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNoteID(String noteID) {
        this.noteID = noteID;
    }

    public String getNoteID() {
        return noteID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}