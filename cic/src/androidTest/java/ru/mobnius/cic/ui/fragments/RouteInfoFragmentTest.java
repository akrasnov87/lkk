package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.RouteInfoFragment;

public class RouteInfoFragmentTest {

    @Test
    public void testRouteInfoFragment() {
        FragmentScenario<RouteInfoFragment> fragmentScenario = FragmentScenario.launchInContainer(RouteInfoFragment.class, new Bundle(), R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_route_info_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_route_info_history)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_route_info_period)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_route_info_txt)).check(matches(isDisplayed()));

    }
}
