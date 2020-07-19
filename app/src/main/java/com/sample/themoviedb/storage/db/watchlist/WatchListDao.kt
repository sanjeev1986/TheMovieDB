package com.sample.themoviedb.storage.db.watchlist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface WatchListDao {

    @Query("SELECT * FROM WatchLists")
    suspend fun getWatchLists(): List<WatchListItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchList(watchListItem: WatchListItem)

    @Update
    suspend fun updateWatchList(watchListItem: WatchListItem)

    @Delete
    suspend fun deleteWatchList(watchListItem: WatchListItem): Int

}