package com.sample.themoviedb.api.movies


import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Movie Api domain object
 */
interface MovieApi {
    @GET("movie/now_playing")
    suspend fun fetchNowInTheatres(@Query("page") page: Int, @Query("region") region: String? = null, @Query("with_genres") genre: String? = null): NowInThreatresResponse
}