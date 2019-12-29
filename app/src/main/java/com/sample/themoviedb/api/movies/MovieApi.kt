package com.sample.themoviedb.api.movies


import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Movie Api domain object
 */
interface MovieApi {
    @GET("movie/now_playing")
    fun fetchNowInTheatres(@Query("page") page: Int, @Query("region") region: String?): Single<NowInThreatresResponse>
}