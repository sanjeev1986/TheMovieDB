package com.sample.themoviedb.browse.intheatres

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.common.BaseActivity
import com.sample.themoviedb.common.BaseFragment
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.details.MovieDetailsActivity
import com.sample.themoviedb.genres.GenresViewModel
import com.sample.themoviedb.utils.ui.baseActivity
import com.sample.themoviedb.utils.ui.loadImage

class InTheatreFragment : BaseFragment() {

    private val genresViewModel by lazy {
        activity?.run {
            ViewModelProviders.of(
                this,
                TheMovieDbApp.getInstance(this).appViewModerFactory.buildGenreViewModelFactory()
            ).get(GenresViewModel::class.java)
        } ?: kotlin.run { throw IllegalStateException() }
    }

    private val inTheatresViewModel by lazy {
        ViewModelProviders.of(
            this
            ,
            TheMovieDbApp.getInstance(requireContext()).appViewModerFactory.buildBrowseMoviesViewModelFactory()
        ).get(InTheatresViewModel::class.java)
    }

    private lateinit var inTheatresListView: RecyclerView
    private lateinit var inThreatresParentView: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.run {
            (this as AppCompatActivity).supportActionBar?.title = "In Theatres"
            genresViewModel.selectedGenres.observe(this, Observer {
                inTheatresViewModel.refresh(it)
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_intheatres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inTheatresListView = view.findViewById(R.id.inTheatresListView)
        inThreatresParentView = view.findViewById(R.id.inThreatresParentView)
        val movieAdapter = MovieListAdapter()

        inTheatresViewModel.resultsLiveData.observe(viewLifecycleOwner, Observer {

            when (it) {
                is ViewModelResult.Progress -> {
                    inThreatresParentView.isRefreshing = true
                }
                is ViewModelResult.Success -> {
                    movieAdapter.submitList(it.result)
                    inThreatresParentView.isRefreshing = false
                }
                is ViewModelResult.Failure -> {
                    inThreatresParentView.isRefreshing = false
                    (requireActivity() as BaseActivity).prepareErrorSnackBar( getString(R.string.no_network),"REFRESH"){
                        inTheatresViewModel.refresh(genresViewModel.selectedGenres.value)
                    }.show()
                }
            }

        })

        inTheatresListView.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        inThreatresParentView.apply {
            setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.white))
            setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent))
            setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, R.color.progress))
            setOnRefreshListener {
                inTheatresViewModel.refresh()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(!genresViewModel.selectedGenres.value.isNullOrEmpty()){
            val animation = ContextCompat.getDrawable(requireContext(),R.drawable.avd_anim) as AnimatedVectorDrawable
            menu.findItem(R.id.action_filter)?.icon = animation
            animation.start()
        }
        menu.findItem(R.id.action_filter).isVisible = true
        menu.findItem(R.id.action_search).isVisible = true
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "In Theatres"
        return super.onPrepareOptionsMenu(menu)

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
        private val movieTitle = view.findViewById<TextView>(R.id.titleTxtView)

        fun bind(movie: Movie) {
            with(movie) {
                posterPath?.apply { movieImage.loadImage(this) }
                movieTitle.text = title
            }
            view.setOnClickListener {
                this@InTheatreFragment.onClick(movieImage, movie)
            }
        }
    }

    fun onClick(movieImage: ImageView, movie: Movie) {
        activity?.run {
            val intent = Intent(activity, MovieDetailsActivity::class.java)
            intent.putExtra(MovieDetailsActivity.EXTRA_ID, movie.id)
            intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_RES, movie.backdropPath)
            intent.putExtra(MovieDetailsActivity.EXTRA_IMAGE_THUMBNAIL, movie.posterPath)
            intent.putExtra(MovieDetailsActivity.EXTRA_TITLE, movie.title)
            intent.putExtra(MovieDetailsActivity.EXTRA_OVERVIEW, movie.overview)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this, movieImage, movie.title ?: ""
            )
            startActivity(intent, options.toBundle())
        }
    }
}