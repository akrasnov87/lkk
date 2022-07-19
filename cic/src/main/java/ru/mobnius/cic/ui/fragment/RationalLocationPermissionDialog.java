package ru.mobnius.cic.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.DialogFragment;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.DialogFragmentLocationExplanationBinding;

public class RationalLocationPermissionDialog extends DialogFragment {
    public final static String RATIONAL_LOCATION_DIALOG_TAG = "ru.mobnius.cic.ui.fragment.RATIONAL_DIALOG_TAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final DialogFragmentLocationExplanationBinding binding = DialogFragmentLocationExplanationBinding.inflate(inflater, container, false);
        if (AppCompatResources.getDrawable(requireContext(), R.drawable.ic_location_permission_explanation_start) instanceof AnimatedVectorDrawable) {
            final AnimatedVectorDrawable drawable =
                    (AnimatedVectorDrawable) AppCompatResources.getDrawable(requireContext(), R.drawable.ic_location_permission_explanation_start);
            binding.locationPermissionDialogIcon.setImageDrawable(drawable);
            if (drawable != null) {
                drawable.start();
            }
        }
        binding.locationPermissionDialogOk.setOnClickListener(v -> {
            final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            final Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            dismiss();
        });
        return binding.getRoot();
    }
}
