package com.example.android.beatswipe.ui.individualbeat;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.databinding.EditBeatBinding;

public class EditBeatFragment extends Fragment implements EditBeatHandler {

    public static final String EDIT_BEAT_TAG = "EditBeatFragment";
    private Beat beat;
    EditBeatBinding binding;
    EditBeatViewModel viewModel;

    public EditBeatFragment() {}
    public void setBeat(Beat beat) { this.beat = beat; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this).get(EditBeatViewModel.class);
        viewModel.setOriginalProperties(beat);
        viewModel.setHandler(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.edit_beat_fragment, container, false);
        View view = binding.getRoot();
        binding.setViewModel(viewModel);
        binding.setBeat(beat);
        return view;
    }

    @Override
    public void goBack() {
        getFragmentManager().popBackStack();
    }
}
