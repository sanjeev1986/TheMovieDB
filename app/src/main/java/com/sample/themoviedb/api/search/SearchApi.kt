package com.sample.themoviedb.api.search

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Search Api domain object
 */
interface SearchApi {
    @GET("search/movie")
    suspend fun searchMovieDb(@Query("page") page: Int, @Query("query") query: String): SearchResponse
}
