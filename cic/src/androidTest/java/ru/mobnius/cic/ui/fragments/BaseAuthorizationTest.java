package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.activity.MainActivity;

public class BaseAuthorizationTest {
    public final static String TEST_LOGIN = "iphone";
    public final static String TEST_PASSWORD = "1234";

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule fineLocationRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);
    @Rule
    public GrantPermissionRule coarseLocationRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);
    @Rule
    public GrantPermissionRule cameraRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);
    @Rule
    public GrantPermissionRule callPhoneRule = GrantPermissionRule.grant(android.Manifest.permission.READ_PHONE_STATE);
    @Rule
    public GrantPermissionRule writeStorageRule = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void setUp() {
        MobniusApplication m = ApplicationProvider.getApplicationContext();
        m.unAuthorized(true);
        Intents.release();
        Intents.init();
        onView(withId(R.id.fragment_auth_default_login)).perform(typeText(TEST_LOGIN));
        onView(withId(R.id.fragment_auth_default_password)).perform(typeText(TEST_PASSWORD));
        onView(withId(R.id.fragment_auth_default_sign_in)).perform(click());
    }

    @After
    public void tearDown() {
        MobniusApplication m = ApplicationProvider.getApplicationContext();
        m.unAuthorized(true);
        ApplicationProvider.getApplicationContext().deleteDatabase("user69.db");
    }

}
