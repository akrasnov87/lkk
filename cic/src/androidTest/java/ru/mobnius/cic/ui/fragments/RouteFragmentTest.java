package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.not;

import android.view.Gravity;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.espresso.contrib.DrawerActions;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.RouteFragment;
import ru.mobnius.cic.ui.fragment.SettingsFragment;

public class RouteFragmentTest {

    @Test
    public void testRouteFragment() {
        FragmentScenario<RouteFragment> fragmentScenario = FragmentScenario.launchInContainer(RouteFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_route_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_route_list)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_route_sync)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_route_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.navigationDrawerExit)).check(matches(isDisplayed()));
        onView(withId(R.id.navigationDrawerMaps)).check(matches(isDisplayed()));
        onView(withId(R.id.navigationDrawerProfile)).check(matches(isDisplayed()));
        onView(withId(R.id.navigationDrawerSettings)).check(matches(isDisplayed()));
        onView(withId(R.id.navigationDrawerSynchronization)).check(matches(isDisplayed()));

    }
}
