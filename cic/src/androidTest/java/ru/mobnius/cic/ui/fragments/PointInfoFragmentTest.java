package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.PointInfoFragment;

public class PointInfoFragmentTest {

    @Test
    public void testPointInfoFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("position", 0);
        FragmentScenario<PointInfoFragment> fragmentScenario = FragmentScenario.launchInContainer(PointInfoFragment.class, bundle, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_point_info_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_point_info_build_route)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_point_info_general_info)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_point_info_result_history)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_point_info_result_list)).check(matches(not(isDisplayed())));

    }

}
