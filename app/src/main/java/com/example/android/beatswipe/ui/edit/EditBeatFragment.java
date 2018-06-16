package com.example.android.beatswipe.ui.edit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.beatswipe.R;
import com.example.android.beatswipe.data.local.Beat;
import com.example.android.beatswipe.databinding.EditBeatBinding;
import com.example.android.beatswipe.utils.Utils;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static android.app.Activity.RESULT_OK;
import static com.example.android.beatswipe.ui.main.MainActivity.IMAGE_FILE_CODE;

public class EditBeatFragment extends DialogFragment implements EditBeatViewModel.EditBeatHandler {

    private AlertDialog dialog;
    private Beat beat;
    EditBeatBinding mEditBeatBinding;
    EditBeatViewModel mEditBeatViewModel;

    public static EditBeatFragment newInstance(@NonNull Beat beat) {
        EditBeatFragment fragment = new EditBeatFragment();
        fragment.beat = beat;
        return fragment;
    }

    private EditBeatBinding newInstance(LayoutInflater inflater, ViewGroup container, int Layout, EditBeatViewModel viewModel, Beat beat) {
        EditBeatBinding binding = DataBindingUtil.inflate(inflater, Layout, container, false);
        binding.setViewModel(viewModel);
        binding.setBeat(beat);
        return binding;
    }

    public EditBeatFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this beat?")
                .setPositiveButton("Yes", (dialog, which) -> mEditBeatViewModel.deleteBeats())
                .setNegativeButton("No", (dialog, which) -> { return; });
        dialog = builder.create();
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mEditBeatViewModel = EditBeatViewModel.getInstance(this, beat, this);
        mEditBeatBinding = newInstance(inflater, container, R.layout.edit_beat_fragment, mEditBeatViewModel, beat);
        if (beat.getImageUrl() != null) {
            Picasso.get().load(beat.getImageUrl()).into(mEditBeatBinding.imageView);
        }
        mEditBeatViewModel.getProgress().observe(this, aDouble -> {
        });
        View view = mEditBeatBinding.getRoot();
        return view;
    }

    @Override
    public void goBack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void deleteBeat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to delete this beat?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mEditBeatViewModel.deleteBeats();
                    goBack();
                })
                .setNegativeButton("No", (dialog, which) -> {});
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void selectImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mEditBeatViewModel.setImageFileUri(resultUri);
                mEditBeatViewModel.setImageFileName(resultUri.getLastPathSegment());
                Picasso.get().load(resultUri).into(mEditBeatBinding.imageView);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void displayProgress(Double progressChange) {
        try {
            if (progressChange >= 0.0 && progressChange < 100.0 ) {
                mEditBeatBinding.progressbar.setProgress((int)Math.round(progressChange));
            } else if (progressChange == 100.0) {
                goBack();
            }
        } catch (NullPointerException e) {
            // Nothing To Show
            e.printStackTrace();
        }
    }
}
