package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.FragmentFilterBinding;
import ru.mobnius.cic.data.search.PointFilter;
import ru.mobnius.cic.ui.viewmodels.PointViewModel;

public class FilterFragment extends BaseNavigationFragment {
    @Nullable
    private FragmentFilterBinding binding;
    @Nullable
    private PointViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PointViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        binding.fragmentFilterToolbar.setNavigationOnClickListener(view -> handleBackButton());
        if (viewModel == null) {
            return binding.getRoot();
        }
        binding.fragmentFilterAllFields.setChecked(viewModel.isAllFields());
        binding.fragmentFilterAllFields.setOnCheckableClickListener(() -> {
            viewModel.setAreaFilter(PointFilter.AREA_ALL);
            binding.fragmentFilterAllFields.select();
            binding.fragmentFilterSubscrNumber.unSelect();
            binding.fragmentFilterDeviceNumber.unSelect();
            binding.fragmentFilterAddress.unSelect();
            binding.fragmentFilterOwner.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });
        binding.fragmentFilterSubscrNumber.setChecked(viewModel.isSubscrNumberOnly());
        binding.fragmentFilterSubscrNumber.setOnCheckableClickListener(() -> {
            viewModel.setAreaFilter(PointFilter.AREA_SUBSCR_NUMBER);
            binding.fragmentFilterAllFields.unSelect();
            binding.fragmentFilterSubscrNumber.select();
            binding.fragmentFilterDeviceNumber.unSelect();
            binding.fragmentFilterAddress.unSelect();
            binding.fragmentFilterOwner.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterDeviceNumber.setChecked(viewModel.isDeviceNumberOnly());
        binding.fragmentFilterDeviceNumber.setOnCheckableClickListener(() -> {
            viewModel.setAreaFilter(PointFilter.AREA_DEVICE_NUMBER);
            binding.fragmentFilterAllFields.unSelect();
            binding.fragmentFilterSubscrNumber.unSelect();
            binding.fragmentFilterDeviceNumber.select();
            binding.fragmentFilterAddress.unSelect();
            binding.fragmentFilterOwner.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterAddress.setChecked(viewModel.isAddressOnly());
        binding.fragmentFilterAddress.setOnCheckableClickListener(() -> {
            viewModel.setAreaFilter(PointFilter.AREA_ADDRESS);
            binding.fragmentFilterAllFields.unSelect();
            binding.fragmentFilterSubscrNumber.unSelect();
            binding.fragmentFilterDeviceNumber.unSelect();
            binding.fragmentFilterAddress.select();
            binding.fragmentFilterOwner.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterOwner.setChecked(viewModel.isOwnerOnly());
        binding.fragmentFilterOwner.setOnCheckableClickListener(() -> {
            viewModel.setAreaFilter(PointFilter.AREA_OWNER_NAME);
            binding.fragmentFilterAllFields.unSelect();
            binding.fragmentFilterSubscrNumber.unSelect();
            binding.fragmentFilterDeviceNumber.unSelect();
            binding.fragmentFilterAddress.unSelect();
            binding.fragmentFilterOwner.select();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterSubscrTypeAll.setChecked(viewModel.companyTypeFilter.isNotAdded() && viewModel.personTypeFilter.isNotAdded());
        binding.fragmentFilterSubscrTypeAll.setOnCheckableClickListener(() -> {
            viewModel.setTypeFilter(PointFilter.TYPE_ALL);
            binding.fragmentFilterSubscrTypeAll.select();
            binding.fragmentFilterSubscrTypePerson.unSelect();
            binding.fragmentFilterSubscrTypeCompany.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterSubscrTypePerson.setChecked(viewModel.personTypeFilter.isAdded());
        binding.fragmentFilterSubscrTypePerson.setOnCheckableClickListener(() -> {
            viewModel.setTypeFilter(PointFilter.TYPE_PERSON);
            binding.fragmentFilterSubscrTypePerson.select();
            binding.fragmentFilterSubscrTypeAll.unSelect();
            binding.fragmentFilterSubscrTypeCompany.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterSubscrTypeCompany.setChecked(viewModel.companyTypeFilter.isAdded());
        binding.fragmentFilterSubscrTypeCompany.setOnCheckableClickListener(() -> {
            viewModel.setTypeFilter(PointFilter.TYPE_COMPANY);
            binding.fragmentFilterSubscrTypeCompany.select();
            binding.fragmentFilterSubscrTypeAll.unSelect();
            binding.fragmentFilterSubscrTypePerson.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterPointStatusAll.setChecked(viewModel.doneFilter.isNotAdded() && viewModel.undoneFilter.isNotAdded());
        binding.fragmentFilterPointStatusAll.setOnCheckableClickListener(() -> {
            viewModel.setStatusFilter(PointFilter.STATUS_ALL);
            binding.fragmentFilterPointStatusAll.select();
            binding.fragmentFilterPointStatusDone.unSelect();
            binding.fragmentFilterPointStatusUndone.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterPointStatusDone.setChecked(viewModel.doneFilter.isAdded());
        binding.fragmentFilterPointStatusDone.setOnCheckableClickListener(() -> {
            viewModel.setStatusFilter(PointFilter.STATUS_DONE);
            binding.fragmentFilterPointStatusDone.select();
            binding.fragmentFilterPointStatusAll.unSelect();
            binding.fragmentFilterPointStatusUndone.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterPointStatusUndone.setChecked(viewModel.undoneFilter.isAdded());
        binding.fragmentFilterPointStatusUndone.setOnCheckableClickListener(() -> {
            viewModel.setStatusFilter(PointFilter.STATUS_UNDONE);
            binding.fragmentFilterPointStatusUndone.select();
            binding.fragmentFilterPointStatusAll.unSelect();
            binding.fragmentFilterPointStatusDone.unSelect();
            handleApplyButton(viewModel.isDifferent());
        });

        binding.fragmentFilterApply.setOnClickListener(view -> {
            if (viewModel.loadedItems.size() > 0) {
                viewModel.setNewState();
            }
            if (isCurrentDestination()) {
                NavHostFragment.findNavController(this).navigateUp();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleApplyButton(final boolean isDifferent) {
        if (binding == null) {
            return;
        }
        if (isDifferent) {
            binding.fragmentFilterApplyLayout.setVisibility(View.VISIBLE);
        } else {
            binding.fragmentFilterApplyLayout.setVisibility(View.GONE);
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
        return destination.getId() == R.id.filter_fragment;
    }

    private void handleBackButton() {
        if (viewModel != null) {
            viewModel.returnToState();
        }
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}
