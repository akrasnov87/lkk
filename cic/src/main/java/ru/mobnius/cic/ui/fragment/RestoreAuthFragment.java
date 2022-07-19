package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import ru.mobnius.cic.R;
import ru.mobnius.cic.concurent.MainTaskExecutor;
import ru.mobnius.cic.concurent.MainTaskExecutorCallback;
import ru.mobnius.cic.concurent.AuthRestoreTask;
import ru.mobnius.cic.databinding.FragmentRestoreAuthBinding;
import ru.mobnius.cic.ui.viewmodels.RestoreAuthViewModel;
import ru.mobnius.simple_core.data.Meta;
import ru.mobnius.simple_core.utils.SimpleTextWatcher;
import ru.mobnius.simple_core.utils.StringUtil;

public class RestoreAuthFragment extends Fragment {
    @Nullable
    private FragmentRestoreAuthBinding binding;
    @Nullable
    private RestoreAuthViewModel restoreAuthViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restoreAuthViewModel = new ViewModelProvider(this).get(RestoreAuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRestoreAuthBinding.inflate(inflater, container, false);
        binding.fragmentRestoreToolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(RestoreAuthFragment.this).navigateUp());
        if (restoreAuthViewModel == null) {
            return binding.getRoot();
        }
        binding.fragmentRestoreButton.setOnClickListener(v -> {
            if (!isValidEmail(binding.fragmentRestoreEMail.getText())) {
                binding.fragmentRestoreEMailLayout.setError(getString(R.string.not_valid_email));
                return;
            }
            MainTaskExecutor taskExecutor = new MainTaskExecutor();
            AuthRestoreTask task = new AuthRestoreTask(restoreAuthViewModel.eMailAddress);
            taskExecutor.executeAsync(task, new MainTaskExecutorCallback<Meta>() {
                @Override
                public void onCallableComplete(@NonNull Meta result) {
                    if (!isAdded()) {
                        return;
                    }
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCallableError(@NonNull String error) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());
                }
            });
        });
        binding.fragmentRestoreEMail.setText(restoreAuthViewModel.eMailAddress);
        binding.fragmentRestoreEMail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                binding.fragmentRestoreEMailLayout.setError(null);
                if (StringUtil.isEmpty(s)) {
                    restoreAuthViewModel.eMailAddress = StringUtil.EMPTY;
                } else {
                    restoreAuthViewModel.eMailAddress = s.toString();
                }
                if (isValidEmail(restoreAuthViewModel.eMailAddress)) {
                    binding.fragmentRestoreEMailLayout.setHelperText(null);
                } else {
                    binding.fragmentRestoreEMailLayout.setHelperText(getString(R.string.not_valid_email));
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public static boolean isValidEmail(final @Nullable CharSequence target) {
        if (StringUtil.isEmpty(target)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}