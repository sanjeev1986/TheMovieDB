package com.sample.themoviedb.watchlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.common.ViewModelResult
import com.sample.themoviedb.storage.db.watchlist.WatchListItem
import com.sample.themoviedb.utils.ui.loadImage
import javax.inject.Inject

class WatchListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: WatchListViewModel.WatchListViewModelFactory

    private val viewModel by viewModels<WatchListViewModel> { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TheMovieDbApp.getInstance(requireContext()).applicationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_watch_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val watchListAdapter = WatchListAdapter()
        val watchListView = view.findViewById<RecyclerView>(R.id.watchListView).apply {
            adapter = watchListAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        val nothingToWatch = view.findViewById<TextView>(R.id.nothingToWatch)

        viewModel.resultsLiveData.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is ViewModelResult.Success -> {
                        if (it.result.isNotEmpty()) {
                            nothingToWatch.visibility = View.GONE
                            watchListView.visibility = View.VISIBLE
                            watchListAdapter.items = it.result.toMutableList()
                            watchListAdapter.notifyDataSetChanged()
                        } else {
                            watchListView.visibility = View.GONE
                            nothingToWatch.visibility = View.VISIBLE
                        }
                    }
                    is ViewModelResult.Failure -> {
                    }
                }
            }
        )

        viewModel.deleteLiveData.observe(
            viewLifecycleOwner,
            Observer {
                when (it) {
                    is ViewModelResult.Success -> {
                        watchListAdapter.items.remove(it.result)
                        watchListAdapter.notifyDataSetChanged()
                        if (watchListAdapter.items.isNotEmpty()) {
                            nothingToWatch.visibility = View.GONE
                            watchListView.visibility = View.VISIBLE
                        } else {
                            nothingToWatch.visibility = View.VISIBLE
                            watchListView.visibility = View.GONE
                        }
                    }
                    is ViewModelResult.Failure -> {
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchWatchLists()
    }

    private inner class WatchListAdapter : RecyclerView.Adapter<WatchListItemHolder>() {

        var items = mutableListOf<WatchListItem>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListItemHolder =
            WatchListItemHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_watch_list, parent, false)
            )

        override fun onBindViewHolder(holder: WatchListItemHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size
    }

    private inner class WatchListItemHolder(private var view: View) :
        RecyclerView.ViewHolder(view) {
        private val movieImage = view.findViewById<ImageView>(R.id.movieImage)
        private val movieTitle = view.findViewById<TextView>(R.id.title)
        private val movieOverview = view.findViewById<TextView>(R.id.description)
        private val deleteMovie = view.findViewById<ImageView>(R.id.delete)

        fun bind(movie: WatchListItem) {
            with(movie) {
                posterPath?.apply { movieImage.loadImage(this) }
                movieTitle.text = title
                movieOverview.text = description
            }

            deleteMovie.setOnClickListener {
                viewModel.deleteFromFavourites(movie)
            }

            view.setOnClickListener {
                findNavController().navigate(
                    WatchListFragmentDirections.actionHomeWatchlistToMovieDetailsFragment(
                        movie.movieId
                    )
                )
            }
        }
    }
}
