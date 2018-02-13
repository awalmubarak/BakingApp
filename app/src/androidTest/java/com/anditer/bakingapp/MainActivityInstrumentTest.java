package com.anditer.bakingapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test to check if volley download the recipes, inflate the recyclerview and click item
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentTest {

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void CheckAndConfirmRecipeProcess(){
        //thread sleep code from github
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.mRecipeRecycler)).perform(RecyclerViewActions.scrollToPosition(2))
                .perform(click());

         onView(withId(R.id.mRecipeStepsRecycler)).perform(RecyclerViewActions.scrollToPosition(2))
                .perform(click());

        onView(withId(R.id.mNextButton)).perform(click());
        onView(withId(R.id.mNextButton)).perform(click());
        onView(withId(R.id.mNextButton)).perform(click());
        onView(withId(R.id.mPreviousButton)).perform(click());
        onView(withId(R.id.mPreviousButton)).perform(click());


    }


}
