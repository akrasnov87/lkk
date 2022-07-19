package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.FragmentProfileBinding;
import ru.mobnius.cic.ui.model.concurent.UserProfile;
import ru.mobnius.cic.ui.viewmodels.ProfileViewModel;
import ru.mobnius.simple_core.data.authorization.Authorization;

public class ProfileFragment extends BaseNavigationFragment {
    @Nullable
    private FragmentProfileBinding binding;
    @Nullable
    private ProfileViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        binding.fragmentProfileToolbar.setNavigationOnClickListener(v -> handleBackButton());
        binding.fragmentProfileToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_personal_account) {
                NavHostFragment.findNavController(this).navigate(R.id.action_profile_to_personal_account);
                return true;
            }
            return false;
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        if (binding == null){
            return;
        }
        final Observer<UserProfile> userProfileObserver = userProfile -> {
            if (userProfile == null) {
                return;
            }
            if (userProfile.isError) {
                Toast.makeText(requireContext(), userProfile.error, Toast.LENGTH_SHORT).show();
                return;
            }
            binding.fragmentProfileUserRating.setRating((float) userProfile.nRating);
            binding.fragmentProfileAddress.setValueText(userProfile.cAddress);
            binding.fragmentProfileEmail.setValueText(userProfile.cEmail);
            binding.fragmentProfileName.setValueText(userProfile.cFirstName);
            binding.fragmentProfileMiddleName.setValueText(userProfile.cMiddleName);
            binding.fragmentProfileSurname.setValueText(userProfile.cLastName);
            binding.fragmentProfileUserNameTitle.setText(userProfile.cLogin);
            binding.fragmentProfileAddress.setValueText(userProfile.cAddress);
            binding.fragmentProfilePhone.setValueText(userProfile.cPhone);
            Glide.with(this).asDrawable()
                    .load(userProfile.imageUrl)
                    .centerCrop().into(binding.fragmentProfileUserPhoto);
        };
        viewModel.loadUserProfile().observe(getViewLifecycleOwner(), userProfileObserver);
        binding.fragmentProfileUserNameTitle.setText(viewModel.getTitleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        return destination.getId() == R.id.profile_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }
}
