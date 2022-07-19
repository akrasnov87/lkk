package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.adaper.SearchResultAdapter;

import ru.mobnius.cic.adaper.holder.point.OnPointItemClickListener;
import ru.mobnius.cic.adaper.holder.route.OnRouteItemClickListener;
import ru.mobnius.cic.databinding.FragmentSearchBinding;
import ru.mobnius.cic.data.search.SearchResult;
import ru.mobnius.cic.ui.viewmodels.SearchViewModel;
import ru.mobnius.simple_core.utils.StringUtil;

public class SearchFragment extends BaseNavigationFragment {

    @Nullable
    private FragmentSearchBinding binding;
    @Nullable
    private SearchViewModel viewModel;
    @Nullable
    private SearchResultAdapter searchResultAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        binding.fragmentSearchToolbar.setNavigationOnClickListener(view -> handleBackButton());
        if (viewModel == null) {
            return binding.getRoot();
        }
        binding.fragementSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        if (searchResultAdapter == null) {
            OnPointItemClickListener pointItemClickListener = (pointItem, position) -> {

            };
            OnRouteItemClickListener routeItemClickListener = routeItem -> {

            };
            searchResultAdapter = new SearchResultAdapter(viewModel.currentItems, pointItemClickListener, routeItemClickListener);
        }
        binding.fragementSearchResults.setAdapter(searchResultAdapter);
        binding.fragementSearchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.fragementSearchInput.clearFocus();
                if (searchResultAdapter == null) {
                    return false;
                }
                if (StringUtil.isEmpty(query)) {
                    viewModel.searchQuery = StringUtil.EMPTY;
                } else {
                    final Observer<List<SearchResult>> searchObs = result -> {
                        if (!isAdded()) {
                            return;
                        }
                        searchResultAdapter.swapList(result);
                    };
                    viewModel.searchQuery = query;
                    viewModel.getSearchResult().removeObservers(getViewLifecycleOwner());
                    viewModel.getSearchResult().observe(getViewLifecycleOwner(), searchObs);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
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
        return destination.getId() == R.id.search_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}
