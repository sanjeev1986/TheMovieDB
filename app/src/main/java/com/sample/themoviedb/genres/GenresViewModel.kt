package com.sample.themoviedb.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.repositories.GenreRepository
import kotlinx.coroutines.launch

class GenresViewModel(private val genreRepository: GenreRepository) : ViewModel() {

    private val _genresLiveData = MutableLiveData<List<Genre>>()
    val genresLiveData: LiveData<List<Genre>>
        get() = _genresLiveData

    val selectedGenres = MutableLiveData<Set<Genre>>()


    fun fetchGenres() {
        viewModelScope.launch {
            try {
                _genresLiveData.value = genreRepository.fetchGenres()
            } catch (e: Exception) {
                _genresLiveData.value = emptyList()
            }

        }

    }
}