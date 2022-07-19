package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.UrlAdapter;
import ru.mobnius.cic.adaper.holder.url.OnSelectNewUrl;
import ru.mobnius.cic.databinding.FragmentSettingsBinding;
import ru.mobnius.cic.ui.viewmodels.SettingsViewModel;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.preferences.GeneralPreferences;
import ru.mobnius.simple_core.utils.AlertDialogUtil;
import ru.mobnius.simple_core.utils.AuthUtil;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.VersionUtil;

public class SettingsFragment extends BaseNavigationFragment {
    @Nullable
    private FragmentSettingsBinding binding;
    @Nullable
    private SettingsViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.fragmentSettingsToolbar.setNavigationOnClickListener(v -> handleBackButton());
        binding.fragmentSettingsVersion.setValue(VersionUtil.getVersionName(requireContext()));
        binding.fragmentSettingsReset.setOnClickListener(v -> showPrefResetAlert());
        if (PreferencesManager.getInstance() == null) {
            return binding.getRoot();
        }
        if (!AuthUtil.isSingleUser(requireContext())) {
            binding.fragmentSettingsEnablePin.setEnabled(false);
            binding.fragmentSettingsEnableTouch.setEnabled(false);
            return binding.getRoot();
        }
        final boolean isPin = PreferencesManager.getInstance().isPin();
        binding.fragmentSettingsEnablePin.setChecked(isPin);
        binding.fragmentSettingsEnablePin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                final SettingsFragmentDirections.ActionSettingsToPin action = SettingsFragmentDirections.actionSettingsToPin().setIsCreate(true);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(SettingsFragment.this).navigate(action);
                }
            } else {
                PreferencesManager.getInstance().setPinAuth(false);
                PreferencesManager.getInstance().setPinCode(StringUtil.EMPTY);
            }
        });
        binding.fragmentSettingsEnableTouch.setChecked(PreferencesManager.getInstance().isTouch());
        binding.fragmentSettingsEnableTouch.setOnCheckedChangeListener((buttonView, isChecked) -> PreferencesManager.getInstance().setTouchAuth(isChecked));
        /**
         binding.fragmentSettingsVersion.setOnClickListener(v -> {
         if (viewModel == null || viewModel.debugEnabled) {
         return;
         }
         viewModel.debugCount++;
         if (viewModel.debugCount == 6) {
         viewModel.debugEnabled = true;
         binding.fragmentSettingsDisableDebug.setVisibility(View.VISIBLE);
         binding.fragmentSettingsUrls.setVisibility(View.VISIBLE);
         return;
         }
         if (viewModel.debugCount == 4) {
         Toast.makeText(requireContext(), getString(R.string.two_more_times), Toast.LENGTH_SHORT).show();
         }
         new Handler(Looper.getMainLooper()).postDelayed(() -> {
         if (viewModel.debugCount == viewModel.tempDebugCount) {
         viewModel.debugCount = 0;
         viewModel.tempDebugCount = 0;
         }
         viewModel.tempDebugCount = viewModel.debugCount + 1;
         }, 1000);
         });
         binding.fragmentSettingsDisableDebug.setOnClickListener(v -> {
         if (viewModel == null) {
         return;
         }
         viewModel.debugCount = 0;
         viewModel.debugEnabled = false;
         binding.fragmentSettingsDisableDebug.setVisibility(View.GONE);
         binding.fragmentSettingsUrls.setVisibility(View.GONE);
         });
         */
        binding.fragmentSettingsUrls.setLayoutManager(new LinearLayoutManager(requireContext()));
        final OnSelectNewUrl onSelectNewUrl = (serverUrl, enviroment) -> AlertDialogUtil.cancelableAlert(requireContext(),
                getString(R.string.change_url_warning, serverUrl, enviroment),
                getString(R.string.good),
                (dialog, which) -> {
                    if (GeneralPreferences.getInstance() == null || !(requireActivity().getApplication() instanceof MobniusApplication)) {
                        return;
                    }
                    /*
                    MobniusApplication application = (MobniusApplication) requireActivity().getApplication();
                    GlobalSettings.ENVIRONMENT = enviroment;
                    GlobalSettings.BASE_URL = serverUrl;
                    GeneralPreferences.getInstance().setServerUrl(serverUrl);
                    GeneralPreferences.getInstance().setEnviroment(enviroment);
                    application.clearUserData();
                    requireActivity().finish();

                     */
                });
        final UrlAdapter urlAdapter = new UrlAdapter(onSelectNewUrl, GlobalSettings.availableUrls);
        binding.fragmentSettingsUrls.setAdapter(urlAdapter);
        return binding.getRoot();
    }

    public void showPrefResetAlert() {
        final AlertDialog.Builder adb = new AlertDialog.Builder(requireContext());
        adb.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
            if (PreferencesManager.getInstance() != null) {
                PreferencesManager.getInstance().setTouchAuth(false);
                PreferencesManager.getInstance().setPinAuth(false);
                PreferencesManager.getInstance().setDebug(false);
            }
            if (isCurrentDestination()) {
                NavHostFragment.findNavController(this).navigate(R.id.action_reset_prefs);
            }
        });
        final AlertDialog alert = adb.create();
        alert.setTitle(getString(R.string.attention));
        alert.setMessage(getString(R.string.pref_reset_warning));
        alert.setCancelable(true);
        alert.show();
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
        return destination.getId() == R.id.settings_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }
}
