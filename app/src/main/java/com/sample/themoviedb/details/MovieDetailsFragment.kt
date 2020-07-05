package com.sample.themoviedb.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.movies.MovieDetailsResponse
import com.sample.themoviedb.common.BaseFragment
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.utils.ui.loadImage
import kotlinx.android.synthetic.main.activity_movie_details.*

/**
 * Displays Backdrop and Overview of the Image
 */
class MovieDetailsFragment : BaseFragment() {

    private lateinit var movie: MovieDetailsResponse

    private val viewModel by viewModels<MovieDetailsViewModel> {
        val safeArgs: MovieDetailsFragmentArgs by navArgs()
        val movie = safeArgs.movie
        TheMovieDbApp.getInstance(
            requireContext()
        ).appViewModerFactory.buildMovieDetailsViewModelFactory(movie.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val genreListAdapter = view.findViewById<RecyclerView>(R.id.movieGenreList)

        viewModel.movieDetailsLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ViewModelResult.Success -> {
                    genreListAdapter.adapter =
                        GenreListAdapter(it.result.genres?.filter { it.id == it.id }
                            ?.map { it.name }
                            ?: emptyList())
                    genreListAdapter.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    movieImage?.run { it.result.backdropPath?.run { backdropImgView.loadImage(this) } }
                        ?: run {
                            backdropImgView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                            backdropImgView.setImageResource(R.drawable.ic_baseline_photo_24px)
                        }
                    it.result.posterPath?.run {
                        view.findViewById<ImageView>(R.id.movieImage).loadImage(this)
                    }
                    titleTxtView.text = it.result.title
                    overviewTxtView.text = it.result.overview
                    movie = it.result
                    view.findViewById<TextView>(R.id.rating).text = "${it.result.voteAverage}"
                    view.findViewById<TextView>(R.id.ratingCount).text = "${it.result.voteCount}"
                }

                is ViewModelResult.Failure -> {

                }
            }
        })
        viewModel.fetchMovieDetails()
    }

    private inner class GenreListAdapter(private val items: List<String>) :
        RecyclerView.Adapter<GenreViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder =
            GenreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_movie_genre, parent, false)
            )

        override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }

    private inner class GenreViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        private val genreChip = view.findViewById<Chip>(R.id.genreChip)

        fun bind(genre: String) {
            genreChip.text = genre
        }
    }
}
