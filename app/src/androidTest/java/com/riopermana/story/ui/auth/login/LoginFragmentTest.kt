package com.riopermana.story.ui.auth.login

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.riopermana.story.BuildConfig
import com.riopermana.story.R
import com.riopermana.story.data.remote.ApiConfig
import com.riopermana.story.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.hamcrest.CoreMatchers.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginFragmentTest {

    private lateinit var loginFragmentScenario: FragmentScenario<LoginFragment>
    private val mockWebServer = MockWebServer()

    private val dummyEmail = "g@g.co"
    private val dummyPassword = "qwertyui"


    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        loginFragmentScenario = launchFragmentInContainer(null, R.style.MainAppTheme)
    }

    @After
    fun tearDown() {
        ApiConfig.BASE_URL = BuildConfig.BASE_URL
        mockWebServer.shutdown()
    }

    @Test
    fun when_login_then_verify_request_path() {
        onView(withId(R.id.ed_login_email)).perform(replaceText(dummyEmail))
        onView(withId(R.id.ed_login_password)).perform(replaceText(dummyPassword))

        val mockResponse = MockResponse()
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.btn_login)).perform(click())
        assertEquals("/v1/login", mockWebServer.takeRequest().path)
    }

    @Test
    fun when_login_with_invalid_email_input_then_verify_error_message() {
        onView(withId(R.id.ed_login_email)).perform(replaceText(""))

        onView(
            allOf(
                isDescendantOfA(withId(R.id.email_field_layout)),
                withClassName(endsWith("TextView")),
                isDisplayed()
            )
        ).check(matches(withText(R.string.invalid_email_message)))
    }

    @Test
    fun when_login_with_invalid_password_input_then_verify_error_message() {
        onView(withId(R.id.ed_login_password)).perform(replaceText(""))

        onView(
            allOf(
                isDescendantOfA(withId(R.id.password_field_layout)),
                withClassName(endsWith("TextView")),
                isDisplayed()
            )
        ).check(matches(withText(R.string.invalid_password)))
    }

    @Test
    fun when_login_with_valid_input_return_error_response() {
        onView(withId(R.id.ed_login_email)).perform(replaceText("asdawa@asd.com"))
        onView(withId(R.id.ed_login_password)).perform(replaceText(dummyPassword))

        val mockResponse = MockResponse()
            .setResponseCode(400)
            .setBody(JsonConverter.readStringFromFile("login_failed_response.json"))

        mockWebServer.enqueue(mockResponse)
        var decorView: View? = null
        loginFragmentScenario.onFragment {
            decorView = it.requireActivity().window?.decorView
        }

        onView(withId(R.id.btn_login)).perform(click())

        onView(withText(R.string.user_not_found))
            .inRoot(
                withDecorView(not(decorView))
            ).check(matches(isDisplayed()))

    }

    @Test
    fun when_login_with_valid_input_and_IOException_has_thrown_then_verify_toast_message() {
        onView(withId(R.id.ed_login_email)).perform(replaceText(dummyEmail))
        onView(withId(R.id.ed_login_password)).perform(replaceText(dummyPassword))

        val mockResponse = MockResponse()
            .setSocketPolicy(SocketPolicy.DISCONNECT_AT_START)

        mockWebServer.enqueue(mockResponse)
        var decorView: View? = null
        loginFragmentScenario.onFragment {
            decorView = it.requireActivity().window?.decorView
        }

        onView(withId(R.id.btn_login)).perform(click())

        onView(withText(R.string.sign_in_failed))
            .inRoot(
                withDecorView(not(decorView))
            ).check(matches(isDisplayed()))

    }


}