package com.sample.themoviedb.trending

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.Movie
import com.sample.themoviedb.common.BaseFragment
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.utils.ui.loadImage
import javax.inject.Inject

class TrendingFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: TrendingViewModel.TrendingViewModelFactory

    private val viewModel by viewModels<TrendingViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TheMovieDbApp.getInstance(requireContext()).applicationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generic_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val moviesListView = view.findViewById<RecyclerView>(R.id.listView)
        val movieAdapter = MovieListAdapter()
        moviesListView.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        view.findViewById<Chip>(R.id.more).setOnClickListener {
            findNavController().navigate(TrendingFragmentDirections.actionHomeTrendingToActionSearch())
        }
        val color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.inThreatresParentView)
        swipeRefreshLayout.setColorSchemeColors(color, color, color)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
        viewModel.resultsLiveData.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is ViewModelResult.Success -> {
                        swipeRefreshLayout.isRefreshing = false
                        movieAdapter.items = it.result
                        movieAdapter.notifyDataSetChanged()
                    }
                    is ViewModelResult.Failure -> {
                        swipeRefreshLayout.isRefreshing = false
                        displayError("Error", "dismiss", null)
                    }
                }
            }
        )
        viewModel.refresh()
    }

    private inner class MovieListAdapter : RecyclerView.Adapter<MovieViewHolder>() {

        var items: List<Movie> = mutableListOf<Movie>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder =
            MovieViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_discover, parent, false)
            )

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }

    private inner class MovieViewHolder(private var view: View) :
        RecyclerView.ViewHolder(view) {
        private val movieImage = view.findViewById<ImageView>(R.id.movieImage)
        private val movieTitle = view.findViewById<TextView>(R.id.titleTxtView)
        private val addToWatchList = view.findViewById<CheckBox>(R.id.addToWatchList)

        fun bind(movie: Movie) {
            addToWatchList.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    viewModel.addToWatchList(movie)
                } else {
                    viewModel.removeFromWatchList(movie)
                }
            }
            with(movie) {
                posterPath?.apply { movieImage.loadImage(this) }
                movieTitle.text = title
            }
            view.setOnClickListener {
                findNavController().navigate(
                    TrendingFragmentDirections.actionHomeTrendingToMovieDetailsFragment(
                        movie.id
                    )
                )
            }
        }
    }
}
