package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import ru.mobnius.cic.R;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.AuthTask;
import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.databinding.FragmentPincodeBinding;
import ru.mobnius.cic.ui.pincode.PinCodeDigitsCallback;
import ru.mobnius.cic.ui.pincode.factory.PinCodeResult;
import ru.mobnius.cic.ui.viewmodels.PinCodeViewModel;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.authorization.AuthorizationMeta;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.data.credentials.BasicCredentials;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.VersionUtil;

public class PinCodeFragment extends Fragment {
    @Nullable
    private FragmentPincodeBinding binding;
    @Nullable
    private PinCodeViewModel viewModel;
    @Nullable
    private Observer<PinCodeResult> refreshPinObserver;
    @Nullable
    private Observer<PinCodeResult> backspacePinObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PinCodeViewModel.class);
        viewModel.isCreate = PinCodeFragmentArgs.fromBundle(getArguments()).getIsCreate();
        refreshPinObserver = result -> {
            if (binding == null) {
                return;
            }
            if (result.isNewPinCode()) {
                handleNewPinCodeClick(result);
            } else {
                binding.fragmentPincodeDigits.fillDot(result.pinCode().length());
                if (PreferencesManager.getInstance() == null) {
                    NavHostFragment.findNavController(PinCodeFragment.this).navigate(R.id.action_pinauth_to_auth);
                }
                if (StringUtil.equals(result.pinCode(), PreferencesManager.getInstance().getPinCode())) {
                    boolean success = Authorization.createPinOrTouchInstance(requireContext());
                    if (success) {
                        handleExistPinCodeSuccess();
                    } else {
                        NavHostFragment.findNavController(PinCodeFragment.this).navigate(R.id.action_pinauth_to_auth);
                    }
                } else {
                    if (!result.continueEnter()) {
                        binding.fragmentPincodeDigits.wrongClear();
                    }
                }
            }
        };

        backspacePinObserver = pinCodeResult -> {
            if (binding == null) {
                return;
            }
            binding.fragmentPincodeDigits.clearOneDot(pinCodeResult.pinCode().length());
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPincodeBinding.inflate(inflater, container, false);
        if (viewModel == null || refreshPinObserver == null || backspacePinObserver == null) {
            return binding.getRoot();
        }
        if (viewModel.isCreate) {
            binding.fragmentPincodeDigits.hideDrop();
        }
        binding.fragmentPincodeDigits.setDigitCallback(new PinCodeDigitsCallback() {
            @Override
            public void onDigitEntered(final @NonNull String digit) {
                viewModel.refreshPinCode(digit).observe(getViewLifecycleOwner(), refreshPinObserver);
            }

            @Override
            public void onBackspacePressed() {
                viewModel.clearOneDigit().observe(getViewLifecycleOwner(), backspacePinObserver);
            }

            @Override
            public void onPinDropConfirmed() {
                if (PreferencesManager.getInstance() != null) {
                    PreferencesManager.getInstance().setPinAuth(false);
                    PreferencesManager.getInstance().setPinCode(StringUtil.EMPTY);
                }
                NavHostFragment.findNavController(PinCodeFragment.this).popBackStack(R.id.pincode_auth_fragment, true);
                final NavController navController = Navigation.findNavController(requireActivity(), R.id.auth_nav_host);
                final NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
                navGraph.setStartDestination(R.id.auth_nav);
                navController.setGraph(navGraph);
            }
        });
        binding.fragmentPincodeDigits.fillDotsUntil(viewModel.pinCode.length());
        return binding.getRoot();
    }

    private void handleNewPinCodeClick(final @NonNull PinCodeResult result) {
        if (binding == null) {
            return;
        }
        if (result.enterPinAgain()) {
            binding.fragmentPincodeDigits.clearDots();
        } else {
            binding.fragmentPincodeDigits.fillDot(result.pinCode().length());
            if (result.continueEnter()) {
                return;
            }
            if (result.pinsNotEqual()) {
                binding.fragmentPincodeDigits.wrongClear();
                return;
            }
            NavHostFragment.findNavController(PinCodeFragment.this).navigate(R.id.action_pin_to_routes);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void handleExistPinCodeSuccess() {
        if (binding == null || Authorization.getInstance() == null || Authorization.getInstance().user == null) {
            return;
        }
        binding.fragmentPincodeProgress.setVisibility(View.VISIBLE);
        binding.fragmentPincodeEnterPin.setVisibility(View.GONE);
        binding.fragmentPincodeDigits.blockDigits();
        final BasicCredentials credentials = Authorization.getInstance().user.getCredentials();
        final MainTaskExecutor mainTaskExecutor = new MainTaskExecutor();
        final AuthTask authTask = new AuthTask(credentials.login, credentials.password, VersionUtil.getVersionName(requireContext()));
        mainTaskExecutor.executeAsync(authTask, new MainTaskExecutorCallback<AuthorizationMeta>() {
            @Override
            public void onCallableComplete(@NonNull AuthorizationMeta result) {
                if (!isAdded()) {
                    return;
                }
                binding.fragmentPincodeProgress.setVisibility(View.GONE);
                binding.fragmentPincodeEnterPin.setVisibility(View.VISIBLE);
                binding.fragmentPincodeDigits.activateDigits();
                ((MobniusApplication) requireActivity().getApplication()).onAuthorized(MobniusApplication.PIN);
                NavHostFragment.findNavController(PinCodeFragment.this).navigate(R.id.action_pin_to_routes);
            }

            @Override
            public void onCallableError(@NonNull String error) {
                binding.fragmentPincodeProgress.setVisibility(View.GONE);
                binding.fragmentPincodeEnterPin.setVisibility(View.VISIBLE);
                binding.fragmentPincodeDigits.activateDigits();
                ((MobniusApplication) requireActivity().getApplication()).onAuthorized(MobniusApplication.PIN);

                NavHostFragment.findNavController(PinCodeFragment.this).navigate(R.id.action_pin_to_routes);
            }
        });
    }

}
