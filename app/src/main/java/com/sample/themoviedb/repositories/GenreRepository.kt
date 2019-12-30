package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.genres.GenreApi
import com.sample.themoviedb.api.genres.GenreResponse
import com.sanj.appstarterpack.storage.disk.DiskCache
import com.sanj.appstarterpack.storage.memory.InMemoryCache
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GenreRepository(
    private val inMemoryCache: InMemoryCache,
    private val diskCache: DiskCache,
    private val genreApi: GenreApi
) {

    fun fetchGenres(refreshCache: Boolean = false): Single<GenreResponse> {
        return if (refreshCache) {
            genreApi.fetchGenres().flatMap {
                inMemoryCache.put(GenreResponse::class.java, it)
                diskCache.saveFile(GenreResponse::class.java, it)
            }.compose {
                it.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
            }
        } else {
            inMemoryCache.get(GenreResponse::class.java)
                .switchIfEmpty(Maybe.defer<GenreResponse> {
                    Maybe.defer {
                        diskCache.readFile(GenreResponse::class.java).compose {
                            it.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        }.map { inMemoryCache.put(GenreResponse::class.java, it) }
                    }
                }).switchIfEmpty(
                    Single.defer {
                        genreApi.fetchGenres()
                            .flatMap {
                                inMemoryCache.put(GenreResponse::class.java, it)
                                diskCache.saveFile(GenreResponse::class.java, it)
                            }.compose {
                                it.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                            }
                    }
                )
        }
    }
}