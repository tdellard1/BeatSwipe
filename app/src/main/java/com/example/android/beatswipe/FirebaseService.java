package com.example.android.beatswipe;

import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class FirebaseService {

    private BeatDao mBeatDao;
    private LiveData<List<Beat>> mAllBeats;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    public static String downloadUrl;

    public FirebaseService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("beats");
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("beats");
    }


    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }

    public void addBeatToStorageAndDatabase(final String name, Uri audioUri) {
        final StorageReference pathRef = storageRef;
        UploadTask uploadTask = pathRef.child(name).putFile(audioUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                // Forward any exceptions
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                pathRef.child(name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Beat beat = new Beat(uri.toString());
                        databaseRef.push().setValue(beat);
                    }
                });
                return null;
            }
        });
    }



}
