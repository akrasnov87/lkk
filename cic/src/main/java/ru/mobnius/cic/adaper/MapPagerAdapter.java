package ru.mobnius.cic.adaper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.HashMap;
import java.util.Map;

import ru.mobnius.cic.ui.fragment.MapFragment;
import ru.mobnius.cic.ui.fragment.MapHelpFragment;

public class MapPagerAdapter extends FragmentStateAdapter {

    public static final int MAP_PAGE_INDEX = 0;
    public static final int MAP_HELP_PAGE_INDEX = 1;

    @NonNull
    public final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    public MapPagerAdapter(final @NonNull Fragment fragment) {
        super(fragment);
        fragmentMap.put(MAP_PAGE_INDEX, new MapFragment());
        fragmentMap.put(MAP_HELP_PAGE_INDEX, new MapHelpFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        final Fragment f = fragmentMap.get(position);
        if (f == null) {
            return new MapFragment();
        }
        return f;
    }

    @Override
    public int getItemCount() {
        return fragmentMap.size();
    }
}
