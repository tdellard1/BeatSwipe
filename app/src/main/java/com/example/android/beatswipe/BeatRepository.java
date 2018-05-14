package com.example.android.beatswipe;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.net.Uri;
import android.os.AsyncTask;

import java.util.List;

public class BeatRepository  {

    private BeatDao mBeatDao;
    private UserDao mUserDao;
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<User> mCurrentUser;
    private FirebaseService firebaseService;
    private String Url;

    BeatRepository(Application application) {
        BeatRoomDatabase db = BeatRoomDatabase.getDatabase(application);
        mBeatDao = db.beatDao();
        mUserDao = db.userDao();
        mAllBeats = mBeatDao.getAllBeats();
        mCurrentUser = mUserDao.getCurrentUser();
        firebaseService = new FirebaseService();
        firebaseService.setNewUserListener(new NewUserListener() {
            @Override
            public void NewUserCreated(String Uid, String name) {
                signInUser(Uid, name);
            }
        });
    }
//region USER FUNCTIONS

    LiveData<User> getCurrentUser() {return mCurrentUser; }

    public void signOutUser() {
        new DeleteUserAsync(mUserDao).execute();

    }

    public void signInUser(String userId, String name) {
        new AddUserAsync(mUserDao).execute(userId, name);
    }

    public void createAccount(String name, String email, String password) {
        firebaseService.createUser(name, email, password);
    }

//endregion

//region BEAT FUNCTIONS

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }

    public void LogInUser(String email, String password) {
        firebaseService.LogInUser(email, password);
    }


    private static class insertAsyncTask extends AsyncTask<Beat, Void, Void> {

        private BeatDao mAsyncTaskDao;

        public insertAsyncTask(BeatDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Beat... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    public void insert(Beat beat) { new insertAsyncTask(mBeatDao).execute(beat); }

    public void uploadFile(String name, Uri audioUri) {
        firebaseService.addBeatToStorageAndDatabase(name, audioUri, mCurrentUser.getValue().getUserId());
    }

//endregion

//region ASYNC TASKS
    private static class DeleteUserAsync extends AsyncTask<Void, Void, Void> {

        private UserDao userDao;

        public DeleteUserAsync(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAll();
            return null;
        }
    }

    private static class AddUserAsync extends AsyncTask<String, Void, Void> {

        private UserDao userDao;

        public AddUserAsync(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(String... params) {
            userDao.deleteAll();
            User user = new User(params[0]);
            user.setName(params[1]);
            userDao.insertUser(user);
            return null;
        }
    }
//endregion










}
