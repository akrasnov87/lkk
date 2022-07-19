package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.PointAdapter;
import ru.mobnius.cic.adaper.holder.point.OnPointInfoClickListener;
import ru.mobnius.cic.adaper.holder.point.OnPointItemClickListener;
import ru.mobnius.cic.databinding.FragmentPointBinding;
import ru.mobnius.cic.ui.model.PointItem;
import ru.mobnius.cic.ui.model.concurent.SavedResult;
import ru.mobnius.cic.ui.viewmodels.PointViewModel;
import ru.mobnius.simple_core.utils.StringUtil;

public class PointFragment extends BaseNavigationFragment {

    @Nullable
    private FragmentPointBinding binding;
    @Nullable
    private PointViewModel viewModel;
    @Nullable
    private PointAdapter pointAdapter;
    @Nullable
    private Observer<List<PointItem>> refreshObserver;
    @Nullable
    private SearchView searchView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PointViewModel.class);
        viewModel.routeId = PointFragmentArgs.fromBundle(getArguments()).getRouteId();
        viewModel.routeTitle = PointFragmentArgs.fromBundle(getArguments()).getRouteTitle();
        if (viewModel.loadingLiveData == null) {
            return;
        }
        refreshObserver = items -> {
            if (!isAdded() || pointAdapter == null) {
                return;
            }
            pointAdapter.setNewPointItems(items);

        };
        viewModel.loadingLiveData.observe(this, (loading) -> {
            if (binding == null) {
                return;
            }
            if (loading) {
                binding.fragmentPointList.setVisibility(View.GONE);
                binding.fragmentPointLoading.setVisibility(View.VISIBLE);
                binding.fragmentPointSort.setEnabled(false);
                binding.fragmentPointFilter.setEnabled(false);
                if (searchView != null) {
                    searchView.setEnabled(false);
                }
            } else {
                binding.fragmentPointList.setVisibility(View.VISIBLE);
                binding.fragmentPointLoading.setVisibility(View.GONE);
                binding.fragmentPointSort.setEnabled(true);
                binding.fragmentPointFilter.setEnabled(true);
                if (searchView != null) {
                    searchView.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = navController.getCurrentBackStackEntry();
        if (backStackEntry == null) {
            return;
        }
        final MutableLiveData<SavedResult> saveData = backStackEntry
                .getSavedStateHandle()
                .getLiveData(MeterResultFragment.ACT_SAVED_RESULT);
        saveData.observe(getViewLifecycleOwner(), savedResult -> {
            if (savedResult == null
                    || StringUtil.isEmpty(savedResult.pointId)
                    || StringUtil.isEmpty(savedResult.resultId)
                    || pointAdapter == null
                    || viewModel == null) {
                return;
            }
            viewModel.setPointDone(savedResult);
            pointAdapter.updateItemDone(savedResult);
            backStackEntry.getSavedStateHandle().remove(MeterResultFragment.ACT_SAVED_RESULT);
        });
        final MutableLiveData<SavedResult> cancelData = backStackEntry
                .getSavedStateHandle()
                .getLiveData(PointInfoFragment.RESULT_CANCELLED);
        cancelData.observe(getViewLifecycleOwner(), savedResult -> {
            if (savedResult == null
                    || StringUtil.isEmpty(savedResult.pointId)
                    || StringUtil.isEmpty(savedResult.resultId)
                    || pointAdapter == null
                    || viewModel == null) {
                return;
            }
            viewModel.setPointUnDone(savedResult);
            pointAdapter.updateItemUnDone(savedResult);
            backStackEntry.getSavedStateHandle().remove(PointInfoFragment.RESULT_CANCELLED);
        });
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPointBinding.inflate(inflater, container, false);
        binding.fragmentPointToolbar.setNavigationOnClickListener(v -> handleBackButton());
        if (viewModel == null) {
            return binding.getRoot();
        }
        if (binding.fragmentPointList.getLayoutManager() == null) {
            binding.fragmentPointList.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        binding.fragmentPointFilter.setOnClickListener(view -> {
            if (isCurrentDestination()) {
                NavHostFragment.findNavController(this).navigate(R.id.action_point_to_filter);
            }
        });

        inflateMenu();
        binding.fragmentPointToolbar.setTitle(viewModel.routeTitle);
        if (viewModel.countFilter() > 0) {
            binding.fragmentPointFilterCount.setVisibility(View.VISIBLE);
            binding.fragmentPointFilterCount.setTextToDraw(String.valueOf(viewModel.countFilter()));
        }
        if (pointAdapter == null) {
            final OnPointItemClickListener itemClickListener = (pointItem, position) -> {
                PointFragmentDirections.ActionPointToTask pointToTask = PointFragmentDirections.actionPointToTask(pointItem, position);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(pointToTask);
                }
            };
            final OnPointInfoClickListener infoClickListener = (pointItem, position) -> {
                PointFragmentDirections.ActionPointToPointInfo actionPointToPointInfo =
                        PointFragmentDirections.actionPointToPointInfo(position).setPointItem(pointItem);
                if (isCurrentDestination()) {
                    NavHostFragment.findNavController(this).navigate(actionPointToPointInfo);
                }
            };
            pointAdapter = new PointAdapter(getResources(), itemClickListener, infoClickListener);
        }
        binding.fragmentPointList.setAdapter(pointAdapter);
        binding.fragmentPointList.scheduleLayoutAnimation();

        if (refreshObserver == null) {
            return binding.getRoot();
        }
        if (viewModel.loadedItems.size() == 0) {
            viewModel.getPointItems().observe(getViewLifecycleOwner(), refreshObserver);
        } else {
            viewModel.refreshPointItems().observe(getViewLifecycleOwner(), refreshObserver);
        }
        binding.fragmentPointSort.setData(viewModel.getSortTypesData(requireContext()), true);
        binding.fragmentPointSort.setSelection(viewModel.getSortType());
        binding.fragmentPointSort.setOnCicIconSelectedListener((map, id) -> {
            if (map == null || pointAdapter == null) {
                return;
            }
            viewModel.setNewSort(id);
            viewModel.refreshPointItems().observe(getViewLifecycleOwner(), refreshObserver);

        });
        return binding.getRoot();
    }


    private void inflateMenu() {
        if (binding == null || viewModel == null) {
            return;
        }

        binding.fragmentPointToolbar.inflateMenu(R.menu.menu_point);
        final Menu menu = binding.fragmentPointToolbar.getMenu();
        if (menu == null) {
            return;
        }
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        if (menuItem == null || !(menuItem.getActionView() instanceof SearchView)) {
            return;
        }

        searchView = (SearchView) menuItem.getActionView();
        final EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_white));
        }
        searchView.setQuery(viewModel.query, false);

        searchView.setOnCloseListener(() -> {
            if (StringUtil.isEmpty(viewModel.query)) {
                return false;//значит не было поиска до этого и ни к чему обновлять весь список
            }
            viewModel.query = StringUtil.EMPTY;
            if (refreshObserver == null) {
                return false;
            }
            viewModel.refreshPointItems().observe(getViewLifecycleOwner(), refreshObserver);
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(@Nullable String query) {
                if (pointAdapter == null) {
                    return false;
                }
                if (StringUtil.isEmpty(query)) {
                    viewModel.query = StringUtil.EMPTY;
                } else {
                    viewModel.setNewSearch(query);
                    if (refreshObserver == null) {
                        return false;
                    }
                    viewModel.refreshPointItems().observe(getViewLifecycleOwner(), refreshObserver);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(@Nullable String newText) {
                return false;
            }
        });
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
        return destination.getId() == R.id.point_fragment;
    }

    private void handleBackButton() {
        requireActivity().getViewModelStore().clear();
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}
