package com.sample.themoviedb.discover

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

class DiscoverFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: DiscoverViewModel.DiscoverViewModelFactory

    private val viewModel by viewModels<DiscoverViewModel> { viewModelFactory }

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
        val color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.inThreatresParentView)
        swipeRefreshLayout.setColorSchemeColors(color, color, color)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
        view.findViewById<Chip>(R.id.more).setOnClickListener {
            findNavController().navigate(DiscoverFragmentDirections.actionHomeDiscoverToActionSearch())
        }
        moviesListView.apply {
            adapter = movieAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
        viewModel.resultsLiveData.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is ViewModelResult.Success -> {
                        swipeRefreshLayout.isRefreshing = false
                        movieAdapter.items = it.result.toMutableList()
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

        var items = mutableListOf<Pair<Movie, Boolean>>()

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

        fun bind(movieWrapper: Pair<Movie, Boolean>) {
            movieWrapper.first.posterPath?.apply { movieImage.loadImage(this) }
            movieTitle.text = movieWrapper.first.title
            addToWatchList.isChecked = movieWrapper.second
            addToWatchList.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    viewModel.addToWatchList(movieWrapper.first)
                } else {
                    viewModel.removeFromWatchList(movieWrapper.first)
                }
            }
            view.setOnClickListener {
                findNavController().navigate(
                    DiscoverFragmentDirections.actionHomeDiscoverToMovieDetailsFragment(
                        movieWrapper.first.id
                    )
                )
            }
        }
    }
}
