package com.sample.themoviedb.api.trending

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.api.genres.Genre
import kotlinx.android.parcel.Parcelize
import retrofit2.http.GET
import retrofit2.http.Path

interface TrendingApi {
    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrending(
        @Path("media_type") media_type: String = "all",
        @Path("time_window") time_window: String = "day"
    ): TrendingResponse

    @Parcelize
    data class TrendingResponse(
        @SerializedName("results")
        @Expose
        val results: List<Movie>? = null,
        @SerializedName("page")
        @Expose
        val page: Int = 0,
        @SerializedName("total_results")
        @Expose
        val totalResults: Int = 0,
        @SerializedName("dates")
        @Expose
        val dates: Dates? = null,
        @SerializedName("total_pages")
        @Expose
        val totalPages: Int = 0
    ) : Parcelable

    @Parcelize
    data class Dates(
        @SerializedName("maximum")
        @Expose
        val maximum: String? = null,
        @SerializedName("minimum")
        @Expose
        val minimum: String? = null
    ) : Parcelable

    @Parcelize
    data class MovieDetailsResponse(

        @SerializedName("adult")
        @Expose
        val adult: Boolean? = null,
        @SerializedName("backdrop_path")
        @Expose
        val backdropPath: String? = null,
        @SerializedName("budget")
        @Expose
        val budget: Int? = null,
        @SerializedName("genres")
        @Expose
        val genres: List<Genre>? = null,
        @SerializedName("homepage")
        @Expose
        val homepage: String? = null,
        @SerializedName("id")
        @Expose
        val id: Int? = null,
        @SerializedName("imdb_id")
        @Expose
        val imdbId: String? = null,
        @SerializedName("original_language")
        @Expose
        val originalLanguage: String? = null,
        @SerializedName("original_title")
        @Expose
        val originalTitle: String? = null,
        @SerializedName("overview")
        @Expose
        val overview: String? = null,
        @SerializedName("popularity")
        @Expose
        val popularity: Double? = null,
        @SerializedName("poster_path")
        @Expose
        val posterPath: String? = null,
        @SerializedName("production_companies")
        @Expose
        val productionCompanies: List<ProductionCompany>? = null,
        @SerializedName("production_countries")
        @Expose
        val productionCountries: List<ProductionCountry>? = null,
        @SerializedName("release_date")
        @Expose
        val releaseDate: String? = null,
        @SerializedName("revenue")
        @Expose
        val revenue: Int? = null,
        @SerializedName("runtime")
        @Expose
        val runtime: Int? = null,
        @SerializedName("spoken_languages")
        @Expose
        val spokenLanguages: List<SpokenLanguage>? = null,
        @SerializedName("status")
        @Expose
        val status: String? = null,
        @SerializedName("tagline")
        @Expose
        val tagline: String? = null,
        @SerializedName("title")
        @Expose
        val title: String? = null,
        @SerializedName("video")
        @Expose
        val video: Boolean? = null,
        @SerializedName("vote_average")
        @Expose
        val voteAverage: Double? = null,
        @SerializedName("vote_count")
        @Expose
        val voteCount: Int? = null
    ) : Parcelable

    @Parcelize
    data class ProductionCountry(

        @SerializedName("iso_3166_1")
        @Expose
        val iso31661: String? = null,
        @SerializedName("name")
        @Expose
        val name: String? = null

    ) : Parcelable

    @Parcelize
    data class SpokenLanguage(

        @SerializedName("iso_639_1")
        @Expose
        val iso6391: String? = null,
        @SerializedName("name")
        @Expose
        val name: String? = null
    ) : Parcelable

    @Parcelize
    data class ProductionCompany(

        @SerializedName("name")
        @Expose
        val name: String? = null,
        @SerializedName("id")
        @Expose
        val id: Int? = null
    ) : Parcelable

}
