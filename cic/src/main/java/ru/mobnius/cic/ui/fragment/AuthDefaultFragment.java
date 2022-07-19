package ru.mobnius.cic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.concurrent.Executor;

import ru.mobnius.cic.R;
import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.databinding.FragmentAuthDefaultBinding;
import ru.mobnius.cic.ui.activity.MainActivity;
import ru.mobnius.cic.ui.viewmodels.AuthViewModel;
import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.BaseApp;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.data.authorization.AuthorizationMeta;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.preferences.GeneralPreferences;
import ru.mobnius.simple_core.utils.NetworkInfoUtil;
import ru.mobnius.simple_core.utils.SimpleTextWatcher;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.VersionUtil;

public class AuthDefaultFragment extends Fragment {
    private final int MIN_LOGIN_LENGTH = 3;
    private final int MIN_PASSWORD_LENGTH = 3;

    @Nullable
    private FragmentAuthDefaultBinding binding;
    @Nullable
    private AuthViewModel authViewModel;
    @Nullable
    private Observer<AuthorizationMeta> authObserver;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authObserver = authorizationMeta -> {
            if (!isAdded() || binding == null) {
                return;
            }
            binding.fragmentAuthDefaultSignIn.setEnabled(true);
            switch (authorizationMeta.status) {
                case Meta.NOT_AUTHORIZATION:
                    failAuthorized(authorizationMeta.message);
                    break;
                case Meta.OK:
                    if (!Authorization.createDefaultInstance(requireContext(), authorizationMeta)
                            || Authorization.getInstance() == null) {
                        Toast.makeText(requireContext(), getString(R.string.auth_critical_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    onAuthorized(BaseApp.LOGIN);
                    break;

                default:
                    failAuthorized(getString(R.string.auth_server_not_available));
                    break;
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthDefaultBinding.inflate(inflater, container, false);
        if (authViewModel == null) {
            return binding.getRoot();
        }
        binding.fragmentAuthDefaultLoginLayout
                .setEndIconOnClickListener(view -> binding.fragmentAuthDefaultLogin.setText(StringUtil.EMPTY));

        binding.fragmentAuthDefaultSignIn.setOnClickListener(
                button -> handleAuthClick());

        binding.fragmentAuthDefaultLogin.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.fragmentAuthDefaultLoginLayout.setError(null);
                if (StringUtil.isEmpty(binding.fragmentAuthDefaultLogin.getText())) {
                    binding.fragmentAuthDefaultLoginLayout.setHelperText(null);
                    return;
                }
                final String login = binding.fragmentAuthDefaultLogin.getText().toString();
                authViewModel.login = login;
                if (StringUtil.isNotEmpty(login)) {
                    if (login.length() < MIN_LOGIN_LENGTH) {
                        binding.fragmentAuthDefaultLoginLayout.setHelperText(
                                getString(R.string.login_too_short, MIN_LOGIN_LENGTH));
                    } else {
                        binding.fragmentAuthDefaultLoginLayout.setHelperText(null);
                    }
                }
            }
        });

        binding.fragmentAuthDefaultPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                binding.fragmentAuthDefaultPasswordLayout.setError(null);
                if (StringUtil.isEmpty(binding.fragmentAuthDefaultPassword.getText())) {
                    binding.fragmentAuthDefaultPasswordLayout.setHelperText(null);
                    return;
                }
                final String password = binding.fragmentAuthDefaultPassword.getText().toString();
                authViewModel.password = password;
                if (StringUtil.isNotEmpty(password)) {
                    if (password.length() < MIN_PASSWORD_LENGTH) {
                        binding.fragmentAuthDefaultPasswordLayout.setHelperText(
                                getString(R.string.login_too_short, MIN_PASSWORD_LENGTH));
                    } else {
                        binding.fragmentAuthDefaultPasswordLayout.setHelperText(null);
                    }
                }
            }
        });

        binding.fragmentAuthDefaultPassword.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    handleAuthClick();
                    if (v != null) {
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm == null) {
                            return true;
                        }
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                    return true;
                }
            }
            return false;
        });
        binding.fragmentAuthDefaultLogin.setText(authViewModel.login);
        binding.fragmentAuthDefaultPassword.setText(authViewModel.password);
        binding.fragmentAuthDefaultVersion.setText(VersionUtil.getVersionName(requireContext()));
        if (GeneralPreferences.getInstance() != null
                && GeneralPreferences.getInstance().isSingleUser()
                && PreferencesManager.getInstance() != null
                && PreferencesManager.getInstance().isTouch()) {
            binding.fragmentAuthDefaultFingerprint.setVisibility(View.VISIBLE);
            binding.fragmentAuthDefaultFingerprint.setOnClickListener(v -> fingerPrintActivate());
        }
        binding.fragmentAuthDefaultForgotAuthInfo.setOnClickListener(v ->
                NavHostFragment.findNavController(AuthDefaultFragment.this).navigate(R.id.action_auth_to_restore));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void singIn() {
        if (binding == null || authViewModel == null || authObserver == null) {
            return;
        }
        binding.fragmentAuthDefaultProgress.setVisibility(View.VISIBLE);
        if (!isLocationPermissionGranted()) {
            launchLocationPermission();
            binding.fragmentAuthDefaultProgress.setVisibility(View.GONE);
            binding.fragmentAuthDefaultSignIn.setEnabled(true);
            return;
        }
        if (NetworkInfoUtil.isNetworkAvailable(requireContext())) {
            final String versionName = VersionUtil.getVersionName(requireContext());
            authViewModel.getAuthorizationDataOnline(versionName).observe(getViewLifecycleOwner(), authObserver);
        } else {
            authViewModel.getAuthorizationOffline(requireContext()).observe(getViewLifecycleOwner(), authObserver);
        }
    }

    private void onAuthorized(final int mode) {
        if (Authorization.getInstance() == null || !Authorization.getInstance().isAgent()) {
            failAuthorized(getString(R.string.auth_access_denied_wrong_role));
            return;
        }
        ((MobniusApplication) requireActivity().getApplication()).onAuthorized(mode);
        requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.user_authorized), Toast.LENGTH_SHORT).show());
        NavHostFragment.findNavController(this).navigate(R.id.action_auth_to_routes);
    }

    private void failAuthorized(final @NonNull String message) {
        if (!isAdded() || binding == null) {
            return;
        }
        if (StringUtil.isNotEmpty(message)) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
        binding.fragmentAuthDefaultProgress.setVisibility(View.GONE);
    }

    private boolean isLocationPermissionGranted() {
        if (requireActivity() instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) requireActivity();
            return mainActivity.permissionsHandler.isLocationPermissionGranted(requireActivity());
        }
        return false;
    }

    private void launchLocationPermission() {
        if (requireActivity() instanceof MainActivity) {
            final MainActivity mainActivity = (MainActivity) requireActivity();
            mainActivity.showRationalLocationPermissionAlert();
        }
    }

    private void handleAuthClick() {
        if (binding == null || authViewModel == null) {
            return;
        }
        binding.fragmentAuthDefaultSignIn.setEnabled(false);
        if (binding.fragmentAuthDefaultLogin.getText() == null || binding.fragmentAuthDefaultPassword.getText() == null) {
            binding.fragmentAuthDefaultSignIn.setEnabled(true);
            return;
        }
        final String login = binding.fragmentAuthDefaultLogin.getText().toString().trim();
        final String password = binding.fragmentAuthDefaultPassword.getText().toString().trim();
        boolean shortArgs = false;
        if (login.length() < MIN_LOGIN_LENGTH) {
            binding.fragmentAuthDefaultLoginLayout.setError(
                    getString(R.string.login_too_short, MIN_LOGIN_LENGTH));
            shortArgs = true;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            binding.fragmentAuthDefaultPasswordLayout.setError(
                    getString(R.string.login_too_short, MIN_LOGIN_LENGTH));
            shortArgs = true;
        }
        if (shortArgs) {
            binding.fragmentAuthDefaultSignIn.setEnabled(true);
            return;
        }
        authViewModel.cancelAuth();
        singIn();
    }

    private void fingerPrintActivate() {
        if (canUseBiometric(requireContext())) {
            final Executor executor = ContextCompat.getMainExecutor(requireContext());
            final BiometricPrompt biometricPrompt = new BiometricPrompt(requireActivity(),
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    if (Authorization.createPinOrTouchInstance(requireContext())) {
                        onAuthorized(BaseApp.TOUCH);
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.auth_critical_error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            });
            final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_auth))
                    .setSubtitle(getString(R.string.use_sensor_plese))
                    .setNegativeButtonText(getString(R.string.cancel))
                    .build();

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(requireContext(), getString(R.string.biometric_auth_unavailable), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean canUseBiometric(final @NonNull Context context) {
        return BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS;
    }

}


