package com.sample.themoviedb.browse

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sample.themoviedb.R
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.details.MovieDetailsActivity
import com.sample.themoviedb.genres.GenresFragment
import com.sample.themoviedb.search.SearchActivity

/**
 * Displays Movies.
 *
 * We have two viewmodels in this activity. This is valid MVVM cos:
 * 1. ViewModels are UI agnostic and should be reusable
 * 2. ViewModel corresponds to a single type of data and
 * works with two way databinding( which is not demonstrated in this app thou)
 */
class MovieBrowserActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        appBarConfiguration = AppBarConfiguration(bottomNavigationView.menu)
        setupActionBarWithNavController(this, navController, appBarConfiguration)
        setupBottomNavMenu(navController)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
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

    fun onClick(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MovieDetailsActivity.EXTRA_ID, movie.id)
        intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_RES, movie.backdropPath)
        intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_THUMBNAIL, movie.posterPath)
        intent.putExtra(MovieDetailsActivity.EXTRA_TITLE, movie.title)
        intent.putExtra(MovieDetailsActivity.EXTRA_OVERVIEW, movie.overview)
        startActivity(intent)
    }
}
