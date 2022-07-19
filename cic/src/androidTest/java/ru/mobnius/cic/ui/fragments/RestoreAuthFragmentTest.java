package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputLayout;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.RestoreAuthFragment;

public class RestoreAuthFragmentTest {

    @Test
    public void testRestoreAuthFragment() {
        String emailErrorText = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.not_valid_email);
        FragmentScenario<RestoreAuthFragment> fragmentScenario = FragmentScenario.launchInContainer(RestoreAuthFragment.class, null, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_restore_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_restore_message)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_restore_e_mail)).perform(typeText("Wrong Email"));
        onView(withId(R.id.fragment_restore_button)).perform(click());
        onView(withId(R.id.fragment_restore_e_mail_layout)).check(matches(hasTextInputLayoutError(emailErrorText)));

    }

    public static Matcher<View> hasTextInputLayoutError(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence errorCharSequence = ((TextInputLayout) view).getError();

                if (errorCharSequence == null) {
                    return false;
                }

                String error = errorCharSequence.toString();

                return expectedErrorText.equals(error);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
