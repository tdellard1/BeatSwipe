package com.example.android.beatswipe;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FirebaseDatabaseService {

    private BeatDao mBeatDao;
    private LiveData<List<Beat>> mAllBeats;

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private static FirebaseDatabaseService fds;

    FirebaseDatabaseService() {
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("beats");
    }

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }

    public Beat beatUrlToDatabase() {

        //Start here and plan how to upload then store beat instantly



        String Url = "https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/April%2013th.mp3?alt=media&token=d5837af3-9b2e-446a-9372-a5b193bde9aa";
        Beat beat = new Beat(Url);
        databaseRef.setValue(beat);
        return beat;
    }
































    /*

    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private String beatUrl;

    FirebaseDatabaseService() {
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("beats");
    }

    public String getBeatUrl() {
        addBeatToDatabase();
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Beat beat = dataSnapshot.getValue(Beat.class);
                beatUrl = beat.getBeatUrl();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return beatUrl;
    }

    public void addBeatToDatabase() {
        String Url = "https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/April%2013th.mp3?alt=media&token=d5837af3-9b2e-446a-9372-a5b193bde9aa";
        Beat beat = new Beat(Url);
        databaseRef.setValue(beat);
    }
*/}
