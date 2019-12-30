package com.sample.themoviedb.browse

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.browse.intheatres.InTheatreFragment
import com.sample.themoviedb.browse.search.SearchViewModel
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.details.MoviewDetailsActivity
import com.sample.themoviedb.genres.GenresFragment
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

    override fun onQueryTextSubmit(query: String?): Boolean = true


    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.trim()
            ?.let {
                if (it.isEmpty()) {
                    //inTheatresViewModel.refresh()
                } else {
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



    private val searchViewModel by lazy {
        ViewModelProviders.of(
            this
            , TheMovieDbApp.getInstance(this).appViewModerFactory.buildSearchViewModelFactory()
        ).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_browse_movies)
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in,0, 0, android.R.animator.fade_out)
            .replace(R.id.fragmentContainer,InTheatreFragment(), InTheatreFragment::class.java.simpleName)
            .addToBackStack(MovieBrowserActivity::class.java.simpleName)
            .commitAllowingStateLoss()

        searchViewModel.resultsLiveData.observe(this, Observer {
            Timber.d(it.toString())
            when (it) {

                is ViewModelResult.Progress -> {}
                is ViewModelResult.Success -> {}
                is ViewModelResult.Failure -> {}
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.browser_menu, menu)
        (menu.findItem(R.id.action_search).actionView as SearchView).setOnQueryTextListener(this)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, 0, 0, android.R.animator.fade_out)
                .replace(R.id.fragmentContainer, GenresFragment(), GenresFragment::class.java.simpleName)
                .addToBackStack(MovieBrowserActivity::class.java.simpleName)
                .commitAllowingStateLoss()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            finish()
        } else {
            super.onBackPressed()
        }
    }

}