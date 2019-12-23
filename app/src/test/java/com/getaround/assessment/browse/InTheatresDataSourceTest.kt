package com.sample.themoviedb.browse

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.browse.intheatres.InTheatresDataSource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class InTheatresDataSourceTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()
    private val api = mockk<MovieApi>(relaxed = true)
    private var region = "NL"
    private lateinit var datasource: InTheatresDataSource
    private val disposables = CompositeDisposable()
    private val errorLiveData = MutableLiveData<Throwable>()

    @Before
    fun setup() {
        datasource =
            InTheatresDataSource(region, api, disposables, errorLiveData)
    }

    @Test
    fun initialload_always_fetches_firstpage() {
        datasource.loadInitial(mockk(), mockk())
        verify(exactly = 1) { api.getNowInThreatres(eq(1), eq("NL")) }
    }

    @Test
    fun initialload_error_handling() {
        every { api.getNowInThreatres(eq(1), eq("NL")) } returns Single.error(IOException())
        datasource.loadInitial(mockk(), mockk())
        Assert.assertTrue(errorLiveData.value is IOException)
    }

    @Test
    fun loadafter_invokes_the_next_page() {
        val params = PageKeyedDataSource.LoadParams<Int>(1, 20)
        datasource.loadAfter(params, mockk())
        verify(exactly = 1) { api.getNowInThreatres(eq(2), eq("NL")) }
    }
}