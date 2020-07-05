package com.sample.themoviedb.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.navArgs
import com.sample.themoviedb.R
import com.sample.themoviedb.common.BaseFragment
import com.sample.themoviedb.utils.ui.loadImage
import kotlinx.android.synthetic.main.activity_movie_details.*

/**
 * Displays Backdrop and Overview of the Image
 */
class MovieDetailsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: MovieDetailsFragmentArgs by navArgs()
        val movie = safeArgs.movie
        movieImage?.run { backdropImgView.loadImage(movie.backdropPath!!) } ?: run {
            backdropImgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            backdropImgView.setImageResource(R.drawable.ic_baseline_photo_24px)
        }

        view.findViewById<ImageView>(R.id.movieImage).loadImage(movie.posterPath!!)

        titleTxtView.text = movie.title
        overviewTxtView.text = movie.overview
    }
}
