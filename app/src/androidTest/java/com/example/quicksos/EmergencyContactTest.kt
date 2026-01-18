package com.example.quicksos

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.core.app.ActivityScenario
import com.example.quicksos.SetupActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmergencyContactTest {

    @Test
    fun testSaveButton() {
        val scenario = ActivityScenario.launch(SetupActivity::class.java)

        onView(withId(R.id.edtNumber))
            .perform(typeText("123456789"))

        onView(withId(R.id.btnSave))
            .perform(click())



    }



    @Test
    fun testSOSButtonAndStopFlashVisibility() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.imgSOS)).perform(click())

        scenario.close()
    }
}

