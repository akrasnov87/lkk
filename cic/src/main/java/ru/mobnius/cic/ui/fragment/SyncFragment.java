package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import java.util.List;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.SyncLogAdapter;
import ru.mobnius.cic.adaper.SyncProgressAdapter;
import ru.mobnius.cic.databinding.FragmentSyncBinding;
import ru.mobnius.cic.ui.model.SyncLogItem;
import ru.mobnius.cic.ui.model.SyncPercentageProgress;
import ru.mobnius.cic.ui.viewmodels.SyncViewModel;
import ru.mobnius.simple_core.data.configuration.PreferencesManager;
import ru.mobnius.simple_core.utils.AlertDialogUtil;
import ru.mobnius.simple_core.utils.NetworkInfoUtil;
import ru.mobnius.simple_core.utils.StringUtil;
import ru.mobnius.simple_core.utils.VersionUtil;

public class SyncFragment extends BaseNavigationFragment {

    @Nullable
    private FragmentSyncBinding binding;
    @Nullable
    private SyncViewModel viewModel;
    @Nullable
    private SyncLogAdapter syncLogAdapter;
    @Nullable
    private SyncProgressAdapter syncProgressAdapter;
    @Nullable
    private Observer<SyncPercentageProgress> progressObserver;
    @Nullable
    private Observer<List<SyncLogItem>> logObserver;
    @Nullable
    private Observer<Boolean> routesReceivedObserver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SyncViewModel.class);
        viewModel.init(VersionUtil.getVersionName(requireContext()));

        progressObserver = item -> {
            if (!isAdded() || syncProgressAdapter == null || binding == null) {
                return;
            }

            if (binding.fragmentSyncLoad.getProgress() < item.progress) {
                binding.fragmentSyncLoad.setProgress(item.progress);
                binding.fragmentSyncLoadText.setText(item.getPercentage(binding.fragmentSyncLoad.getMax()));
            }

        };

        logObserver = items -> {
            if (!isAdded() || syncLogAdapter == null) {
                return;
            }
            syncLogAdapter.submitList(items);
        };

        viewModel.startStopLoadingData.observe(this, (loading) -> {
            if (!isAdded() || binding == null) {
                return;
            }
            if (loading) {
                binding.fragmentSyncCancel.setVisibility(View.VISIBLE);
                binding.fragmentSyncStart.setEnabled(false);
            } else {
                binding.fragmentSyncCancel.setVisibility(View.INVISIBLE);
                binding.fragmentSyncStart.setEnabled(true);
            }
        });

        viewModel.circleProgressData.observe(this, (loading) -> {
            if (!isAdded() || binding == null) {
                return;
            }
            if (loading) {
                binding.fragmentSyncConnectionProgress.setVisibility(View.VISIBLE);
            } else {
                binding.fragmentSyncConnectionProgress.setVisibility(View.INVISIBLE);
            }
        });

        viewModel.socketConnectionData.observe(this, (socketConnectionInfo -> {
            if (!isAdded() || binding == null) {
                return;
            }
            if (socketConnectionInfo.connecting) {
                binding.fragmentSyncConnectionProgress.setVisibility(View.VISIBLE);
            } else {
                binding.fragmentSyncConnectionProgress.setVisibility(View.INVISIBLE);
            }
            if (socketConnectionInfo.connectionError) {
                binding.fragmentSyncInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
            } else {
                binding.fragmentSyncInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText));
            }
            binding.fragmentSyncInfo.setText(socketConnectionInfo.message);
        }));

        final Observer<Boolean> syncFinishObserver = success -> {
            if (!isAdded()) {
                return;
            }
            if (success && routesReceivedObserver != null) {
                viewModel.getRoutesReceived().observe(this, routesReceivedObserver);
            } else {
                viewModel.circleProgressData.setValue(false);
                AlertDialogUtil.alert(requireContext(), getString(R.string.sync_finished_with_problems),
                        getString(R.string.good),
                        (dialogInterface, i) -> {
                            viewModel.startStopLoadingData.setValue(false);
                            dialogInterface.dismiss();
                        },
                        null,
                        null);
            }
        };
        viewModel.syncSuccessData.observe(this, syncFinishObserver);

        routesReceivedObserver = aBoolean -> {
            if (!isAdded()) {
                return;
            }
            if (aBoolean) {
                AlertDialogUtil.alert(requireContext(), getString(R.string.sync_finished),
                        getString(R.string.good), (dialogInterface, i) -> dialogInterface.dismiss(),
                        null, null);
            } else {
                Toast.makeText(requireContext(), MobniusApplication.ERROR, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSyncBinding.inflate(inflater, container, false);
        binding.fragmentSyncToolbar.setNavigationOnClickListener(v -> handleBackButton());
        binding.fragmentSyncLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        RecyclerView.ItemAnimator animator = binding.fragmentSyncLogs.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        if (syncProgressAdapter == null) {
            syncProgressAdapter = new SyncProgressAdapter();
        }
        if (syncLogAdapter == null) {
            syncLogAdapter = new SyncLogAdapter();
        }
        binding.fragmentSyncLogs.setAdapter(syncLogAdapter);
        if (progressObserver == null || logObserver == null || viewModel == null) {
            return binding.getRoot();
        }
        binding.fragmentSyncStart.setOnClickListener((v) -> {
            if (viewModel == null) {
                return;
            }
            viewModel.isSyncSuccess = true;
            if (!NetworkInfoUtil.isNetworkAvailable(requireContext())) {
                Toast.makeText(requireContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                return;
            }
            binding.fragmentSyncLoad.setProgress(0);
            binding.fragmentSyncLoadText.setText(StringUtil.EMPTY);
            binding.fragmentSyncCancel.setVisibility(View.VISIBLE);
            binding.fragmentSyncStart.setEnabled(false);
            viewModel.clearLogItems();
            start();
        });

        binding.fragmentSyncCancel.setOnClickListener(view -> {
            stop();
            binding.fragmentSyncStart.setEnabled(true);
            binding.fragmentSyncCancel.setVisibility(View.INVISIBLE);
            if (syncLogAdapter == null) {
                return;
            }
            viewModel.addStopLog(getString(R.string.force_sync_finished));
        });

        viewModel.getProgressItems().observe(getViewLifecycleOwner(), progressObserver);
        viewModel.getLogItems().observe(getViewLifecycleOwner(), logObserver);

        return binding.getRoot();
    }

    private void start() {
        if (viewModel == null || viewModel.syncListener == null) {
            return;
        }
        if (PreferencesManager.getInstance() == null) {
            return;
        }
        try {
            viewModel.start(VersionUtil.getVersionName(requireContext()), PreferencesManager.getInstance().getZip());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stop() {
        if (viewModel == null || viewModel.syncListener == null) {
            return;
        }
        viewModel.syncListener.stop();
        if (binding == null) {
            return;
        }
        if (syncProgressAdapter == null) {
            return;
        }
        viewModel.clearProgressItems();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel == null) {
            return;
        }
        viewModel.destroy();
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
        return destination.getId() == R.id.sync_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            if (viewModel != null && viewModel.syncListener != null) {
                viewModel.syncListener.stop();
            }
            NavHostFragment.findNavController(this).popBackStack(R.id.sync_fragment, true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
