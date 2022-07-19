package ru.mobnius.cic.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.osmdroid.config.Configuration;

import ru.mobnius.cic.BuildConfig;
import ru.mobnius.cic.data.map.MapManager;
import ru.mobnius.cic.data.map.PointClickListener;
import ru.mobnius.cic.databinding.FragmentMapBinding;
import ru.mobnius.cic.ui.model.PointItem;

public class MapFragment extends Fragment implements PointClickListener {
    @Nullable
    private FragmentMapBinding binding;
    @Nullable
    private MapManager mapManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        mapManager = new MapManager(binding.fragmentMapView, this);
        binding.fragmentMapView.setMultiTouchControls(true);
        mapManager.addOnlineOverlays();
        mapManager.buildOnlinePoints();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapManager != null) {
            mapManager.destroy();
        }
        binding = null;
    }

    @Override
    public void onPointClick(@NonNull PointItem pointItem) {
        MapContainerFragmentDirections.ActionMapToTask actionMapToTask = MapContainerFragmentDirections.actionMapToTask(pointItem, -1);
        NavHostFragment.findNavController(this).navigate(actionMapToTask);

    }

}
