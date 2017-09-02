package com.rkndika.udacitybakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class PlayerViewTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void checkPlayerViewIsVisible_RecipeDetailActivity1() {
        onView(withId(R.id.recyclerview_recipes)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.tv_menu_steps)).perform(click());
        onView(withId(R.id.rv_steps_content)).perform(RecyclerViewActions.actionOnItemAtPosition(0,click()));
        onView(withId(R.id.container_media_view)).check(matches(withChild(withId(R.id.sep_step_player))));
    }
}