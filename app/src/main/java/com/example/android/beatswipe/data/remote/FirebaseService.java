package com.example.android.beatswipe.data.remote;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.utils.FirebaseListener;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.example.android.beatswipe.ui.signup.SignUpViewModel.USERNAME;
import static com.example.android.beatswipe.ui.signup.SignUpViewModel.USERPASSWORD;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.GENRE;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.NAME;
import static com.example.android.beatswipe.ui.upload.UploadFileFragment.URL;
import static com.example.android.beatswipe.ui.signup.SignUpViewModel.USEREMAIL;
import static com.example.android.beatswipe.utils.ErrorConstants.LOGIN_ERROR;
import static com.example.android.beatswipe.utils.ErrorConstants.LOGIN_HANDLER;
import static com.example.android.beatswipe.utils.ErrorConstants.SIGNUP_HANDLER;
import static com.example.android.beatswipe.utils.Utils.testRef;

public class FirebaseService {

    private final String LOGIN_ERROR_MESSAGE_ONE = "There is no user record corresponding to this identifier. The user may have been deleted.";
    private final String LOGIN_ERROR_MESSAGE_TWO = "The password is invalid or the user does not have a password.";
    public static final String Error = "Error";
    public static final String ErrorCode = "ErrorCode";
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private StorageReference storageRef;



    private FirebaseListener listener;
    private Map<String, String> error = new HashMap<>();
    private MutableLiveData<Map<String, String>> ERROR = new MutableLiveData<>();
    private MutableLiveData<Double> progressloaded = new MutableLiveData<>();

    public FirebaseService() {
        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    /*********************************************************************************************************************************************
     ********************************************* M E T H O D S   R E T U R N I N G   O B J E C T S *********************************************
     *********************************************************************************************************************************************/

    public LiveData<Double> getProgressLoaded() {
        return progressloaded;
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public LiveData<Map<String, String>> getERROR() {
        return ERROR;
    }

    public void addFirebaseListener(FirebaseListener firebaseListener) {
        this.listener = firebaseListener;
    }

    /*********************************************************************************************************************************************
     ******************************************* S T O R A G E   A N D   D A T A B A S E   M E T H O D S *****************************************
     *********************************************************************************************************************************************/

    public void addBeatToStorageAndDatabase(final Map FileMap) {
            final StorageReference uploadRef = storageRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child((String) FileMap.get(NAME));
            uploadRef.putFile(Uri.parse((String) FileMap.get(URL))).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressloaded.postValue((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    }
            }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        //// Add Error Message To Relay Upwards
                        return null;
                    }
                    progressloaded.postValue(100.0);
                    return uploadRef.getDownloadUrl();
                }
            }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(@NonNull Uri uri) {
                    fromStorageToDatabase(new Beat((String) FileMap.get(NAME), uri.toString(),mAuth.getCurrentUser().getUid(),(String)  FileMap.get(GENRE)));
                }
            });
    }

    private void fromStorageToDatabase(final Beat beat) {
        databaseRef.child("beats").child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("beats").child(beat.getName()).setValue(beat);
    }

    public void deleteBeat(final String beatName) {
        databaseRef.child("test").push().setValue("deleteBeat from firebase service called");
        StorageReference deleteRef = storageRef.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child(beatName);
        deleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    databaseRef.child("test").push().setValue("successfully deleted beat");
                    DatabaseReference removeBeatRef = databaseRef.child("beats").child("users").child(mAuth.getCurrentUser().getUid()).child("beats").child(beatName);
                    removeBeatRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            databaseRef.child("test").push().setValue("beat removed from database");

                        }
                    });
                }
            }
        });
    }
 /* *********************************************************************************************************************************************
    *************************************** M E T H O D S   T A R G E T I N G   C U R R E N T   U S E R S ***************************************
    *********************************************************************************************************************************************/

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
                            databaseRef.child("users").child(user.getUserId()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (listener != null) {
                                        listener.LogIn(mAuth.getCurrentUser());
                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                listener.ErrorMessage(task.getException().getMessage(), SIGNUP_HANDLER);
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
                listener.LogIn(mAuth.getCurrentUser());
            } else {
                switch (task.getException().getMessage()) {
                    case LOGIN_ERROR_MESSAGE_ONE:
                        listener.ErrorMessage("Couldn't Find Account Matching Given Email!", LOGIN_HANDLER);
                        break;
                    case LOGIN_ERROR_MESSAGE_TWO:
                        listener.ErrorMessage("Email ANd Password Combination Incorrect.", LOGIN_HANDLER);
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

    /*********************************************************************************************************************************************
     *************************************** M E T H O D S   T A R G E T I N G   R O O M   D A T A B A S E ***************************************
     *********************************************************************************************************************************************/

    /**
     * syncAllBeats() is used to add all beats from Firebase Database to Room Database within the app.
     * This method should be called and active when user first signs on, plus more.
     *
     * currently not sure how much a room database can hold all together
     *
     * TODO 1: test to see if I add a new beat if it will add just that one beat and not the rest.
     * TODO 2: Figure out when to detach the listener.
     */
    public void syncAllBeats() {
        DatabaseReference uidRef = databaseRef.child("users");
        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    DatabaseReference currentUserRef = databaseRef.child("beats").child("users/" + postSnapshot.getKey() + "/beats");
                    ChildEventListener userEventListener = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            listener.AddOneBeatToRoomDatabase(dataSnapshot.getValue(Beat.class));
                        }
                        @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            listener.UpdateBeat(dataSnapshot.getValue(Beat.class));
                        }
                        @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                            listener.RemoveBeatFromRoomDatabase(dataSnapshot.getValue(Beat.class));
                        }
                        @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                        @Override public void onCancelled(DatabaseError databaseError) {}
                    };
                    currentUserRef.addChildEventListener(userEventListener);
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {

            }
        };
        uidRef.addValueEventListener(userEventListener);
    }

    /**
     * syncAllBeats() is used to add all beats from Firebase Database to Room Database within the app.
     * This method should be called and active when user first signs on, plus more.
     */
    public void syncAllProducers() {
        DatabaseReference uidRef = databaseRef.child("users");
        ValueEventListener userEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listener.AddUserToRoomDatabase(postSnapshot.getValue(User.class));
                }
            }
            @Override public void onCancelled(DatabaseError databaseError) {

            }
        };
        uidRef.addValueEventListener(userEventListener);
    }


    public void updateBeat(final Beat originalBeat) {
        DatabaseReference updateBeatRef = databaseRef.child("beats").child("users").child(mAuth.getCurrentUser().getUid()).child("beats").child(originalBeat.getName());
        updateBeatRef.setValue(originalBeat);
    }
}
