package ru.mobnius.cic.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.PointInfoResultsAdapter;
import ru.mobnius.cic.adaper.holder.pointinfo.OnPointInfoResultCancelListener;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.databinding.FragmentPointInfoBinding;
import ru.mobnius.cic.ui.component.ExpandableTextLayout;
import ru.mobnius.cic.ui.component.SimpleExpandableItem;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.ResultItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.cic.ui.viewmodels.PointInfoViewModel;
import ru.mobnius.simple_core.utils.DoubleUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class PointInfoFragment extends BaseNavigationFragment {
    public static final String RESULT_CANCELLED = "ru.mobnius.cic.ui.fragment.RESULT_CANCELLED";

    @Nullable
    private PointItem pointItem;
    @Nullable
    private PointInfoViewModel viewModel;
    @Nullable
    private FragmentPointInfoBinding binding;
    @Nullable
    private PointInfoResultsAdapter resultsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointItem = PointInfoFragmentArgs.fromBundle(getArguments()).getPointItem();
        final int layoutPosition = PointInfoFragmentArgs.fromBundle(getArguments()).getPosition();
        viewModel = new ViewModelProvider(this).get(PointInfoViewModel.class);
        viewModel.layoutPosition = layoutPosition;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPointInfoBinding.inflate(inflater, container, false);
        binding.fragmentPointInfoToolbar.setNavigationOnClickListener(v -> handleBackButton());
        binding.fragmentPointInfoResultList.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (resultsAdapter == null) {
            final OnPointInfoResultCancelListener resultCancelListener = (resultId, listener) -> {
                if (DataManager.getInstance() == null) {
                    return;
                }
                if (DataManager.getInstance().cancelResult(resultId)) {
                    Toast.makeText(requireContext(), getString(R.string.result_cancelled), Toast.LENGTH_SHORT).show();
                    if (viewModel == null) {
                        return;
                    }
                    final NavController controller = NavHostFragment.findNavController(this);
                    final NavBackStackEntry backStackEntry = controller.getPreviousBackStackEntry();
                    if (backStackEntry == null) {
                        NavHostFragment.findNavController(this).popBackStack(R.id.meter_result_fragment, true);
                        return;
                    }
                    if (pointItem != null && StringUtil.isNotEmpty(pointItem.resultId)) {
                        backStackEntry.getSavedStateHandle().set(RESULT_CANCELLED, new SavedResult(pointItem.id, pointItem.resultId, true, StringUtil.EMPTY));
                        listener.onResultCuncelled();
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.fail_cancelling_result), Toast.LENGTH_SHORT).show();
                }

            };
            resultsAdapter = new PointInfoResultsAdapter(resultCancelListener);
        }
        binding.fragmentPointInfoResultList.setAdapter(resultsAdapter);
        if (pointItem == null) {
            return binding.getRoot();
        }

        final List<ExpandableTextLayout.OnExpandableItem> generalInfoList = new ArrayList<>();
        generalInfoList.add(new SimpleExpandableItem(getString(R.string.account_number), pointItem.accountNumber));
        generalInfoList.add(new SimpleExpandableItem(getString(R.string.subscr_name), pointItem.owner));
        generalInfoList.add(new SimpleExpandableItem(getString(R.string.address), pointItem.address));
        generalInfoList.add(new SimpleExpandableItem(getString(R.string.device_number), pointItem.deviceNumber));

        final List<ExpandableTextLayout.OnExpandableItem> resultHistoryList;
        if (DataManager.getInstance() != null && StringUtil.isNotEmpty(pointItem.resultId)) {
            resultHistoryList = DataManager.getInstance().getResultHistoryList(pointItem.resultId);
            binding.fragmentPointInfoResultHistory.setAltContent(resultHistoryList);
        }
        binding.fragmentPointInfoGeneralInfo.setAltContent(generalInfoList);

        final Observer<List<ResultItem>> observer = resultItems -> resultsAdapter.setResults(resultItems);
        binding.fragmentPointInfoBuildRoute.setOnClickListener(view -> {
            try {
                final String userLat = DoubleUtil.getNonNullDoubleString(getLocation().getLatitude());
                final String userLong = DoubleUtil.getNonNullDoubleString(getLocation().getLongitude());
                final String pointLat = DoubleUtil.getNonNullDoubleString(pointItem.latitude);
                final String pointLong = DoubleUtil.getNonNullDoubleString(pointItem.longitude);
                final String yandexUri = getString(R.string.yandex_deep_link, userLat,
                        userLong, pointLat, pointLong);
                final Uri uri = Uri.parse(yandexUri);
                final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (ActivityNotFoundException | SecurityException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), getString(R.string.yandex_maps_not_found), Toast.LENGTH_SHORT).show();
            }
        });
        if (viewModel != null) {
            viewModel.getResults(pointItem.id).observe(getViewLifecycleOwner(), observer);
        }
        return binding.getRoot();
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
        return destination.getId() == R.id.point_info_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}
