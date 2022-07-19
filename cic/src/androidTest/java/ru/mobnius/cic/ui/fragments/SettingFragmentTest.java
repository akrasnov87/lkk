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
import ru.mobnius.cic.ui.fragment.SettingsFragment;

public class SettingFragmentTest {

    @Test
    public void testSettingFragment() {
        FragmentScenario<SettingsFragment> fragmentScenario = FragmentScenario.launchInContainer(SettingsFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_settings_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_settings_common)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_settings_version)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_settings_enable_pin)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_settings_enable_touch)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_settings_disable_debug)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_settings_reset)).perform(click());
    }
}
