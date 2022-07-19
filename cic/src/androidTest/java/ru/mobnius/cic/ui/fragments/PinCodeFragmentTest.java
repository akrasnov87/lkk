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
import ru.mobnius.cic.ui.fragment.PinCodeFragment;

public class PinCodeFragmentTest {

    @Test
    public void testPinCodeFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isCreate", true);
        FragmentScenario<PinCodeFragment> fragmentScenario = FragmentScenario.launchInContainer(PinCodeFragment.class, bundle, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_pincode_digits)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_pincode_enter_pin)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_zero)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_one)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_two)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_three)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_four)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_five)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_six)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_seven)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_eight)).check(matches(isDisplayed()));
        onView(withId(R.id.pin_digits_btn_backspace)).check(matches(isDisplayed()));
        onView(withId(R.id.dot_line_first_dot)).check(matches(isDisplayed()));
        onView(withId(R.id.dot_line_second_dot)).check(matches(isDisplayed()));
        onView(withId(R.id.dot_line_third_dot)).check(matches(isDisplayed()));
        onView(withId(R.id.dot_line_fourth_dot)).check(matches(isDisplayed()));
    }

}
