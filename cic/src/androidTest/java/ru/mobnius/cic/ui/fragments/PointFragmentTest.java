package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
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
import ru.mobnius.cic.ui.fragment.PointFragment;

public class PointFragmentTest {

    @Test
    public void testPointFragment() {
        final Bundle bundle = new Bundle();
        bundle.putString("routeId", "randomId");
        final String ROUTE_TITLE = "someRouteTitle";
        bundle.putString("routeTitle", ROUTE_TITLE);
        final TestNavHostController controller = new TestNavHostController(ApplicationProvider.getApplicationContext());
        final FragmentScenario<PointFragment> fragmentScenario = FragmentScenario.launchInContainer(PointFragment.class, bundle, R.style.LkkBlueTheme, new FragmentFactory() {
            @NonNull
            @Override
            public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                PointFragment pointFragment = new PointFragment();
                pointFragment.getViewLifecycleOwnerLiveData().observeForever(viewLifecycleOwner -> {

                    if (viewLifecycleOwner != null) {
                        controller.setViewModelStore(new ViewModelStore());
                        controller.setGraph(R.navigation.work_nav_graph);
                        Navigation.setViewNavController(pointFragment.requireView(), controller);
                    }

                });
                return pointFragment;
            }
        });
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_point_filter)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_point_sort)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_point_filter_count)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_point_toolbar)).check(matches(isDisplayed()));
        onView(withText(ROUTE_TITLE)).check(matches(isDisplayed()));


    }

}
