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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BeatViewModel beatViewModel;
    private TextView urlTextView;
    private Button playButton, loadButton;
    private MediaPlayer mediaPlayer;
    private String Url;

    FirebaseDatabase database;
    DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("beats");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        playButton = findViewById(R.id.play_button);
        loadButton = findViewById(R.id.load_beat);

        beatViewModel = ViewModelProviders.of(this).get(BeatViewModel.class);

        beatViewModel.getAllBeats().observe(this, new Observer<List<Beat>>() {
            @Override
            public void onChanged(@Nullable final List<Beat> beats) {
                for (Beat beat : beats) {
                    if (beats != null) {
                        Url = beats.get(0).getBeatUrl();
                    } else {
                        Toast.makeText(MainActivity.this, "Null", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beatViewModel.loadBeat();
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
