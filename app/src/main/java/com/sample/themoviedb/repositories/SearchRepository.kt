package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.search.SearchApi
import com.sample.themoviedb.api.search.SearchResponse
import com.sanj.appstarterpack.storage.memory.InMemoryCache
import io.reactivex.Single

class SearchRepository(
    private val memoryCache: InMemoryCache,
    private val movieApi: SearchApi
) {

    fun getNowInThreatres(
        page: Int,
        region: String,
        refreshCache: Boolean = false
    ): Single<SearchResponse> {
        return if (refreshCache) {
            movieApi.searchMovieDb(page, region)
                .map {
                    memoryCache.put(SearchResponse::class.java, it)
                }
        } else {
            memoryCache.get(SearchResponse::class.java)
                .switchIfEmpty(
                    Single.defer { movieApi.searchMovieDb(page, region) }
                )
                .map {
                    memoryCache.put(SearchResponse::class.java, it)
                }
        }
    }
}