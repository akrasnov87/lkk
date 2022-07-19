package ru.mobnius.cic.ui.fragments;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

import android.app.Activity;
import android.app.Instrumentation;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.runner.intent.IntentCallback;
import androidx.test.runner.intent.IntentMonitorRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import ru.mobnius.cic.MobniusApplication;
import ru.mobnius.cic.R;
import ru.mobnius.cic.ui.component.CicEditText;

public class MeterResultTest extends BaseAuthorizationTest {

    private final String THOUSAND = "1000";

    @Test
    public void testMeterResult() {
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
        onView(withText(ApplicationProvider.getApplicationContext().getString(R.string.good))).perform(click());
        Espresso.pressBack();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_route_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickChildViewWithId(R.id.item_route_type_items)));
        onView(withId(R.id.fragment_point_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText(ApplicationProvider.getApplicationContext().getString(R.string.must_to_be_not_empty))).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withText(containsString(MobniusApplication.MUST_MAKE_PHOTO_OF))).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_meter_meters_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, typeText(THOUSAND)));
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
        onView(withId(R.id.fragment_point_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_meter_meters_list)).check(matches(matchTextatPosition(0, THOUSAND+".0")));
        onView(ViewMatchers.withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withId(R.id.fragment_meter_image_list)).check(checkRvSize(4));
        onView(withId(R.id.fragment_meter_image_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, clickRvImage(R.id.item_image_thumb)));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.fragment_image_delete)).perform(click());
        onView(withText(ApplicationProvider.getApplicationContext().getString(R.string.yes))).perform(click());
        onView(withText(containsString(MobniusApplication.MUST_MAKE_PHOTO_OF))).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.fragment_meter_scroll)).perform(ViewActions.swipeUp());
        onView(withId(R.id.fragment_meter_image_list)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
        onView(withId(R.id.dialog_photo_type_list)).perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));
        onView(withId(R.id.dialog_photo_type_take_picture)).perform(click());
        onView(withId(R.id.fragment_meter_save)).perform(click());
    }

    @SuppressWarnings("rawtypes")
    public static ViewAssertion checkRvSize(int expectedSize) {
        return (view, noViewFoundException) -> {
            if (!(view instanceof RecyclerView)) {
                throw noViewFoundException;
            }
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter == null) {
                throw noViewFoundException;
            }
            if (adapter.getItemCount() != expectedSize) {
                throw noViewFoundException;
            }
        };
    }

    public static ViewAction clickRvImage(final int id) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return null;
            }

            @Override
            public String getDescription() {
                return "Кликаем по изображению в списке";
            }

            @Override
            public void perform(UiController uiController, View view) {
                View v = view.findViewById(id);
                v.performClick();
            }
        };
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

    public static Matcher<View> matchTextatPosition(final int position, final String text) {
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
                CicEditText editText = viewHolder.itemView.findViewById(R.id.item_meter_reading_current);
                if (editText.getText() == null) {
                    return false;
                }
                return editText.getText().equals(text);
            }
        };
    }

}
