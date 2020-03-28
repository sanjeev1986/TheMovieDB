package com.sample.themoviedb.details

import android.os.Bundle
import android.widget.ImageView
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.utils.ui.loadImage
import com.sample.themoviedb.R
import kotlinx.android.synthetic.main.activity_movie_details.*

/**
 * Displays Backdrop and Overview of the Image
 */
class MovieDetailsActivity : BaseActivity() {
    companion object {
        /**
         * Movie Title
         */
        const val EXTRA_TITLE = "com.moviedb.android.detail.MoviewDetailsActivity.EXTRA_TITLE"
        /**
         * Movie Overview
         */
        const val EXTRA_OVERVIEW = "com.moviedb.android.detail.MoviewDetailsActivity.EXTRA_OVERVIEW"
        /**
         * Backdrop image path
         */
        const val EXTRA_IMAGE_RES = "com.moviedb.android.detail.MoviewDetailsActivity.EXTRA_IMAGE_RES"
        /**
         * Movie thumbnail
         */
        const val EXTRA_IMAGE_THUMBNAIL = "com.moviedb.android.detail.MoviewDetailsActivity.EXTRA_IMAGE_THUMBNAIL"
        /**
         * Movie ID
         */
        const val EXTRA_ID = "com.moviedb.android.detail.MoviewDetailsActivity.EXTRA_ID"
    }

    private var movieTitle: String? = null
    private var movieOverview: String? = null
    private var movieImage: String? = null
    private var movieThumbnail: String? = null
    private var movieId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        if (savedInstanceState == null) {
            movieTitle = intent.getStringExtra(EXTRA_TITLE)
            movieOverview = intent.getStringExtra(EXTRA_OVERVIEW)
            movieImage = intent.getStringExtra(EXTRA_IMAGE_RES)
            movieThumbnail = intent.getStringExtra(EXTRA_IMAGE_THUMBNAIL)
            movieId = intent.getIntExtra(EXTRA_ID, -1)
        } else {
            movieTitle = savedInstanceState.getString(EXTRA_TITLE) ?: ""
            movieOverview = savedInstanceState.getString(EXTRA_OVERVIEW) ?: ""
            movieImage = savedInstanceState.getString(EXTRA_IMAGE_RES) ?: ""
            movieThumbnail = savedInstanceState.getString(EXTRA_IMAGE_THUMBNAIL) ?: ""
            movieId = savedInstanceState.getInt(EXTRA_ID, -1)
        }

        setSupportActionBar(toolbarDetails)
        supportActionBar?.apply {
            title = null
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        movieImage?.run { backdropImgView.loadImage(this) } ?: run {
            backdropImgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            backdropImgView.setImageResource(R.drawable.ic_baseline_photo_24px)
        }
        movieThumbnail?.run {
            findViewById<ImageView>(R.id.movieImage).loadImage(this)
        }
        titleTxtView.text = movieTitle
        overviewTxtView.text = movieOverview
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_TITLE, movieTitle)
        outState.putString(EXTRA_OVERVIEW, movieOverview)
        outState.putString(EXTRA_IMAGE_RES, movieImage)
        outState.putString(EXTRA_IMAGE_THUMBNAIL, movieThumbnail)
        outState.putInt(EXTRA_ID, movieId)
    }

}