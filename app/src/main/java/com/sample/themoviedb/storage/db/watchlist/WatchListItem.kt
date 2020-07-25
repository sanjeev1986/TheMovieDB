package com.sample.themoviedb.storage.db.watchlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "WatchLists")
data class WatchListItem(
    @PrimaryKey val movieId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "posterpath") val posterPath: String?
)
