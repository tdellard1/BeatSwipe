package com.example.android.beatswipe.ui.profile;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.databinding.FragmentProfileBinding;
import com.example.android.beatswipe.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private static final String ARGUMENT_USER_ID = "ARGUMENT_USER_ID";

    private FragmentProfileBinding mProfileFragmentBinding;
    private ProfileViewAdapter mProfileViewAdapter;
    private ProfileViewModel mProfileViewModel;
    private RecyclerView mRecyclerView;
    private String mUserId;
    private DetachViewListener mListener;
    public static final String PROFILE_FRAGMENT_TAG = "ProfileFragmentTag";

    public static ProfileFragment newInstance(String userId,@Nullable DetachViewListener listener) {
        ProfileFragment profileFragment = new ProfileFragment();
        if (listener != null) profileFragment.mListener = listener;
        Bundle args = null;
        if (userId != null) {
            args = new Bundle();
            args.putString(ARGUMENT_USER_ID, userId);
        }
        profileFragment.setArguments(args);
        return profileFragment;
    }

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserId = bundle.getString(ARGUMENT_USER_ID);
        }
        mProfileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        mProfileViewModel.setFragmentManager(getFragmentManager());
        mProfileFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        mProfileViewModel.getAllUsers().observe(this, users -> {
            if (users != null) {
                for (User user : users) {
                    if (user.getUserId().equals(mUserId)) {
                        mProfileFragmentBinding.setUser(user);
                    } //else getFragmentManager().popBackStack();
                }
            } else mProfileViewModel.usersNull();
        });
        mProfileFragmentBinding.setViewModel(mProfileViewModel);
        View view = mProfileFragmentBinding.getRoot();
        mRecyclerView = view.findViewById(R.id.profile_recycler_view);
        mProfileViewModel.getAllBeats().observe(this, beats -> {
            List<Beat> ProfileUsersBeats = new ArrayList<>();
            if (beats != null) {
                for (Beat beat : beats) {
                    if (beat.getOwner().equals(mUserId)) ProfileUsersBeats.add(beat);
                }
                boolean profileIsUsers = mProfileViewModel.getCurrentUser().getValue().getUid().equals(mUserId);
                mProfileViewModel.setIsCurrentUser(profileIsUsers);
                mProfileViewAdapter = new ProfileViewAdapter(ProfileUsersBeats, mProfileViewModel, profileIsUsers);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.setAdapter(mProfileViewAdapter);
            } else mProfileViewModel.beatsNull();
        });
        return view;
    }

    @Override
    public void onDetach() {
        if (mListener != null) mListener.onFragmentBacked();
        try {
            mProfileViewModel.mPlayer.stop();
            mProfileViewModel.mPlayer.release();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        Activity main = getActivity();
        if (main != null) {
            main.invalidateOptionsMenu();
        }
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
        super.onDetach();
    }

    public interface DetachViewListener{
        void onFragmentBacked();
    }
}



