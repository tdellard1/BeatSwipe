package com.example.android.beatswipe;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageService {

    private FirebaseStorage storage;
    private StorageReference storageRef;

    public FirebaseStorageService() {
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
    }

}
