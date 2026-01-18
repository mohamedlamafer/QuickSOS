package com.example.quicksos

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @Test
    fun testSwitchesAndBackButton() {
        val scenario = ActivityScenario.launch(SettingsActivity::class.java)


        onView(withId(R.id.swReminder)).perform(click())
        onView(withId(R.id.swReminder)).check(matches(isChecked()))

        onView(withId(R.id.swFlash)).perform(click())

        onView(withId(R.id.swVibrate)).perform(click())

        onView(withId(R.id.swSound)).perform(click())

        scenario.close()
    }
}
