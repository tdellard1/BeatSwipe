package com.example.android.beatswipe.ui.profile;

import android.app.Application;
import android.app.FragmentManager;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.Bindable;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.databinding.PropertyChangeRegistry;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.example.android.beatswipe.BR;
import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.ui.individualbeat.EditBeatFragment;
import com.example.android.beatswipe.utils.BeatRepository;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;

import static com.example.android.beatswipe.utils.Utils.testRef;

public class ProfileViewModel extends AndroidViewModel implements MediaPlayer.OnPreparedListener, Observable{

    private PropertyChangeRegistry registry = new PropertyChangeRegistry();
    private ObservableField<Integer> progress = new ObservableField<>();
    private ObservableField<Integer> max = new ObservableField<>();

    @Bindable
    public ObservableField<Integer> getProgress() {
        return progress;
    }

    @Bindable
    public ObservableField<Integer> getMax() {
        return max;
    }

    private BeatRepository mRepository;
    private LiveData<List<User>> mAllUsers;
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<FirebaseUser> currentUser;

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> genre = new ObservableField<>();



    public MediaPlayer mPlayer;
    private Handler handler;
    private android.support.v4.app.FragmentManager fragmentManager;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        mAllUsers = mRepository.getAllUsers();
        mAllBeats = mRepository.getAllBeats();
        currentUser = mRepository.getCurrentUser();
    }

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }
    LiveData<List<User>> getAllUsers() { return mAllUsers; }
    LiveData<FirebaseUser> getCurrentUser() { return currentUser; }

    public void setFragmentManager(android.support.v4.app.FragmentManager fragmentManager1) {
        this.fragmentManager = fragmentManager1;
    }
    public void setProgress(int progress) {
        this.progress.set(progress);
        registry.notifyChange(this, BR.progress);
    }
    public void setMax(int max) {
        this.max.set(max);
        registry.notifyChange(this, BR.max);
    }

    public void beatsNull() {
        /*
        Add Method To Retrieve Beats
         */
    }

    public void usersNull() {
        /*
        Add Method To Retrieve Users
         */
    }

    public void play(Beat beat) {
        playMedia(beat);
        setViews(beat);
    }

    private void setViews(Beat beat) {
        name.set(beat.getName());
        genre.set(beat.getGenre());
    }

    public void editBeat(Beat beat) {
        EditBeatFragment editBeatFragment = new EditBeatFragment();
        editBeatFragment.setBeat(beat);
        fragmentManager.beginTransaction().add(R.id.profile_frag, editBeatFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        handler = new Handler();
        setMax(mPlayer.getDuration());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setProgress(mPlayer.getCurrentPosition());
                handler.postDelayed(this, 250);
            }
        }, 250);
    }

    private void playMedia(Beat beat) {
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.reset();
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mPlayer.setOnPreparedListener(this);
        try {
            mPlayer.setDataSource(beat.getUrl());
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }
}
