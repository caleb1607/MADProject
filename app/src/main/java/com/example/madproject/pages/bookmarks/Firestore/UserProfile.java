package com.example.madproject.pages.bookmarks.Firestore;

import java.util.List;

public class UserProfile {
    private String email;
    private String username;
    private List<SavedArrivalTime> savedarrivaltimes;

    public UserProfile() {}

    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public List<SavedArrivalTime> getSavedarrivaltimes() { return savedarrivaltimes; }
}

