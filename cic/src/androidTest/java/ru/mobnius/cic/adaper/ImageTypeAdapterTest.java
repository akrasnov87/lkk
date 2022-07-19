package ru.mobnius.cic.adaper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import java.util.Objects;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.MeterResultFragment;
import ru.mobnius.cic.ui.model.PointItem;

public class ImageTypeAdapterTest {

    @Test
    public void testImageTypeAdapter() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("pointItem", PointItem.getTestInstance());
        bundle.putInt("position", 0);

        final TestNavHostController controller = new TestNavHostController(ApplicationProvider.getApplicationContext());

        final FragmentScenario<MeterResultFragment> fragmentScenario = FragmentScenario.launchInContainer(MeterResultFragment.class, bundle, R.style.LkkBlueTheme, new FragmentFactory() {
            @NonNull
            @Override
            public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                MeterResultFragment meterResultFragment = new MeterResultFragment();
                meterResultFragment.getViewLifecycleOwnerLiveData().observeForever(viewLifecycleOwner -> {

                    if (viewLifecycleOwner != null) {
                        controller.setViewModelStore(new ViewModelStore());
                        controller.setGraph(R.navigation.work_nav_graph);
                        controller.setCurrentDestination(R.id.meter_result_fragment, bundle);
                        Navigation.setViewNavController(meterResultFragment.requireView(), controller);
                    }

                });
                return meterResultFragment;
            }
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        fragmentScenario.onFragment(fragment -> {
            RecyclerView recyclerView =  Objects.requireNonNull(fragment.getView()).findViewById(R.id.fragment_meter_image_list);
        });
    }
}