package com.sample.themoviedb.api.search


import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Search Api domain object
 */
interface SearchApi {
    @GET("search/movie")
    fun searchMovieDb(@Query("page") page: Int, @Query("query") query: String): Single<SearchResponse>
}