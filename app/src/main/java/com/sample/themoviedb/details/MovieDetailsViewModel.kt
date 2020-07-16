package com.sample.themoviedb.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.movies.MovieDetailsResponse
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.repositories.MovieDetailsRepository
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val movieDetailsRepository: MovieDetailsRepository
) : ViewModel() {

    private val _movieDetailsLiveData =
        MutableLiveData<ViewModelResult<MovieDetailsResponse, Throwable>>()
    val movieDetailsLiveData: LiveData<ViewModelResult<MovieDetailsResponse, Throwable>>
        get() = _movieDetailsLiveData

    fun fetchMovieDetails(movieId: Int) {
        viewModelScope.launch {
            try {
                val movieDetails = ViewModelResult.Success(
                    movieDetailsRepository.fetchMovieDetails(movieId)
                )
                _movieDetailsLiveData.value = movieDetails
            } catch (e: Exception) {
                _movieDetailsLiveData.value = ViewModelResult.Failure(e)
            }
        }
    }

}