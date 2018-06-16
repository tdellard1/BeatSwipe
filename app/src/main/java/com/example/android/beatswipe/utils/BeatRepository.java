package com.example.android.beatswipe.utils;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.BeatDao;
import com.example.android.beatswipe.data.local.BeatRoomDatabase;
import com.example.android.beatswipe.data.remote.FirebaseService;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.data.local.UserDao;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

import static com.example.android.beatswipe.utils.ErrorConstants.LOGIN_HANDLER;
import static com.example.android.beatswipe.utils.ErrorConstants.SIGNUP_HANDLER;
import static com.example.android.beatswipe.utils.ErrorConstants.UPLOAD_HANDLER;
import static com.example.android.beatswipe.utils.Utils.testRef;

public class BeatRepository implements FirebaseService.FirebaseListener {

    private BeatDao mBeatDao;
    private UserDao mUserDao;
    private FirebaseService mFirebaseService;

    /** LiveData/Observable Fields */
    private LiveData<Double> mProgress;
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<List<User>> mAllUsers;
    private MutableLiveData<FirebaseUser> mCurrentUser = new MutableLiveData<>();
    private MutableLiveData<String> mLoginErrorMessage = new MutableLiveData<>();
    private MutableLiveData<String> mSignUpErrorMessage = new MutableLiveData<>();
    private MutableLiveData<String> mUploadErrorMessage = new MutableLiveData<>();

    /** Getters For LiveData/Observed Dispatched Fields */
    public LiveData<Double> getProgress() { return mProgress; }
    public LiveData<List<Beat>> getAllBeats() { return mAllBeats; }
    public LiveData<List<User>> getAllUsers() { return mAllUsers; }
    public LiveData<FirebaseUser> getCurrentUser() { return mCurrentUser; }
    public LiveData<String> getLoginErrorMessage() { return mLoginErrorMessage; }
    public LiveData<String> getSignUpErrorMessage() { return mSignUpErrorMessage; }
    public LiveData<String> getUploadErrorMessage() { return mSignUpErrorMessage; }

    public BeatRepository(Application application) {
        BeatRoomDatabase db = BeatRoomDatabase.getDatabase(application);
        mBeatDao = db.beatDao();
        mUserDao = db.userDao();
        mAllBeats = mBeatDao.getAllBeats();
        mAllUsers = mUserDao.getAllUsers();
        mFirebaseService = new FirebaseService();
        mFirebaseService.addFirebaseListener(this);
        mProgress = mFirebaseService.getProgressLoaded();
        mCurrentUser.setValue(mFirebaseService.getCurrentUser());
    }

    //region FIREBASE LISTENER
    @Override
    public void AddUserToRoomDatabase(User user) {
        new AddOneUserAsync(mUserDao).execute(user);
    }

    @Override
    public void AddOneBeatToRoomDatabase(Beat beat) {
        new AddOneBeatAsyncTask(mBeatDao).execute(beat);
    }

    @Override
    public void LogIn(FirebaseUser firebaseUser) {
        loadAllUsers();
        loadAllBeats();
        mCurrentUser.setValue(firebaseUser);
    }

    @Override
    public void RemoveBeatFromRoomDatabase(Beat beat) {
        new DeleteOneBeatAsync(mBeatDao).execute(beat);
    }

    @Override
    public void UpdateBeat(Beat beat) {
        new UpdateBeatAsync(mBeatDao).execute(beat);
    }

    @Override
    public void ErrorMessage(String message, String handler) {
        switch (handler) {
            case LOGIN_HANDLER:
                mLoginErrorMessage.setValue(message);
                break;
            case SIGNUP_HANDLER:
                mSignUpErrorMessage.setValue(message);
                break;
            case UPLOAD_HANDLER:
                mUploadErrorMessage.setValue(message);
                break;
        }
    }
    //endregion

    //region USER FUNCTIONS

    public void LogInUser(String email, String password) {
        mFirebaseService.logInUser(email, password);
    }

    public void signOutUser() {
        /**
         * Signs the user out of Firebase/App
         * Deletes all loaded beats and users
         * TODO: Think about if I want to remove and load full Room Database for every change of User State.
         */
        mCurrentUser.setValue(mFirebaseService.signOutUser());
        deleteAllBeats();
        deleteAllUsers();
    }

    public void createAccount(Map UserMap) {
        mFirebaseService.createUser(UserMap);
    }

//endregion

    //region BEAT FUNCTIONS

    private void deleteAllBeats() { new DeleteAllBeatsAsync(mBeatDao).execute(); }

    private void deleteAllUsers() { new DeleteAllUsersAsync(mUserDao).execute(); }

    public void uploadFile(Map FileMap) {
        mFirebaseService.addAudioToStorage(FileMap);
    }

    public void loadAllBeats() {
        mFirebaseService.syncAllBeats();
    }

    public void loadAllUsers() { mFirebaseService.syncAllProducers(); }

    /**
     * TEST THIS WHEN STORAGE STARTS TO WORK.
     * @param beatName
     */
    public void deleteUserBeatFromRemoteDatabase(String beatName) {
        mFirebaseService.deleteBeat(beatName);
    }

    public void updateBeat(Beat oldBeat, Map<String, String> newBeat) {
        testRef.push().setValue("BeatRepository: updateBeat Called");
        mFirebaseService.updateBeat(oldBeat, newBeat);
    }

    //endregion

    //region ASYNC TASKS
    private static class DeleteAllUsersAsync extends AsyncTask<Void, Void, Void> {

        private UserDao userDao;

        DeleteAllUsersAsync(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAll();
            return null;
        }
    }

    private static class AddOneUserAsync extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        AddOneUserAsync(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... params) {
            userDao.insertUser(params[0]);
            return null;
        }
    }

    private static class DeleteAllBeatsAsync extends AsyncTask<Void, Void, Void> {

        private BeatDao beatDao;

        DeleteAllBeatsAsync(BeatDao beatDao) {
            this.beatDao = beatDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            beatDao.deleteAll();
            return null;
        }
    }

    private static class DeleteOneBeatAsync extends AsyncTask<Beat, Void, Void> {

        private BeatDao beatDao;

        DeleteOneBeatAsync(BeatDao beatDao) {
            this.beatDao = beatDao;
        }

        @Override
        protected Void doInBackground(Beat... params) {
            beatDao.deleteBeat(params[0]);
            return null;
        }
    }

    private static class UpdateBeatAsync extends AsyncTask<Beat, Void, Void> {

        private BeatDao beatDao;

        UpdateBeatAsync(BeatDao beatDao) {
            this.beatDao = beatDao;
        }

        @Override
        protected Void doInBackground(Beat... params) {
            beatDao.updateBeat(params[0]);
            return null;
        }
    }

    private static class AddOneBeatAsyncTask extends AsyncTask<Beat, Void, Void> {

        private BeatDao mAsyncTaskDao;

        AddOneBeatAsyncTask(BeatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Beat... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    //endregion











}
