package com.example.android.beatswipe.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.example.android.beatswipe.data.local.Beat;

import java.io.IOException;

public class MediaPlayerHolder extends MediaPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{

    private Beat beat;
    private boolean startMediaPlayer;
    private MediaPlayer mediaPlayer;
    private MediaPlayerHolderListener mediaListener;

    public void setPHL(MediaPlayerHolderListener listener) {
        this.mediaListener = listener;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MediaPlayerHolder(Beat beat, boolean startMediaPlayer) {
        this.beat = beat;
        this.startMediaPlayer = startMediaPlayer;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(beat.getUrl());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException | NullPointerException npio) {
            npio.printStackTrace();
        }
    }

    public void start() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();
        }
        mediaPlayer.start();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (startMediaPlayer) {
            mp.start();
        }
    }

    public void prepareAsync(boolean startMediaPlayer) {
        this.startMediaPlayer = startMediaPlayer;
        mediaPlayer.prepareAsync();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void reset() {
        mediaPlayer.reset();
    }

    public void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    /*public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }*/

    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //mediaListener.playbackComplete(false);
    }
}
