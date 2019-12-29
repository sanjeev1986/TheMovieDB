package com.sample.themoviedb.api.movies

import android.os.Parcelable
import com.sample.themoviedb.api.Movie
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NowInThreatresResponse(
    @SerializedName("results")
    @Expose
    var results: List<Movie>? = null,
    @SerializedName("page")
    @Expose
    var page: Int = 0,
    @SerializedName("total_results")
    @Expose
    var totalResults: Int = 0,
    @SerializedName("dates")
    @Expose
    var dates: Dates? = null,
    @SerializedName("total_pages")
    @Expose
    var totalPages: Int = 0
):Parcelable

@Parcelize
data class Dates(
    @SerializedName("maximum")
    @Expose
    var maximum: String? = null,
    @SerializedName("minimum")
    @Expose
    var minimum: String? = null
):Parcelable
