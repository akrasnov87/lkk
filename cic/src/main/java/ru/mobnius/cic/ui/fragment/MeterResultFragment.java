package ru.mobnius.cic.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.ImageAdapter;
import ru.mobnius.cic.adaper.ReadingsAdapter;
import ru.mobnius.cic.adaper.holder.image.OnImageChangeClickListener;
import ru.mobnius.cic.adaper.holder.image.OnImageItemClickListener;
import ru.mobnius.cic.adaper.holder.image.OnPlaceholderClickListener;
import ru.mobnius.cic.databinding.FragmentMeterResultBinding;
import ru.mobnius.cic.ui.component.CicEditText;
import ru.mobnius.cic.ui.model.ImageItem;
import ru.mobnius.cic.ui.model.ImageType;
import ru.mobnius.cic.ui.model.MeterItem;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.cic.ui.verification.VerifiableField;
import ru.mobnius.cic.ui.viewmodels.result.BaseResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.NewResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.SavedResultViewModel;
import ru.mobnius.cic.ui.viewmodels.result.TakePhotoObserver;
import ru.mobnius.simple_core.utils.AlertDialogUtil;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.FileUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class MeterResultFragment extends BaseNavigationFragment {
    public static final String ACT_SAVED_RESULT = "ru.mobnius.cic.ui.fragment.ACT_SAVED_RESULT";


    @Nullable
    private BaseResultViewModel viewModel;
    @Nullable
    private FragmentMeterResultBinding binding;
    @Nullable
    private TakePhotoObserver takePhotoObserver;
    @Nullable
    private ImageAdapter imageAdapter;
    @Nullable
    private ReadingsAdapter readingsAdapter;
    @Nullable
    private Observer<List<ImageItem>> imageObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final PointItem pointItem = MeterResultFragmentArgs.fromBundle(getArguments()).getPointItem();
        if (StringUtil.isEmpty(pointItem.resultId)) { //TODO: сделать бы это как то по другому
            viewModel = new ViewModelProvider(requireActivity()).get(NewResultViewModel.class);
        } else {
            viewModel = new ViewModelProvider(requireActivity()).get(SavedResultViewModel.class);
        }
        viewModel.pointItem = pointItem;
        viewModel.initNewModel();
        final ActivityResultCallback<Boolean> callback = result -> {
            if (result == null || !result || viewModel.absFilePath == null) {
                return;
            }
            final Observer<ImageItem> imageItemObserver = item -> {
                if (imageAdapter == null || item == null || !isAdded()) {
                    return;
                }
                imageAdapter.insert(item);
                if (binding != null) {
                    binding.fragmentMeterSave.setEnabled(enableSave());
                    binding.fragmentPhotoValidationMessage.setText(viewModel.getPhotoValidationMessage());
                }
            };
            viewModel.processNewImage(getLocation()).observe(this, imageItemObserver);
        };
        takePhotoObserver = new TakePhotoObserver(requireActivity().getActivityResultRegistry(), callback);
        getLifecycle().addObserver(takePhotoObserver);

        imageObserver = imageItems -> {
            if (imageAdapter == null || !isAdded()) {
                return;
            }
            imageAdapter.swapList(imageItems);
            if (binding == null || viewModel == null) {
                return;
            }
            binding.fragmentPhotoValidationMessage.setText(viewModel.getPhotoValidationMessage());
        };

        viewModel.loadingImageItemsData.observe(this, loading -> {
            if (binding == null || isAdded()) {
                return;
            }
            if (loading) {
                binding.fragmentMeterImageList.setVisibility(View.GONE);
                binding.fragmentMeterImageLoading.setVisibility(View.VISIBLE);
            } else {
                binding.fragmentMeterImageList.setVisibility(View.VISIBLE);
                binding.fragmentMeterImageLoading.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = NavHostFragment.findNavController(this);
        final NavBackStackEntry backStackEntry = navController.getBackStackEntry(R.id.meter_result_fragment);
        final LifecycleEventObserver observer = (source, event) -> {
            if (takePhotoObserver == null || viewModel == null) {
                return;
            }
            if (!event.equals(Lifecycle.Event.ON_RESUME)) {
                return;
            }
            if (backStackEntry.getSavedStateHandle().contains(PhotoTypeDialogFragment.TAKE_PHOTO_RESULT)) {
                final ImageType result = backStackEntry.getSavedStateHandle().get(PhotoTypeDialogFragment.TAKE_PHOTO_RESULT);
                backStackEntry.getSavedStateHandle().remove(PhotoTypeDialogFragment.TAKE_PHOTO_RESULT);
                if (result == null) {
                    Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!permissionsHandler.isFileAndCameraPermissionGranted(requireActivity())) {
                    AlertDialogUtil.alert(requireContext(), getString(R.string.file_and_camera_no_permission_message),
                            getString(R.string.confirm),
                            (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }, null, null);
                    return;
                }
                try {
                    final File photoFile = FileUtil.getFileForCamera(requireContext());
                    if (photoFile == null) {
                        return;
                    }
                    final Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", photoFile);
                    requireContext().grantUriPermission(requireContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    viewModel.selectedImageType = result;
                    viewModel.absFilePath = photoFile.getAbsolutePath();
                    viewModel.fileName = photoFile.getName();
                    takePhotoObserver.takePhoto(uri);
                } catch (IllegalArgumentException | IOException e) {
                    e.printStackTrace();
                }
            }

            if (backStackEntry.getSavedStateHandle().contains(PhotoTypeDialogFragment.CHANGE_PHOTO_RESULT)) {
                final ImageType result = backStackEntry.getSavedStateHandle().get(PhotoTypeDialogFragment.CHANGE_PHOTO_RESULT);
                backStackEntry.getSavedStateHandle().remove(PhotoTypeDialogFragment.CHANGE_PHOTO_RESULT);
                if (result == null) {
                    return;
                }
                viewModel.updateImage(result);

                if (imageAdapter == null) {
                    return;
                }
                imageAdapter.updateImageItem(result);
                if (binding == null) {
                    return;
                }
                binding.fragmentMeterSave.setEnabled(enableSave());
                binding.fragmentPhotoValidationMessage.setText(viewModel.getPhotoValidationMessage());
            }
        };

        backStackEntry.getLifecycle().addObserver(observer);

        getViewLifecycleOwner().getLifecycle().addObserver((LifecycleEventObserver) (source, event) ->
        {
            if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                final NavController removeObserverController = NavHostFragment.findNavController(this);
                final NavBackStackEntry removeObserverBackStackEntry = removeObserverController.getCurrentBackStackEntry();
                if (removeObserverBackStackEntry == null) {
                    return;
                }
                removeObserverBackStackEntry.getLifecycle().removeObserver(observer);
            }
        });
        final NavBackStackEntry curretnBackStackEntry = navController.getCurrentBackStackEntry();
        if (curretnBackStackEntry == null) {
            return;
        }
        final MutableLiveData<String> saveData = backStackEntry
                .getSavedStateHandle()
                .getLiveData(ImageViewFragment.IMAGE_DELETE_RESULT);
        saveData.observe(getViewLifecycleOwner(), imageId -> {
            if (StringUtil.isEmpty(imageId)
                    || imageAdapter == null
                    || viewModel == null
                    || binding == null) {
                return;
            }
            imageAdapter.removeImageItem(imageId);
            viewModel.disableImage(imageId);
            binding.fragmentMeterSave.setEnabled(enableSave());
            binding.fragmentPhotoValidationMessage.setText(viewModel.getPhotoValidationMessage());
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeterResultBinding.inflate(inflater, container, false);
        binding.fragmentMeterToolbar.setNavigationOnClickListener(v -> handleBackButton());
        binding.fragmentMeterImageList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.fragmentMeterMetersList.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (viewModel == null || viewModel.pointItem == null) {
            return binding.getRoot();
        }
        if (viewModel.pointItem.reject) {
            binding.fragmentMeterDispatcherComment.setVisibility(View.VISIBLE);
            binding.fragmentMeterDispatcherComment.setValue(viewModel.getRejectMessage());
        }

        binding.fragmentMeterToolbar.setTitle(MobniusApplication.METER_READINGS_ACT_NAME);
        binding.fragmentMeterAccountNumber.setValue(viewModel.pointItem.accountNumber);
        binding.fragmentMeterOwnerName.setValue(viewModel.pointItem.owner);
        binding.fragmentMeterDeviceNumber.setValue(viewModel.pointItem.deviceNumber);
        binding.fragmentMeterDeviceType.setValue(viewModel.pointItem.deviceTypeName);

        binding.fragmentMeterFailureReason.setData(viewModel.getMobileCausesList(), false);
        binding.fragmentMeterFailureReason.setOnCicIconSelectedListener((map, id) -> {
            viewModel.getMobileCauseItem().mobileCauseId = id;
            binding.fragmentMeterSave.setEnabled(enableSave());
            binding.fragmentPhotoValidationMessage.setText(viewModel.getPhotoValidationMessage());
        });

        binding.fragmentMeterNotice.addCicTextChangedListener(editable -> {
            viewModel.notice = StringUtil.defaultEmptyString(editable);
            binding.fragmentMeterSave.setEnabled(enableSave());
        });
        binding.fragmentMeterNotice.setText(viewModel.notice);
        binding.fragmentMeterFailureReason.setSelectionById(viewModel.getMobileCauseItem().mobileCauseId);


        if (imageAdapter == null) {
            final OnImageItemClickListener itemClickListener = imageItem -> {
                if (isCurrentDestination()) {
                    MeterResultFragmentDirections.ActionMeterToImage action =
                            MeterResultFragmentDirections.actionMeterToImage(imageItem.id, viewModel.pointItem.done);
                    NavHostFragment.findNavController(this).navigate(action);
                }
            };
            final OnImageChangeClickListener changeClickListener = imageItem -> {
                if (isCurrentDestination()) {
                    MeterResultFragmentDirections.ActionMeterToPhotoType action =
                            MeterResultFragmentDirections.actionMeterToPhotoType().setIsLoadedFromDb(viewModel.pointItem.done);
                    imageItem.imageType.imageId = imageItem.id;
                    action.setImageType(imageItem.imageType);
                    NavHostFragment.findNavController(this).navigate(action);
                }
            };
            final OnPlaceholderClickListener placeholderClickListener = () -> {
                if (isCurrentDestination()) {
                    MeterResultFragmentDirections.ActionMeterToPhotoType action =
                            MeterResultFragmentDirections.actionMeterToPhotoType().setIsLoadedFromDb(viewModel.pointItem.done);
                    NavHostFragment.findNavController(this).navigate(action);
                }
            };
            imageAdapter = new ImageAdapter(itemClickListener, changeClickListener, placeholderClickListener);
        }
        binding.fragmentMeterImageList.setAdapter(imageAdapter);
        if (imageObserver != null) {
            viewModel.getImageItems(requireContext()).observe(getViewLifecycleOwner(), imageObserver);
        }
        final CicEditText.CicMetersUpdateListener metersUpdateListener = (value, someId) -> {
            if (StringUtil.isEmpty(someId)) {
                return;
            }
            viewModel.updateMeter(value, someId);
            binding.fragmentMeterSave.setEnabled(enableSave());
        };
        final Observer<List<MeterItem>> meterObserver = meterItems -> {
            if (readingsAdapter == null) {
                readingsAdapter = new ReadingsAdapter(meterItems, metersUpdateListener, viewModel.verificationManager);
            }
            if (meterItems.size() > 0) {
                final Date previousDate = meterItems.get(0).previousDate;
                final String dateText = DateUtil.getNonNullDateTextShort(previousDate);
                binding.fragmentMeterPreviousDate.setValue(dateText);
            }
            binding.fragmentMeterCurrentDate.setValue(viewModel.getCurrentDate());
            binding.fragmentMeterMetersList.setAdapter(readingsAdapter);
            binding.fragmentPhotoValidationMessage.setText(viewModel.getPhotoValidationMessage());
        };
        viewModel.getMeterItems().observe(getViewLifecycleOwner(), meterObserver);

        binding.fragmentMeterSave.setOnClickListener(view -> {
            if (isLocationEnabled()) {
                onValidateMessage(viewModel.verificationManager.verify(binding.fragmentMeterScroll, viewModel.isMobileCauseSelected()));
                return;
            }
            AlertDialogUtil.alert(requireContext(),
                    getString(R.string.location_not_enabled_can_not_save_message), getString(R.string.good), (dialog, which) -> {
                        try {
                            final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            startActivity(intent);
                        } catch (ActivityNotFoundException | SecurityException e) {
                            Toast.makeText(requireContext(),
                                    StringUtil.defaultEmptyString(e.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }, null, null);
        });

        binding.fragmentMeterReceipt.setChecked(viewModel.isReceipt);
        binding.fragmentMeterNotificationOrp.setChecked(viewModel.isNotificationOrp);
        binding.fragmentMeterReceipt
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    viewModel.isReceipt = isChecked;
                    binding.fragmentMeterSave.setEnabled(enableSave());
                });
        binding.fragmentMeterNotificationOrp
                .setOnCheckedChangeListener((buttonView, isChecked) -> {
                    viewModel.isNotificationOrp = isChecked;
                    binding.fragmentMeterSave.setEnabled(enableSave());
                });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean enableSave() {
        if (viewModel == null) {
            return false;
        }
        return viewModel.enableSaveButton();
    }

    protected void onValidateMessage(int status) {
        if (viewModel == null || binding == null) {
            return;
        }
        final Location userLocation = getLocation();
        final Observer<SavedResult> saveResultObserver = saveResult -> {
            if (!isAdded()) {
                return;
            }
            Toast.makeText(requireContext(), saveResult.message, Toast.LENGTH_SHORT).show();
            if (saveResult.isSuccess && isCurrentDestination()) {
                final NavController controller = NavHostFragment.findNavController(this);
                final NavBackStackEntry backStackEntry = controller.getPreviousBackStackEntry();
                if (backStackEntry == null) {
                    NavHostFragment.findNavController(this).popBackStack(R.id.meter_result_fragment, true);
                    return;
                }
                backStackEntry.getSavedStateHandle().set(ACT_SAVED_RESULT, saveResult);
                if (viewModel != null) {
                    viewModel.destroy();
                }
                NavHostFragment.findNavController(this).popBackStack(R.id.meter_result_fragment, true);

            }
        };
        switch (status) {
            case VerifiableField.CAN_SAVE:
                viewModel.save(userLocation).observe(getViewLifecycleOwner(), saveResultObserver);
                break;
            case VerifiableField.CAN_NOT_SAVE:
                AlertDialogUtil.alert(requireContext(), getString(R.string.can_not_save),
                        getString(R.string.good), (dialog, which) -> dialog.dismiss(),
                        null, null);
                break;
            case VerifiableField.CAN_SAVE_AFTER_NEW_LESS_OLD_MESSAGE:
                AlertDialogUtil.alert(requireContext(), getString(R.string.new_less_then_old),
                        getString(R.string.yes), (dialog, which) -> viewModel.save(userLocation).observe(getViewLifecycleOwner(), saveResultObserver),
                        getString(R.string.no), (dialog, which) -> dialog.dismiss());
                break;
            case VerifiableField.CAN_SAVE_AFTER_MOUNT_AVG_MESSAGE:
                AlertDialogUtil.alert(requireContext(), getString(R.string.avg_high_save),
                        getString(R.string.yes), (dialog, which) -> viewModel.save(userLocation).observe(getViewLifecycleOwner(), saveResultObserver),
                        getString(R.string.no), (dialog, which) -> dialog.dismiss());
                break;
            default:
                break;
        }
    }

    /**
     * Обработка нажатия системной кнопки назад
     */
    @NonNull
    @Override
    public OnBackPressedCallback getBackPressedCallBack() {
        return new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackButton();
            }
        };
    }

    /**
     * Проверка является ли текущий фрагмент подходящим для навигации
     */
    @Override
    public boolean isCurrentDestination() {
        final NavDestination destination = NavHostFragment.findNavController(this).getCurrentDestination();
        if (destination == null) {
            return false;
        }
        return destination.getId() == R.id.meter_result_fragment;
    }

    private void handleBackButton() {
        if (viewModel != null) {
            viewModel.destroy();
        }
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).popBackStack(R.id.meter_result_fragment, true);
        }
    }
}
