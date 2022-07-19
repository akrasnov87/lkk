package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.Gravity;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.contrib.DrawerActions;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.TextFieldView;
import ru.mobnius.simple_core.data.authorization.Authorization;
import ru.mobnius.simple_core.utils.VersionUtil;

public class SettingsTest extends BaseAuthorizationTest {

    @Test
    public void testSettingFragment() {
        Authorization.createTest();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_route_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.navigationDrawerProfile)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_profile_surname)).check(checkTextFieldViewContainsString("тестирования"));
        onView(withId(R.id.fragment_profile_name)).check(checkTextFieldViewContainsString("для"));
        onView(withId(R.id.fragment_profile_middle_name)).check(checkTextFieldViewContainsString("iPhone"));
        try {
            onView(withId(R.id.action_personal_account)).perform(click());
        } catch (NoMatchingViewException e) {
            openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext());
            onView(withText(ApplicationProvider.getApplicationContext().getString(R.string.personal_account))).perform(click());
        }
        Espresso.pressBack();
        Espresso.pressBack();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_route_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.navigationDrawerSettings)).perform(click());
        onView(withId(R.id.fragment_settings_enable_pin)).check(matches(isDisplayed()));
        onView(withText(VersionUtil.getVersionName(ApplicationProvider.getApplicationContext()))).check(matches(isDisplayed()));
    }

    public static ViewAssertion checkTextFieldViewContainsString(String str) {
        return (view, noViewFoundException) -> {
            if (view instanceof TextFieldView) {
                if (!((TextFieldView) view).getValue().equals(str)) {
                    throw noViewFoundException;
                }
            } else {
                throw noViewFoundException;
            }
        };
    }

}
