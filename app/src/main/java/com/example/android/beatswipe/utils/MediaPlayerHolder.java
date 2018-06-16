package com.example.android.beatswipe.utils;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.MediaStore;

import com.example.android.beatswipe.data.local.Beat;

import java.io.IOException;

public class MediaPlayerHolder extends MediaPlayer implements MediaPlayer.OnPreparedListener {

    private Beat beat;
    private boolean startMediaPlayer;
    private MediaPlayer mediaPlayer;

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MediaPlayerHolder(Beat beat, boolean startMediaPlayer) {
        this.beat = beat;
        this.startMediaPlayer = startMediaPlayer;
        mediaPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mediaPlayer.setDataSource(beat.getAudioUrl());
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
        } catch (IOException | NullPointerException npio) {
            npio.printStackTrace();
        }
    }

    public void start() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();/*
            startMediaPlayer = true;
            mediaPlayer.prepareAsync()*/
            mediaPlayer.start();
        }
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
}
