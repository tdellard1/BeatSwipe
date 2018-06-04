package com.example.android.beatswipe.utils;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.google.firebase.auth.FirebaseUser;

public interface FirebaseListener {
    void AddUserToRoomDatabase(User user);
    void AddOneBeatToRoomDatabase(Beat beat);
    void LogIn(FirebaseUser firebaseUser);
    void RemoveBeatFromRoomDatabase(Beat beat);
    void UpdateBeat(Beat beat);
    void ErrorMessage(String message, String handler);
}
