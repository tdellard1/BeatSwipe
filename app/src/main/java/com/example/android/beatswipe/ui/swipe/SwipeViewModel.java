package com.example.android.beatswipe.ui.swipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.PropertyChangeRegistry;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.android.beatswipe.BR;
import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.utils.BeatRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class SwipeViewModel extends AndroidViewModel implements Observable{

    public static SwipeViewModel getInstance(FragmentActivity activity, SwipeHandler handler){
        SwipeViewModel viewModel = ViewModelProviders.of(activity).get(SwipeViewModel.class);
        viewModel.sHandler = handler;
        return viewModel;
    }

    private SwipeHandler sHandler;
    public ObservableField<Boolean> isPlaying = new ObservableField<>(false);
    private List<User> userList;
    public Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                //mediaPlayer.prepare();
                isPlaying.set(false);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    };


    public ObservableField<Boolean> getIsPlaying() {
        return isPlaying;
    }

    private SwipeHandler getsHandler() { return sHandler; }

    /**
     * Public observable fields that are used to display info from each beat.
     */
    public ObservableField<String> beat_name = new ObservableField<>();
    public ObservableField<String> beat_genre = new ObservableField<>();
    public ObservableField<String> beat_owner = new ObservableField<>();

    /**
     * Observable Object filled with all the beat */
     private LiveData<List<Beat>> mAllBeats;
     private LiveData<List<User>> mAllUsers;
     /**
     * Queue List of Beats and MediaPlayer Objects.
     * Queue was used to cos/users that are inside Room Database.
     * Observed from Activity class then filtered to find the correct beats/users.
     * Completely remove items instead of indexing through them for convenience.
     *
     * @params beatQueue: List of beats.
     * @params mediaQueue: Pre-loaded <MediaPlayer> objects.
     */
     private ArrayList<Beat> reverseBeats = new ArrayList<>();
    private Queue<Beat>beatQueue = new LinkedList<>();
    private ArrayList<MediaPlayer> reverseMedia = new ArrayList<>();
    private Queue<MediaPlayer> mediaQueue = new LinkedList<>();
    /**
     * Single objects of the current beat and current media player exposed tot the user
     *
     * @params currentBeat: Current Beat that is showing to the user.
     * @params mediaPLayerHolder: Holds the currently displayed MediaPlayer playing the current beat.
     */
    private Beat currentBeat;
    private MediaPlayer mediaPlayer;

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }

    BeatRepository mRepository;

    /**
     * Used in order to set the list/queue of beats and Media Players.
     * @param beat
     * @param media
     */
    public void addBeatAndMedia(Beat beat, MediaPlayer media) {
        reverseMedia.add(media);
        reverseBeats.add(beat);
    }

    public void addUsers(List<User> users) {
        this.userList = users;
    }

    public SwipeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BeatRepository(application);
        mAllBeats = mRepository.getAllBeats();
        mAllUsers = mRepository.getAllUsers();
    }

    LiveData<List<Beat>> getAllBeats() { return mAllBeats; }
    LiveData<List<User>> getAllUsers() { return mAllUsers; }

    public void Profile() {
        for (User user : userList) {
            if (user.getName().equals(beat_owner.get())) {
                mediaPlayer.stop();
                getsHandler().StartProfileFragment(user.getUserId());
            }
        }
    }

    public void reorderBeats() {
        for (int i = reverseBeats.size() - 1; i >= 0; i--) {
            beatQueue.offer(reverseBeats.get(i));
            mediaQueue.offer(reverseMedia.get(i));
        }
        /*for (Beat beat : reverseBeats) {
            beatQueue.offer(beat);
        }*/
    }

    /**
     * play() is the first instance of utilization of this activity.
     * Starts the (planned)Swiping queue that users will go through.
     */
    public void play() {
        try {
            /*mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();*/
        } catch (IllegalStateException e) {
            Log.d("tag", "IllegalStateCalled");
            mediaPlayer.start();
            if (e.getLocalizedMessage() != null) {
                Log.d("tag", e.getLocalizedMessage());
            }
            if (e.getMessage() != null) {
                Log.d("tag", e.getMessage());
            }
        }
        mediaPlayer.start();
        isPlaying.set(true);

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 30*1000);
    }



    public void playNext() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            currentBeat = beatQueue.remove();
            mediaPlayer = mediaQueue.remove();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            sHandler.goBack();
            return;
        }

        beat_name.set(currentBeat.getName());
        beat_genre.set(currentBeat.getGenre());
        for (User user : userList) {
            if (user.getUserId().equals(currentBeat.getOwner())) {
                beat_owner.set(user.getName());
            }
        }
        play();
    }

    public void cancelPlay() {
        Log.d("tag", "mRunnable Callback Removed");
        mHandler.removeCallbacks(mRunnable);
    }

    public void purchaseBeat() {
        sHandler.BrowserIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBeat.getLink())));
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
         registry.remove(callback);
    }

    private PropertyChangeRegistry registry = new PropertyChangeRegistry();

    public interface SwipeHandler {
        void StartProfileFragment(String uid);
        void BrowserIntent(Intent intent);
        void goBack();
    }
}
