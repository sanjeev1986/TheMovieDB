package com.sample.themoviedb.api.movies


import com.sample.themoviedb.utils.network.HttpStack
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Movie Api domain object
 */
class MovieApi(private val httpStack: HttpStack) {
    private val movieApiInternal: MovieApiInternal by lazy(LazyThreadSafetyMode.NONE) { httpStack.constructApiFacade<MovieApiInternal>() }

    fun getNowInThreatres(page: Int, region: String): Single<NowInThreatresResponse> {
        return httpStack.dispatchHttpRequest(movieApiInternal.fetchNowInTheatres(page, region))
    }

    /**
     * Internal http structures are hidden from invoker
     */
    private interface MovieApiInternal {
        @GET("movie/now_playing")
        fun fetchNowInTheatres(@Query("page") page: Int, @Query("region") region: String?): Single<NowInThreatresResponse>
    }
}