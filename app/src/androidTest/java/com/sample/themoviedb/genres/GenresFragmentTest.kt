package com.sample.themoviedb.genres

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sample.themoviedb.R
import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.common.AppViewModerFactory
import com.sample.themoviedb.repositories.GenreRepository
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GenresFragmentTest {
    private val genreRepository: GenreRepository = mockk()

    @Before
    fun setup() {
        AppViewModerFactory.setInstance(
            GenresViewModel::class,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return GenresViewModel(genreRepository, mockk()) as T
                }
            }
        )
    }

    @Test
    fun test_genres_loaded() {
        coEvery { genreRepository.fetchGenres(any()) } returns listOf(
            Genre(1, "A"),
            Genre(2, "B"),
            Genre(3, "C"),
            Genre(4, "D"),
            Genre(5, "E")
        )

        launchFragmentInContainer<GenresFragment>(
            null, R.style.AppTheme
        )
    }
}
