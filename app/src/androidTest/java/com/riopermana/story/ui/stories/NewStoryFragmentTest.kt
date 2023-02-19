package com.riopermana.story.ui.stories

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.riopermana.story.BuildConfig
import com.riopermana.story.R
import com.riopermana.story.data.remote.ApiConfig
import com.riopermana.story.utils.EspressoIdlingResource
import com.riopermana.story.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class NewStoryFragmentTest {

    @get:Rule
    val intentsRule = IntentsRule()

    @get:Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule
        .grant(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

    private lateinit var newStoryFragmentScenario: FragmentScenario<NewStoryFragment>
    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        newStoryFragmentScenario = launchFragmentInContainer(null, R.style.MainAppTheme)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        ApiConfig.BASE_URL = BuildConfig.BASE_URL
        mockWebServer.shutdown()
    }

    @Test
    fun test_open_camera() {
        val expectedIntent = allOf(
            hasAction(MediaStore.ACTION_IMAGE_CAPTURE),
            hasExtraWithKey(MediaStore.EXTRA_OUTPUT)
        )

        val activityResult = createImageCaptureActivityResultStub()
        intending(expectedIntent).respondWith(activityResult)

        onView(withId(R.id.open_camera)).perform(click())
        intended(expectedIntent)
    }

    private fun createImageCaptureActivityResultStub(): ActivityResult {
        val resultData = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultData.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri())
        return ActivityResult(RESULT_OK, resultData)
    }

    @Test
    fun test_add_image_from_gallery() {
        val expectedIntent = allOf(
            hasAction(Intent.ACTION_GET_CONTENT),
            hasCategories(setOf(Intent.CATEGORY_OPENABLE)),
            hasType("image/*")
        )
        val activityResult = getGalleryPickActivityResultIntentStub()
        intending(expectedIntent).respondWith(activityResult)

        onView(withId(R.id.add_image_gallery)).perform(click())
        intended(expectedIntent)
    }

    private fun getImageUri(): Uri {
        val res = ApplicationProvider.getApplicationContext<Context>().resources
        return Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                    "${res.getResourcePackageName(R.drawable.ic_launcher_background)}/" +
                    "${res.getResourceTypeName(R.drawable.ic_launcher_background)}/" +
                    res.getResourceEntryName(R.drawable.ic_launcher_background)
        )
    }

    private fun getGalleryPickActivityResultIntentStub(): ActivityResult {
        val resultIntent = Intent()
        resultIntent.data = getImageUri()
        resultIntent.addCategory(Intent.CATEGORY_OPENABLE)
        resultIntent.action = Intent.ACTION_GET_CONTENT
        return ActivityResult(RESULT_OK, resultIntent)
    }

    @Test
    fun test_upload_new_story_with_image_from_gallery() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        newStoryFragmentScenario.onFragment { fragment ->
            navController.setGraph(R.navigation.story_navigation)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        val expectedIntent = allOf(
            hasAction(Intent.ACTION_GET_CONTENT),
            hasCategories(setOf(Intent.CATEGORY_OPENABLE)),
            hasType("image/*")
        )
        val activityResult = getGalleryPickActivityResultIntentStub()
        intending(expectedIntent).respondWith(activityResult)

        onView(withId(R.id.add_image_gallery)).perform(click())
        intended(expectedIntent)

        onView(withId(R.id.ed_description)).perform(replaceText("description"))

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("upload_story_success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.btn_upload)).perform(click())
    }
}


