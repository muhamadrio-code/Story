package com.riopermana.story.ui.auth.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.riopermana.story.R
import com.riopermana.story.ui.auth.WelcomeFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class LoginFragmentTest {

    @Test
    fun getHeadlineNews_Success() {
        launchFragmentInContainer<WelcomeFragment>()
        onView(withId(R.id.btn_log_in)).check(matches(withText("login")))
    }

    @Test
    fun test_Login_with_valid_input_result_success() {
        launchFragmentInContainer<LoginFragment>()
        onView(withId(R.id.ed_login_email)).perform(replaceText("g@gasd.co"))
        onView(withId(R.id.ed_login_password)).perform(replaceText("qwertyuias"))
        onView(withId(R.id.btn_login)).perform(click())


    }


}