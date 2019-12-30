package com.sample.themoviedb.genres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.repositories.GenreRepository
import io.reactivex.disposables.CompositeDisposable

class GenresViewModel(private val genreRepository: GenreRepository) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val _genresLiveData = MutableLiveData<List<Genre>>()
    val genresLiveData: LiveData<List<Genre>>
        get() = _genresLiveData

    val selectedGenres = MutableLiveData<Set<Genre>>()


    fun fetchGenres() {
        disposables.add(genreRepository.fetchGenres().subscribe({
            _genresLiveData.value = it.genres
        }, {
            _genresLiveData.value = emptyList()
        }))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}