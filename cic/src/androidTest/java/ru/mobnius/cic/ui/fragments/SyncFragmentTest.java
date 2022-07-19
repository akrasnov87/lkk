package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.SyncFragment;

public class SyncFragmentTest {

    @Test
    public void testSyncFragment() {
        FragmentScenario<SyncFragment> fragmentScenario = FragmentScenario.launchInContainer(SyncFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_sync_start)).perform(click());
        onView(withId(R.id.fragment_sync_info)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_sync_connection_progress)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_sync_load)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_sync_load_text)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_sync_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_sync_cancel)).perform(click());
        onView(withId(R.id.fragment_sync_cancel)).check(matches(not(isDisplayed())));
    }

}
