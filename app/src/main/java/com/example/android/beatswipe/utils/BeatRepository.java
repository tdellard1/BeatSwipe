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

import static com.example.android.beatswipe.utils.ErrorConstants.LOGIN_ERROR;
import static com.example.android.beatswipe.utils.ErrorConstants.LOGIN_HANDLER;
import static com.example.android.beatswipe.utils.ErrorConstants.SIGNUP_HANDLER;

public class BeatRepository implements FirebaseListener{

    private BeatDao mBeatDao;
    private UserDao mUserDao;
    private FirebaseService firebaseService;

    /** LiveData/Observable Fields */
    private LiveData<Double> progress;
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<List<User>> mAllUsers;
    private MutableLiveData<FirebaseUser> mCurrentUser = new MutableLiveData<>();
    private MutableLiveData<String> loginErrorMessage = new MutableLiveData<>();
    private MutableLiveData<String> signUpErrorMessage = new MutableLiveData<>();

    /** Getters For LiveData/Observed Dispatched Fields */
    public LiveData<Double> getProgress() { return progress; }
    public LiveData<List<Beat>> getAllBeats() { return mAllBeats; }
    public LiveData<List<User>> getAllUsers() { return mAllUsers; }
    public LiveData<FirebaseUser> getCurrentUser() { return mCurrentUser; }
    public LiveData<String> getLoginErrorMessage() { return loginErrorMessage; }
    public LiveData<String> getSignUpErrorMessage() { return signUpErrorMessage; }

    public BeatRepository(Application application) {
        BeatRoomDatabase db = BeatRoomDatabase.getDatabase(application);
        mBeatDao = db.beatDao();
        mUserDao = db.userDao();
        mAllBeats = mBeatDao.getAllBeats();
        mAllUsers = mUserDao.getAllUsers();
        firebaseService = new FirebaseService();
        firebaseService.addFirebaseListener(this);
        progress = firebaseService.getProgressLoaded();
        mCurrentUser.setValue(firebaseService.getCurrentUser());
    }


    //region USER FUNCTIONS

    public void LogInUser(String email, String password) {
        firebaseService.logInUser(email, password);
    }

    public void signOutUser() {
        /**
         * Signs the user out of Firebase/App
         * Deletes all loaded beats and users
         * TODO: Think about if I want to remove and load full Room Database for every change of User State.
         */
        mCurrentUser.setValue(firebaseService.signOutUser());
        deleteBeats();
        deleteUsers();
    }

    public void createAccount(Map UserMap) {
        firebaseService.createUser(UserMap);
    }

//endregion

    //region BEAT FUNCTIONS

    public void deleteBeats() { firebaseService.syncAllBeats(); }

    public void deleteUsers() { new DeleteUsersAsync(mUserDao).execute(); }

    public void uploadFile(Map FileMap) {
        firebaseService.addBeatToStorageAndDatabase(FileMap);
    }

    public void loadAllBeats() {
        firebaseService.syncAllBeats();
    }

    public void loadAllUsers() { firebaseService.syncAllProducers(); }

    /**
     * TEST THIS WHEN STORAGE STARTS TO WORK.
     * @param beatName
     */
    public void deleteUserBeat(String beatName) {
        firebaseService.deleteBeat(beatName);
    }

    public void updateBeat(Beat originalBeat) {
        firebaseService.updateBeat(originalBeat);
    }

    //endregion

    //region ASYNC TASKS
    private static class DeleteUsersAsync extends AsyncTask<Void, Void, Void> {

        private UserDao userDao;

        DeleteUsersAsync(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAll();
            return null;
        }
    }

    private static class AddUserAsync extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        AddUserAsync(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... params) {
            userDao.insertUser(params[0]);
            return null;
        }
    }

    private static class DeleteBeatsAsync extends AsyncTask<Void, Void, Void> {

        private BeatDao beatDao;

        DeleteBeatsAsync(BeatDao beatDao) {
            this.beatDao = beatDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            beatDao.deleteAll();
            return null;
        }
    }

    private static class DeleteBeatAsync extends AsyncTask<Beat, Void, Void> {

        private BeatDao beatDao;

        DeleteBeatAsync(BeatDao beatDao) {
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

    private static class insertOneBeatAsyncTask extends AsyncTask<Beat, Void, Void> {

        private BeatDao mAsyncTaskDao;

        insertOneBeatAsyncTask(BeatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Beat... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
    //endregion

    //region FIREBASE LISTENER
    @Override
    public void AddUserToRoomDatabase(User user) {
        new AddUserAsync(mUserDao).execute(user);
    }

    @Override
    public void AddOneBeatToRoomDatabase(Beat beat) {
        new insertOneBeatAsyncTask(mBeatDao).execute(beat);
    }

    @Override
    public void LogIn(FirebaseUser firebaseUser) {
        mCurrentUser.setValue(firebaseUser);
    }

    @Override
    public void RemoveBeatFromRoomDatabase(Beat beat) {
        new DeleteBeatAsync(mBeatDao).execute(beat);
    }

    @Override
    public void UpdateBeat(Beat beat) {
        new UpdateBeatAsync(mBeatDao).execute(beat);
    }

    @Override
    public void ErrorMessage(String message, String handler) {
        switch (handler) {
            case LOGIN_HANDLER:
                loginErrorMessage.setValue(message);
                break;
            case SIGNUP_HANDLER:
                signUpErrorMessage.setValue(message);
                break;

        }
    }
    //endregion









}
