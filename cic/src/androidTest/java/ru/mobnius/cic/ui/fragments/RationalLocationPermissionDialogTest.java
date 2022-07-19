package ru.mobnius.cic.ui.fragments;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.fragment.app.testing.FragmentScenario;

import org.junit.Test;

import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.fragment.RationalLocationPermissionDialog;

public class RationalLocationPermissionDialogTest {

    @Test
    public void testRationalLocationPermissionDialogFragment() {
        FragmentScenario<RationalLocationPermissionDialog> fragmentScenario =
                FragmentScenario.launch(RationalLocationPermissionDialog.class, null, R.style.LkkBlueTheme);
        fragmentScenario.onFragment(fragment -> {
            assertNotNull(fragment.getDialog());
            assertTrue(fragment.getDialog().isShowing());
            fragment.dismiss();
            fragment.getParentFragmentManager().executePendingTransactions();
            assertNull(fragment.getDialog());
        });
        onView(withId(R.id.location_permission_dialog_ok)).check(doesNotExist());
    }

}
