package com.api.moviedb.search

import com.api.moviedb.Movie
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchResponse(

    @SerializedName("page")
    @Expose
    var page: Int = 0,
    @SerializedName("total_results")
    @Expose
    var totalResults: Int = 0,
    @SerializedName("total_pages")
    @Expose
    var totalPages: Int = 0,
    @SerializedName("results")
    @Expose
    var results: List<Movie>? = null

)