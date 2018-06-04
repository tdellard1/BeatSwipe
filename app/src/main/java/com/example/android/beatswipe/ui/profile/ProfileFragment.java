package com.example.android.beatswipe.ui.profile;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.data.local.User;
import com.example.android.beatswipe.databinding.FragmentProfileBinding;
import com.example.android.beatswipe.ui.login.LogInActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private ProfileViewAdapter profileViewAdapter;
    private RecyclerView recyclerView;
    private FragmentProfileBinding profileFragmentBinding;
    private User mUser;

    private String uid;

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        profileViewModel.setFragmentManager(getFragmentManager());
        profileFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        profileViewModel.getAllUsers().observe(this, users -> {
            if (users != null) {
                for (User user : users) {
                    if (user.getUserId().equals(uid)) {
                        profileFragmentBinding.setUser(user);
                    } //else getFragmentManager().popBackStack();
                }
            } else profileViewModel.usersNull();
        });
        profileFragmentBinding.setViewModel(profileViewModel);
        View view = profileFragmentBinding.getRoot();
        recyclerView = view.findViewById(R.id.profile_recycler_view);
        profileViewModel.getAllBeats().observe(this, beats -> {
            List<Beat> ProfileUsersBeats = new ArrayList<>();
            if (beats != null) {
                for (Beat beat : beats) {
                    if (beat.getOwner().equals(uid)) ProfileUsersBeats.add(beat);
                }
                boolean profileIsUsers = profileViewModel.getCurrentUser().getValue().getUid().equals(uid);
                profileViewAdapter = new ProfileViewAdapter(ProfileUsersBeats, profileViewModel, profileIsUsers);
                recyclerView.setAdapter(profileViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            } else profileViewModel.beatsNull();
        });
        return view;
    }

    @Override
    public void onDetach() {
        try {
            profileViewModel.mPlayer.stop();
            profileViewModel.mPlayer.release();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        super.onDetach();
    }
}



