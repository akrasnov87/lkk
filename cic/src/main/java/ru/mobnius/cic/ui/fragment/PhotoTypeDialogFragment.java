package ru.mobnius.cic.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.ImageTypeAdapter;
import ru.mobnius.cic.adaper.holder.phototypes.OnImageTypeSelectedListener;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.databinding.DialogFragmentImageTypeBinding;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.cic.ui.viewmodels.PhotoTypeViewModel;
import ru.mobnius.cic.ui.viewmodels.result.BaseResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.NewResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.SavedResultViewModel;
import ru.mobnius.simple_core.utils.StringUtil;

public class PhotoTypeDialogFragment extends BottomSheetDialogFragment {
    public static final String TAKE_PHOTO_RESULT = "ru.mobnius.cic.ui.fragment.TAKE_PHOTO_RESULT";
    public static final String CHANGE_PHOTO_RESULT = "ru.mobnius.cic.ui.fragment.CHANGE_PHOTO_RESULT";
    @Nullable
    private BaseResultViewModel viewModel;
    @Nullable
    private PhotoTypeViewModel photoTypeViewModel;
    @Nullable
    private ImageType existingImageType;
    @Nullable
    private DialogFragmentImageTypeBinding binding;
    @Nullable
    private ImageTypeAdapter imageTypeAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        existingImageType = PhotoTypeDialogFragmentArgs.fromBundle(getArguments()).getImageType();
        final boolean isLoadedFromDb = PhotoTypeDialogFragmentArgs.fromBundle(getArguments()).getIsLoadedFromDb();
        if (isLoadedFromDb) {
            viewModel = new ViewModelProvider(requireActivity()).get(SavedResultViewModel.class);
        } else {
            viewModel = new ViewModelProvider(requireActivity()).get(NewResultViewModel.class);
        }
        if (getParentFragment() == null) {
            return;
        }
        photoTypeViewModel = new ViewModelProvider(requireParentFragment()).get(PhotoTypeViewModel.class);
        if (photoTypeViewModel.currentlySelected == null) {
            photoTypeViewModel.currentlySelected = existingImageType;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogFragmentImageTypeBinding.inflate(inflater, container, false);

        if (viewModel == null
                || DataManager.getInstance() == null
                || photoTypeViewModel == null) {
            return binding.getRoot();
        }
        if (existingImageType != null) {
            binding.dialogPhotoTypeTakePicture.setText(R.string.change);
        }
        binding.dialogPhotoTypeList.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (imageTypeAdapter == null) {
            final List<ImageType> imageTypes = DataManager.getInstance().loadPhotoTypes();
            if (photoTypeViewModel.currentlySelected == null) {
                for (int i = 0; i < imageTypes.size(); i++) {
                    final ImageType imageType = imageTypes.get(i);
                    if (imageType.isDefault || i == imageTypes.size() - 1) {
                        photoTypeViewModel.currentlySelected = imageType;
                        break;
                    }
                }
                if (photoTypeViewModel.currentlySelected == null) {
                    return binding.getRoot();
                }
            }
            int selection = -1;
            for (int i = 0; i < imageTypes.size(); i++) {
                if (imageTypes.get(i).typeId == photoTypeViewModel.currentlySelected.typeId) {
                    selection = i;
                }
            }
            binding.dialogPhotoTypeNotice.setText(photoTypeViewModel.currentlySelected.notice);
            final OnImageTypeSelectedListener photoTypeSelectedListener =
                    photoType -> photoTypeViewModel.currentlySelected = photoType;
            imageTypeAdapter = new ImageTypeAdapter(imageTypes, photoTypeSelectedListener, selection);
        }
        binding.dialogPhotoTypeList.setAdapter(imageTypeAdapter);


        binding.dialogPhotoTypeTakePicture.setOnClickListener(v -> {
            if (photoTypeViewModel.currentlySelected == null) {
                Toast.makeText(requireContext(), getString(R.string.must_make_choise), Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            final NavController controller = NavHostFragment.findNavController(requireParentFragment());
            final NavBackStackEntry backStackEntry = controller.getPreviousBackStackEntry();
            if (backStackEntry == null) {
                dismiss();
                return;
            }
            photoTypeViewModel.currentlySelected.notice = StringUtil.defaultEmptyString(binding.dialogPhotoTypeNotice.getText());
            String resultKey;
            if (existingImageType == null) {
                resultKey = TAKE_PHOTO_RESULT;
            } else {
                resultKey = CHANGE_PHOTO_RESULT;
                photoTypeViewModel.currentlySelected.imageId = existingImageType.imageId;

            }
            final ImageType resultImageType = new ImageType(photoTypeViewModel.currentlySelected);
            backStackEntry.getSavedStateHandle().set(resultKey, resultImageType);
            dismiss();
        });
        return binding.getRoot();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (photoTypeViewModel == null) {
            return;
        }
        photoTypeViewModel.currentlySelected = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
