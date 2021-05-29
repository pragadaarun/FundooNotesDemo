package com.example.fundoonotes.Firebase.Model;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserModel {

    private String userName;
    private String userEmail;
    private String phone;

    public FirebaseUserModel(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public Map<String, String> asMap() {
        return new HashMap<String, String>() {{
            put("name", userName);
            put("email", userEmail);
            put("phone", phone);
        }};

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

