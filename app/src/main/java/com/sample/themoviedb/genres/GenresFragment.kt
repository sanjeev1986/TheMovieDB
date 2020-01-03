package com.sample.themoviedb.genres

import android.os.Bundle
import android.view.*
import android.widget.CheckedTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sample.themoviedb.R
import com.sample.themoviedb.TheMovieDbApp
import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.utils.ui.GridItemDecoration

class GenresFragment : Fragment() {

    private val listOfSelectedGenres = mutableSetOf<Genre>()
    private lateinit var genreGridView: RecyclerView
    private lateinit var menu:Menu

    private val viewModel by lazy {
        activity?.let {
            ViewModelProviders.of(
                it,
                TheMovieDbApp.getInstance(it).appViewModerFactory.buildGenreViewModelFactory()
            ).get(GenresViewModel::class.java)
        } ?: kotlin.run { throw IllegalStateException("Fragment not attached to activity anymore") }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_genres, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val refreshGenreListView = view.findViewById<SwipeRefreshLayout>(R.id.refreshGenreListView)
        refreshGenreListView.isEnabled = false
        genreGridView = view.findViewById(R.id.genreGridView)
        genreGridView.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            addItemDecoration(GridItemDecoration(resources.getDimensionPixelOffset(R.dimen.genre_filter_margin)))
        }
        activity?.run {
            viewModel.selectedGenres.value?.forEach{listOfSelectedGenres.add(it)}
            viewModel.genresLiveData.observe(this, Observer {
                genreGridView.adapter = GenreGridAdapter(it)
            })
            viewModel.fetchGenres()
        }
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        this.menu = menu
        (activity as AppCompatActivity).supportActionBar?.title = "Filters"
        menu.findItem(R.id.action_filter).isVisible = false
        menu.findItem(R.id.action_search).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            viewModel.selectedGenres.value = listOfSelectedGenres
            activity?.supportFragmentManager?.popBackStack()
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private inner class GenreGridAdapter(private val listOfGenre: List<Genre>) : RecyclerView.Adapter<GenreViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder =
            GenreViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_genre,
                    parent,
                    false
                )
            )

        override fun getItemCount(): Int = listOfGenre.size

        override fun onBindViewHolder(holder: GenreViewHolder, position: Int) = holder.bind(listOfGenre[position])

    }

    private inner class GenreViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val genretxtView by lazy(LazyThreadSafetyMode.NONE) {
            view.findViewById<CheckedTextView>(R.id.genretxtView)
        }

        fun bind(genre: Genre) {
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.genre_unselected))
            genretxtView.text = genre.name
            if (viewModel.selectedGenres.value?.contains(genre) == true) {
                genretxtView.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
                genretxtView.isChecked = true
            }
            view.setOnClickListener {
                genretxtView.toggle()
                if (genretxtView.isChecked) {
                    genretxtView.setTextColor(ContextCompat.getColor(view.context, R.color.colorAccent))
                    listOfSelectedGenres.add(genre)
                } else {
                    genretxtView.setTextColor(ContextCompat.getColor(view.context, R.color.white))
                    listOfSelectedGenres.remove(genre)
                }
            }
        }
    }
}