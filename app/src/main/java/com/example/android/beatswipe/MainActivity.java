package com.example.android.beatswipe;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private TextView loggedInTextView;
    private Button playButton, selectFileButton, uploadButton, signOutButton, nextButton;
    private MediaPlayer mediaPlayer;
    private String Url;
    Uri audioUri;

    public static final int READ_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        nextButton = findViewById(R.id.play_next);
        signOutButton = findViewById(R.id.btn_sign_out);
        playButton = findViewById(R.id.play_button);
        selectFileButton = findViewById(R.id.btn_select_file);
        uploadButton = findViewById(R.id.btn_upload_beat);
        loggedInTextView = findViewById(R.id.tv_logged_in);

        beatViewModel = ViewModelProviders.of(this).get(BeatViewModel.class);

        beatViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null)
                {
                    loggedInTextView.setText(user.getName() + " is logged in.");
                } else {
                    beatViewModel.SignInPage(MainActivity.this);
                }
            }
        });

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

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beatViewModel.signOutUser();
            }
        });

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beatViewModel.selectBeat(MainActivity.this);


            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/beats%2FDrama?alt=media&token=433f7930-52e1-4067-8c86-6e25fd03063c");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                try {
                    mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/beatswipe-10a20.appspot.com/o/beats%2FHonest?alt=media&token=f5726548-b667-4997-ab80-94738d2dc5a2");
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioUri != null) {
                    beatViewModel.uploadFile(MainActivity.this, audioUri);
                }
            }
        });
    }

   @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == READ_REQUEST_CODE) {
            audioUri = data.getData();
            Toast.makeText(MainActivity.this,audioUri.toString(),Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "Please select a file", Toast.LENGTH_SHORT).show();
        }
    }
}
