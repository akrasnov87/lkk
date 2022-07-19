package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.ProfileFragment;

public class ProfileFragmentTest {

    @Test
    public void testProfileFragment() {
        FragmentScenario<ProfileFragment> fragmentScenario = FragmentScenario.launchInContainer(ProfileFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_profile_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_user_photo)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_user_rating)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_quality)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_cash_bonus)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_surname)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_name)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_middle_name)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_address)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_email)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_profile_phone)).check(matches(isDisplayed()));

    }
}
