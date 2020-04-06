package com.sample.themoviedb.browse

import android.content.Intent
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.browse.intheatres.InTheatreFragment
import com.sample.themoviedb.search.SearchActivity
import com.sample.themoviedb.search.SearchViewModel
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.details.MovieDetailsActivity
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
class MovieBrowserActivity : BaseActivity() {

    fun onClick(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MovieDetailsActivity.EXTRA_ID, movie.id)
        intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_RES, movie.backdropPath)
        intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_THUMBNAIL, movie.posterPath)
        intent.putExtra(MovieDetailsActivity.EXTRA_TITLE, movie.title)
        intent.putExtra(MovieDetailsActivity.EXTRA_OVERVIEW, movie.overview)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.title_browse_movies)
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, 0, 0, android.R.animator.fade_out)
            .replace(
                R.id.fragmentContainer,
                InTheatreFragment(),
                InTheatreFragment::class.java.simpleName
            )
            .addToBackStack(MovieBrowserActivity::class.java.simpleName)
            .commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.browser_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, 0, 0, android.R.animator.fade_out)
                .replace(
                    R.id.fragmentContainer,
                    GenresFragment(),
                    GenresFragment::class.java.simpleName
                )
                .addToBackStack(MovieBrowserActivity::class.java.simpleName)
                .commitAllowingStateLoss()
            return true
        } else if (item.itemId == R.id.action_search) {
            startActivity(Intent(this, SearchActivity::class.java))
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


