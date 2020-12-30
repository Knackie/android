package com.example.projetamio.activity;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.projetamio.R;
import com.example.projetamio.config.Parameters;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ListCaptorGitlabPages {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void listCaptorGitlabPages() {
        Parameters.URLData = "http://quentin.millardet.gl-pages.telecomnancy.univ-lorraine.fr/android-pages/API/android/TestApp/lightOn.json";
        ViewInteraction materialButton = onView(
                allOf(withId(R.id.toggle), withText("On/Off"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                1),
                        isDisplayed()));
        materialButton.perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.viewMoteButton), withText("Captors"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_host_fragment),
                                        0),
                                2),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tvCapteur), withText("Bureau 2.6"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(withId(R.id.allmoterecyclerview)))),
                        isDisplayed()));
        textView.check(matches(withText("Bureau 2.6")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvBattery), withText("Battery = 2.55 V"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(withId(R.id.allmoterecyclerview)))),
                        isDisplayed()));
        textView2.check(matches(withText("Battery = 2.55 V")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tvCapteur), withText("Amphi Nord"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(withId(R.id.allmoterecyclerview)))),
                        isDisplayed()));
        textView3.check(matches(withText("Amphi Nord")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.tvCapteur), withText("Amphi Sud"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(withId(R.id.allmoterecyclerview)))),
                        isDisplayed()));
        textView4.check(matches(withText("Amphi Sud")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
