package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.FilterFragment;

public class FilterFragmentTest {

    @Test
    public void testFilterFragment() {
        FragmentScenario<FilterFragment> fragmentScenario = FragmentScenario.launchInContainer(FilterFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_filter_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_search_group_label)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_all_fields)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_subscr_number)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_device_number)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_address)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_owner)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_subscr_type_label)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_subscr_type_all)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_subscr_type_person)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_status_group_label)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_point_status_all)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_point_status_done)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_filter_point_status_undone)).check(matches(isDisplayed()));
    }

}
