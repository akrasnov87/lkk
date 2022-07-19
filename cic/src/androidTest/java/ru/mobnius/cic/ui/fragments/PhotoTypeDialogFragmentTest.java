package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.PhotoTypeDialogFragment;
import ru.mobnius.cic.ui.fragment.RationalLocationPermissionDialog;

public class PhotoTypeDialogFragmentTest {

    @Test
    public void testPhotoTypeDialogFragment() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isLoadedFromDb", false);
        FragmentScenario<PhotoTypeDialogFragment> fragmentScenario =
                FragmentScenario.launch(PhotoTypeDialogFragment.class, bundle, R.style.LkkBlueTheme);
        fragmentScenario.onFragment(fragment -> {
            assertNotNull(fragment.getDialog());
            assertTrue(fragment.getDialog().isShowing());
            fragment.dismiss();
            fragment.getParentFragmentManager().executePendingTransactions();
            assertNull(fragment.getDialog());
        });
        onView(withId(R.id.dialog_photo_type_take_picture)).check(doesNotExist());
        onView(withId(R.id.dialog_photo_type_notice)).check(doesNotExist());
        onView(withId(R.id.dialog_photo_type_list)).check(doesNotExist());
    }

}
