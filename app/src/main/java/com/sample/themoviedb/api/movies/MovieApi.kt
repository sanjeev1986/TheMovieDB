package com.sample.themoviedb.api.movies


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Movie Api domain object
 */
interface MovieApi {
    @GET("movie/now_playing")
    suspend fun fetchNowInTheatres(@Query("page") page: Int, @Query("region") region: String? = null, @Query("with_genres") genre: String? = null): NowInThreatresResponse

    @GET("movie/{movie_id}")
    suspend fun fetchMovieDetails(@Path("movie_id") id: Int): MovieDetailsResponse
}