package com.sample.themoviedb.search

import android.content.Context
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.common.BaseFragment
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.utils.ui.loadImage
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SearchFragment : BaseFragment(), TextWatcher {

    @Inject
    @field:Named("Search")
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<SearchViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TheMovieDbApp.getInstance(requireContext()).applicationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.backKey).setOnClickListener {
            requireActivity().onBackPressed()
        }
        view.findViewById<AppCompatEditText>(R.id.searchQueryEtTxt).apply {
            addTextChangedListener(this@SearchFragment)
            setOnEditorActionListener(
                OnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        return@OnEditorActionListener true
                    }
                    false
                }
            )
        }
        val movieAdapter = MovieListAdapter()
        searchList.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        viewModel.resultsLiveData.observe(
            viewLifecycleOwner,
            Observer {
                Timber.d(it.toString())
                when (it) {

                    is ViewModelResult.Progress -> {
                    }
                    is ViewModelResult.Success -> {
                        if (it.result.isEmpty()) {
                            view.findViewById<LottieAnimationView>(R.id.animation_view).apply {
                                visibility = View.VISIBLE
                                playAnimation()
                            }
                            view.findViewById<RecyclerView>(R.id.searchList).visibility =
                                View.INVISIBLE
                        } else {
                            view.findViewById<LottieAnimationView>(R.id.animation_view).visibility =
                                View.INVISIBLE
                            view.findViewById<RecyclerView>(R.id.searchList).visibility =
                                View.VISIBLE
                            movieAdapter.submitList(it.result)
                        }
                    }
                    is ViewModelResult.Failure -> {
                        movieAdapter.submitList(null)
                        view.findViewById<RecyclerView>(R.id.searchList).visibility = View.INVISIBLE
                        view.findViewById<LottieAnimationView>(R.id.animation_view).apply {
                            visibility = View.VISIBLE
                            playAnimation()
                        }
                    }
                }
            }
        )
    }

    val diffUtil: DiffUtil.ItemCallback<Movie> =
        object : DiffUtil.ItemCallback<Movie>() {
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
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_discover, parent, false)
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
                // onClick(movieImage, movie)
            }
        }
    }

    /* fun onClick(movieImage: ImageView, movie: Movie) {
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
         requireActivity().finish()
     }*/

    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        s?.trim()
            ?.run { viewModel.search(toString()) }
    }
}
