package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.MeterResultFragment;
import ru.mobnius.cic.ui.model.PointItem;

public class MeterResultFragmentTest {

    @Test
    public void testMeterResultFragment() {

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
                        controller.setGraph(R.navigation.work_nav_graph);;
                        controller.setCurrentDestination(R.id.meter_result_fragment, bundle);
                        Navigation.setViewNavController(meterResultFragment.requireView(), controller);
                    }

                });
                return meterResultFragment;
            }
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_meter_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_dispatcher_comment)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_meter_account_number)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_owner_name)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_device_number)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_device_type)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_first_reading_title)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_previous_label)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_previous_date)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_current_label)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_current_date)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_failure_reason)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_notice)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_photo_validation_message)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_save)).check(matches(isDisplayed()));


    }

}
