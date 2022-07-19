package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.Gravity;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.activity.MainActivity;

public class AuthDefaultFragmentTest extends BaseAuthorizationTest {


    @Test
    public void testAuthDefaultFragment() throws InterruptedException {
        try {
            //нет никакого смысла в проверке этой кнопки, просто в зависимости от условий она может присутствовать на экране и её нужно нажать в этом случае
            onView(withText("Понятно")).perform(click());
        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }
        mActivityTestRule.getScenario().close();
        ActivityScenario.launch(MainActivity.class);
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
        onView(withId(R.id.fragment_auth_default_login)).perform(typeText(TEST_LOGIN));
        onView(withId(R.id.fragment_auth_default_password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.fragment_auth_default_sign_in)).perform(click());
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
        mActivityTestRule.getScenario().close();
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.fragment_auth_default_login)).perform(typeText(TEST_LOGIN));
        onView(withId(R.id.fragment_auth_default_password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.fragment_auth_default_sign_in)).perform(click());
        onView(withId(R.id.fragment_route_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.navigationDrawerSettings)).perform(click());
        onView(withId(R.id.fragment_settings_enable_pin)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        mActivityTestRule.getScenario().close();
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        mActivityTestRule.getScenario().close();
        ActivityScenario.launch(MainActivity.class);
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        onView(withId(R.id.pin_digits_btn_one)).perform(click());
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
    }
}
