package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.MapHelpFragment;

public class MapHelpFragmentTest {

    @Test
    public void testMapHelpFragment() {
        FragmentScenario<MapHelpFragment> fragmentScenario = FragmentScenario.launchInContainer(MapHelpFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);

        onView(withId(R.id.map_help_current)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_current_location)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_done_point_image)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_done_point_label)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_undone_point)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_undone_point_label)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_done_cluster_rl)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_done_cluster)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_done_cluster_label)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_undone_cluster_rl)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_undone_cluster)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_undone_cluster_label)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_user_cluster_rl)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_user_cluster)).check(matches(isDisplayed()));
        onView(withId(R.id.map_help_area_current_location)).check(matches(isDisplayed()));

    }
}
