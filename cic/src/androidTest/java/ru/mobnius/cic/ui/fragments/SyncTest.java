package ru.mobnius.cic.ui.fragments;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.util.Checks.checkNotNull;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.intent.IntentCallback;
import androidx.test.runner.intent.IntentMonitorRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import ru.mobnius.cic.R;

public class SyncTest extends BaseAuthorizationTest {

    private String goodText;

    @Before
    public void setString() {
        goodText = ApplicationProvider.getApplicationContext().getString(R.string.good);
    }

    @Test
    public void testSync() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_route_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.navigationDrawerSynchronization)).perform(click());
        onView(withId(R.id.fragment_sync_start)).perform(click());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(goodText)).perform(click());
        Espresso.pressBack();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_route_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.item_route_type_items)));
        onView(withId(R.id.fragment_point_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.fragment_meter_meters_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, typeText("1000")));
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
                new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        IntentCallback intentCallback = intent -> {
            if (intent.getAction().equals("android.media.action.IMAGE_CAPTURE")) {
                try {
                    Uri imageUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
                    final Bitmap.Config config = Bitmap.Config.ARGB_8888;
                    final Bitmap emptyBitmap = Bitmap.createBitmap(1, 1, config);
                    OutputStream out = ApplicationProvider.getApplicationContext().getContentResolver().openOutputStream(imageUri);
                    emptyBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        IntentMonitorRegistry.getInstance().addIntentCallback(intentCallback);
        onView(ViewMatchers.withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withId(R.id.fragment_meter_image_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.dialog_photo_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.dialog_photo_type_take_picture)).perform(click());
        onView(ViewMatchers.withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withId(R.id.fragment_meter_image_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.dialog_photo_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
        onView(withId(R.id.dialog_photo_type_take_picture)).perform(click());
        onView(ViewMatchers.withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withId(R.id.fragment_meter_image_list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.dialog_photo_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(4, click()));
        onView(withId(R.id.dialog_photo_type_take_picture)).perform(click());
        onView(withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withId(R.id.fragment_meter_save)).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_point_list)).check(matches(atPosition(1, R.id.item_point_done_status)));
        Espresso.pressBack();
        onView(withId(R.id.fragment_route_drawer_layout)).check(matches(isClosed(Gravity.LEFT))).perform(DrawerActions.open());
        onView(withId(R.id.navigationDrawerMaps)).perform(click());
    }


    public static ViewAction clickChildViewWithId(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Кликаем на внутреннем RecyclerView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = view.findViewById(id);
                Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(1)).itemView.performClick();
            }
        };
    }

    public static Matcher<View> atPosition(final int position, final int iconId) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                AppCompatImageView icon = viewHolder.itemView.findViewById(iconId);
                return icon.getVisibility() == View.VISIBLE;
            }
        };
    }
}
