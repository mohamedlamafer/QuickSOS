package com.example.quicksos

import android.content.SharedPreferences
import android.os.Looper
import android.widget.Switch
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import kotlin.test.assertFalse

class SettingsActivityTest {

    private lateinit var settingsActivity: SettingsActivity

    @Before
    fun setUp() {
        val mockLooper: Looper = mock(Looper::class.java)
        val mockHandler = mock(android.os.Handler::class.java)

        `when`(Looper.getMainLooper()).thenReturn(mockLooper)

        settingsActivity = SettingsActivity()
        settingsActivity.prefs = mock(SharedPreferences::class.java)
    }

    @Test
    fun testSwitchFlash_onCheckedChange() {
        val switchFlash = Switch(settingsActivity)
        switchFlash.isChecked = false

        settingsActivity.onCreate(null)

        verify(settingsActivity.prefs?.edit(), times(1))?.putBoolean("pref_flash", false)

        assertFalse(switchFlash.isChecked)
    }
}
