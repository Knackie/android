package com.example.projetamio;


import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.example.projetamio.activity.Settings;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BaseTest {

    @Rule
    public ActivityTestRule<Settings> mActivityTestRule = new ActivityTestRule<>(Settings.class);

    @Test
    public void baseTest() {
        ViewInteraction textView = onView(
                withText("ProjetAMIO"));
        textView.check(matches(withText("ProjetAMIO")));

        ViewInteraction imageView = onView(
                allOf(withContentDescription("Plus d'options"),
                        withParent(withParent(withId(R.id.action_bar))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));
    }
}
