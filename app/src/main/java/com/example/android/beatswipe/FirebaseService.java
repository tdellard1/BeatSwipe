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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import static android.content.ContentValues.TAG;

public class FirebaseService {

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    public static String downloadUrl;
    private NewUserListener listener;

    public FirebaseService() {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("beats");
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
    }

    public void setNewUserListener(NewUserListener newUserListener) {
        this.listener = newUserListener;
    }

    public void addBeatToStorageAndDatabase(final String name, Uri audioUri,final String Uid) {
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
                        //Beat beat = new Beat(uri.toString());
                        databaseRef.child(Uid).child("beats").child(name).setValue(uri.toString());
                    }
                });
                return null;
            }
        });
    }

    public void createUser(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name).build();
                    mAuth.getCurrentUser().updateProfile(profileUpdates);
                    User user = new User();
                    user.setUserId(mAuth.getCurrentUser());
                    user.setName(name);
                    databaseRef.child(mAuth.getCurrentUser().getUid()).setValue(user);
                    if (listener != null) {
                        listener.NewUserCreated(mAuth.getCurrentUser().getUid(), name);
                    }
                }
            }
        });
    }

    public void LogInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (listener != null) {
                        listener.NewUserCreated(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getDisplayName());
                    }
                }
            }
        });
    }
}
