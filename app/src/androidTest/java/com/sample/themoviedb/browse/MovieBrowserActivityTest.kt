package com.sample.themoviedb.browse

import android.content.ComponentName
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withParent
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sample.themoviedb.R
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.common.AppViewModerFactory
import com.sample.themoviedb.genres.GenresViewModel
import com.sample.themoviedb.intheatres.InTheatresViewModel
import com.sample.themoviedb.repositories.GenreRepository
import com.sample.themoviedb.search.SearchFragment
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MovieBrowserActivityTest {

    private val movieApi: MovieApi = mockk()
    private val genreApi: GenreRepository = mockk()
    private lateinit var scenari: ActivityScenario<MovieBrowserActivity>

    @Before
    fun setup() {
        AppViewModerFactory.setInstance(
            InTheatresViewModel::class,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return InTheatresViewModel(mockk(), movieApi) as T
                }
            })
        AppViewModerFactory.setInstance(GenresViewModel::class, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GenresViewModel(genreApi, mockk()) as T
            }
        })
        scenari = launchActivity()
    }

    @After
    fun tearDown() {
        scenari.close()
    }

    @Test
    fun testToolbar_title_action_icons() {
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.toolbar))))
            .check(matches(withText("In Theatres")))
        onView(withId(R.id.action_filter)).check(matches(isDisplayed()))
        onView((withId(R.id.action_search))).check(matches(isDisplayed()))
    }

    @Test
    fun test_genre_filter_click_launches_genre_screen() {
        onView(withId(R.id.action_filter)).perform(click())
        onView(withId(R.id.genreGridView)).check(matches(isDisplayed()))
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.toolbar))))
            .check(matches(withText("In Theatres")))
    }

    @Test
    fun test_search_click_launches_search_screen() {
        Intents.init()
        onView(withId(R.id.action_search)).perform(click())
        Intents.intended(
            hasComponent(
                ComponentName(
                    getApplicationContext(),
                    SearchFragment::class.java
                )
            )
        )
    }

}