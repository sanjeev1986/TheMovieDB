package com.sample.themoviedb.api.search


import com.sample.themoviedb.utils.network.HttpStack
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Search Api domain object
 */
class SearchApi(private val httpStack: HttpStack) {
    private val api: SearchApiInternal by lazy(LazyThreadSafetyMode.NONE) { httpStack.constructApiFacade<SearchApiInternal>() }

    fun searchMovies(page: Int, query: String): Single<SearchResponse> {
        return httpStack.dispatchHttpRequest(api.searchMovieDb(page, query))
    }

    /**
     * Internal http structures are hidden from invoker
     */
    private interface SearchApiInternal {
        @GET("search/movie")
        fun searchMovieDb(@Query("page") page: Int, @Query("query") query: String): Single<SearchResponse>
    }
}