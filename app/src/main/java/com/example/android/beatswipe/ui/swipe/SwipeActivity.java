package com.example.android.beatswipe.ui.swipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.databinding.ActivitySwipeBinding;
import com.example.android.beatswipe.ui.profile.ProfileFragment;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SwipeActivity extends AppCompatActivity implements View.OnTouchListener, SwipeViewModel.SwipeHandler, ProfileFragment.DetachViewListener {

    private static final String GENRE = "GENRE";
    private ActivitySwipeBinding mActivitySwipeBinding;
    private SwipeViewModel mSwipeViewModel;
    private MediaPlayer mPlayer;
    boolean remove = false;
    private int globalIterator = 0;
    private int FragmentCardId;
    private List<Beat> beatsList = new ArrayList<>();

    public static Intent getSwipeIntent(Context context,String genre) {
        Intent swipeIntent = new Intent(context, SwipeActivity.class);
        swipeIntent.putExtra(GENRE, genre);
        return swipeIntent;
    }

    private ActivitySwipeBinding getInstance(SwipeViewModel viewModel) {
        ActivitySwipeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_swipe);
        binding.setSwipeViewModel(viewModel);
        return binding;
    }

    @Override
    protected void onDestroy() {
        if (mSwipeViewModel.getMediaPlayer() != null) {
            mSwipeViewModel.getMediaPlayer().stop();
            mSwipeViewModel.getMediaPlayer().reset();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String genre = getIntent().getStringExtra("GENRE");
        mSwipeViewModel = SwipeViewModel.getInstance(this, this);
        mActivitySwipeBinding = getInstance(mSwipeViewModel);
        mSwipeViewModel.getAllBeats().observe(this, beats -> {
            try {
                if (beats != null && beats.size() > 0) {
                    Collections.shuffle(beats);
                    for (Beat beat: beats) {
                        if (beat.getGenre().equals(genre)) {
                            this.beatsList.add(beat);
                        }
                    }
                    initCardViewStack(globalIterator);
                }
            } catch (NullPointerException e) {
                goBack();
            }
        });
        mSwipeViewModel.getAllUsers().observe(this, users -> mSwipeViewModel.addUsers(users));
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float maxFirstQuarter = mActivitySwipeBinding.constraintLayout.getMeasuredWidth() / 4;
        double minLastQuarter = mActivitySwipeBinding.constraintLayout.getMeasuredWidth() * .75;
        float x_Center_Cord = mActivitySwipeBinding.constraintLayout.getMeasuredWidth() / 2;
        float y_Center_Cord = mActivitySwipeBinding.constraintLayout.getMeasuredHeight() / 2;
        float X_adjustment = v.getWidth()/2;
        float Y_adjustment = v.getHeight()/2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                v.setX(event.getRawX() - X_adjustment);
                v.setY(event.getRawY() - Y_adjustment);
                if (event.getRawX() < maxFirstQuarter) {
                    remove = true;
                } else if (event.getRawX() > minLastQuarter) {
                    remove = true;
                } else if (minLastQuarter > event.getRawX() && event.getRawX() > maxFirstQuarter ) {
                    remove = false;
                }
                break;
            case  MotionEvent.ACTION_UP:
                if (remove) {
                    removeView(v);
                } else {
                    v.setX(x_Center_Cord - X_adjustment);
                    v.setY(y_Center_Cord - Y_adjustment);
                }
                break;
        }
        return true;
    }

    private void removeView(View v) {
        if (mSwipeViewModel.mHandler != null) mSwipeViewModel.cancelPlay();
        mActivitySwipeBinding.constraintLayout.removeView(v);
        mSwipeViewModel.playNext();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void StartProfileFragment(String uid) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.blank_inflation_layout, null);

        cardView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.MATCH_PARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardView.setElevation(8);
        }
        ConstraintSet set = new ConstraintSet();
        FragmentCardId = cardView.getId();
        mActivitySwipeBinding.constraintLayout.addView(cardView);
        set.clone(mActivitySwipeBinding.constraintLayout);
        set.applyTo(mActivitySwipeBinding.constraintLayout);

        mSwipeViewModel.cancelPlay();
        mSwipeViewModel.getMediaPlayer().prepareAsync();
        mSwipeViewModel.getIsPlaying().set(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.blank_card, ProfileFragment.newInstance(uid, this)).addToBackStack(null).commit();
    }

    @Override
    public void BrowserIntent(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    private void initCardViewStack(int iterator) {
        createCardStackView(iterator);
    }

    private void createCardStackView(final int iterator) {
        if (iterator < this.beatsList.size()) {
            Beat beat = this.beatsList.get(iterator);
            mSwipeViewModel.addBeatAndMedia(beat, createMediaPlayer(beat));
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.swipe_card_view, null);
            cardView.setOnTouchListener(this);
            cardView.setLayoutParams(new CardView.LayoutParams(1080, 1080));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(8);
            }
            ImageView beatImage = cardView.findViewById(R.id.beat_image);
            Picasso.get().load(beat.getImageUrl()).into(beatImage, new Callback() {
                @Override
                public void onSuccess() { }

                @Override
                public void onError(Exception e) { }
            });
            ConstraintSet set = new ConstraintSet();
            mActivitySwipeBinding.constraintLayout.addView(cardView);
            set.clone(mActivitySwipeBinding.constraintLayout);
            set.connect(cardView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            set.connect(cardView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            set.applyTo(mActivitySwipeBinding.constraintLayout);
            initCardViewStack(++globalIterator);
        } else {
            CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.welcome_beatswipe_cardview, null);
            cardView.setOnTouchListener(this);
            cardView.setLayoutParams(new CardView.LayoutParams(1080, 1080));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(8);
            }
            ConstraintSet set = new ConstraintSet();
            mActivitySwipeBinding.constraintLayout.addView(cardView);
            set.clone(mActivitySwipeBinding.constraintLayout);
            set.connect(cardView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            set.connect(cardView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
            set.applyTo(mActivitySwipeBinding.constraintLayout);
            mSwipeViewModel.reorderBeats();
        }
    }

    private MediaPlayer createMediaPlayer(Beat beat) {
        mPlayer = new MediaPlayer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build());
        } else {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        try {
            mPlayer.setDataSource(beat.getAudioUrl());
            mPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mPlayer;
    }

    @Override
    public void onFragmentBacked() {
        mActivitySwipeBinding.constraintLayout.removeView(mActivitySwipeBinding.constraintLayout.getViewById(FragmentCardId));
    }
}