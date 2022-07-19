package ru.mobnius.cic.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import ru.mobnius.cic.R;
import ru.mobnius.cic.data.permissions.PermissionsHandler;
import ru.mobnius.cic.ui.activity.MainActivity;

public abstract class BaseNavigationFragment extends Fragment {
    public final PermissionsHandler permissionsHandler = new PermissionsHandler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, getBackPressedCallBack());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!permissionsHandler.isLocationPermissionGranted(requireActivity())) {
            Toast.makeText(requireContext(), getString(R.string.no_location_permission), Toast.LENGTH_SHORT).show();
            NavHostFragment.findNavController(this).navigate(R.id.nav_graph);
        } else {
            if (requireActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) requireActivity();
                mainActivity.handleLocationUpdates();
            }
        }
    }

    @NonNull
    public Location getLocation() {
        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            return mainActivity.getLocation();
        } else {
            return new Location(getString(R.string.app_name));
        }
    }

    public boolean isLocationEnabled() {
        if (requireActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) requireActivity();
            return mainActivity.isLocationEnabled();
        } else {
            return false;
        }
    }

    /**
     * Обработка нажатия системной кнопки назад
     */
    @NonNull
    public abstract OnBackPressedCallback getBackPressedCallBack();

    /**
     * Проверка является ли текущий фрагмент подходящим для навигации
     * подробнее - https://stackoverflow.com/questions/51060762/illegalargumentexception-navigation-destination-xxx-is-unknown-to-this-navcontr
     */
    public abstract boolean isCurrentDestination();
}
