package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mobnius.cic.R;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.databinding.FragmentRouteInfoBinding;
import ru.mobnius.cic.ui.component.ExpandableTextLayout;
import ru.mobnius.cic.ui.component.SimpleExpandableItem;
import ru.mobnius.cic.ui.model.RouteInfoHistory;
import ru.mobnius.cic.ui.model.RouteItem;
import ru.mobnius.simple_core.utils.DateUtil;
import ru.mobnius.simple_core.utils.StringUtil;

public class RouteInfoFragment extends BaseNavigationFragment {
    @Nullable
    private RouteItem routeItem;
    @Nullable
    private FragmentRouteInfoBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b =getArguments();
        routeItem = RouteInfoFragmentArgs.fromBundle(b).getRouteItem();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRouteInfoBinding.inflate(inflater, container, false);
        binding.fragmentRouteInfoToolbar.setNavigationOnClickListener(view -> handleBackButton());

        if (DataManager.getInstance() == null || routeItem == null) {
            return binding.getRoot();
        }

        binding.fragmentRouteInfoToolbar.setSubtitle(routeItem.name);

        final List<ExpandableTextLayout.OnExpandableItem> items = new ArrayList<>();
        items.add(new SimpleExpandableItem(getString(R.string.begining), routeItem.dateStart));
        final Date d = new Date(routeItem.dateEnd.getTime() + DateUtil.ONE_DAY_TIME);
        items.add(new SimpleExpandableItem(getString(R.string.end), d));
        if (routeItem.isExtended) {
            items.add(new SimpleExpandableItem(getString(R.string.extended), d));
        }
        binding.fragmentRouteInfoPeriod.setContent(items);
        items.clear();

        for (final RouteInfoHistory history : DataManager.getInstance().getRouteHistory(routeItem.id)) {
            items.add(new SimpleExpandableItem(history.status, history.date));
        }
        binding.fragmentRouteInfoHistory.setContent(items);

        if (StringUtil.isNotEmpty(routeItem.notice)) {
            binding.fragmentRouteInfoTxt.setText(Html.fromHtml(routeItem.notice));
        } else {
            binding.fragmentRouteInfoTxt.setVisibility(View.GONE);
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
        return destination.getId() == R.id.route_info_fragment;
    }

    private void handleBackButton() {
        if (isCurrentDestination()) {
            NavHostFragment.findNavController(this).navigateUp();
        }
    }

}
