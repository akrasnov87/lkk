package ru.mobnius.cic.ui.fragment;

import android.annotation.SuppressLint;
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

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.FragmentPersonalAccountBinding;
import ru.mobnius.cic.ui.viewmodels.PersonalAccountViewModel;
import ru.mobnius.simple_core.data.GlobalSettings;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.utils.NetworkInfoUtil;

public class PersonalAccountFragment extends BaseNavigationFragment {
    @Nullable
    private PersonalAccountViewModel viewModel;
    @Nullable
    private FragmentPersonalAccountBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PersonalAccountViewModel.class);
        viewModel.init();
        final Observer<Boolean> loadingObserver = loaded -> {
            if (loaded && binding != null && isAdded()) {
                binding.fragmentPersonalAccountLoading.setVisibility(View.GONE);
            }
        };
        viewModel.loadingLiveData.observe(this, loadingObserver);
    }



    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPersonalAccountBinding.inflate(inflater, container, false);
        binding.fragmentPersonalAccountToolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());
        binding.fragmentPersonalAccountWeb.getSettings().setJavaScriptEnabled(true);
        binding.fragmentPersonalAccountWeb.getSettings().setDomStorageEnabled(true);
        if (viewModel != null && viewModel.webViewClient != null) {
            binding.fragmentPersonalAccountWeb.setWebViewClient(viewModel.webViewClient);
        }
        if (!NetworkInfoUtil.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            binding.fragmentPersonalAccountLoading.setVisibility(View.GONE);
            return binding.getRoot();
        }
        if (Authorization.getInstance() != null && Authorization.getInstance().user != null) {
            final String token = Authorization.getInstance().user.getCredentials().getToken();
            final String url = GlobalSettings.BASE_URL + "/arm/" + GlobalSettings.ENVIRONMENT
                    + "#lkk?rpc-authorization=" + token;
            binding.fragmentPersonalAccountWeb.loadUrl(url);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

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

    @Override
    public boolean isCurrentDestination() {
        final NavDestination destination = NavHostFragment.findNavController(this).getCurrentDestination();
        if (destination == null) {
            return false;
        }
        return destination.getId() == R.id.personal_account_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

}