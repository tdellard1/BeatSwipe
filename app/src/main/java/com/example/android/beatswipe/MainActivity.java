package com.example.android.beatswipe;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BeatViewModel beatViewModel;
    private TextView urlTextView;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    private String Url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playButton = findViewById(R.id.play_button);

        beatViewModel = ViewModelProviders.of(this).get(BeatViewModel.class);

        beatViewModel.getAllBeats().observe(this, new Observer<List<Beat>>() {
            @Override
            public void onChanged(@Nullable final List<Beat> beats) {
                for (Beat beat : beats) {
                    if (beats != null) {
                        Url = beats.get(0).getBeatUrl();
                        //int length = beats.size();
                        //Toast.makeText(MainActivity.this, Integer.toString(length), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.setDataSource(Url);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


    }
}
