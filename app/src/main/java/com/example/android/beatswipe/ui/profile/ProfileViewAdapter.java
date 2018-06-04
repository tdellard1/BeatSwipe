package com.example.android.beatswipe.ui.profile;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.databinding.BeatBinding;
import java.util.List;

/**
 *
 * TODO #1: Get Media Player to only have one instance playing.
 * TODO #2: Create A Bar AT The Bottom Of The Layout TO Display Currently Playing Media
 * TODO #3: Create A Way For Users To Update(Edit or Delete) Each Beat.
 */
public class ProfileViewAdapter extends RecyclerView.Adapter<ProfileViewAdapter.ViewHolder> {

    private boolean profileIsUser;
    private List<Beat> beats;
    private ProfileViewModel profileViewModel;
    private LayoutInflater layoutInflater;

    ProfileViewAdapter(List<Beat> beats, ProfileViewModel profileViewModel, boolean profileIsUser) {
        this.beats = beats;
        this.profileViewModel = profileViewModel;
        this.profileIsUser = profileIsUser;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        BeatBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.layout_individual_beat, parent, false);
        binding.setViewModel(profileViewModel);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Beat beat = beats.get(position);
        holder.beatBinding.setBeat(beat);
        if (profileIsUser) holder.beatBinding.EditBeatButton.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() { return beats.size(); }

    class ViewHolder extends RecyclerView.ViewHolder{

        private BeatBinding beatBinding;

        ViewHolder(BeatBinding binding) {
            super(binding.getRoot());
            this.beatBinding = binding;
        }
    }
}
