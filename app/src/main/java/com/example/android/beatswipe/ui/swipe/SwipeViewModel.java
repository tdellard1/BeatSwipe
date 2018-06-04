package com.example.android.beatswipe.ui.swipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;

import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.utils.BeatRepository;
import com.example.android.beatswipe.utils.MediaPlayerHolder;
import com.example.android.beatswipe.utils.MediaPlayerHolderListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import static com.example.android.beatswipe.utils.Utils.testRef;

public class SwipeViewModel extends AndroidViewModel implements MediaPlayerHolderListener {

    private SwipeHandler sHandler;
    public ObservableBoolean isPlaying = new ObservableBoolean(false);
    private boolean mediaStarted = false;
    private List<User> userList;

    private SwipeHandler getsHandler() { return sHandler; }

    public void setsHandler(SwipeHandler sHandler) { this.sHandler = sHandler; }

    /**
     * Public observable fields that are used to display info from each beat.
     */
    public ObservableField<String> beat_name = new ObservableField<>();
    public ObservableField<String> beat_genre = new ObservableField<>();
    public ObservableField<String> beat_owner = new ObservableField<>();

    /**
     * Observable Object filled with all the beats/users that are inside Room Database.
     * Observed from Activity class then filtered to find the correct beats/users.
     */
    private LiveData<List<Beat>> mAllBeats;
    private LiveData<List<User>> mAllUsers;
    /**
     * Queue List of Beats and MediaPlayer Objects.
     * Queue was used to completely remove items instead of indexing through them for convenience.
     *
     * @params beatQueue: List of beats.
     * @params mediaQueue: Pre-loaded <MediaPlayer> objects.
     */
    private ArrayList<Beat> reverseBeats = new ArrayList<>();
    private Queue<Beat>beatQueue = new LinkedList<>();
    private Queue<MediaPlayerHolder> mediaQueue = new LinkedList<>();
    /**
     * Single objects of the current beat and current media player exposed tot the user
     *
     * @params currentBeat: Current Beat that is showing to the user.
     * @params mediaPLayerHolder: Holds the currently displayed MediaPlayer playing the current beat.
     */
    private Beat currentBeat;
    private MediaPlayerHolder mediaPlayerHolder;

    public MediaPlayerHolder getMediaPlayerHolder() { return mediaPlayerHolder; }

    BeatRepository mRepository;

    /**
     * Used in order to set the list/queue of beats and Media Players.
     * @param beat
     * @param media
     */
    public void addBeatAndMedia(Beat beat, MediaPlayerHolder media) {
        this.mediaQueue.offer(media);
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
                mediaPlayerHolder.stop();
                getsHandler().StartProfileFragment(user.getUserId());
            }
        }
    }

    public void reorderBeats() {
        Collections.reverse(reverseBeats);
        for (Beat beat : reverseBeats) {
            beatQueue.offer(beat);
        }
        play();
    }

    /**
     * play() is the first instance of utilization of this activity.
     * Starts the (planned)Swiping queue that users will go through.
     */
    public void play() {
        try {
            currentBeat = beatQueue.remove();
            mediaPlayerHolder = mediaQueue.remove();
            //mediaPlayerHolder.setPHL(this);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            sHandler.goBack();
            return;
        }
        mediaPlayerHolder.start();
        beat_name.set(currentBeat.getName());
        beat_genre.set(currentBeat.getGenre());
        for (User user : userList) {
            if (user.getUserId().equals(currentBeat.getOwner())) {
                beat_owner.set(user.getName());
            }
        }
        isPlaying.set(true);
    }

    public void playNext() {
        if (mediaPlayerHolder != null && mediaPlayerHolder.isPlaying()) {
            mediaPlayerHolder.stop();
            isPlaying.set(false);
        }
        try {
            currentBeat = beatQueue.remove();
            mediaPlayerHolder = mediaQueue.remove();
            mediaPlayerHolder.setPHL(this);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            sHandler.goBack();
            return;
        }

        for (User user : userList) {
            if (user.getUserId().equals(currentBeat.getOwner())) {
                beat_owner.set(user.getName());
            }
        }
        beat_name.set(currentBeat.getName());
        beat_genre.set(currentBeat.getGenre());
        mediaPlayerHolder.start();
        isPlaying.set(true);
    }

    public void test() {
        mRepository.deleteBeats();
    }

    @Override
    public void playbackComplete(boolean currentlyPlaying) {
        isPlaying.set(currentlyPlaying);
    }
}
