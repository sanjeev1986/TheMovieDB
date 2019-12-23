package com.sample.themoviedb.browse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.R
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.browse.intheatres.InTheatresViewModel
import com.sample.themoviedb.browse.search.SearchViewModel
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.details.MoviewDetailsActivity
import com.sample.themoviedb.utils.ui.loadImage
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

/**
 * Displays Movies.
 *
 * We have two viewmodels in this activity. This is valid MVVM cos:
 * 1. ViewModels are UI agnostic and should be reusable
 * 2. ViewModel corresponds to a single type of data and
 * works with two way databinding( which is not demonstrated in this app thou)
 */
class MovieBrowserActivity : BaseActivity(), SearchView.OnQueryTextListener {
    private var isFirstLoad = true

    override fun onQueryTextSubmit(query: String?): Boolean = true


    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.trim()
            ?.let {
                if (it.isEmpty()) {
                    discoverParentLayout.isEnabled = true
                    inTheatresViewModel.refresh()
                } else {
                    discoverParentLayout.isEnabled = false
                    searchViewModel.search(it)
                }
            }
        return true
    }

    fun onClick(movie: Movie) {
        val intent = Intent(this, MoviewDetailsActivity::class.java)
        intent.putExtra(MoviewDetailsActivity.EXTRA_ID, movie.id)
        intent.putExtra(MoviewDetailsActivity.EXTRA_IMAGE_RES, movie.backdropPath)
        intent.putExtra(MoviewDetailsActivity.EXTRA_IMAGE_THUMBNAIL, movie.posterPath)
        intent.putExtra(MoviewDetailsActivity.EXTRA_TITLE, movie.title)
        intent.putExtra(MoviewDetailsActivity.EXTRA_OVERVIEW, movie.overview)
        startActivity(intent)
    }


    private val inTheatresViewModel by lazy {
        ViewModelProviders.of(
            this
            , TheMovieDbApp.getInstance(this).appViewModerFactory.buildBrowseMoviesViewModelFactory()
        ).get(InTheatresViewModel::class.java)
    }

    private val searchViewModel by lazy {
        ViewModelProviders.of(
            this
            , TheMovieDbApp.getInstance(this).appViewModerFactory.buildSearchViewModelFactory()
        ).get(SearchViewModel::class.java)
    }

    val diffUtil: DiffUtil.ItemCallback<Movie> = object : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_browse_movies)

        discoverParentLayout.isRefreshing = true

        val movieAdapter = MovieListAdapter()
        movieListView.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        searchViewModel.resultsLiveData.observe(this, Observer {
            Timber.d(it.toString())
            when (it) {

                is ViewModelResult.Progress -> {
                    discoverParentLayout.isRefreshing = true
                }
                is ViewModelResult.Success -> {
                    if (savedInstanceState == null && isFirstLoad) {
                        isFirstLoad = false
                    }
                    Timber.d("result = ${it.result.size}")

                    movieAdapter.submitList(it.result)
                    discoverParentLayout.isRefreshing = false
                }
                is ViewModelResult.Failure -> {
                    discoverParentLayout.isRefreshing = false
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.no_network),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        })

        inTheatresViewModel.resultsLiveData.observe(this, Observer {

            when (it) {
                is ViewModelResult.Progress -> {
                    discoverParentLayout.isRefreshing = true
                }
                is ViewModelResult.Success -> {
                    if (savedInstanceState == null && isFirstLoad) {
                        isFirstLoad = false
                    }
                    movieAdapter.submitList(it.result)
                    discoverParentLayout.isRefreshing = false
                }
                is ViewModelResult.Failure -> {
                    discoverParentLayout.isRefreshing = false
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.no_network),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }

        })
        discoverParentLayout.apply {
            setProgressBackgroundColorSchemeColor(resources.getColor(R.color.white))
            setColorSchemeColors(resources.getColor(R.color.colorAccent))
            setProgressBackgroundColorSchemeColor(resources.getColor(R.color.western))
            setOnRefreshListener {
                inTheatresViewModel.refresh()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.browser_menu, menu)
        (menu.findItem(R.id.action_search).actionView as SearchView).setOnQueryTextListener(this)
        return true
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
                this@MovieBrowserActivity.onClick(movie)
            }
        }
    }

}