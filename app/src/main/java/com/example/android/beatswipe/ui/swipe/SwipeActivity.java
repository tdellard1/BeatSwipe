package com.example.android.beatswipe.ui.swipe;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.databinding.ActivitySwipeBinding;
import com.example.android.beatswipe.ui.main.BeatViewModel;
import com.example.android.beatswipe.ui.profile.ProfileFragment;
import com.example.android.beatswipe.utils.MediaPlayerHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.android.beatswipe.utils.Utils.testRef;
import static java.util.Collections.shuffle;

public class SwipeActivity extends AppCompatActivity implements View.OnTouchListener, SwipeHandler{

    private ActivitySwipeBinding swipeBinding;
    private SwipeViewModel swipeViewModel;
    boolean remove = false;
    private int maxFirstQuarter;
    private double minLastQuarter;
    private float X_Center_Cord;
    private float Y_Center_Cord;

    @Override
    protected void onDestroy() {
        if (swipeViewModel.getMediaPlayerHolder() != null) {
            swipeViewModel.getMediaPlayerHolder().stop();
            swipeViewModel.getMediaPlayerHolder().release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String genre = getIntent().getStringExtra("GENRE");
        swipeViewModel = ViewModelProviders.of(this).get(SwipeViewModel.class);
        swipeViewModel.setsHandler(this);
        swipeBinding = DataBindingUtil.setContentView(this, R.layout.activity_swipe);
        swipeBinding.setSwipeViewModel(swipeViewModel);
        swipeViewModel.getAllBeats().observe(this, beats -> {
            try {
                if (beats != null && beats.size() > 0) {
                    Collections.shuffle(beats);
                    int i = 0;
                    for (Beat beat: beats) {
                        if (beat.getGenre().equals(genre)) {
                            MediaPlayerHolder mediaPlayerHolder = new MediaPlayerHolder(beat, false);
                            swipeViewModel.addBeatAndMedia(beat, mediaPlayerHolder);
                            createCardView(beat);
                        }
                        if (i++ == beats.size() -1 ) {
                            swipeViewModel.reorderBeats();
                        }
                    }
                }
            } catch (NullPointerException e) {
                goBack();
            }
        });
        swipeViewModel.getAllUsers().observe(this, users -> swipeViewModel.addUsers(users));
    }

    //@SuppressLint({"ResourceType", "ClickableViewAccessibility"})
    private void createCardView(Beat beat) {
        CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.swipe_card_view, null);
        cardView.setOnTouchListener(this);
        cardView.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, 600));
        TextView textView = cardView.findViewById(R.id.textView);
        textView.setText(beat.getName());
        ConstraintSet set = new ConstraintSet();
        swipeBinding.constraintLayout.addView(cardView);
        set.clone(swipeBinding.constraintLayout);
        set.connect(cardView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
        set.connect(cardView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        set.applyTo(swipeBinding.constraintLayout);
    }

    /**
     *
     * TODO #1: Figure out best fragment practices
     * TODO #2: Give Fragment A Background(So Other Activity Doesn't Seep Through)
     * @param uid
     */
    @Override
    public void StartProfileFragment(String uid) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setUid(uid);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.constraint_layout, profileFragment).addToBackStack(null).commit();
    }

    @Override
    public void goBack() {
        onBackPressed();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        maxFirstQuarter = swipeBinding.constraintLayout.getMeasuredWidth()/4;
        minLastQuarter = swipeBinding.constraintLayout.getMeasuredWidth() * .75;
        X_Center_Cord = swipeBinding.constraintLayout.getMeasuredWidth() / 2;
        Y_Center_Cord = swipeBinding.constraintLayout.getMeasuredHeight() / 2;
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
                }
                break;
            case  MotionEvent.ACTION_UP:
                if (remove) {
                    removeView(v);
                } else {
                    v.setX(X_Center_Cord - X_adjustment);
                    v.setY(Y_Center_Cord - Y_adjustment);
                }
                break;
        }
        return true;
    }

    private void removeView(View v) {
        swipeBinding.constraintLayout.removeView(v);
        swipeViewModel.playNext();
    }
}
