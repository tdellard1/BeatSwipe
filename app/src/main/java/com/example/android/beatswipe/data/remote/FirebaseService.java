package com.example.android.beatswipe.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.Objects;

import static com.example.android.beatswipe.ui.signup.SignUpViewModel.USERNAME;
import static com.example.android.beatswipe.ui.signup.SignUpViewModel.USERPASSWORD;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.FIREBASE_AUDIO_URL;
import static com.example.android.beatswipe.ui.signup.SignUpViewModel.USEREMAIL;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.FIREBASE_IMAGE_URL;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.AUDIO_FILE_NAME;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.AUDIO_FILE_URL;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.FILE_URL;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.GENRE;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.IMAGE_FILE_NAME;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.IMAGE_FILE_URL;
import static com.example.android.beatswipe.ui.upload.UploadViewModel.PURCHASE_LINK;
import static com.example.android.beatswipe.utils.ErrorConstants.LOGIN_HANDLER;
import static com.example.android.beatswipe.utils.ErrorConstants.SIGNUP_HANDLER;
import static com.example.android.beatswipe.utils.ErrorConstants.UPLOAD_HANDLER;
import static com.example.android.beatswipe.utils.Utils.testRef;

public class FirebaseService {

    private final String LOGIN_ERROR_MESSAGE_ONE = "There is no user record corresponding to this identifier. The user may have been deleted.";
    private final String LOGIN_ERROR_MESSAGE_TWO = "The password is invalid or the user does not have a password.";
    private MutableLiveData<Double> mProgressLoaded = new MutableLiveData<>();
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseListener mListener;
    private FirebaseAuth mAuth;


    public FirebaseService() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Double> getProgressLoaded() { return mProgressLoaded; }

    public FirebaseUser getCurrentUser() { return mAuth.getCurrentUser(); }

    public void addFirebaseListener(FirebaseListener firebaseListener) {
        this.mListener = firebaseListener;
    }

    public void addAudioToStorage(final Map FileMap) {
        final StorageReference AudioUploadRef = mStorageRef.child(mAuth.getCurrentUser().getUid()).child("AUDIO")
            .child((String) FileMap.get(AUDIO_FILE_NAME));

        AudioUploadRef.putFile(Uri.parse((String) FileMap.get(AUDIO_FILE_URL))).addOnProgressListener(taskSnapshot ->
                    mProgressLoaded.postValue((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount())).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    mListener.ErrorMessage(task.getException().getMessage(), UPLOAD_HANDLER);
                    return null;
                } else {
                    mProgressLoaded.postValue(99.0);
                    return AudioUploadRef.getDownloadUrl();
                }
            }).addOnSuccessListener(uri -> {
                FileMap.put(FIREBASE_AUDIO_URL, uri.toString());
                addImageToStorage(FileMap);
            });
    }

    public void updateBeat(final Beat oldBeat, Map<String, String> newBeat) {
        testRef.push().setValue("FirebaseService: updateBeat initiated");
        final StorageReference ImageUploadRef = mStorageRef.child(mAuth.getCurrentUser().getUid()).child("IMAGE")
                .child(newBeat.get(IMAGE_FILE_NAME));

        testRef.push().setValue("FirebaseService: updateBeat: Ref Made");

        ImageUploadRef.putFile(Uri.parse(newBeat.get(IMAGE_FILE_URL))).addOnProgressListener(taskSnapshot ->{
            testRef.push().setValue("FirebaseService: updateBeat:  Progress Listener");
            mProgressLoaded.postValue((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());}).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                testRef.push().setValue(task.getException().getMessage());
                //mListener.ErrorMessage(task.getException().getMessage(), UPLOAD_HANDLER);
                return null;
            } else {
                mProgressLoaded.postValue(100.0);
                testRef.push().setValue("FirebaseService: updateBeat tasksuccessful");
                return ImageUploadRef.getDownloadUrl();
            }
        }).addOnSuccessListener(uri -> {
            testRef.push().setValue("FirebaseService: putFile called");
            Beat beat = new Beat(
                    newBeat.get(AUDIO_FILE_NAME),
                    newBeat.get(AUDIO_FILE_URL),
                    mAuth.getCurrentUser().getUid(),
                    newBeat.get(GENRE),
                    newBeat.get(PURCHASE_LINK),
                    uri.toString());
            removeAndAddBeat(oldBeat, beat);
            //transferBeatFromStorageToDatabase(newBeat);
        });
    }

    public void deleteBeat(final String beatName) {
        StorageReference deleteRef =
                mStorageRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())
                        .child(beatName);
        deleteRef.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DatabaseReference removeBeatRef = mDatabaseRef.child("beats").child("users")
                        .child(mAuth.getCurrentUser().getUid()).child("beats").child(beatName);
                removeBeatRef.removeValue();
            }
        });
    }

    /**
     * <METHOD>:createUser() - used to create an authentic and authorized user for the app.
     * 1. Submit Email Address & Password into Firebase Auth method to attempt to create a new user
     * 2. If a new user is able to be created, we update that user with a display name that they submitted. If not we submit errors (FINISH MAKING ERRORS)
     * 3. If profile is successfully updated, We add a create a <User> object with the the email, uid, and display name, to the database.
     * 4. Lastly we take the mAuth User and pass that into the LogIn method of the repository.
     * TODO: Create Outbound Errors
     * @param UserMap - Holds Email, Password, and Username of newly requested user
     */
    public void createUser(final Map UserMap) {
    mAuth.createUserWithEmailAndPassword((String) UserMap.get(USEREMAIL), (String) UserMap.get(USERPASSWORD)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName((String) UserMap.get(USERNAME)).build();
                Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            final User user = new User(mAuth.getCurrentUser());
                            if (mAuth.getCurrentUser().getDisplayName() == null) {
                                user.setName((String) UserMap.get(USERNAME));
                            }
                            mDatabaseRef.child("users").child(user.getUserId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (mListener != null) {
                                        mListener.LogIn(mAuth.getCurrentUser());
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                mListener.ErrorMessage(task.getException().getMessage(), SIGNUP_HANDLER);
            }
        }
    });
}


    /**
     * <METHOD/>: logInUser() - used to authenticate a user and if successful logs them into the app
     * TODO: Leave Comments
     * @param email
     * @param password
     */
    public void logInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mListener.LogIn(mAuth.getCurrentUser());
            } else {
                switch (task.getException().getMessage()) {
                    case LOGIN_ERROR_MESSAGE_ONE:
                        mListener.ErrorMessage("Couldn't Find Account Matching Given Email!", LOGIN_HANDLER);
                        break;
                    case LOGIN_ERROR_MESSAGE_TWO:
                        mListener.ErrorMessage("Email ANd Password Combination Incorrect.", LOGIN_HANDLER);
                        break;
                }
            }
        });
    }

    /**
     * TODO: Leave Comments
     * @return FirebaseUser
     */
    public FirebaseUser signOutUser() {
        mAuth.signOut();
        return mAuth.getCurrentUser();
    }

    /**
     * syncAllBeats() is used to add all beats from Firebase Database to Room Database within the app.
     * This method should be called and active when user first signs on, plus more.
     *
     * currently not sure how much a room database can hold all together
     *
     * TODO 1: test to see if I add a new beat if it will add just that one beat and not the rest.
     * TODO 2: Figure out when to detach the mListener.
     */
    public void syncAllBeats() {
        DatabaseReference uidRef = mDatabaseRef.child("users");
        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference currentUserRef = mDatabaseRef.child("beats").child("users/" + postSnapshot.getKey() + "/beats");
                    ChildEventListener userEventListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            mListener.AddOneBeatToRoomDatabase(dataSnapshot.getValue(Beat.class));
                        }
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            mListener.UpdateBeat(dataSnapshot.getValue(Beat.class));
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                            mListener.RemoveBeatFromRoomDatabase(dataSnapshot.getValue(Beat.class));
                        }
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    };
                    currentUserRef.addChildEventListener(userEventListener);
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {}
        };
        uidRef.addValueEventListener(userEventListener);
    }

    /**
     * syncAllBeats() is used to add all beats from Firebase Database to Room Database within the app.
     * This method should be called and active when user first signs on, plus more.
     */
    public void syncAllProducers() {
        DatabaseReference uidRef = mDatabaseRef.child("users");
        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mListener.AddUserToRoomDatabase(postSnapshot.getValue(User.class));
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {

            }
        };
        uidRef.addValueEventListener(userEventListener);
    }

    private void transferBeatFromStorageToDatabase(Map FileMap) {
        Beat beat = new Beat(
                (String) FileMap.get(AUDIO_FILE_NAME),
                (String) FileMap.get(FIREBASE_AUDIO_URL),
                mAuth.getCurrentUser().getUid(),(String)  FileMap.get(GENRE),
                (String) FileMap.get(PURCHASE_LINK),
                (String) FileMap.get(FIREBASE_IMAGE_URL));
        mDatabaseRef.child("beats").child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("beats").child(beat.getName()).setValue(beat);
    }

    private void addImageToStorage(final Map FileMap) {
        final StorageReference ImageUploadRef = mStorageRef.child(mAuth.getCurrentUser().getUid()).child("IMAGE")
                .child((String) FileMap.get(IMAGE_FILE_NAME));

        ImageUploadRef.putFile(Uri.parse((String) FileMap.get(IMAGE_FILE_URL))).addOnProgressListener(taskSnapshot ->
                mProgressLoaded.postValue((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount())).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                mListener.ErrorMessage(task.getException().getMessage(), UPLOAD_HANDLER);
                return null;
            } else {
                mProgressLoaded.postValue(100.0);
                return ImageUploadRef.getDownloadUrl();
            }
        }).addOnSuccessListener(uri -> {
            FileMap.put(FIREBASE_IMAGE_URL, uri.toString());
            transferBeatFromStorageToDatabase(FileMap);
        });
    }

    private void removeAndAddBeat(Beat oldBeat, Beat newBeat) {
        testRef.push().setValue("FirebaseService: removeAndAddBeat called");
        mDatabaseRef.child("beats").child("users").child(mAuth.getCurrentUser().getUid()).child("beats").child(oldBeat.getName()).removeValue();
        mDatabaseRef.child("beats").child("users").child(mAuth.getCurrentUser().getUid()).child("beats").child(newBeat.getName()).setValue(newBeat);
    }

    public interface FirebaseListener {
        void AddUserToRoomDatabase(User user);
        void AddOneBeatToRoomDatabase(Beat beat);
        void LogIn(FirebaseUser firebaseUser);
        void RemoveBeatFromRoomDatabase(Beat beat);
        void UpdateBeat(Beat beat);
        void ErrorMessage(String message, String handler);
    }
}