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
import ru.mobnius.cic.ui.fragment.ImageViewFragment;

public class ImageViewFragmentTest {

    @Test
    public void testImageViewFragment() {
        final Bundle bundle = new Bundle();
        bundle.putString("imageId", "someImageId");
        bundle.putBoolean("isPointDone", false);
        FragmentScenario<ImageViewFragment> fragmentScenario = FragmentScenario.launchInContainer(ImageViewFragment.class, bundle, R.style.LkkBlueTheme);
        fragmentScenario.moveToState(Lifecycle.State.RESUMED);
        onView(withId(R.id.fragment_image_toolbar)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_image_view)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_image_center)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_image_error)).check(matches(not(isDisplayed())));
        onView(withId(R.id.fragment_image_delete)).check(matches(isDisplayed()));

    }

}
