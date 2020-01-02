package com.sample.themoviedb.api.genres

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import kotlinx.android.parcel.Parcelize
import retrofit2.http.GET

interface GenreApi {
    @GET("genre/movie/list")
    suspend fun fetchGenres(): GenreResponse
}

@Parcelize
class Genre : Parcelable {

    @SerializedName("id")
    @Expose
    var id: Int = 0
    @SerializedName("name")
    @Expose
    var name: String? = null

}

@Parcelize
class GenreResponse : Parcelable {

    @SerializedName("genres")
    @Expose
    var genres: List<Genre>? = null

}