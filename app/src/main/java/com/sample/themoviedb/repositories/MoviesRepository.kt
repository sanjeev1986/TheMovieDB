package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.movies.NowInThreatresResponse
import com.sanj.appstarterpack.storage.disk.DiskCache
import com.sanj.appstarterpack.storage.memory.InMemoryCache
import io.reactivex.Maybe
import io.reactivex.Single

class MoviesRepository(
    private val memoryCache: InMemoryCache,
    private val diskCache: DiskCache,
    private val movieApi: MovieApi
) {

    fun getNowInThreatres(
        page: Int,
        region: String,
        refreshCache: Boolean = false
    ): Single<NowInThreatresResponse> {
        return if (refreshCache) {
            movieApi.fetchNowInTheatres(page, region)
                .flatMap {
                    memoryCache.put(NowInThreatresResponse::class.java, it)
                    diskCache.saveFile(NowInThreatresResponse::class.java, it)
                }
        } else {
            memoryCache.get(NowInThreatresResponse::class.java)
                .switchIfEmpty(Maybe.defer<NowInThreatresResponse> {
                    diskCache.readFile(NowInThreatresResponse::class.java)
                }).switchIfEmpty(
                    Single.defer { movieApi.fetchNowInTheatres(page, region) }
                )
                .flatMap {
                    memoryCache.put(NowInThreatresResponse::class.java, it)
                    diskCache.saveFile(NowInThreatresResponse::class.java, it)
                }
        }
    }
}