package com.sample.themoviedb.browse

import android.content.ComponentName
import android.widget.TextView
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sample.themoviedb.R
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.browse.intheatres.InTheatresViewModel
import com.sample.themoviedb.common.AppViewModerFactory
import com.sample.themoviedb.genres.GenresViewModel
import com.sample.themoviedb.repositories.GenreRepository
import com.sample.themoviedb.search.SearchActivity
import io.mockk.mockk
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MovieBrowserActivityTest {
    @get:Rule
    val mCountingTaskExecutorRule = InstantTaskExecutorRule()

    private val movieApi: MovieApi = mockk()
    private val genreApi: GenreRepository = mockk()

    @Before
    fun setup() {
        AppViewModerFactory.setInstance(
            InTheatresViewModel::class,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return InTheatresViewModel(movieApi) as T
                }
            })
        AppViewModerFactory.setInstance(GenresViewModel::class, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GenresViewModel(genreApi) as T
            }
        })
    }

    @Test
    fun testToolbar_title_action_icons() {
        val scenario = launchActivity<MovieBrowserActivity>()
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.toolbar))))
            .check(matches(withText("In Theatres")))
        onView(withId(R.id.action_filter)).check(matches(isDisplayed()))
        onView((withId(R.id.action_search))).check(matches(isDisplayed()))
        scenario.close()
    }

    @Test
    fun test_genre_filter_click_launches_genre_screen() {
        val scenario = launchActivity<MovieBrowserActivity>()
        scenario.moveToState(Lifecycle.State.RESUMED)
        onView(withId(R.id.action_filter)).perform(click())
        onView(withId(R.id.genreGridView)).check(matches(isDisplayed()))
        onView(allOf(instanceOf(TextView::class.java), withParent(withId(R.id.toolbar))))
            .check(matches(withText("In Theatres")))
        scenario.close()
    }

    @Test
    fun test_search_click_launches_search_screen() {
        val scenario = launchActivity<MovieBrowserActivity>()
        scenario.moveToState(Lifecycle.State.RESUMED)
        Intents.init()
        onView(withId(R.id.action_search)).perform(click())
        Intents.intended(
            hasComponent(
                ComponentName(
                    getApplicationContext(),
                    SearchActivity::class.java
                )
            ))
        scenario.close()
    }

}