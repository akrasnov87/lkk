package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.tabs.TabLayoutMediator;

import ru.mobnius.cic.R;
import ru.mobnius.cic.databinding.FragmentMapContainerBinding;
import ru.mobnius.cic.adaper.MapPagerAdapter;

public class MapContainerFragment extends BaseNavigationFragment {
    @Nullable
    private FragmentMapContainerBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapContainerBinding.inflate(inflater, container, false);
        binding.fragmentMapContainerToolbar.setNavigationOnClickListener(v -> handleBackButton());
        binding.fragmetnMapContainerPager.setAdapter(new MapPagerAdapter(this));
        binding.fragmetnMapContainerPager.setUserInputEnabled(false);
        binding.fragmetnMapContainerPager.setOffscreenPageLimit(2);
        final TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.fragmentMapContainerTab,
                binding.fragmetnMapContainerPager, (tab, position) -> tab.setIcon(getTabIcon(position)));
        tabLayoutMediator.attach();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private int getTabIcon(final int position) {
        if (position == MapPagerAdapter.MAP_HELP_PAGE_INDEX) {
            return R.drawable.map_clarification_help;
        }
        return R.drawable.ic_map_24;
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
        return destination.getId() == R.id.map_container_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}
