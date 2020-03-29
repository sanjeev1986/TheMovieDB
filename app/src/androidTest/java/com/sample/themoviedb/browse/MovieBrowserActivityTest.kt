package com.sample.themoviedb.browse

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.browse.intheatres.InTheatresViewModel
import com.sample.themoviedb.common.AppViewModerFactory
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieBrowserActivityTest {
    @get:Rule
    var mCountingTaskExecutorRule = InstantTaskExecutorRule()

    private val movieApi: MovieApi = mockk()

    @Before
    fun setup() {
        AppViewModerFactory.setInstance(object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return InTheatresViewModel(movieApi) as T
            }
        })
    }
}