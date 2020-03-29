package com.sample.themoviedb.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.details.MovieDetailsActivity
import com.sample.themoviedb.utils.ui.loadImage
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber


class SearchActivity : BaseActivity(), TextWatcher {

    private val searchViewModel by lazy {
        ViewModelProviders.of(
            this,
            TheMovieDbApp.getInstance(this).appViewModerFactory.buildSearchViewModelFactory()
        ).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search)
        setSupportActionBar(toolbar)
        findViewById<AppCompatImageView>(R.id.backKey).setOnClickListener {
            finish()
        }
        findViewById<AppCompatImageView>(R.id.voiceSearch).setOnClickListener {
            finish()
        }
        findViewById<AppCompatEditText>(R.id.searchQueryEtTxt).apply {
            addTextChangedListener(this@SearchActivity)
            setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    return@OnEditorActionListener true
                }
                false
            })
        }
        val movieAdapter = MovieListAdapter()
        searchList.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        searchViewModel.resultsLiveData.observe(this, Observer {
            Timber.d(it.toString())
            when (it) {

                is ViewModelResult.Progress -> {
                }
                is ViewModelResult.Success -> {
                    movieAdapter.submitList(it.result)
                }
                is ViewModelResult.Failure -> {
                }
            }

        })
    }

    val diffUtil: DiffUtil.ItemCallback<Movie> = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }

    private inner class MovieListAdapter :
        PagedListAdapter<Movie, MovieViewHolder>(diffUtil) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
            MovieViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_discover, parent, false)
            )

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            getItem(position)?.apply {
                holder.bind(this)
            }
        }
    }

    private inner class MovieViewHolder(private var view: View) :
        RecyclerView.ViewHolder(view) {
        private val movieImage = view.findViewById<ImageView>(R.id.movieImage)

        fun bind(movie: Movie) {
            with(movie) {
                posterPath?.apply { movieImage.loadImage(this) }
            }
            view.setOnClickListener {
                onClick(movieImage, movie)
            }
        }
    }

    fun onClick(movieImage: ImageView, movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MovieDetailsActivity.EXTRA_ID, movie.id)
        intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_RES, movie.backdropPath)
        intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_THUMBNAIL, movie.posterPath)
        intent.putExtra(MovieDetailsActivity.EXTRA_TITLE, movie.title)
        intent.putExtra(MovieDetailsActivity.EXTRA_OVERVIEW, movie.overview)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this, movieImage, movie.title ?: ""
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.trim()
            ?.let {
                if (it.isEmpty()) {
                    //inTheatresViewModel.refresh()
                } else {
                    searchViewModel.search(it.toString())
                }
            }
    }

}